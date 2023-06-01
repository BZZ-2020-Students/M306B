import {BASE_URL} from "./Global.js";
import {SDATFileDayChart} from "./SDATFileDayChart.js";

export function setupHomeScreen(chartType: string, data: []) {
    getDiagramTypes().then(r => {
        const diagramTypes = r;

        const diagramTypeSelect = document.getElementById("chart-selection-dropdown") as HTMLSelectElement;
        diagramTypes.forEach(diagramType => {
            const option = document.createElement("option");
            option.value = diagramType;
            option.text = diagramType.charAt(0).toUpperCase() + diagramType.slice(1).toLowerCase();
            diagramTypeSelect.appendChild(option);

            console.log(diagramType, chartType)
            if (diagramType === chartType) {
                option.selected = true;
            }
        });
    });

    switch (chartType) {
        case "USAGE": {
            SDATFileDayChart(data)
            break;
        }
        case "METER": {
            console.log("Unsupported chart type: " + chartType)
            break;
        }
    }
}

async function getDiagramTypes() {
    const response = await fetch(`${BASE_URL}/diagramTypes`);

    return await response.json() as string[];
}
