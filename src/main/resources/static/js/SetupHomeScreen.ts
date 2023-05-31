import {BASE_URL} from "./Global.js";

setupHomeScreen().then(r => console.log("result",r));

async function setupHomeScreen() {
    return loadDiagramTypes();
}

async function loadDiagramTypes() {
    const fetchUrl = `${BASE_URL}/diagramTypes`;
    console.log("fetchUrl",fetchUrl);
    const response = await fetch(`${BASE_URL}/diagramTypes`);

    return await response.json();
}
