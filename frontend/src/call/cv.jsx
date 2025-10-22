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


        const { data } = await api.post("/uploadCvFile", form,
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

export async function getCvById(id) {
    try {
        const token = getToken();
        if (!token) {

            throw new Error("There is no token in Local Storage");
        }

        const response = await api.get(`/${id}`,
            {
                headers:
                {
                    Authorization: `Bearer ${token}`
                }
            }
        )

        return response.data;

    }

    catch (error) {
        console.error("Error fetching CV:", error);
        throw error;
    }




}


export async function getLoggedUsersCvsSim({ page = 0, size = 12, sort = "uploadTime,desc" } = {}) {

    try {
        const token = getToken();
        if (!token) {
            throw new Error("There is no token in Local Storage");
        }

        const { data } = await api.get("/me/cvs",
            {
                params: { page, size, sort },
                headers:
                {
                    Authorization: `Bearer ${token}`
                }
            }
        )
        console.log(data)
        return data;

    } catch (err) {
        const msg = err?.response?.data?.message || err.message || "Failed to load your CVs.";
        throw new Error(msg);
    }







}