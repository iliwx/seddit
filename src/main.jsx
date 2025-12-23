import React from 'react'
import ReactDOM from 'react-dom/client'
import { createBrowserRouter, RouterProvider, Navigate } from "react-router-dom";
import { HelmetProvider } from 'react-helmet-async';
import './index.css';
import App from './App.jsx'; // Import the clean App
import Login from './pages/Login.jsx';
import Dashboard from './pages/Dashboard.jsx';
import NotFound from './pages/NotFound.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';
import ProfileView from './components/dashboard/ProfileView.jsx'; // The Content
import AccountSettings from './components/dashboard/AccountSettings.jsx';
import AccountPreferences from './components/dashboard/AccountPreferences.jsx';
import CommunityPage from './pages/CommunityPage.jsx';
import CreateCommunity from './pages/CreateCommunity.jsx';
import ModDashboard from './pages/ModDashboard.jsx';
import ApiTester from './pages/ApiTester.jsx';
import HomeFeed from './pages/HomeFeed.jsx';


const router = createBrowserRouter([
  {
    path: "/",
    element: <App />, // Use App as the wrapper
    errorElement: <NotFound />, 
    children: [
      {
        // THIS IS THE CHANGE:
        // Instead of <Navigate to="/login" />, we render HomeFeed inside ProtectedRoute
        index: true,
        element: (
          
            <HomeFeed />
          
        ) 
      },
      // {
      //   // When url is "/", redirect to "/login"
      //   index: true,
      //   element: <Navigate to="/login" replace /> 
      // },
      {
        path: "login",
        element: <Login />,
      },
      {
        path: "create-community",
        element: (
            <CreateCommunity />
        )
      },
      {
        // The ":name" part is the variable.
        // It matches /r/spotify, /r/gaming, /r/anything
        path: "r/:name", 
        element: <CommunityPage /> 
      },
      {
        path: "test-api",
        element: (
          
            <ApiTester />
          
        )
      },

      // --- THE MODERATION ROUTE ---
      {
        // This matches /r/spotify/manage, /r/gaming/manage
        path: "r/:name/manage",
        element: (
          
            <ModDashboard />
          
        )
      },
      {
        path: "dashboard",
        element: (
                  <Dashboard />
                  ),
        children: [
          {
            // If path is exactly "/dashboard", show Home
            index: true,
            element: <ProfileView />
          },
          {
            // If path is "/dashboard/profile", show Home (or make a dedicated profile page)
            path: "profile",
            element: <ProfileView />
          },
          {
            // If path is "/dashboard/settings", show Settings
            path: "settings",
            element: <AccountSettings />
          },
          {
            path: "preferences",
            element: <AccountPreferences />
          }
        ]
      },
      // <ProtectedRoute></ProtectedRoute>
      {
        path: "*",
        element: <NotFound />
      },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <HelmetProvider>
      <RouterProvider router={router} />
    </HelmetProvider>
  </React.StrictMode>,
)