import React, { useState, useEffect } from 'react';
import Header from '../components/Header';
import PostCard from '../components/community/PostCard';
import { Link, useParams } from 'react-router-dom'; 
import CreatePostModal from '../components/CreatePostModal';
import { 
  ChevronDown, ChevronUp, Home, TrendingUp, Users, Star, 
  Gamepad2, Music, Code, HelpCircle, Bell, MoreHorizontal,
  Clock, Calendar, PanelLeftClose, PanelLeftOpen, 
  PanelRightClose, PanelRightOpen, Maximize2, Shield, AlertCircle
} from 'lucide-react';

// --- MOCK DATABASE ---
// In a real app, this data comes from your Backend API based on the URL parameter
const communityDatabase = {
  spotify: {
    name: 'spotify',
    title: 'r/spotify',
    icon: Music,
    theme: 'from-green-400 to-green-600',
    iconBg: 'bg-green-500',
    description: 'This subreddit is mainly for sharing Spotify playlists. We are not a support community.',
    members: '342k',
    online: '1.6k',
    created: 'Nov 11, 2008',
    rules: [
      'No tech support questions',
      'No illegal content',
      'Be respectful',
      'No spamming playlists'
    ],
    posts: [
      {
        id: 1,
        author: 'music_fan_99',
        authorAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix',
        timeAgo: '13 hours ago',
        title: 'All new Christmas songs, every one from 2025',
        content: 'I created a massive playlist of every single holiday release from this year.',
        votes: 3420,
        comments: 160
      }
    ]
  },
  gaming: {
    name: 'gaming',
    title: 'r/gaming',
    icon: Gamepad2,
    theme: 'from-red-500 to-orange-600',
    iconBg: 'bg-red-500',
    description: 'A subreddit for (almost) anything related to games - video games, board games, card games, etc.',
    members: '32.1m',
    online: '142k',
    created: 'Sep 17, 2007',
    rules: [
      'No piracy',
      'No NSFW content',
      'No hate speech',
      'Original content only'
    ],
    posts: [
      {
        id: 2,
        author: 'retro_gamer',
        authorAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Mario',
        timeAgo: '2 hours ago',
        title: 'Found this gem in my attic today',
        content: 'Still works perfectly after 20 years. Who remembers this intro screen?',
        votes: 15400,
        comments: 890
      }
    ]
  },
  webdev: {
    name: 'webdev',
    title: 'r/webdev',
    icon: Code,
    theme: 'from-blue-600 to-indigo-700',
    iconBg: 'bg-blue-600',
    description: 'A community for web developers. Discussion, help, and news about web development.',
    members: '1.4m',
    online: '2.3k',
    created: 'Jan 25, 2009',
    rules: [
      'Be helpful',
      'Show your code',
      'No self-promotion'
    ],
    posts: [
       {
        id: 3,
        author: 'css_wizard',
        authorAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Code',
        timeAgo: '5 hours ago',
        title: 'Stop using px for font sizes',
        content: 'Here is why you should switch to rem and em immediately for accessibility.',
        votes: 892,
        comments: 340
      }
    ]
  }
};



// --- Reusable Sidebar Section (Unchanged) ---
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

// Update SidebarItem to use Link instead of a href="#" so navigation works without reload
const SidebarItem = ({ label, icon: Icon, path }) => (
  <Link to={path || '#'} className="flex items-center gap-3 px-3 py-2 text-sm font-medium text-slate-600 rounded-md hover:bg-gray-200 transition-colors">
    {Icon && <Icon className="w-5 h-5" />}
    {label}
  </Link>
);

