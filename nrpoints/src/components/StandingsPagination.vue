<script lang="ts" setup>

import { reactive } from 'vue';

const emit = defineEmits(['setCurrent']);

const props = defineProps({
    current: {type: Number, required:true},
    length: {type:Number, required:true},
    pagBtns: Array<number>,
    addedClasses: String
})

//const addedClasses = 'justify-content-md-end justify-content-center mb-2';

//const pagBtns = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

const nextPage = () => {
    emit('setCurrent', props.current === props.length-1 ? props.current : props.current+1);
}

const prevPage = () => {
    emit('setCurrent', props.current == 0 ? 0 : props.current-1);
}

const lastPage = () => {
    emit('setCurrent', props.length-1);
}

const firstPage = () => {
    emit('setCurrent', 0);
}

const setCurrent = (index:number) => {
    emit('setCurrent', index);
}


</script>

<template>
    <ul :class="'pagination pagination-sm ' + addedClasses">
        <li :class="[current===0 ? 'disabled' : '' , 'page-item']">
            <a href="javascript:void(0)" class="page-link" aria-lable="Previous" @click="firstPage"><span aria-hidden="true">&laquo;</span></a>
        </li>
        <li :class="[current===0 ? 'disabled' : '' , 'page-item']">
            <a href="javascript:void(0)" class="page-link" @click="prevPage">&lsaquo;</a>
        </li>

        <li v-for="(btn, index) in pagBtns" :class="[current===index ? 'active':'' , 'page-item']">
            <a v-if="(index > current-3 && index < current+3) || (current < 3 && index < 5) || (current > length-4 && index > length-6)" href="javascript:void(0)" class="page-link" @click="setCurrent(index)">{{ btn+1 }}</a>
        </li>
        
        <li :class="[current===length-1 ? 'disabled' : '' , 'page-item']">
            <a href="javascript:void(0)" class="page-link" @click="nextPage">&rsaquo;</a>
        </li>
        <li :class="[current===length-1 ? 'disabled' : '' , 'page-item']">
            <a href="javascript:void(0)" class="page-link" aria-lable="Previous" @click="lastPage"><span aria-hidden="true">&raquo;</span></a>
        </li>
    </ul>

</template>