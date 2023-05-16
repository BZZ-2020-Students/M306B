enum FileType {
    Consumption = "Consumption",
    Production = "Production"
}

enum MeasureUnit {
    KWH = "KWH",
}

enum TimeUnit {
    MIN = "MIN",
}

interface FileDate {
    fileCreationDate: string;
    startDate: string;
    endDate: string;
}

interface Resolution {
    timeUnit: TimeUnit;
    resolution: number;
}

interface Observation {
    position: number;
    volume: number;
}

interface SdatFile {
    fileName: string;
    fileType: FileType;
    resolution: Resolution;
    measureUnit: MeasureUnit;
    observations: Observation[];
}

interface Data {
    fileDate: FileDate;
    sdatfiles: SdatFile[];
}

async function SDATFileDayChart(sdatFilesRaw: any) {
    let jsonData: Data = JSON.parse(sdatFilesRaw);

    const datasets = jsonData.sdatfiles.map(sdatFile => {
        return {
            label: sdatFile.fileType,
            data: sdatFile.observations.map(observation => observation.volume)
        }
    });

    const startDate = jsonData.fileDate.startDate
    const resolution = jsonData.sdatfiles[0].resolution;
    let dates = [];
    jsonData.sdatfiles[0].observations.forEach(function (observation) {
        let minute = (observation.position - 1) * resolution.resolution;
        let newDate = new Date(startDate)
        newDate.setMinutes(newDate.getMinutes() + minute)

        dates.push(newDate.toLocaleString())
    })
    const data = {
        labels: dates,
        datasets: datasets
    }

    // @ts-ignore
    new Chart(
        document.getElementById('sdat-file-chart'),
        {
            type: 'line',
            data: data,
        }
    );
}
