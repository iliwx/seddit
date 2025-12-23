import React from 'react';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import LoginForm from '../components/LoginForm';
import loginImage from '../assets/images/login.png';

const Login = () => {
  return (
    <>
      {/* Main Container with subtle gradient */}
      <div className="min-h-screen bg-gradient-to-br from-gray-300 to-gray-600 flex items-center justify-center p-4 sm:p-6 lg:p-8">
        <div className="w-full max-w-6xl">
          <div className="bg-white rounded-3xl shadow-2xl overflow-hidden">
            <div className="flex flex-col lg:flex-row">
              
              {/* Left Side: Form */}
              <div className="flex-1 p-8 lg:p-12 xl:p-16 flex items-center justify-center">
                <LoginForm />
              </div>

              {/* Right Side: Image & Decoration */}
              {/* Hidden on small screens if you want, or stacked on top */}
              <div className="flex-1 relative hidden lg:block min-h-[600px]">
                {/* Overlay for better text readability */}
                <div className="absolute inset-0 bg-gradient-to-t from-black/40 via-transparent to-transparent z-10"></div>
                <div className="absolute inset-0 bg-indigo-600/10 z-10 mix-blend-multiply"></div>
                
                <img
                  src={loginImage}
                  alt="Community connection"
                  className="w-full h-full object-cover"
                  loading="lazy"
                />
                
                <div className="absolute bottom-12 left-12 right-12 z-20 text-white">
                  <div className="bg-white/10 backdrop-blur-md border border-white/20 rounded-2xl p-6 shadow-lg">
                    <h3 className="text-2xl font-bold mb-2">Join thousands of communities</h3>
                    <p className="text-gray-100 leading-relaxed">
                      Experience seamless connection with our modern platform designed for today's society.
                    </p>
                  </div>
                </div>
              </div>

            </div>
          </div>
        </div>
      </div>

      <ToastContainer
        position="top-right"
        autoClose={4000}
        hideProgressBar={false}
        newestOnTop
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />
    </>
  );
};

export default Login;