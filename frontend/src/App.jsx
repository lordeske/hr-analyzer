import { Routes, Route } from "react-router-dom";
import ProtectedRoute from "./pages/ProtectedRoute.jsx";

import Home from "./pages/Home.jsx";
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
    <Routes>
      
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register-candidate" element={<RegisterCandidate />} />
      <Route path="/register-hr" element={<RegisterHR />} />

     
      <Route element={<ProtectedRoute />}>
        <Route path="/jobs" element={<JobsList />} />
        <Route path="/job/:id" element={<JobDetails />} />
        <Route path="/apply/:jobId" element={<ApplyCvPage />} />
        <Route path="/cv/:id" element={<CvResultPage />} />
        <Route path="/my-cvs" element={<MyCvs />} />
      </Route>

     
    </Routes>
  );
}
