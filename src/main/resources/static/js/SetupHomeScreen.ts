import {BASE_URL} from "./Global.js";
import {SDATFileDayChart} from "./SDATFileDayChart.js";
import {MeterReadingChart} from "./MeterReadingChart.js";

interface ChartType {
    priority: number;
    name: string;
    value: string;
}

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
            SDATFileDayChart(data)
            break;
        }
        case "METER": {
            MeterReadingChart(data)
            break;
        }
    }
}

async function getDiagramTypes() {
    const response = await fetch(`${BASE_URL}/diagramTypes`);

    return await response.json() as ChartType[];
}
