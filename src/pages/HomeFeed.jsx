import React, { useState } from 'react';
import Header from '../components/Header';
import Feed from '../components/Feed'; // Import the Feed we just made
import { Link } from 'react-router-dom';
import CreatePostModal from '../components/CreatePostModal';
import { 
  ChevronDown, ChevronUp, Home, TrendingUp, Users, 
  Gamepad2, Music, Code, Clock,
  PanelLeftClose, PanelLeftOpen, PanelRightClose, PanelRightOpen, Maximize2
} from 'lucide-react';

// --- Reusable Sidebar Section ---
const SidebarSection = ({ title, icon: Icon, children, defaultOpen = true }) => {
  const [isOpen, setIsOpen] = useState(defaultOpen);
  return (
    <div className="mb-2">
      <button 
        onClick={() => setIsOpen(!isOpen)}
        className="w-full flex items-center justify-between p-2 text-slate-500 hover:bg-gray-100 rounded-md transition-colors group"
      >
        <div className="flex items-center gap-3">
            {Icon && <Icon className="w-5 h-5" />}
            <span className="text-xs font-bold uppercase tracking-wider">{title}</span>
        </div>
        {isOpen ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
      </button>
      {isOpen && (
        <div className="ml-2 mt-1 border-l-2 border-slate-200 pl-2 space-y-1">
          {children}
        </div>
      )}
    </div>
  );
};

const SidebarItem = ({ label, icon: Icon, path }) => (
  <Link to={path || '#'} className="flex items-center gap-3 px-3 py-2 text-sm font-medium text-slate-600 rounded-md hover:bg-gray-200 transition-colors">
    {Icon && <Icon className="w-5 h-5" />}
    {label}
  </Link>
);

// --- MAIN PAGE COMPONENT ---
const HomeFeed = () => {
  const [isPostModalOpen, setIsPostModalOpen] = useState(false);
  const [isLeftOpen, setIsLeftOpen] = useState(true);
  const [isRightOpen, setIsRightOpen] = useState(true);

  const toggleFocusMode = () => {
    if (isLeftOpen || isRightOpen) {
        setIsLeftOpen(false);
        setIsRightOpen(false);
    } else {
        setIsLeftOpen(true);
        setIsRightOpen(true);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
        {/* 3. ADD MODAL COMPONENT (Can be anywhere, preferably at the bottom) */}
       <CreatePostModal 
         isOpen={isPostModalOpen} 
         onClose={() => setIsPostModalOpen(false)}
         defaultCommunity={name} // Pass 'spotify' or 'gaming'
       />
      <Header />

      <div className="w-full flex justify-center flex-1 relative transition-all duration-300">
        
        {/* --- LEFT SIDEBAR (Navigation) --- */}
        <aside 
            className={`
                sticky top-16 h-[calc(100vh-4rem)] overflow-y-auto py-4 px-2 scrollbar-hide border-r border-gray-200 bg-gray-100
                transition-all duration-300 ease-in-out
                ${isLeftOpen ? 'w-64 translate-x-0 opacity-100 hidden lg:block' : 'w-0 -translate-x-full opacity-0 overflow-hidden'}
            `}
        >
          <div className="min-w-[240px]">
            <nav className="space-y-2">
                <SidebarItem icon={Home} label="Home" path="/" />
                <SidebarItem icon={TrendingUp} label="Popular" path="/popular" />
                <div className="my-4 border-b border-gray-200" />
                
                <SidebarSection title="Recent" icon={Clock} defaultOpen={true}>
                    <SidebarItem icon={Music} label="r/spotify" path="/r/spotify" />
                    <SidebarItem icon={Code} label="r/webdev" path="/r/webdev" />
                </SidebarSection>

                <SidebarSection title="Communities" icon={Users} defaultOpen={true}>
                    <SidebarItem icon={Gamepad2} label="Gaming" path="/r/gaming" />
                    <SidebarItem icon={Music} label="Music" path="/r/spotify" />
                    <SidebarItem icon={Code} label="Programming" path="/r/webdev" />
                </SidebarSection>
            </nav>
          </div>
        </aside>

        {/* --- MIDDLE SECTION (THE FEED) --- */}
        <main className={`flex-1 w-full py-6 transition-all duration-300 ${isLeftOpen && isRightOpen ? 'max-w-4xl px-0 md:px-6' : 'max-w-6xl px-4'}`}>
          
          {/* View Controls */}
          <div className="flex items-center justify-between mb-4 text-slate-500">
            <button onClick={() => setIsLeftOpen(!isLeftOpen)} className="p-1.5 hover:bg-white hover:shadow-sm rounded-md transition-all text-sm flex items-center gap-2">
               {isLeftOpen ? <PanelLeftClose className="w-5 h-5" /> : <PanelLeftOpen className="w-5 h-5" />}
               <span className="hidden sm:inline text-xs font-semibold">{isLeftOpen ? 'Hide Menu' : 'Show Menu'}</span>
            </button>

            <button onClick={toggleFocusMode} className="p-1.5 hover:bg-white hover:shadow-sm rounded-md transition-all flex items-center gap-2 text-indigo-600">
                <Maximize2 className="w-4 h-4" />
                <span className="text-xs font-bold uppercase">Focus Mode</span>
            </button>

            <button onClick={() => setIsRightOpen(!isRightOpen)} className="p-1.5 hover:bg-white hover:shadow-sm rounded-md transition-all text-sm flex items-center gap-2">
                <span className="hidden sm:inline text-xs font-semibold">{isRightOpen ? 'Hide Info' : 'Show Info'}</span>
                {isRightOpen ? <PanelRightClose className="w-5 h-5" /> : <PanelRightOpen className="w-5 h-5" />}
            </button>
          </div>

          {/* RENDER THE FEED COMPONENT */}
          <Feed />

        </main>

        {/* --- RIGHT SIDEBAR (Generic Home Info) --- */}
        <aside 
            className={`
                sticky top-16 h-fit py-6 px-2 space-y-4
                transition-all duration-300 ease-in-out
                ${isRightOpen ? 'w-80 translate-x-0 opacity-100 hidden xl:block' : 'w-0 translate-x-full opacity-0 overflow-hidden'}
            `}
        >
          <div className="min-w-[300px]">
            {/* Home Info Card */}
            <div className="bg-white border border-slate-200 rounded-md p-4">
                <div className="flex items-center gap-2 mb-3">
                     <div className="w-8 h-8 bg-indigo-100 rounded-full flex items-center justify-center text-indigo-600">
                         <Home className="w-5 h-5" />
                     </div>
                    <h2 className="text-sm font-bold text-slate-700 uppercase">Home</h2>
                </div>
                <p className="text-sm text-zinc-700 mb-4">
                    Your personal Seddit frontpage. Come here to check in with your favorite communities.
                </p>
                
                <div className="border-t border-slate-200 my-4" />
                
                <Link to="/create-community" className="block w-full text-center py-2 mb-2 bg-indigo-600 text-white rounded-full font-bold hover:bg-indigo-700 transition-colors">
                    Create Community
                </Link>
                <button 
                onClick={() => setIsPostModalOpen(true)}
                className="w-full py-2 border border-indigo-600 text-indigo-600 rounded-full font-bold hover:bg-indigo-50 transition-colors">
                    Create Post
                </button>
            </div>

            {/* Premium / Footer Card */}
            <div className="bg-white border border-slate-200 rounded-md p-4 mt-4 text-xs text-slate-500">
                <div className="flex flex-wrap gap-2 mb-4">
                    <a href="#" className="hover:underline">User Agreement</a>
                    <a href="#" className="hover:underline">Privacy Policy</a>
                    <a href="#" className="hover:underline">Content Policy</a>
                </div>
                <p>Seddit © 2025. All rights reserved.</p>
            </div>
          </div>
        </aside>

      </div>
    </div>
  );
};

export default HomeFeed;