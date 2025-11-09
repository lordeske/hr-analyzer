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
import HrDashboard from "./pages/HrDashboard.jsx";
import CvList from "./pages/CvList.jsx";
import Forbidden from "./pages/Forbidden.jsx";
import RequireRole from "./pages/RequiredRole.jsx";

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
        <Route path="/forbidden" element={<Forbidden />} />


        <Route element={<RequireRole allowed={["CANDIDATE"]} />}>
          <Route path="/apply/:jobId" element={<ApplyCvPage />} />
          <Route path="/my-cvs" element={<MyCvs />} />
        </Route>

      
        <Route element={<RequireRole allowed={["HR"]} />}>
          <Route path="/hr-dashboard" element={<HrDashboard />} />
          <Route path="/job/:id/cvs" element={<CvList />} />
        </Route>

       
        <Route path="/cv/:id" element={<CvResultPage />} />
      </Route>
    </Routes>
  );
}
