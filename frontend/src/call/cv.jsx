import axios from "axios";
import { getToken } from "./tokenJson";


const URL = '/api/cv'

const api = axios.create({
    baseURL: URL,
    timeout: 5000,
    
});



export async function uploadCvFile({ jobId, file }) {


    try {
        const token = getToken();
        if (!token) {

            throw new Error("There is no token in Local Storage");
        }

        const form = new FormData();
        form.append("file", file);
        form.append("jobId", String(jobId));


        const { data } = await api.post("/uploadCvFile", form ,
            {
                headers:
                {
                    Authorization: `Bearer ${token}`
                }
            }
         );
        return data;
    }

    catch (err) {

        console.log(err)
        throw err;



    }
}

