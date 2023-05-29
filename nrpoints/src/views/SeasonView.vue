<script lang="ts" setup>
import { onMounted, reactive } from 'vue';
import { useRoute } from 'vue-router';
import router from "@/router";
import axios from 'axios';
import { useToast, POSITION } from "vue-toastification";
import SeasonTable from '@/components/SeasonTable.vue';
import ResultsTable from '@/components/ResultsTable.vue';
import StandingsPagination from '@/components/StandingsPagination.vue';
import LoadingScreen from '@/components/LoadingScreen.vue';
import { type iSeason, addRacesToCurrentResults, validateFilesSelected } from '@/lib/fileutils';

const route = useRoute();
const toast = useToast();

const id = route.params.id;

const season = reactive ({
    data: {} as iSeason,
    btns:[0],
});

const displayController = reactive ({
    standings: true,
    curStandings: 36,
    curResults: 0,
    loading: false
});


const swapDisplayController = () => {
    displayController.standings = !displayController.standings;
}

const setCurStandings = (index:number) => {
    displayController.curStandings = index;
}

const setCurResults = (index:number) => {
    displayController.curResults = index;
}

const errorMessage = (message: string) => {
    toast.error(message, {
        timeout: 4000,
        position: POSITION.TOP_CENTER
    })
}

async function handleAddRaces(event:any) {
    console.log('adding races');
    const addBtn = document.querySelector("#addRace");
    addBtn!.innerHTML = `<div class="spinner-border spinner-border-sm" role="status"><span class="visually-hidden">Loading...</span></div>`;
    const result = validateFilesSelected(event.target.files);
    if(!result.error) {        
        try {
            const rjson = await addRacesToCurrentResults(event.target.files, season.data.results); //this throws error
            const response = await axios.post('season/standings', rjson);
            addBtn!.innerHTML = `Add Race(s)`;
            if(response.status === 200) {
                response.data.id = season.data.id;
                response.data.userId = season.data.userId;
                season.data = response.data;
                season.btns = [...Array(season.data.racesRun).keys()];
            } else {
                errorMessage("There was an error adding races. Please try again or refresh the page.");
            }   
        } catch (err) {
            addBtn!.innerHTML = `Add Race(s)`;
            errorMessage("Unable to read files. Please try again.");
        }              
    } else {
        addBtn!.innerHTML = `Add Race(s)`;
        errorMessage(result.message);
    }
}

async function handleSaveSeason(event:any) {
    const saveBtn = document.querySelector("#saveSeason");
    saveBtn!.innerHTML = `<div class="spinner-border spinner-border-sm" role="status"><span class="visually-hidden">Loading...</span></div>`;
    const response = await axios.post('season/save', season.data);
    saveBtn!.innerHTML = `Save`;
    if(response.status === 200) {
        toast.success('Season Saved', {
            timeout: 4000,
            position: POSITION.TOP_CENTER
        });
        season.data = response.data;
        season.btns = [...Array(season.data.racesRun).keys()];
    } else {
        errorMessage("There was an error saving season. Please try again or refresh the page.");
    }
}

async function handleDeleteSeason(event:any) {
    displayController.loading = true;
    axios.delete(`season/delete?id=${id}`)
        .then(response => {
            console.log(response);
            toast.success('Season deleted successfully', {
                timeout: 4000,
                position: POSITION.TOP_CENTER
            });
            displayController.loading = false;
            router.push('/dashboard');
        })
        .catch(error => {
            displayController.loading = false;
            errorMessage("There was an error deleting the season");
        })
}

onMounted( async () => {
    console.log('hello');
    const response = await axios.get(`season/id?id=${id}`);
    if(response.status === 200) {        
        season.data = response.data;
        season.btns = [...Array(season.data.racesRun).keys()];
        displayController.curStandings = season.data.racesRun-1;
        displayController.curResults = season.data.racesRun-1;
    } else {
        errorMessage("There was an error loading user data");
        router.back();
    }
});


</script>

