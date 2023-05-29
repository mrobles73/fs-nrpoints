<script lang="ts" setup>

import { reactive, ref } from 'vue';
import { useToast, POSITION } from "vue-toastification";
import { getJSONFromHTML, validateFilesSelected } from '@/lib/fileutils';

const emit = defineEmits(['handleSubmit']);

const fileUpload = ref();

const races = reactive({
    year: 2003,
    series: 'CUP',
})

const toast = useToast();

async function onSubmit() {
    const files = fileUpload.value.files;
    const result = validateFilesSelected(files);
    if(!result.error) {
        getJSONFromHTML(files, races.year, races.series).then(rjson => {
            emit('handleSubmit', rjson);            
        }).catch(e => toast.error("Error reading through files. Please try again", {
            timeout: 2000,
            position: POSITION.TOP_CENTER
        }));
    } else {
        toast.error(result.message, {
            timeout: 2000,
            position: POSITION.TOP_CENTER
        })
    }
} 

</script>

<template>
<div class='p-5 bg-light border rounded-3 text-center shadow-lg'>
            <h3>Upload Races</h3>
            <form data-bitwarden-watching="1" @submit.prevent="onSubmit">
                <div class="d-flex flex-row justify-content-center gap-3">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" id="cup" name="series" value="CUP" checked v-model="races.series"/>
                        <label class="radio-label" for="cup">Cup</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" id="gns" name="series" value="GNS" v-model="races.series"/>
                        <label class="radio-label" for="gns">Xfinity</label>                    
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" id="trucks" name="series" value="TRUCKS" v-model="races.series"/>
                        <label class="radio-label" for="trucks">Trucks</label>
                    </div>
                </div>
                

                <div class="input-group mb-3">
                    <span class="input-group-text" for="season-year" >Year</span>
                    <input class="form-control" id="season-year" name="season-year" type="number" min="1975" max="" step="1" v-model="races.year"/>
                </div>
                <div class="mb-3">            
                    <input ref="fileUpload" class="form-control" type="file" id="race-files" name="race-files" accept=".html" multiple />
                </div>
                <button class="btn btn-primary" type="submit">Submit</button>
            </form>
        </div>

</template>