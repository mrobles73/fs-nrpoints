<script lang="ts" setup>
import axios from 'axios';
import UploadRaces from './UploadRaces.vue';
import SeasonDisplay from './SeasonDisplay.vue';
import { reactive } from 'vue';


const seasonData = reactive({
    display: false,
    data: {type:Object},
    initialData: {type:Object}
});

async function handleSubmit(seasonJSON: any) {
    seasonData.initialData = seasonJSON;
    const response = await axios.post('season/standings', seasonJSON);
    if(response.status === 200) {
        seasonData.display = true;
        seasonData.data = response.data;
        
    } else {
        console.log('error fetching season data');
    }

}



function resetSeasonData(current:number, ) {
    seasonData.display = false;
}

function setSeasonData(data:any, initialData:any) {
    console.log(data.allStandings.length);
    seasonData.data = data;
    seasonData.initialData = initialData;
}


</script>

<template>
    <SeasonDisplay v-if="seasonData.display" :seasonData="seasonData.data" :initialData="seasonData.initialData" @resetSeasonData="resetSeasonData" @setSeasonData="setSeasonData"/>
    <UploadRaces v-else @handleSubmit="handleSubmit"/>
</template>