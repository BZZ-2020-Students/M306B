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
    fileName: string,
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
    economicActivity: FileType;
    resolution: Resolution;
    measureUnit: MeasureUnit;
    observations: Observation[];
}

interface SdatWithFileDate {
    fileDate: FileDate;
    sdatfiles: SdatFile[];
}

interface ChartData {
    label: FileType,
    data: number[]
}

let sdatFileChart = null

export function SDATFileDayChart(sdatFilesRaw: any) {
    let jsonData: SdatWithFileDate[] = JSON.parse(sdatFilesRaw);

    const fileTypes: FileType[] = [];
    for (let i = 0; i < jsonData.length; i++) {
        const sdatWithFileDate = jsonData[i];
        for (let j = 0; j < sdatWithFileDate.sdatfiles.length; j++) {
            const sdatFile = sdatWithFileDate.sdatfiles[j];
            if (fileTypes.indexOf(sdatFile.economicActivity) === -1) {
                fileTypes.push(sdatFile.economicActivity);
            }
        }
    }

    const datasets: ChartData[] = [];
    for (let i = 0; i < fileTypes.length; i++) {
        const fileType = fileTypes[i];
        const data: number[] = [];
        for (let j = 0; j < jsonData.length; j++) {
            const sdatWithFileDate = jsonData[j];
            for (let k = 0; k < sdatWithFileDate.sdatfiles.length; k++) {
                const sdatFile = sdatWithFileDate.sdatfiles[k];
                if (sdatFile.economicActivity === fileType) {
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

    let dates: string[] = [];
    for (let i = 0; i < jsonData.length; i++) {
        const sdatWithFileDate = jsonData[i];
        const startDate = sdatWithFileDate.fileDate.startDate;

        let longest_sdat_file = undefined;
        for (let j = 0; j < sdatWithFileDate.sdatfiles.length; j++) {
            const sdatFile = sdatWithFileDate.sdatfiles[j];
            if (longest_sdat_file === undefined || sdatFile.observations.length > longest_sdat_file.observations.length) {
                longest_sdat_file = sdatFile;
            }
        }

        for (let j = 0; j < longest_sdat_file.observations.length; j++) {
            const resolution = longest_sdat_file.resolution;
            const observation = longest_sdat_file.observations[j];
            let minute = (observation.position - 1) * resolution.resolution;
            let newDate = new Date(startDate);
            newDate.setMinutes(newDate.getMinutes() + minute)

            dates.push(newDate.toLocaleString())
        }
    }


    // const maxDataPoints = 100 / datasets[0].data.length;
    // console.log("maxDataPoints: " + maxDataPoints )
    // console.log("DATA LENGTH RAAAAA: " + datasets[0].data.length)
    // const groupSize = datasets[0].data.length * maxDataPoints;
    // console.log("groupSize:" + groupSize)
    // const mergedData = Array.from({length: 100}, (_, i) => {
    //     console.log("i: " + i)
    //     const groupStart = i * groupSize;
    //     console.log("groupStart: " + groupStart)
    //     const groupEnd = groupStart + groupSize;
    //     console.log("groupEnd: " + groupEnd)
    //     const groupValues = datasets[0].data.slice(groupStart, groupEnd);
    //     const groupSum = groupValues.reduce((sum, value) => sum + value, 0);
    //     return groupSum / groupSize;
    // });

    const maxDataPoints = 100;
    const groupSize = Math.ceil(datasets[0].data.length / maxDataPoints);
    console.log("groupSize:" + groupSize)
    datasets[0].data = Array.from({length: maxDataPoints}, (_, i) => {
        const groupStart = i * groupSize;
        const groupEnd = groupStart + groupSize;
        console.log("groupStart: " + groupStart)
        console.log("groupEnd: " + groupEnd)
        const groupValues = datasets[0].data.slice(groupStart, groupEnd);
        const groupSum = groupValues.reduce((sum, value) => sum + value, 0);
        return groupSum / groupValues.length;
    })
    datasets[1].data = Array.from({length: maxDataPoints}, (_, i) => {
        const groupStart = i * groupSize;
        const groupEnd = groupStart + groupSize;
        console.log("groupStart: " + groupStart)
        console.log("groupEnd: " + groupEnd)
        const groupValues = datasets[1].data.slice(groupStart, groupEnd);
        const groupSum = groupValues.reduce((sum, value) => sum + value, 0);
        return groupSum / groupValues.length;
    })

    const data = {
        labels: dates,
        datasets: datasets
    }

    const zoomOptions = {
        pan: {
            enabled: true,
            mode: 'x',
            modifierKey: 'ctrl',
        },
        zoom: {
            mode: 'x',
            drag: {
                enabled: true,
                borderColor: 'rgb(54, 162, 235)',
                borderWidth: 1,
                backgroundColor: 'rgba(54, 162, 235, 0.3)'
            }
        }
    };


    // @ts-ignore
     sdatFileChart = new Chart(
        document.getElementById('sdat-file-chart') as HTMLCanvasElement,
        {
            type: 'line',
            data: data,
            options: {
                plugins: {
                    zoom: zoomOptions,
                },
                scales: {
                    y: {
                        stacked: true
                    }
                }
            }
        }
    );


}
export function resetZoomChart() {
    sdatFileChart.resetZoom()
}

