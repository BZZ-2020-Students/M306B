import {BASE_URL} from "./Global.js";

setupHomeScreen();

function setupHomeScreen() {
    getDiagramTypes().then(r => {
        const diagramTypes = r;

        const diagramTypeSelect = document.getElementById("chart-selection-dropdown") as HTMLSelectElement;
        diagramTypes.forEach(diagramType => {
            const option = document.createElement("option");
            option.value = diagramType;
            option.text = diagramType.charAt(0).toUpperCase() + diagramType.slice(1).toLowerCase();
            diagramTypeSelect.appendChild(option);
        });

        // go through all options, select "Usage" by default
        for (let i = 0; i < diagramTypeSelect.options.length; i++) {
            if (diagramTypeSelect.options[i].value.toLowerCase() === "usage") {
                diagramTypeSelect.selectedIndex = i;
                break;
            }
        }
    });
}

async function getDiagramTypes() {
    const response = await fetch(`${BASE_URL}/diagramTypes`);

    return await response.json() as string[];
}
