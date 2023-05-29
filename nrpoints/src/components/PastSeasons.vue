<script lang="ts" setup>
import { computed, onMounted, reactive } from 'vue';
import axios, { type AxiosResponse } from 'axios';
import { useAuthStore } from '@/store/index';
import { useToast, POSITION } from "vue-toastification";
import StandingsPagination from '@/components/StandingsPagination.vue';
import type { iSeason } from '@/lib/fileutils';

const toast = useToast();
const store = useAuthStore();
const user = store.user;

const S_PER_PAGE = 5;

const pastSeasons = reactive ({
    data: null,
    nullText: '',
    filter: 'ALL',
    prevSort: '',
    pgBtns: [0],
    curPage: 0    
});

const displayedSeasons = computed(() => {
    if(pastSeasons.data) {
        const ret = (pastSeasons.data as Array<iSeason>).filter((s: { series: string; }) => (s.series === pastSeasons.filter || 'ALL' === pastSeasons.filter));        
        return ret.filter((s: { series: string; }, index: number) => (index < (S_PER_PAGE * (pastSeasons.curPage+1)) && index >= (S_PER_PAGE * (pastSeasons.curPage))));
    } else {
        return null;
    }
});

const setSeasonFilter = (event:any) => {
    pastSeasons.filter = event.target.value;
    if(pastSeasons.data) {
        const filtered = (pastSeasons.data as Array<iSeason>).filter((s: {series: string}) => (s.series === pastSeasons.filter || 'ALL' === pastSeasons.filter));
        pastSeasons.pgBtns = [...Array(Math.ceil(filtered.length / S_PER_PAGE)).keys()];
        pastSeasons.curPage = 0;
    }    
}

const setSortFilter = (event:any) => {
    if(pastSeasons.data) {
        const sortby = event.target.getAttribute("sortby");
        const reverse = pastSeasons.prevSort === sortby ? true : false;
        pastSeasons.prevSort = sortby;
        (pastSeasons.data as Array<iSeason>).sort((s1, s2) => {
            if(sortby === 'series') {
                return reverse ? s2.series.localeCompare(s1.series) : s1.series.localeCompare(s2.series);
            } else if(sortby === 'year') {
                return reverse ? s2.year - s1.year : s1.year - s2.year;
            } else if(sortby === 'races') {
                return reverse ? s1.racesRun - s2.racesRun : s2.racesRun - s1.racesRun;
            } else {
                return 0;
            }
        });
    }
}

const refreshSeasons = async () => {
    const refreshBtn = document.querySelector("#refreshSeasonsBtn");
    refreshBtn!.innerHTML = `<div class="spinner-border spinner-border-sm" role="status"><span class="visually-hidden">Loading...</span></div>`;
    await loadSeasonData();
    refreshBtn!.innerHTML = `<i class="bi bi-arrow-clockwise me-2"></i>Refresh`;
}

const loadSeasonData = async () => {
    const response = await axios.get(`season/userid?userId=${user.id}`) as AxiosResponse<any, any> | any;
    if(response.status === 200) {
        if(response.data.length > 0) {
            pastSeasons.data = response.data;
            pastSeasons.pgBtns = [...Array(Math.ceil(response.data.length / S_PER_PAGE)).keys()];
        }                
        //console.log(pastSeasons.data);
    } else if (response.logout) { //response.toString() === 'logout'
        store.logout();        
    } else {
        toast.error("There was an error loading user data", {
            timeout: 4000,
            position: POSITION.TOP_CENTER
        });
    }
}

const setCurrentSeasonPage = (index:number) => {
    pastSeasons.curPage = index;
}



onMounted( async () => {
    await loadSeasonData();
    pastSeasons.nullText = "Nothing Here Yet";
    //console.log(pastSeasons.data);
});

</script>

<template>
            <div class="d-flex justify-content-between pt-3 border-bottom">
                <h2>Past Seasons</h2>
                
                <div class="btn-toolbar mb-2 mb-md-0 ">
                    <div class="me-1">
                        <select class="form-select " aria-label="Default select example" @change="setSeasonFilter">
                            <option value="ALL" selected>All</option>
                            <option value="CUP" >Cup</option>
                            <option value="GNS">Xfinity</option>
                            <option value="TRUCKS">Trucks</option>                            
                        </select>
                    </div>
                    
                    <div class="dropdown me-1">
                        <button type="button" class="btn btn-outline-primary dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false"><i class="bi bi-sort-down pe-none me-2"></i>Sort</button>
                        <!-- <a class="nav-link dropdown-toggle" href="#"  >Sort</a> -->
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item sort-link" sortby="series" href="javascript:void(0)" @click="setSortFilter">Series</a></li>
                            <li><a class="dropdown-item sort-link" sortby="year" href="javascript:void(0)" @click="setSortFilter">Year</a></li>
                            <!-- <li><a class="dropdown-item sort-link" sortby="date" href="javascript:void(0)" @click="setSortFilter">Date</a></li> -->
                            <li><a class="dropdown-item sort-link" sortby="races" href="javascript:void(0)" @click="setSortFilter">Races</a></li>
                        </ul>
                    </div>
                    <div>
                        <button type="button" class="btn btn-outline-primary " id="refreshSeasonsBtn" @click="refreshSeasons"><i class="bi bi-arrow-clockwise me-2"></i>Refresh</button>
                    </div>
                    
                </div>                
            </div>            
            <div class="my-3">
                <div class="list-group">
                        <a v-for="(season, index) in displayedSeasons" :href="'/season/'+season.id" class="list-group-item list-group-item-action d-flex gap-3 py-3" aria-current="true">
                            <i class="bi bi-trophy" style="font-size: 1.5em;"></i>
                            <div class="d-flex gap-2 w-100 justify-content-between">
                                <div>
                                <h6 class="mb-0">{{ season.year }} {{ season.series }} Series</h6>
                                <p class="mb-0 opacity-75">{{ season.racesRun }} races</p>
                                </div>
                                <!-- <small class="opacity-50 text-nowrap">now</small> -->
                            </div>
                        </a>
                </div>
            </div>
            <div v-if="displayedSeasons === null || displayedSeasons.length === 0" class="my-5 text-center">
                <h4 class="text-muted">{{ pastSeasons.nullText }}</h4>
            </div>
            <StandingsPagination v-if="pastSeasons.pgBtns.length > 1" :current="pastSeasons.curPage" :length="pastSeasons.pgBtns.length" :pagBtns="pastSeasons.pgBtns" addedClasses="justify-content-center mt-2" @setCurrent="setCurrentSeasonPage"/>
    
</template>