import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; // Needed for logout navigation
import { Edit, Calendar, MapPin, Link as LinkIcon, BarChart3, MessageSquare, Bookmark, LogOut } from 'lucide-react';
import { toast } from 'react-toastify';

const ProfileView = () => {
  const [isProfileModalOpen, setIsProfileModalOpen] = useState(false);
  const [user, setUser] = useState({ name: 'Guest', email: '' }); // Local state for user info
  const navigate = useNavigate();

  // 1. Load User Data on Mount
  useEffect(() => {
    // We get the string from storage and parse it back to an Object
    const storedUser = localStorage.getItem('userData');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  // 2. Handle Logout
  const handleLogout = () => {
    // OPTION 1: Nuclear option (Clears EVERYTHING in local storage for this domain)
    localStorage.clear(); 
    
    // OPTION 2: Specific removal (What you have now - safer if you want to keep some settings like 'theme')
    // localStorage.removeItem('authToken');
    // localStorage.removeItem('userData');
    
    toast.info('Logged out successfully');
    navigate('/login');
  };

  const stats = [
    { label: 'Post Karma', value: '12,847', icon: BarChart3, color: 'text-indigo-600' },
    { label: 'Comment Karma', value: '3,291', icon: MessageSquare, color: 'text-blue-500' },
    { label: 'Saved Posts', value: '156', icon: Bookmark, color: 'text-slate-600' },
  ];

  const recentPosts = [
    {
      title: 'Just built my first React app with TypeScript!',
      subSeddit: 'reactjs',
      upvotes: 1247,
      comments: 89,
      timeAgo: '4 hours ago'
    },
    {
      title: 'Tips for better code organization in large projects',
      subSeddit: 'programming',
      upvotes: 892,
      comments: 156,
      timeAgo: '1 day ago'
    },
  ];

  return (    
        <main className="flex-1 p-4 md:p-6">
          <div className="max-w-4xl mx-auto">
            
            {/* Profile Header */}
            <div className="bg-white border border-slate-200 rounded-2xl shadow-sm p-6 mb-6">
              <div className="flex flex-col md:flex-row md:items-start justify-between gap-4">
                <div className="flex items-center space-x-4">
                  <div className="w-20 h-20 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-600 text-2xl font-bold">
                    {/* Show first letter of name */}
                    {user.name ? user.name.charAt(0).toUpperCase() : 'U'}
                  </div>
                  <div>
                    {/* DYNAMIC NAME */}
                    <h1 className="text-2xl font-bold text-zinc-800">
                      {user.name || 'User'}
                    </h1>
                    <p className="text-slate-600 mb-2">
                      {user.email || 'No email provided'}
                    </p>
                    
                    <div className="flex flex-wrap gap-4 text-sm text-slate-500">
                      <div className="flex items-center space-x-1">
                        <Calendar className="w-4 h-4" />
                        <span>Joined recently</span>
                      </div>
                      <div className="flex items-center space-x-1">
                        <MapPin className="w-4 h-4" />
                        <span>Earth</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="flex gap-3">
                    <button
                    onClick={() => setIsProfileModalOpen(true)}
                    className="flex items-center space-x-2 px-4 py-2 bg-white border border-slate-300 text-slate-700 rounded-lg hover:bg-slate-50 transition-colors shadow-sm"
                    >
                    <Edit className="w-4 h-4" />
                    <span>Edit</span>
                    </button>
                    
                    {/* LOGOUT BUTTON */}
                    <button
                    onClick={handleLogout}
                    className="flex items-center space-x-2 px-4 py-2 bg-red-50 text-red-600 border border-red-100 rounded-lg hover:bg-red-100 transition-colors"
                    >
                    <LogOut className="w-4 h-4" />
                    <span>Logout</span>
                    </button>
                </div>
              </div>
            </div>

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
              {stats.map((stat) => {
                const Icon = stat.icon;
                return (
                  <div key={stat.label} className="bg-white border border-slate-200 rounded-xl p-6 shadow-sm hover:shadow-md transition-shadow">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-sm font-medium text-slate-500 mb-1">{stat.label}</p>
                        <p className="text-2xl font-bold text-zinc-800">{stat.value}</p>
                      </div>
                      <div className={`p-3 rounded-lg bg-opacity-10 ${stat.color.replace('text', 'bg')}`}>
                         <Icon className={`w-6 h-6 ${stat.color}`} />
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Recent Activity */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <div className="bg-white border border-slate-200 rounded-xl shadow-sm p-6">
                <h2 className="text-lg font-bold text-zinc-800 mb-4">Recent Posts</h2>
                <div className="space-y-4">
                  {recentPosts.map((post, index) => (
                    <div key={index} className="group border-b border-slate-100 pb-4 last:border-b-0 last:pb-0">
                      <h3 className="font-medium text-zinc-800 mb-1 group-hover:text-indigo-600 cursor-pointer transition-colors line-clamp-1">
                        {post.title}
                      </h3>
                      <div className="flex flex-wrap items-center gap-x-2 text-xs text-slate-500">
                        <span className="font-medium text-slate-700">r/{post.subSeddit}</span>
                        <span>•</span>
                        <span>{post.timeAgo}</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div className="bg-white border border-slate-200 rounded-xl shadow-sm p-6">
                <h2 className="text-lg font-bold text-zinc-800 mb-4">Account Status</h2>
                <div className="space-y-4">
                   <div className="flex justify-between items-center p-3 bg-slate-50 rounded-lg">
                    <span className="text-slate-600 text-sm">Account Type</span>
                    <span className="font-semibold text-indigo-600 bg-indigo-50 px-3 py-1 rounded-full text-xs">Free Tier</span>
                  </div>
                   {/* Add more account details here */}
                </div>
              </div>
            </div>

          </div>
        </main>
  );
};

export default ProfileView;