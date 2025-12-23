import { useNavigate } from 'react-router-dom';
import React, { useState } from 'react';

function AuthPage() {
  const [authMode, setAuthMode] = useState('login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const navigate = useNavigate();

  const toggleAuthMode = () => {
    setEmail('');
    setPassword('');
    setError('');
    setAuthMode(currentMode => (currentMode === 'login' ? 'signup' : 'login'));
  };

  const inputClasses = "w-full p-2 border-2 border-gr1-900 rounded-md bg-gr2-300 text-center text-gr3-700 placeholder-gr3-700 focus:outline-none focus:border-gr3-700 transition-colors";
  const labelClasses = "text-xl block mb-1 text-sm font-medium text-gr3-700 text-center text-yell-300";

  const handleSubmit = async (event) => {

  event.preventDefault();
  setError('');

  const endpoint = authMode === 'login' ? '/api/auth/login' : '/api/auth/signup';
  
  const requestBody = {
    email: email,
    password: password,
  };

  try {
    const response = await fetch(endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestBody),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Something went wrong');
    }

    const data = await response.json();

    localStorage.setItem('authToken', data.token);
    console.log('Successfully logged in! Token:', data.token);

    navigate('/');

  } catch (err) {
    setError(err.message);
    console.error('Authentication failed:', err);
  }
  };

  return (
    <div className="max-w-md w-full p-8 border border-gray-200 rounded-lg shadow-md bg-buttercup-100 bg-opacity-75 backdrop-blur-sm flex flex-col justify-center">
      
      <h1 className="text-4xl text-center mb-6 text-gr1-300">
        {authMode === 'login' ? 'Login' : 'SIGN UP'}
      </h1>

        {error && <p className="text-red-500 text-center mb-4">{error}</p>}
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
        <label htmlFor="email" className={labelClasses}>Email</label>
        <input
        placeholder='user@seddid.com'
        type="email"
        id="email"
        required
        className={inputClasses}
        value={email} // The input's value is now tied to our state
        onChange={e => setEmail(e.target.value)} // When user types, update the state
        />
        </div>

        <div className="mb-4">
        <label htmlFor="password" className={labelClasses}>Password</label>
        <input
        placeholder='enter your password'
        type="password"
        id="password"
        required
        className={inputClasses}
        value={password} // The input's value is now tied to our state
        onChange={e => setPassword(e.target.value)} // When user types, update the state
        />
        </div>

        {authMode === 'signup' && (
          <div className="mb-6">
            <label htmlFor="confirmPassword" className={labelClasses}>Confirm Password</label>
            <input type="password" id="confirmPassword" required className={inputClasses} />
          </div>
        )}

        {/* Note the 'hover:' prefix. This is a state variant! */}
        <button type="submit" className="w-full py-2 px-4 bg-gr1-500 text-gr3-700 hover:bg-blue-700 font-semibold rounded-md transition-colors">
          {authMode === 'login' ? 'Log In' : 'Create Account'}
        </button>
      </form>

      <button onClick={toggleAuthMode} className="mt-4 block mx-auto text-sm text-yell-100 hover:underline">
        {authMode === 'login' ? 'Need an account? Sign Up' : 'Already have an account? Login'}
      </button>

    </div>
  );
}

export default AuthPage;
