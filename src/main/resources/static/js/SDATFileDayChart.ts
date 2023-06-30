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
    relativeTime: number;
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
    x: number,
    y: number
}

interface ChartData {
    label: string,
    data: ChartSingleData[],
}

let sdatFileChart = null

export function SDATFileDayChart(sdatFilesRaw: any) {
    let jsonData: SdatWithFileDate[] = JSON.parse(sdatFilesRaw);

    const minDate = new Date(jsonData[0].fileDate.startDate);
    const maxDate = new Date(jsonData[jsonData.length - 1].fileDate.startDate);
    maxDate.setDate(maxDate.getDate() + 1);

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

    let datasets: ChartData[] = [];
    for (let sdatWithFileDate of jsonData) {
        for (let sdatFile of sdatWithFileDate.sdatfiles) {
            let dataset = datasets.find(dataset => dataset.label === sdatFile.economicActivity);
            if (!dataset) {
                dataset = {label: sdatFile.economicActivity, data: []};
                datasets.push(dataset);
            }
            for (let observation of sdatFile.observations) {
                dataset.data.push({
                    x: observation.relativeTime,
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
                animation: false,
                plugins: {
                    zoom: zoomOptions,
                    decimation: {
                        enabled: true,
                        algorithm: 'lttb',
                        samples: 500,
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
    sdatFileChart.update();

    const decimationButton = document.getElementById('chartToggleDecimation');
    decimationButton.innerText = newDecimation ? 'Disable Decimation' : 'Enable Decimation';
}

export function toggleAnimation() {
    const currentAnimation = sdatFileChart.options.animation;
    const newAnimation = !currentAnimation;
    console.log(newAnimation)
    sdatFileChart.options.animation = newAnimation;

    const animationButton = document.getElementById('chartToggleAnimation');
    animationButton.innerText = newAnimation ? 'Disable Animation' : 'Enable Animation';
}
