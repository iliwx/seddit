import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Home, ArrowLeft } from 'lucide-react';

const NotFound = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gray-400 flex items-center justify-center p-4">
      <div className="max-w-md w-full text-center">
        {/* Big 404 Text with background effect */}
        <div className="relative mb-8">
          <h1 className="text-9xl font-extrabold text-gray-200 select-none">
            404
          </h1>
          <div className="absolute inset-0 flex items-center justify-center">
            <h2 className="text-2xl md:text-3xl font-bold text-gray-800">
              Page not found
            </h2>
          </div>
        </div>

        <p className="text-gray-500 mb-8 px-4">
          Oops! It seems like the page you're looking for doesn't exist or has been moved.
        </p>

        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          {/* Go Back Button */}
          <button
            onClick={() => navigate(-1)}
            className="flex items-center justify-center px-6 py-3 border border-gray-300 rounded-xl text-gray-700 bg-white hover:bg-gray-50 transition-colors shadow-sm text-sm font-medium"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            Go Back
          </button>

          {/* Go Home Button */}
          <button
            onClick={() => navigate('/dashboard')}
            className="flex items-center justify-center px-6 py-3 border border-transparent rounded-xl text-white bg-indigo-600 hover:bg-indigo-700 transition-colors shadow-sm text-sm font-medium"
          >
            <Home className="w-4 h-4 mr-2" />
            Go Home
          </button>
        </div>
      </div>
    </div>
  );
};

export default NotFound;