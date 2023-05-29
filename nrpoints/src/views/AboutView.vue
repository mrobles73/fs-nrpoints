<script lang="ts" setup>

</script>

<template>
    <div class="container pt-nav d-flex justify-content-center text-center pb-footer">
        <div class='w-100'>                    
            <div class='p-5 bg-light border rounded-3 text-center shadow-lg'>
                <h2 class='pb-3'>About</h2>
                <p class='text-start'>Since the in-game scoring system for NR2003 is limited up to 2003, to be able to calculate full season standings for any other year with accurate scoring, I made this application and decided to put it here to easily use it. The way it works is using the html file that you can export after running a race in NR2003. 
                    You just select the series, the year (ranging from 1975 to now), pick the export files and submit. 
                    Make sure the file names end with the correlating race number so the race scoring order can be accurate (If not race order is mixed up and points will not be calculated correctly).
                    Once you submit and the standings are displayed, you can add more races as well by clicking the Add Race button and selecting the files. 
                    If the series had a playoffs/chase that year, the program will only read up to the number of races that are usually run in that series year (ex. 36 for cup). 
                    File types for now are any html result files that have been exported from the game or that match the export file structure.</p>
                <p class='text-start'>By creating an account you can save your uploaded seasons to make it easier to keep track of all your past and current seasons.</p>
                <h4>Current Issues</h4>
                <p class='text-start'>The scoring system is accurate to the NASCAR points system for each year except for the years that stage points were introduced since there wasn't a way to track that from the race exports. Playoff points are given to the winner of the race, just not any stage points, so it's close but not completely accurate.</p>
                <p class='text-start'>Another issue is with drivers that have the same name. It uses the name for distinguishing between drivers since that is the closest way to identify them. The issue is with the way the game exports the race results with just the first letter of each driver along with the last name. So drivers like Kyle and Kurt Busch will both be K Busch. They will obviously be running different numbers but numbers can change during a season so that will not always be accurate. For now the only way I've come up with to solve this is by leaving the first name empty in game and adding their first name to the last name section.</p>
            </div>                    
        </div>                
    </div>
</template>
