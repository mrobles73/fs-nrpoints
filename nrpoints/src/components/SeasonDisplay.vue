<script lang="ts" setup>
import { computed, onMounted, reactive } from 'vue';
import axios from 'axios';
import StandingsPagination from './StandingsPagination.vue';
import SeasonTable from './SeasonTable.vue';
import { type iSeason, addRacesToCurrentResults, validateFilesSelected } from '@/lib/fileutils';
import { useAuthStore } from '@/store/index';
import { useToast, POSITION } from "vue-toastification";

const store = useAuthStore();
const auth = computed(() => store.auth);
const user = store.user;
const emit = defineEmits(['resetSeasonData', 'setSeasonData']);
const toast = useToast();

let seasonId: null = null;

const seasonPageData = reactive({
    current: 0,
    length: 1,
    pagBtns: [0],
    standings: [{driver:{ pointsPosition: 0, name:'', number:0, points:0, pointsToLeader:0, pointsToNext:0, racesRun:0, poles:0, wins:0, t5:0, t10:0, dnfs:0, lapsRun:0, lapsLed:0, avgStart:0, avgFinish:0  }}],
    initial: {type:Object},
    saveData: {} as iSeason
});


const props = defineProps({
    seasonData: {type: Object, required:true},
    initialData: {type:Object}
})

const setCurrent = (index:number) => {
    seasonPageData.current = index;
}

const resetSeasonData = () => {
    seasonId = null;
    emit('resetSeasonData', seasonPageData.current);
}

const initializeData = (sdata:any, idata:any) => {
    seasonPageData.saveData = sdata;
    seasonPageData.standings = sdata.allStandings;
    seasonPageData.length = sdata.allStandings.length;
    seasonPageData.pagBtns = [...Array(seasonPageData.length).keys()];
    seasonPageData.initial = idata;
}

const errorMessage = (message: string) => {
    toast.error(message, {
        timeout: 4000,
        position: POSITION.TOP_CENTER
    })
}

//might have to do something when adding races to a season that was just saved while still in this view, i think this is fixed
async function handleAddRaces(event:any) {
    const result = validateFilesSelected(event.target.files);
    if(!result.error) {
        try {
            const rjson = await addRacesToCurrentResults(event.target.files, seasonPageData.initial);
            const response = await axios.post('season/standings', rjson);
            if(response.status === 200) {
                if(user) {
                    response.data.userId = user.id;
                    response.data.id = seasonId;
                }            
                emit('setSeasonData', response.data, rjson);
                initializeData(response.data, rjson);
            } else {
                errorMessage("There was an error adding races. Please try again or refresh the page.");
            }
        } catch (err) {
            errorMessage("Unable to read files. Please try again.");
        }        
    } else {
        errorMessage(result.message);
    }
}

async function handleSaveSeason(event:any) {
    const saveBtn = document.querySelector("#saveSeason");
    saveBtn!.innerHTML = `<div class="spinner-border spinner-border-sm" role="status"><span class="visually-hidden">Loading...</span></div>`;
    seasonPageData.saveData.userId = user.id;
    seasonPageData.saveData.id = seasonId;
    const response = await axios.post('season/save', seasonPageData.saveData);
    saveBtn!.innerHTML = `Save Season`;
    if(response.status === 200) {
        toast.success('Season Saved', {
            timeout: 4000,
            position: POSITION.TOP_CENTER
        });
        seasonId = response.data.id;
    } else {
        errorMessage("There was an error saving season. Please try again or refresh the page.");
    }
}


onMounted( () => {
    initializeData(props.seasonData, props.initialData);
});

</script>

<template>
    <div class='text-center'>
            <h2>{{ seasonData.year }} {{ seasonData.series }} Season Standings - Race {{ seasonPageData.current+1 }}</h2>
            <h6>{{ seasonData.raceTracks[seasonPageData.current] }}</h6>
            <div class='d-flex flex-md-row flex-column justify-content-between'>                
                <div class="">            
                    <label :class="[seasonPageData.saveData.ended ? 'disabled' : '', 'form-label btn btn-primary btn-sm']" htmlFor="add-race">Add Race(s)</label>
                    <input type="file" class="form-control form-control-sm cust-input-file" id="add-race" name="add-race" accept=".html" multiple @change="handleAddRaces"/>
                    <button type="button" class="btn btn-primary btn-sm mb-2 ms-1" @click="resetSeasonData">Reset/New Season</button>
                    <button v-if="auth" type="button" class="btn btn-primary btn-sm mb-2 ms-1" id="saveSeason" @click="handleSaveSeason">Save Season
                        
                    </button>   
                </div>
                <StandingsPagination :current="seasonPageData.current" :length="seasonPageData.length" :pagBtns="seasonPageData.pagBtns" addedClasses="justify-content-md-end justify-content-center mb-2" @setCurrent="setCurrent"/>                
            </div>
            <div  v-for="(single, index) in seasonPageData.standings">
                <SeasonTable v-if="index === seasonPageData.current" :standings="single" />
                
            </div>
                        
            <StandingsPagination :current="seasonPageData.current" :length="seasonPageData.length" :pagBtns="seasonPageData.pagBtns" addedClasses="justify-content-center mt-2" @setCurrent="setCurrent"/>
        </div> 


</template>