import { Routes, Route, Link } from "react-router-dom";
import LoginPage from "./pages/LoginPage.jsx";
import RegisterCandidate from "./pages/RegisterCandidate.jsx";
import RegisterHR from "./pages/RegisterHR.jsx";
import JobDetails from "./pages/JobDetails.jsx";
import JobsList from "./pages/JobsList.jsx";
import ApplyCvPage from "./pages/ApplyCvPage.jsx";
import CvResultPage from "./pages/CvResultPage.jsx";
import MyCvs from "./pages/MyCvs.jsx";

export default function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register-candidate" element={<RegisterCandidate />} />
        <Route path="/register-hr" element={<RegisterHR />} />
        <Route path="/job/:id" element={<JobDetails />} />
        <Route path="/jobs" element={<JobsList />} />
        <Route path="/apply/:jobId" element={<ApplyCvPage />} />
        <Route path="/cv/:id" element={<CvResultPage />} />
        <Route path="/my-cvs" element={<MyCvs />} />

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
            to="/register-candidate"
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
