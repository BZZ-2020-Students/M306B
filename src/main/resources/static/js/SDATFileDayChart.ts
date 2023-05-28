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

interface SdatWithFileDate {
    fileDate: FileDate;
    sdatFiles: SdatFile[];
}

function SDATFileDayChart(sdatFilesRaw: any) {
    let jsonData: SdatWithFileDate[] = JSON.parse(sdatFilesRaw);
    console.log(jsonData)

    if (!Array.isArray(jsonData)) {
        console.error('jsonData is not an array');
        return;
    }

    const fileTypes: FileType[] = [];
    for (let i = 0; i < jsonData.length; i++) {
        const sdatWithFileDate = jsonData[i];
        for (let j = 0; j < sdatWithFileDate.sdatFiles.length; j++) {
            const sdatFile = sdatWithFileDate.sdatFiles[j];
            if (fileTypes.indexOf(sdatFile.fileType) === -1) {
                fileTypes.push(sdatFile.fileType);
            }
        }
    }

    const datasets = [];
    for (let i = 0; i < fileTypes.length; i++) {
        const fileType = fileTypes[i];
        const data: number[] = [];
        for (let j = 0; j < jsonData.length; j++) {
            const sdatWithFileDate = jsonData[j];
            for (let k = 0; k < sdatWithFileDate.sdatFiles.length; k++) {
                const sdatFile = sdatWithFileDate.sdatFiles[k];
                if (sdatFile.fileType === fileType) {
                    for (let l = 0; l < sdatFile.observations.length; l++) {
                        const observation = sdatFile.observations[l];
                        data.push(observation.volume);
                    }
                }
            }
        }
        datasets.push({
            label: fileType,
            data: data
        });
    }

    const startDate = jsonData[0].fileDate.startDate
    const resolution = jsonData[0].sdatFiles[0].resolution;
    let dates: string[] = [];
    for (let i = 0; i < jsonData.length; i++) {
        const sdatWithFileDate = jsonData[i];
        for (let j = 0; j < sdatWithFileDate.sdatFiles.length; j++) {
            const sdatFile = sdatWithFileDate.sdatFiles[j];
            for (let k = 0; k < sdatFile.observations.length; k++) {
                const observation = sdatFile.observations[k];
                let minute = (observation.position - 1) * resolution.resolution;
                let newDate = new Date(startDate)
                newDate.setMinutes(newDate.getMinutes() + minute)

                dates.push(newDate.toLocaleString())
            }
        }
    }

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

