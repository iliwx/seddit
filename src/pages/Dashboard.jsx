import React from 'react';
import { Outlet } from 'react-router-dom'; // IMPORT THIS
import Header from '../components/Header';
import DashboardSidebar from '../components/DashboardSidebar';
import Footer from '../components/Footer';

const Dashboard = () => {
  return (
    <div className="min-h-screen bg-gray-300 flex flex-col">
      <Header />
      
      <div className="flex flex-1">
        <div className="hidden md:block">
          <DashboardSidebar />
        </div>
        
        <main className="flex-1 p-6">
          {/* 
              This OUTLET is the magic. 
              It will display <DashboardHome /> OR <AccountSettings /> 
              depending on the URL. 
          */}
          <Outlet />
        </main>
      </div>

      <Footer />
    </div>
  );
};

export default Dashboard;