// --- MAIN PAGE COMPONENT ---
const CommunityPage = () => {
  const { name } = useParams(); // Get the name from URL (e.g., 'gaming')
  
  // 1. SELECT DATA BASED ON URL
  // If the name exists in our DB, use it. Otherwise default to Spotify.
  const communityData = communityDatabase[name] || communityDatabase['spotify'];
  const CommunityIcon = communityData.icon; // Dynamic Component
  const [isPostModalOpen, setIsPostModalOpen] = useState(false);

  // Sidebar States
  const [isLeftOpen, setIsLeftOpen] = useState(true);
  const [isRightOpen, setIsRightOpen] = useState(true);

  // Scroll to top when switching communities
  useEffect(() => {
    window.scrollTo(0, 0);
  }, [name]);

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

        {/* --- MIDDLE SECTION (Dynamic Content) --- */}
        <main className={`flex-1 w-full py-6 transition-all duration-300 ${isLeftOpen && isRightOpen ? 'max-w-4xl px-0 md:px-6' : 'max-w-6xl px-4'}`}>
          
          {/* Toolbar */}
          <div className="flex items-center justify-between mb-4 text-slate-500">
            <div className="flex items-center gap-2">
                <button 
                    onClick={() => setIsLeftOpen(!isLeftOpen)}
                    className="p-1.5 hover:bg-white hover:shadow-sm rounded-md transition-all text-sm flex items-center gap-2"
                >
                    {isLeftOpen ? <PanelLeftClose className="w-5 h-5" /> : <PanelLeftOpen className="w-5 h-5" />}
                    <span className="hidden sm:inline text-xs font-semibold">{isLeftOpen ? 'Hide Menu' : 'Show Menu'}</span>
                </button>
            </div>

            <button onClick={toggleFocusMode} className="p-1.5 hover:bg-white hover:shadow-sm rounded-md transition-all flex items-center gap-2 text-indigo-600">
                <Maximize2 className="w-4 h-4" />
                <span className="text-xs font-bold uppercase">Focus Mode</span>
            </button>

            <div className="flex items-center gap-2">
                <button 
                    onClick={() => setIsRightOpen(!isRightOpen)}
                    className="p-1.5 hover:bg-white hover:shadow-sm rounded-md transition-all text-sm flex items-center gap-2"
                >
                    <span className="hidden sm:inline text-xs font-semibold">{isRightOpen ? 'Hide Details' : 'Show Details'}</span>
                    {isRightOpen ? <PanelRightClose className="w-5 h-5" /> : <PanelRightOpen className="w-5 h-5" />}
                </button>
            </div>
          </div>

          {/* DYNAMIC COMMUNITY HEADER */}
          <div className="bg-white rounded-md border border-slate-200 overflow-hidden mb-6">
            {/* Dynamic Banner Color */}
            <div className={`h-32 bg-gradient-to-r ${communityData.theme}`}></div>
            
            <div className="px-6 pb-4">
                <div className="flex items-end -mt-8 mb-4">
                    {/* Dynamic Icon */}
                    <div className="w-20 h-20 bg-white p-1 rounded-full relative z-10">
                        <div className={`w-full h-full ${communityData.iconBg} rounded-full flex items-center justify-center`}>
                             <CommunityIcon className="w-10 h-10 text-white" />
                        </div>
                    </div>
                    
                    {/* Dynamic Titles */}
                    <div className="ml-4 flex-1 flex items-end justify-between">
                        <div>
                            <h1 className="text-2xl font-bold text-zinc-900 leading-none">{communityData.title}</h1>
                            <p className="text-sm text-slate-500 mt-1">r/{communityData.name}</p>
                        </div>
                        <div className="flex gap-2">
                            <button className="px-6 py-1.5 rounded-full border border-indigo-600 text-indigo-600 font-bold hover:bg-indigo-50 transition-colors">
                                Join
                            </button>
                        </div>
                    </div>
                </div>
                <div className="flex gap-6 border-b border-slate-200">
                    <button className="pb-3 border-b-2 border-zinc-900 font-bold text-zinc-900">Posts</button>
                    <button className="pb-3 border-b-2 border-transparent text-slate-500 hover:text-zinc-800">Wiki</button>
                    <button className="pb-3 border-b-2 border-transparent text-slate-500 hover:text-zinc-800">Rules</button>
                </div>
            </div>
          </div>

          {/* DYNAMIC POSTS FEED */}
          <div className="space-y-4">
             {communityData.posts.map(post => (
                <PostCard key={post.id} post={post} />
             ))}
          </div>

        </main>

        {/* --- RIGHT SIDEBAR (Dynamic Details) --- */}
        <aside 
            className={`
                sticky top-16 h-fit py-6 px-2 space-y-4
                transition-all duration-300 ease-in-out
                ${isRightOpen ? 'w-80 translate-x-0 opacity-100 hidden xl:block' : 'w-0 translate-x-full opacity-0 overflow-hidden'}
            `}
        >
          <div className="min-w-[300px]">
            {/* About Card */}
            <div className="bg-white border border-slate-200 rounded-md p-4">
                <div className="flex items-center justify-between mb-3">
                    <h2 className="text-sm font-bold text-slate-500 uppercase">About Community</h2>
                    <MoreHorizontal className="w-4 h-4 text-slate-400" />
                </div>
                {/* Dynamic Description */}
                <p className="text-sm text-zinc-700 mb-4 leading-relaxed">
                    {communityData.description}
                </p>
                <div className="flex items-center gap-2 mb-2 text-slate-600">
                    <Calendar className="w-4 h-4" />
                    <span className="text-sm">Created {communityData.created}</span>
                </div>
                <div className="border-t border-slate-200 my-4" />
                <div className="flex gap-8 mb-4">
                    <div>
                        <div className="text-lg font-bold text-zinc-900">{communityData.members}</div>
                        <div className="text-xs text-slate-500">Members</div>
                    </div>
                    <div>
                        <div className="text-lg font-bold text-zinc-900 flex items-center gap-1">
                            <span className="w-2 h-2 rounded-full bg-green-500"></span>
                            {communityData.online}
                        </div>
                        <div className="text-xs text-slate-500">Online</div>
                    </div>
                </div>
                <button 
                onClick={() => setIsPostModalOpen(true)} // <--- ADD ONCLICK
                className="w-full py-2 bg-indigo-600 text-white rounded-full font-bold hover:bg-indigo-700 transition-colors"
             >
                Create Post
             </button>
                
                {/* Dynamic Link for Moderation */}
                <div className="mt-4 pt-4 border-t border-slate-200">
                    <h3 className="text-xs font-bold text-slate-400 uppercase mb-2">Moderation</h3>
                    <Link 
                        to={`/r/${communityData.name}/manage`} 
                        className="flex items-center gap-2 w-full px-4 py-2 bg-slate-100 text-slate-700 font-bold rounded-md hover:bg-slate-200 transition-colors text-sm"
                    >
                        <Shield className="w-4 h-4" /> Mod Tools
                    </Link>
                </div>
            </div>

            {/* Dynamic Rules Card */}
            <div className="bg-white border border-slate-200 rounded-md p-4 mt-4">
                <h2 className="text-sm font-bold text-slate-500 uppercase mb-3">r/{communityData.name} Rules</h2>
                <ol className="list-decimal list-inside space-y-2 text-sm text-zinc-700 font-medium">
                    {communityData.rules.map((rule, index) => (
                        <li key={index} className="pb-2 border-b border-slate-100 last:border-0">{rule}</li>
                    ))}
                </ol>
            </div>
          </div>
        </aside>

      </div>
    </div>
  );
};

export default CommunityPage;