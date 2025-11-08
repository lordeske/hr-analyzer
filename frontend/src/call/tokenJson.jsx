

export function saveToken(data) {
  
  const toStore = {

    token : data.token ?? data,
    role : data.role ?? null
  

    };

    localStorage.setItem("auth", JSON.stringify(toStore))



}

export function getToken() {
  const raw = localStorage.getItem("auth");
  if (!raw) return null;
  try {
    const parsed = JSON.parse(raw);
    return parsed?.token ?? null;
  } catch {
    return null;
  }
}

export function getRole() {
  const raw = localStorage.getItem("auth");
  if (!raw) return null;
  try {
    const parsed = JSON.parse(raw);
    return parsed?.role ?? null;
  } catch {
    return null;
  }
}

export function clearToken() {
  localStorage.removeItem("auth");
}
