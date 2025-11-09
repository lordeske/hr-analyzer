import axios from "axios";
import { saveToken } from "./tokenJson.jsx";

const api = axios.create({
  baseURL: "/api/auth",
  timeout: 5000,
  headers: { "Content-Type": "application/json" },
});

export async function registerCandidateAPI(request) {
  try {
    const { data } = await api.post("/registerCandidat", request);
    return data;
  } catch (err) {
    const status = err?.response?.status;
    const msgFromBackend = err?.response?.data?.message || err?.response?.data?.error;
    if (!err?.response) throw new Error("Network error. Check your connection.");
    if (status === 409) throw new Error(msgFromBackend || "User already exists.");
    if (status === 400) throw new Error(msgFromBackend || "Invalid registration data.");
    throw new Error(msgFromBackend || "Registration failed. Please try again.");
  }
}

export async function RegisterHrApi(request) {
  try {
    const { data } = await api.post("/register", request);
    return data;
  } catch (err) {
    const status = err?.response?.status;
    const msgFromBackend = err?.response?.data?.message || err?.response?.data?.error;
    if (!err?.response) throw new Error("Network error. Check your connection.");
    if (status === 409) throw new Error(msgFromBackend || "User already exists.");
    if (status === 400) throw new Error(msgFromBackend || "Invalid registration data.");
    throw new Error(msgFromBackend || "Registration failed. Please try again.");
  }
}

export async function login(loginRequest) {
  try {
    const { data } = await api.post("/login", loginRequest);
    saveToken(data);
    return data;
  } catch (err) {
    const status = err?.response?.status;
    const msgFromBackend = err?.response?.data?.message || err?.response?.data?.error;
    if (!err?.response) throw new Error("Network error. Check your connection.");
    if (status === 400 || status === 401) throw new Error(msgFromBackend || "Bad credentials.");
    throw new Error(msgFromBackend || "Login failed. Please try again.");
  }
}
