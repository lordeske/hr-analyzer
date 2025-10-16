import axios from "axios";
import { getToken } from "./tokenJson";


const URL = '/api/jobs'

const api = axios.create({
    baseURL: URL,
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
    },
});



export async function getJobById(id) {
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
        console.error("Error fetching job:", error);
        throw error;
    }




}

export async function getJobs({ page = 0, size = 12, sort = "createdAt,desc" } = {}) {
    try {

        const token = getToken();
        if (!token) {

            throw new Error("There is no token in Local Storage");
        }

        const { data } = await api.get("", {
            params: { page, size, sort },
            headers: { Authorization: `Bearer ${token}` }
        });

        return data;
    } catch (err) {
        const msg = err?.response?.data?.message || err.message || "Failed to load jobs.";
        throw new Error(msg);
    }
}


export async function advancedSearchJobs({
    keyword = "",
    page = 0,
    size = 9,
    sort = "createdAt,desc",
} = {}) {

    const token = getToken();
    if (!token) {
        throw new Error("There is no token in Local Storage");
    }

    const body = {};
    if (keyword.trim()) body.keyword = keyword.trim();

    const { data } = await api.post("/advancedSearch", body, {
        params: { page, size, sort },
        headers: { Authorization: `Bearer ${token}` }
    });
    return data;
}