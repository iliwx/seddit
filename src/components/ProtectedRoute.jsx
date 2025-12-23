import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';

const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('authToken');
  const location = useLocation();

  if (!token) {
    // If no token found, redirect to login
    // "state" saves where they were trying to go, so we can send them back there after login (optional advanced feature)
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // If token exists, render the child component (Dashboard)
  return children;
};

export default ProtectedRoute;