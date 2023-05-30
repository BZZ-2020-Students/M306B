import {BASE_URL} from "./Global.js";

export async function setupHomeScreen() {
    await loadDiagramTypes();
}

async function loadDiagramTypes() {
    const fetchUrl = `${BASE_URL}/diagramTypes`;
    console.log(fetchUrl);
    const response = await fetch(`${BASE_URL}/diagramTypes`);

    const diagramTypes = await response.json();
    console.log(diagramTypes);

    return diagramTypes;
}
