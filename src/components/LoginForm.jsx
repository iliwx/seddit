import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Eye, EyeOff, Mail, Lock, User } from 'lucide-react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom'; // Import this for redirection

// CONFIGURATION: Change this to your actual Backend URL
const API_BASE_URL = 'http://localhost:5000/api'; 

const LoginForm = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  
  // Hook for redirection
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm();

  const toggleMode = () => {
    setIsLogin(!isLogin);
    reset();
  };

  const onSubmit = async (data) => {
    // 1. Define the Endpoint
    const endpoint = isLogin 
      ? `${API_BASE_URL}/auth/login` 
      : `${API_BASE_URL}/auth/signup`;

    try {
      // 2. Prepare the Payload
      // If logging in, we only need email/pass. If signing up, we might need name too.
      const payload = isLogin 
        ? { email: data.email, password: data.password }
        : { name: data.name, email: data.email, password: data.password };

      // 3. Make the Request
      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          // 'Accept': 'application/json' // Optional, depending on backend
        },
        body: JSON.stringify(payload),
      });

      // 4. Parse the response
      const result = await response.json();

      // 5. Handle Server Errors (e.g., 400 Bad Request, 401 Unauthorized)
      if (!response.ok) {
        // Use the error message from backend if available, otherwise a generic one
        throw new Error(result.message || 'Something went wrong');
      }

      // 6. SUCCESS HANDLING
      if (isLogin) {
        toast.success('Login successful!');
        
        // A. Save Token to Local Storage
        // Assuming your backend returns: { token: "xyz...", user: { ... } }
        localStorage.setItem('authToken', result.token);
        localStorage.setItem('userData', JSON.stringify(result.user));

        // B. Redirect to Dashboard/Home
        navigate('/dashboard'); 
      } else {
        // Signup Successful
        toast.success('Account created! Please log in.');
        setIsLogin(true); // Switch to login mode automatically
        reset(); // Clear form fields
      }

    } catch (error) {
      console.error('Auth Error:', error);
      // Show the specific error message from the backend (e.g., "Email already exists")
      toast.error(error.message || 'Connection failed. Please try again.');
    }
  };

  return (
    <div className="w-full max-w-md">
      <div className="text-center mb-8">
        <h1 className="text-3xl font-bold text-zinc-800 mb-2">
          {isLogin ? 'Welcome Back' : 'Create Account'}
        </h1>
        <p className="text-slate-400">
          {isLogin
            ? 'Sign in to your account to continue'
            : 'Join us today and start your journey'}
        </p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        
        {/* NAME FIELD (Only for Signup) */}
        {!isLogin && (
          <div className="animate-in fade-in slide-in-from-top-4 duration-300">
            <label htmlFor="name" className="block text-sm font-medium text-zinc-800 mb-2">
              Full Name
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <User className="h-5 w-5 text-slate-400" />
              </div>
              <input
                {...register('name', { required: 'Name is required' })}
                type="text"
                id="name"
                className="block w-full pl-10 pr-3 py-3 border border-slate-300 rounded-xl shadow-sm placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:border-transparent transition-all duration-200"
                placeholder="John Doe"
              />
            </div>
            {errors.name && <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>}
          </div>
        )}

        {/* EMAIL FIELD */}
        <div>
          <label htmlFor="email" className="block text-sm font-medium text-zinc-800 mb-2">
            Email Address
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Mail className="h-5 w-5 text-slate-400" />
            </div>
            <input
              {...register('email', {
                required: 'Email is required',
                pattern: {
                  value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                  message: 'Invalid email address',
                },
              })}
              type="email"
              id="email"
              className={`block w-full pl-10 pr-3 py-3 border ${errors.email ? 'border-red-500' : 'border-slate-300'} rounded-xl shadow-sm placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:border-transparent transition-all duration-200`}
              placeholder="Enter your email"
            />
          </div>
          {errors.email && (
            <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
          )}
        </div>

        {/* PASSWORD FIELD */}
        <div>
          <label htmlFor="password" className="block text-sm font-medium text-zinc-800 mb-2">
            Password
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Lock className="h-5 w-5 text-slate-400" />
            </div>
            <input
              {...register('password', {
                required: 'Password is required',
                minLength: {
                  value: 6,
                  message: 'Password must be at least 6 characters',
                },
              })}
              type={showPassword ? 'text' : 'password'}
              id="password"
              className={`block w-full pl-10 pr-12 py-3 border ${errors.password ? 'border-red-500' : 'border-slate-300'} rounded-xl shadow-sm placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:border-transparent transition-all duration-200`}
              placeholder="Enter your password"
            />
            <button
              type="button"
              className="absolute inset-y-0 right-0 pr-3 flex items-center"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? (
                <EyeOff className="h-5 w-5 text-slate-400 hover:text-zinc-800 transition-colors" />
              ) : (
                <Eye className="h-5 w-5 text-slate-400 hover:text-zinc-800 transition-colors" />
              )}
            </button>
          </div>
          {errors.password && (
            <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>
          )}
        </div>

        {isLogin && (
          <div className="flex items-center justify-between animate-in fade-in duration-300">
            <div className="flex items-center">
              <input
                id="remember-me"
                type="checkbox"
                className="h-4 w-4 text-indigo-600 focus:ring-indigo-600 border-slate-300 rounded cursor-pointer"
              />
              <label htmlFor="remember-me" className="ml-2 block text-sm text-zinc-800 cursor-pointer">
                Remember me
              </label>
            </div>
            <div className="text-sm">
              <a href="#" className="font-medium text-indigo-600 hover:text-blue-500 transition-colors">
                Forgot password?
              </a>
            </div>
          </div>
        )}

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full flex justify-center py-3 px-4 border border-transparent rounded-xl shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-blue-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-600 disabled:opacity-70 disabled:cursor-not-allowed transition-all duration-200 transform active:scale-[0.98]"
        >
          {isSubmitting ? 'Processing...' : isLogin ? 'Sign in' : 'Create Account'}
        </button>

        <div className="text-center">
          <p className="text-sm text-slate-400">
            {isLogin ? "Don't have an account? " : "Already have an account? "}
            <button
              type="button"
              onClick={toggleMode}
              className="font-medium text-indigo-600 hover:text-blue-500 transition-colors focus:outline-none underline-offset-2 hover:underline"
            >
              {isLogin ? 'Sign up' : 'Log in'}
            </button>
          </p>
        </div>
      </form>
    </div>
  );
};

export default LoginForm;