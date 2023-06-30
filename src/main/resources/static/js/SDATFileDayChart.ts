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
    relativeTime: string;
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
    let jsonData: SdatWithFileDate[] = JSON.parse(sdatFilesRaw);
    console.log(jsonData)

    const minDate = new Date(jsonData[0].fileDate.startDate);
    const maxDate = new Date(jsonData[0].fileDate.endDate);
    console.log(minDate)
    console.log(maxDate)

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

    const datasets: ChartData[] = [];
    for (const sdatWithFileDate of jsonData) {
        for (const sdatFile of sdatWithFileDate.sdatfiles) {
            let dataset = datasets.find(dataset => dataset.label === sdatFile.economicActivity);
            if (!dataset) {
                dataset = {label: sdatFile.economicActivity, data: []};
                datasets.push(dataset);
            }
            for (const observation of sdatFile.observations) {
                dataset.data.push({
                    x: new Date(observation.relativeTime),
                    y: observation.volume
                });
            }
        }
    }

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
                normalized: true,
                spanGaps: true,
                plugins: {
                    zoom: zoomOptions,
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
