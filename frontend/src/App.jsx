import { Routes, Route, Link } from "react-router-dom";
import LoginPage from "./pages/LoginPage.jsx";
import RegisterCandidate from "./pages/RegisterCandidate.jsx";
import RegisterHR from "./pages/RegisterHR.jsx";

export default function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register-cand" element={<RegisterCandidate />} />
        <Route path="/register-hr" element={<RegisterHR />} />
      </Routes>
    </>
  );
}

function Home() {
  return (
    <div style={{ minHeight: "100vh", display: "grid", placeItems: "center" }}>
      <div style={{ textAlign: "center", color: "#fff", fontFamily: "Poppins, sans-serif", background:"#080710", width:"100%", height:"100%", position:"fixed", inset:0 }}>
        <div style={{ position:"relative", top:"30vh" }}>
          <h1 style={{ marginBottom: "16px" }}>Početna</h1>
          <Link
            to="/login"
            style={{
              background: "#fff",
              color: "#080710",
              padding: "12px 20px",
              borderRadius: "8px",
              textDecoration: "none",
              fontWeight: 600,
            }}
          >
            Idi na Login
          </Link>
          <Link
            to="/register-cand"
            style={{
              background: "#fff",
              color: "#080710",
              padding: "12px 20px",
              borderRadius: "8px",
              textDecoration: "none",
              fontWeight: 600,
            }}
          >
            Idi na register candidate
          </Link>
          <Link
            to="/register-hr"
            style={{
              background: "#fff",
              color: "#080710",
              padding: "12px 20px",
              borderRadius: "8px",
              textDecoration: "none",
              fontWeight: 600,
            }}
          >
            Idi na register hr
          </Link>
        </div>
      </div>
    </div>
  );
}
