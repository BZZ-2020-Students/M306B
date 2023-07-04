import {BASE_URL} from "./Global.js";
import {SDATFileDayChart} from "./SDATFileDayChart.js";
import {MeterReadingChart} from "./MeterReadingChart.js";

interface ChartType {
    priority: number;
    name: string;
    value: string;
}

let powerChart = null;

export function setupHomeScreen(chartType: ChartType, data: []) {
    getDiagramTypes().then(r => {
        const diagramTypes = r;

        const diagramTypeSelect = document.getElementById("chart-selection-dropdown") as HTMLSelectElement;
        diagramTypes.forEach(diagramType => {
            const option = document.createElement("option");
            option.value = diagramType.value;
            option.text = diagramType.name.charAt(0).toUpperCase() + diagramType.name.slice(1).toLowerCase();
            diagramTypeSelect.appendChild(option);

            if (diagramType.value === chartType.value) {
                option.selected = true;
            }
        });
    });

    switch (chartType.value) {
        case "USAGE": {
            powerChart = SDATFileDayChart(data)
            break;
        }
        case "METER": {
            powerChart = MeterReadingChart(data)
            break;
        }
    }
}

async function getDiagramTypes() {
    const response = await fetch(`${BASE_URL}/diagramTypes`);

    return await response.json() as ChartType[];
}

export function resetZoomChart() {
    powerChart.resetZoom()
}

export function toggleDecimation() {
    const currentDecimation = powerChart.options.plugins.decimation.enabled;
    const newDecimation = !currentDecimation;
    powerChart.options.plugins.decimation.enabled = newDecimation;
    powerChart.update();

    const decimationButton = document.getElementById('chartToggleDecimation');
    decimationButton.innerText = newDecimation ? 'Dezimierung deaktivieren' : 'Dezimierung aktivieren';
}

export function toggleAnimation() {
    const currentAnimation = powerChart.options.animation;
    const newAnimation = !currentAnimation;
    powerChart.options.animation = newAnimation;

    const animationButton = document.getElementById('chartToggleAnimation');
    animationButton.innerText = newAnimation ? 'Animationen deaktivieren' : 'Animationen aktivieren';
}
