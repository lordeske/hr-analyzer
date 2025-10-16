// tu 

export function saveToken(data) {
    const toStore = typeof data === "string" ? { token: data } : data;
    localStorage.setItem("token", JSON.stringify(toStore));
}

export function getToken() {
  const raw = localStorage.getItem("token");
  if (!raw) return null;
  try {
    const parsed = JSON.parse(raw);
    return parsed?.token ?? null;
  } catch {
    return raw;
  }
}

export function clearToken() {
  localStorage.removeItem("token");
}