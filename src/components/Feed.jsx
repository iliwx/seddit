import React from 'react';
import PostCard from './community/PostCard';
import { Image as ImageIcon, Link as LinkIcon } from 'lucide-react';

const Feed = () => {
  // MOCK DATA: Posts from various communities
  const feedPosts = [
    {
      id: 101,
      community: 'spotify',
      author: 'music_fan_99',
      timeAgo: '2 hours ago',
      title: 'My 2024 Wrapped is broken?',
      content: 'Is anyone else seeing weird stats? It says I listened to 4000 hours of White Noise.',
      votes: 1204,
      comments: 89
    },
    {
      id: 102,
      community: 'gaming',
      author: 'retro_king',
      timeAgo: '4 hours ago',
      title: 'Just finished GTA VI',
      content: 'No spoilers, but the ending absolutely blew my mind. Worth the 12 year wait.',
      votes: 45002,
      comments: 3201
    },
    {
      id: 103,
      community: 'webdev',
      author: 'react_enjoyer',
      timeAgo: '6 hours ago',
      title: 'React 19 is confusing me',
      content: 'Can someone explain the new compiler? Do I still need useMemo?',
      votes: 340,
      comments: 112
    }
  ];

  return (
    <div className="space-y-4">
      {/* Create Post Input (Optional, looks nice on feed) */}
      <div className="bg-white border border-slate-200 p-3 rounded-md flex items-center gap-3 mb-6">
        <div className="w-8 h-8 bg-slate-200 rounded-full flex-shrink-0"></div>
        <input 
            type="text" 
            placeholder="Create Post" 
            className="bg-slate-100 border border-slate-200 text-sm rounded-md px-4 py-2 flex-1 hover:bg-white focus:bg-white focus:ring-2 focus:ring-indigo-500 outline-none transition-all"
        />
        <button className="p-2 hover:bg-slate-100 rounded text-slate-500"><ImageIcon className="w-5 h-5" /></button>
        <button className="p-2 hover:bg-slate-100 rounded text-slate-500"><LinkIcon className="w-5 h-5" /></button>
      </div>

      {feedPosts.map(post => (
        <PostCard key={post.id} post={post} />
      ))}
    </div>
  );
};

export default Feed;