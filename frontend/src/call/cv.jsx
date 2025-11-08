// cv.jsx
import axios from "axios";
import { getToken, clearToken } from "./tokenJson";

const api = axios.create({
  baseURL: "/api/cv",
  timeout: 10000,
});


api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});


api.interceptors.response.use(
  (res) => res,
  (error) => {
    const status = error?.response?.status;

    if (!error?.response) {
      error.message = "Network error. Check your connection.";
      return Promise.reject(error);
    }

    if (status === 401) {
      error.message = "Please log in first.";
      
    } else if (status === 403) {
      error.message = "Access denied.";
    } else {
      error.message =
        error.response.data?.message ||
        error.response.data?.error ||
        `Request failed with status code ${status}`;
    }

    return Promise.reject(error);
  }
);

// =================== API FUNKCIJE ===================

export async function uploadCvFile({ jobId, file }) {
  try {
   
    const form = new FormData();
    form.append("file", file);
    form.append("jobId", String(jobId));

    const { data } = await api.post("/uploadCvFile", form);
    return data;
  } catch (err) {
    throw new Error(err?.message || "Failed to upload CV file.");
  }
}

export async function getCvById(id) {
  try {
    const { data } = await api.get(`/${id}`);
    return data;
  } catch (err) {
    throw new Error(err?.message || "Failed to load CV.");
  }
}

export async function getLoggedUsersCvsSim({
  page = 0,
  size = 12,
  sort = "uploadTime,desc",
} = {}) {
  try {
    const { data } = await api.get("/me/cvs", { params: { page, size, sort } });
    return data;
  } catch (err) {
    throw new Error(err?.message || "Failed to load your CVs.");
  }
}



