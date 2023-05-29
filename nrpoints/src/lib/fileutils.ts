export interface iSeason {
    id: string | null;
    userId: string;
    year: number;
    racesRun: number;
    ended: boolean;
    series: string;
    results: any;
    currentStandings: any;
    allStandings: any;
}

export function getJSONFromHTML(files: any, year: number, series: string) {
    return new Promise(function(resolve, reject) {
        const filePromises = [];
        for(let i=0; i<files.length; i++){
            filePromises.push(readFile(files[i]));
        }
        Promise.all(filePromises).then((result) => {
            const rarr: any[] = [];
            const resultsJSON = { year:year, series:series, races:rarr};
            result.forEach(htmlText => {      
                const {track, date, results} = getResults(htmlText);                          
                resultsJSON.races.push({track:track, date:date, results:results});
            });
            //console.log(JSON.stringify(resultsJSON));
            resolve(resultsJSON);
        }).catch((error) => {
            reject(error.message);
        });
    });
}

export function addRacesToCurrentResults(files:any, currentResults:any) {
    return new Promise((resolve, reject) => {
        const filePromises = [];
        for(let i=0; i<files.length; i++){
            filePromises.push(readFile(files[i]));
        }
        Promise.all(filePromises).then((result) => {
            result.forEach(htmlText => {      
                const {track, date, results} = getResults(htmlText);          
                currentResults.races.push({track:track, date:date, results:results});
            });
            resolve(currentResults);
        }).catch((error) => {
            reject(error.message);
        });
    });
}

export function validateFilesSelected(files: any) {
    if(files.length === 0)
        return { error: true, message:"Please select a file"};
    else {
        for(let file of files) {
            if(file.type != 'text/html') {
                console.log('Wrong file type. Please try again.');
                return { error: true, message:"Wrong file type. Please try again."};
            }
        }
        return { error: false, message:""};
    }
}

export function validatePassword(password: string) {
    return /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/.test(password);
}

export function validateUsername(username: string) {
    return /(?=^.{3,15}$)(?!.*[_-]{2,})[^<>[\]{}|\\\/^~%# :;,$%?\0-\cZ]+$/.test(username);
}

function getResults(htmlText: any) {
    try {
        const doc = document.createElement('html');
        //console.log(el);
        doc.innerHTML = htmlText;
        let h3 = doc.querySelectorAll('h3');
        let track = h3[0].innerText.trim();
        let date = h3[1].innerText.trim();
        let tables = doc.querySelectorAll('tbody');
        let tableRows;
        if(tables.length === 1) {
            tableRows = tables[0].querySelectorAll("tr");
        } else {
            tableRows = tables[1].querySelectorAll("tr");
        }
        let results: { name: string; finish: number; start: number; number: number; interval: string; lapsRun: number; lapsLed: number; points: number; status: string; lapsLedLeader: boolean; }[] = [];
        tableRows.forEach(row => {
            let tableData = row.querySelectorAll('td');
            if(tableData[0].innerText.trim() !== "F") {
                let name = tableData[3].innerText.trim();
                let finish = parseInt(tableData[0].innerText.trim());
                let start = parseInt(tableData[1].innerText.trim());
                let number = parseInt(tableData[2].innerText.trim());
                let laps = parseInt(tableData[5].innerText.trim());
                let led = 0;
                let lapsLedLeader = false;
                if(tableData[6].innerText.trim().includes("*")) {
                    lapsLedLeader = true;
                    led = parseInt(tableData[6].innerText.trim().replace("*", ""));
                } else {
                    led = parseInt(tableData[6].innerText.trim());
                }
                let points = parseInt(tableData[7].innerText.trim());
                let status = tableData[8].innerText.trim();
                let interval = tableData[4].innerText.trim();
                results.push({name:name, finish:finish, start:start, number:number, interval:interval, lapsRun:laps, lapsLed:led, points:points, status:status, lapsLedLeader:lapsLedLeader});
            }
        });
        return {track:track, date:date, results:results};
    } catch (error) {
        throw new Error('Unable to read files');
    }
    
}

function readFile(file: any) {
    return new Promise(function(resolve, reject) {
        let reader = new FileReader();
        reader.onload = () => {
            if(reader.result === '') {
                reject(new Error("A file passed in was empty. Please try again."));
            } else {
                resolve(reader.result);
            }
        }
        reader.onerror = () => {
            reject(new Error("Error reading file. Please try again."));
        }
        reader.readAsText(file);
    
    });
}

