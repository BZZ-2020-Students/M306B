interface DataPoint {
    value: number;
    type: string;
}

interface Data {
    [timestamp: string]: DataPoint[];
}


interface ChartSingleData {
    x: number, // timestamp
    y: number // value
}

interface ChartData {
    label: string,
    data: ChartSingleData[],
}

let meterReadingChart = null

export function MeterReadingChart(meterReadingRaw: any): HTMLCanvasElement {
    const jsonData: Data = JSON.parse(meterReadingRaw);

    const timestamps = Object.keys(jsonData);
    // Convert earliestDate and latestDate to Date objects
    const earliestDate = new Date(Number(timestamps[0]));
    const latestDate = new Date(timestamps[timestamps.length - 1]);

    const consumptionData: { x: number; y: number }[] = [];
    const productionData: { x: number; y: number }[] = [];

    for (const timestamp in jsonData) {
        const timestampNumber = Number(timestamp);
        for (const dataPoint of jsonData[timestamp]) {
            if (dataPoint.type === "Consumption") {
                consumptionData.push({x: timestampNumber, y: dataPoint.value});
            } else if (dataPoint.type === "Production") {
                productionData.push({x: timestampNumber, y: dataPoint.value});
            }
        }
    }

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
    return meterReadingChart = new Chart(
        document.getElementById('power-chart') as HTMLCanvasElement,
        {
            type: 'line',
            data: {
                datasets: [
                    {
                        label: 'Consumption',
                        data: consumptionData,
                    },
                    {
                        label: 'Production',
                        data: productionData,
                    },
                ],
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
                        suggestedMin: earliestDate,
                        suggestedMax: latestDate,
                        ticks: {
                            source: 'auto',
                            autoSkip: true,
                            maxRotation: 0,
                        },
                        time: {
                            displayFormats: {
                                'millisecond': 'hh:mm:ss',
                                'second': 'hh:mm:ss',
                                'minute': 'hh:mm:ss',
                                'hour': 'DD.MM.YYYY, hh:mm:ss',
                                'day': 'DD.MM.YYYY, hh:mm:ss',
                                'week': 'MMM DD',
                                'month': 'MMM DD YYYY',
                                'quarter': 'MMM DD YYYY',
                                'year': 'MMM DD YYYY',
                            }
                        }
                    }
                }
            }
        }
    );
}
