
import axios from "axios";
import { getToken, clearToken } from "./tokenJson";

const api = axios.create({
  baseURL: "/api/jobs",
  timeout: 10000,
  headers: { "Content-Type": "application/json" },
});


api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  } else {

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
      error.message = "Access denied. Please login";
    } else {
      error.message =
        error.response.data?.message ||
        error.response.data?.error ||
        `Request failed with status code ${status}`;
    }

    return Promise.reject(error);
  }
);



export async function getJobById(id) {
  try {
    const { data } = await api.get(`/${id}`);
    return data;
  } catch (err) {

    throw new Error(err?.message || "Failed to load job.");
  }
}

export async function getJobs({ page = 0, size = 12, sort = "createdAt,desc" } = {}) {
  try {
    const { data } = await api.get("", { params: { page, size, sort } });
    return data;
  } catch (err) {
    throw new Error(err?.message || "Failed to load jobs.");
  }
}

export async function advancedSearchJobs({
  keyword = "",
  page = 0,
  size = 9,
  sort = "createdAt,desc",
} = {}) {
  try {
    const body = {};
    if (keyword.trim()) body.keyword = keyword.trim();

    const { data } = await api.post("/advancedSearch", body, {
      params: { page, size, sort },
    });
    return data;
  } catch (err) {
    throw new Error(err?.message || "Failed to search jobs.");
  }
}


export async function getMyJobs({ page = 0, size = 20, sort = "createdAt,desc" } = {}) {
  const { data } = await api.get("/my-jobs", { params: { page, size, sort } });

  return {
    items: Array.isArray(data?.content) ? data.content : [],
    page: data?.pageable?.pageNumber ?? 0,
    size: data?.pageable?.pageSize ?? size,
    totalPages: data?.totalPages ?? 1,
    totalElements: data?.totalElements ?? (data?.content?.length ?? 0),
  };
}

export async function createJob(payload) {
  try {
    const { data } = await api.post("/create", payload);
    return data;
  } catch (err) {
    const msg = err?.response?.data?.message || err?.message || "Failed to create job.";
    throw new Error(msg);
  }
}


export async function getCvsByJob(id, { page = 0, size = 12, sort = "matchScore,desc" } = {}) {
  try {
    const { data } = await api.get(`/${id}/cvs`, {
      params: { page, size, sort },
    });
    return data;
  } catch (err) {
    const msg =
      err?.response?.data?.message ||
      err?.message ||
      "Failed to get CVs by Job.";
    throw new Error(msg);
  }
}
