import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { getRole } from "../call/tokenJson.jsx";

export default function RequireRole({ allowed }) {
  const role = getRole(); 
  if (!role || !allowed.includes(role)) return <Navigate to="/forbidden" replace />;
  return <Outlet />;
}
