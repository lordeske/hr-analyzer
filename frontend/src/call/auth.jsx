import axios from "axios";
import {saveToken} from "./tokenJson.jsx"


const URL = '/api/auth'

const api = axios.create({
    baseURL: URL,
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
    },
});


export async function registerCandidateAPI(reigsterRequest) {

    try {
        const response = await api.post('/registerCandidat', reigsterRequest);
        return response.data;

    }
    catch (error) {

        throw error;

    }


}


export async function RegisterHrApi(reigsterRequest) {

    try {
        const response = await api.post('/register', reigsterRequest);
        console.log(response);
        return response.data;

    }
    catch (error) {

        throw error;

    }


}

export async function login(loginRequest) {
    try {
        const response = await api.post("/login", loginRequest);
        saveToken(response.data.token ?? response.data);

        return response.data;
    } catch (error) {
        throw error;
    }
}