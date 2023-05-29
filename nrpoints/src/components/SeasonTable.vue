<script lang="ts" setup>

const props = defineProps({
    standings: {type: Object, required:true},
})

</script>

<template>

    <div class='shadow-lg table-responsive'>
        <table   class="table table-primary table-striped table-hover table-bordered table-sm">
            <thead class='table-dark'>
                <tr class="">                        
                    <th width="5%" scope="col">RANK</th>
                    <th width="17%" scope="col">DRIVER</th>
                    <th width="5%" scope="col">CAR</th>
                    <th width="7%" scope="col">POINTS</th>
                    <th width="7%" scope="col">LDR</th>
                    <th width="7%" scope="col">NXT</th>
                    <th width="3%" scope="col">STARTS</th>
                    <th width="3%" scope="col">POLES</th>
                    <th width="3%" scope="col">WINS</th>
                    <th width="3%" scope="col">T5</th>
                    <th width="3%" scope="col">T10</th>
                    <th width="3%" scope="col">DNF</th>
                    <th width="9%" scope="col">LC</th>
                    <th width="7%" scope="col">LL</th>
                    <th width="9%" scope="col">AVG START</th>
                    <th width="9%" scope="col">AVG FINISH</th>
                </tr>
            </thead>
            <tbody class='table-group-divider'>
                <tr v-for="(driver, i) in standings" class="">
                    <th scope="row">{{ driver.pointsPosition }}</th>
                    <td>{{ driver.name }}</td>
                    <td>{{ driver.number }}</td>
                    <td>{{ driver.points }}</td>
                    <td>{{ driver.pointsToLeader }}</td>
                    <td>{{ driver.pointsToNext }}</td>
                    <td>{{ driver.racesRun }}</td>
                    <td>{{ driver.poles }}</td>
                    <td>{{ driver.wins }}</td>
                    <td>{{ driver.t5 }}</td>
                    <td>{{ driver.t10 }}</td>
                    <td>{{ driver.dnfs }}</td>
                    <td>{{ driver.lapsRun }}</td>
                    <td>{{ driver.lapsLed }}</td>
                    <td>{{ driver.avgStart.toFixed(2) }}</td>
                    <td>{{ driver.avgFinish.toFixed(2) }}</td>
                </tr>               
            </tbody>
        </table>
    </div>

    
</template>