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

interface ChartSingleData {
    x: Date,
    y: number
}

interface ChartData {
    label: string,
    data: ChartSingleData[],
}

let sdatFileChart = null

export function SDATFileDayChart(sdatFilesRaw: any) {
    console.log(sdatFilesRaw)
    let jsonData: SdatWithFileDate[] = JSON.parse(sdatFilesRaw);
    console.log(jsonData)

    let dates: Date[] = [];
    for (let i = 0; i < jsonData.length; i++) {
        const sdatWithFileDate = jsonData[i];
        const startDate = sdatWithFileDate.fileDate.startDate;

        let longest_sdat_file = undefined;
        for (let i = 0; i < sdatWithFileDate.sdatfiles.length; i++) {
            const sdatFile = sdatWithFileDate.sdatfiles[i];
            if (longest_sdat_file === undefined || sdatFile.observations.length > longest_sdat_file.observations.length) {
                longest_sdat_file = sdatFile;
            }
        }

        for (let i = 0; i < longest_sdat_file.observations.length; i++) {
            const resolution = longest_sdat_file.resolution;
            const observation = longest_sdat_file.observations[i];
            let minute = (observation.position - 1) * resolution.resolution;
            let newDate = new Date(startDate);
            newDate.setMinutes(newDate.getMinutes() + minute)
            dates.push(newDate)
        }
    }

    const minDate = dates.reduce(function (a, b) {
        return a < b ? a : b;
    });
    const maxDate = dates.reduce(function (a, b) {
        return a > b ? a : b;
    });

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

    let datasets: ChartData[] = [];

    for (let i = 0; i < fileTypes.length; i++) {
        const fileType = fileTypes[i];
        let data: ChartSingleData[] = [];
        for (let j = 0; j < dates.length; j++) {
            const date = dates[j];
            let volume = 0;
            for (let k = 0; k < jsonData.length; k++) {
                const sdatWithFileDate = jsonData[k];
                for (let l = 0; l < sdatWithFileDate.sdatfiles.length; l++) {
                    const sdatFile = sdatWithFileDate.sdatfiles[l];
                    if (sdatFile.economicActivity === fileType) {
                        for (let m = 0; m < sdatFile.observations.length; m++) {
                            const observation = sdatFile.observations[m];
                            let minute = (observation.position - 1) * sdatFile.resolution.resolution;
                            let newDate = new Date(sdatWithFileDate.fileDate.startDate);
                            newDate.setMinutes(newDate.getMinutes() + minute)
                            if (newDate.getTime() === date.getTime()) {
                                volume += observation.volume;
                            }
                        }
                    }
                }
            }
            data.push({x: date, y: volume});
        }
        datasets.push({label: fileType, data: data});
    }

    console.log(datasets)

    const zoomOptions = {
        pan: {
            enabled: true,
            mode: 'x',
            modifierKey: 'ctrl',
        },
        zoom: {
            wheel: {
                enabled: true,
            },
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
            data: {
                datasets: datasets,
            },
            options: {
                indexAxis: 'x',
                parsing: false,
                plugins: {
                    zoom: zoomOptions,
                    decimation: {
                        enabled: true,
                        algorithm: 'lttb',
                    }
                },
                scales: {
                    x: {
                        type: 'time',
                        suggestedMin: minDate,
                        suggestedMax: maxDate,
                        ticks: {
                            source: 'auto',
                            autoSkip: true,
                            maxRotation: 0,
                        },
                        time: {
                            displayFormats: {
                                'day': 'DD.MM.YYYY, hh:mm:ss',
                                'hour': 'DD.MM.YYYY, hh:mm:ss',
                                'minute': 'hh:mm:ss',
                                'second': 'hh:mm:ss',
                                'millisecond': 'hh:mm:ss',
                                'week': 'MMM DD',
                                'month': 'MMM DD',
                                'quarter': 'MMM DD',
                                'year': 'MMM DD',
                            }
                        }
                    }
                }
            }
        }
    );
}

export function resetZoomChart() {
    sdatFileChart.resetZoom()
}

export function toggleDecimation() {
    const currentDecimation = sdatFileChart.options.plugins.decimation.enabled;
    const newDecimation = !currentDecimation;
    console.log(newDecimation)
    sdatFileChart.options.plugins.decimation.enabled = newDecimation;
}
