<script lang="ts" setup>
const props = defineProps({
    results: {type: Object, required:true},
});

</script>

<template>
    <div class='shadow-lg table-responsive'>
        <table   class="table table-primary table-striped table-hover table-bordered table-sm">
            <thead class='table-dark'>
                <tr class="">                        
                    <th  scope="col">FINISH</th>
                    <th  scope="col">START</th>
                    <th  scope="col">CAR</th>
                    <th  scope="col">DRIVER</th>
                    <th  scope="col">INTERVAL</th>
                    <th  scope="col">LAPS</th>
                    <th  scope="col">LED</th>
                    <th  scope="col">POINTS</th>
                    <th  scope="col">STATUS</th>
                </tr>
            </thead>
            <tbody class='table-group-divider'>
                <tr v-for="(driver, index) in results.results">
                    <th scope="row">{{ driver.finish }}</th>
                    <td>{{ driver.start }}</td>
                    <td>{{ driver.number }}</td>
                    <td>{{ driver.name }}</td>
                    <td>{{ driver.interval }}</td>
                    <td>{{ driver.lapsRun }}</td>
                    <td>{{ driver.lapsLed }}</td>
                    <td>{{ driver.points }}</td>
                    <td>{{ driver.status }}</td>
                </tr>
             
            </tbody>
        </table>
    </div>
    
</template>