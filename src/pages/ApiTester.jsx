import React, { useState } from 'react';
import { Send, Play, Trash2, Code, Shield, AlertCircle, CheckCircle, Clock } from 'lucide-react';
import { toast } from 'react-toastify';

const ApiTester = () => {
  // State for Request
  const [method, setMethod] = useState('POST');
  const [url, setUrl] = useState('http://localhost:8080/api/');
  const [payload, setPayload] = useState('{\n  "key": "value"\n}');
  const [includeToken, setIncludeToken] = useState(true);
  const [isLoading, setIsLoading] = useState(false);

  // State for Response
  const [response, setResponse] = useState(null);
  const [responseStatus, setResponseStatus] = useState(null);
  const [responseTime, setResponseTime] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setResponse(null);
    setResponseStatus(null);
    setIsLoading(true);
    
    const startTime = performance.now();

    try {
      // 1. Prepare Headers
      const headers = {
        'Content-Type': 'application/json',
      };

      if (includeToken) {
        const token = localStorage.getItem('authToken');
        if (token) {
          headers['Authorization'] = `Bearer ${token}`;
        } else {
          toast.warn('No auth token found in local storage');
        }
      }

      // 2. Prepare Body (if not GET)
      let body = null;
      if (method !== 'GET' && method !== 'HEAD') {
        try {
          // Verify JSON is valid before sending
          JSON.parse(payload); 
          body = payload;
        } catch (err) {
          toast.error('Invalid JSON in Request Body');
          setIsLoading(false);
          return;
        }
      }

      // 3. Fire Request
      const res = await fetch(url, {
        method,
        headers,
        body
      });

      const endTime = performance.now();
      setResponseTime((endTime - startTime).toFixed(2));
      setResponseStatus(res.status);

      // 4. Parse Response
      const contentType = res.headers.get("content-type");
      if (contentType && contentType.indexOf("application/json") !== -1) {
        const data = await res.json();
        setResponse(data);
      } else {
        const text = await res.text();
        setResponse({ message: "Non-JSON response received", raw: text });
      }

    } catch (error) {
      console.error(error);
      setResponse({ error: "Network Error or CORS issue", details: error.message });
      setResponseStatus(0); // 0 indicates network failure
    } finally {
      setIsLoading(false);
    }
  };

  const prettifyJson = () => {
    try {
      const parsed = JSON.parse(payload);
      setPayload(JSON.stringify(parsed, null, 2));
    } catch (e) {
      toast.error('Invalid JSON, cannot prettify');
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 p-6 md:p-12 animate-in fade-in duration-300">
      <div className="max-w-6xl mx-auto space-y-6">
        
        {/* Header */}
        <div className="flex items-center space-x-3 mb-6">
          <div className="p-3 bg-indigo-600 rounded-lg text-white">
            <Code className="w-6 h-6" />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-zinc-900">API Playground</h1>
            <p className="text-slate-500">Testing my backend endpoints directly</p>
          </div>
        </div>

        {/* --- REQUEST SECTION --- */}
        <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
          <div className="p-4 bg-slate-100 border-b border-slate-200 flex items-center justify-between">
            <h2 className="font-semibold text-zinc-700 flex items-center gap-2">
              <Send className="w-4 h-4" /> Request
            </h2>
            <div className="flex items-center gap-4">
              <label className="flex items-center gap-2 text-sm text-slate-600 cursor-pointer select-none">
                <input 
                  type="checkbox" 
                  checked={includeToken} 
                  onChange={(e) => setIncludeToken(e.target.checked)}
                  className="rounded border-slate-300 text-indigo-600 focus:ring-indigo-500"
                />
                <Shield className="w-4 h-4" /> Include Auth Token
              </label>
            </div>
          </div>

          <div className="p-6 space-y-4">
            {/* URL Bar */}
            <div className="flex flex-col md:flex-row gap-0 md:gap-0 rounded-lg shadow-sm">
              <select 
                value={method}
                onChange={(e) => setMethod(e.target.value)}
                className="w-full md:w-32 px-4 py-3 bg-slate-50 border border-slate-300 rounded-t-lg md:rounded-l-lg md:rounded-tr-none md:rounded-b-none font-bold text-zinc-700 focus:ring-2 focus:ring-indigo-500 outline-none"
              >
                <option value="GET">GET</option>
                <option value="POST">POST</option>
                <option value="PUT">PUT</option>
                <option value="DELETE">DELETE</option>
                <option value="PATCH">PATCH</option>
              </select>
              <input 
                type="text" 
                value={url}
                onChange={(e) => setUrl(e.target.value)}
                placeholder="Enter endpoint URL (e.g. http://localhost:8080/api/login)"
                className="flex-1 px-4 py-3 border border-t-0 md:border-t md:border-l-0 border-slate-300 focus:ring-2 focus:ring-indigo-500 outline-none font-mono text-sm"
              />
              <button 
                onClick={handleSubmit}
                disabled={isLoading}
                className="w-full md:w-auto px-8 py-3 bg-indigo-600 text-white font-bold rounded-b-lg md:rounded-r-lg md:rounded-l-none hover:bg-indigo-700 transition-colors disabled:opacity-50 flex items-center justify-center gap-2"
              >
                {isLoading ? 'Sending...' : <><Play className="w-4 h-4" /> Send</>}
              </button>
            </div>

            {/* Request Body (Hidden for GET) */}
            {method !== 'GET' && method !== 'HEAD' && (
              <div>
                <div className="flex justify-between items-center mb-2">
                   <label className="text-sm font-medium text-slate-700">Request Body (JSON)</label>
                   <div className="flex gap-2">
                     <button onClick={prettifyJson} className="text-xs text-indigo-600 hover:underline">Prettify</button>
                     <button onClick={() => setPayload('')} className="text-xs text-red-500 hover:underline">Clear</button>
                   </div>
                </div>
                <textarea 
                  value={payload}
                  onChange={(e) => setPayload(e.target.value)}
                  className="w-full h-48 p-4 font-mono text-sm bg-slate-900 text-green-400 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none resize-y"
                  spellCheck="false"
                />
              </div>
            )}
          </div>
        </div>

        {/* --- RESPONSE SECTION --- */}
        {responseStatus !== null && (
          <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden animate-in slide-in-from-bottom-4 duration-300">
            <div className={`p-4 border-b border-slate-200 flex items-center justify-between ${responseStatus >= 200 && responseStatus < 300 ? 'bg-green-50' : 'bg-red-50'}`}>
              <div className="flex items-center gap-4">
                <h2 className="font-semibold text-zinc-700">Response</h2>
                
                {/* Status Badge */}
                <span className={`px-3 py-1 rounded-full text-xs font-bold flex items-center gap-1 ${
                  responseStatus >= 200 && responseStatus < 300 
                    ? 'bg-green-200 text-green-800' 
                    : 'bg-red-200 text-red-800'
                }`}>
                  {responseStatus >= 200 && responseStatus < 300 ? <CheckCircle className="w-3 h-3" /> : <AlertCircle className="w-3 h-3" />}
                  Status: {responseStatus}
                </span>

                {/* Timing Badge */}
                <span className="px-3 py-1 bg-slate-200 text-slate-700 rounded-full text-xs font-bold flex items-center gap-1">
                  <Clock className="w-3 h-3" />
                  {responseTime} ms
                </span>
              </div>
              <button 
                onClick={() => { setResponse(null); setResponseStatus(null); }} 
                className="text-slate-400 hover:text-red-500"
              >
                <Trash2 className="w-4 h-4" />
              </button>
            </div>

            <div className="p-0">
              <pre className="p-6 font-mono text-sm bg-slate-900 text-blue-300 overflow-x-auto max-h-[600px]">
                {JSON.stringify(response, null, 2)}
              </pre>
            </div>
          </div>
        )}

      </div>
    </div>
  );
};

export default ApiTester;