<template>

    <LoadingScreen v-if="displayController.loading"/>

    <div v-else class="container pt-nav">
        <div class="px-md-4">
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3  mb-3 border-bottom">
                <h1 class="h2">{{ season.data.year }} {{ season.data.series }} Season</h1>
                <div class="btn-toolbar mb-2 mb-md-0">
                    <div class="btn-group me-2">            
                        <label :class="[season.data.ended ? 'disabled' : '', 'form-label btn btn-outline-primary btn-sm']" htmlFor="add-race" id="addRace">Add Race(s)</label>
                        <input type="file" class="form-control cust-input-file" id="add-race" name="add-race" accept=".html" multiple @change="handleAddRaces"/>
                        
                        <button type="button" class="btn btn-outline-success mb-2 btn-sm" id="saveSeason" @click="handleSaveSeason">Save</button>   
                        <button type="button" class="btn btn-sm btn-outline-danger mb-2" data-bs-toggle="modal" data-bs-target="#deleteConfirmModal">Delete</button>
                        <button type="button" class="btn btn-sm btn-outline-primary mb-2 disabled">Share</button>
                        <button type="button" class="btn btn-sm btn-outline-primary mb-2 disabled">Export</button>              
                    </div>


                    <div class="btn-group " role="group" aria-label="Basic radio toggle button group">
                        <input type="radio" class="btn-check" name="tbdisplay" id="standingsradio" autocomplete="off" checked @click="swapDisplayController">
                        <label class="btn btn-outline-primary btn-sm mb-2" for="standingsradio">Standings</label>

                        <input type="radio" class="btn-check" name="tbdisplay" id="resultsradio" autocomplete="off" @click="swapDisplayController">
                        <label class="btn btn-outline-primary btn-sm mb-2" for="resultsradio">Race Results</label>
                    </div>
                    
                </div>
            </div>

            <!-- Delete Modal -->
            <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModal" aria-hidden="true">
                <div class="modal-dialog ">
                    <div class="modal-content rounded-0 shadow">
                        <div class="modal-body p-4 text-center">
                            <h5 class="mb-0">Are you sure you want to delete this season?</h5>
                            <p class="mb-0">Deleting is final.</p>
                        </div>
                        <div class="modal-footer flex-nowrap p-0">
                            <button type="button" class="btn btn-lg btn-danger fs-6 text-decoration-none col-6  m-0 rounded-0" data-bs-dismiss="modal" @click="handleDeleteSeason">Delete</button>
                            <button type="button" class="btn btn-lg btn-primary fs-6 text-decoration-none col-6  m-0 rounded-0" data-bs-dismiss="modal">No thanks</button>
                        </div>
                    </div>                    
                </div>
            </div>

            <div class="pb-3 mb-5 border-bottom">
                <div class="btn-toolbar mb-2 mb-md-0">
                    
                </div>  
                <div v-if="displayController.standings" class='text-center'>
                    <h2>Season Standings</h2>
                    <h6>Races Run {{ displayController.curStandings+1 }}/{{ season.data.racesRun }}</h6>
                    <StandingsPagination :current="displayController.curStandings" :length="season.data.racesRun" :pagBtns="season.btns" addedClasses="justify-content-md-end justify-content-center mb-2" @setCurrent="setCurStandings"/>                    
                    <div v-for="(standing, index) in season.data.allStandings">
                        <SeasonTable v-if="index===displayController.curStandings" :standings="standing" />
                    </div>
                    <StandingsPagination :current="displayController.curStandings" :length="season.data.racesRun" :pagBtns="season.btns" addedClasses="justify-content-center mt-2" @setCurrent="setCurStandings"/>                    
                </div>
                <div v-else class='text-center'>
                    <div v-for="(result, index) in season.data.results.races">
                        
                        <div v-if="index===displayController.curResults">
                            <h2>Race {{ displayController.curResults+1 }}/{{ season.data.racesRun }}</h2>
                            <h6>{{ result.track }}</h6>
                            <StandingsPagination :current="displayController.curResults" :length="season.data.racesRun" :pagBtns="season.btns" addedClasses="justify-content-md-end justify-content-center mb-2" @setCurrent="setCurResults"/>                            
                            <ResultsTable :results="result" />
                            <StandingsPagination :current="displayController.curResults" :length="season.data.racesRun" :pagBtns="season.btns" addedClasses="justify-content-center mt-2" @setCurrent="setCurResults"/>
                        </div>
                                                                    
                    </div>
                    
                </div>
                   
            </div>

        </div>
    </div>
    

</template>