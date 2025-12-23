import React, { useState } from 'react';
import { ArrowBigUp, ArrowBigDown, MessageSquare, Share2, MoreHorizontal } from 'lucide-react';
// Import the recursive component
import Comment from './Comment'; 
import { Link } from 'react-router-dom'; // 1. Import Link

const PostCard = ({ post }) => {
  const [isRepliesOpen, setIsRepliesOpen] = useState(false);
  const [voteCount, setVoteCount] = useState(post.votes);

  // --- MOCK BACKEND RESPONSE ---
  // This structure allows infinite nesting
  const mockComments = [
    {
      id: 1,
      author: "audiophile_king",
      timeAgo: "2 hours ago",
      content: "This is exactly what I was looking for! The transition from track 3 to 4 is legendary.",
      votes: 450,
      isOp: false,
      replies: [
        {
          id: 11,
          author: "music_fan_99", // The OP
          timeAgo: "1 hour ago",
          content: "Glad you liked it! It took me forever to find that specific cover.",
          votes: 120,
          isOp: true, // Highlights author
          replies: [
            {
                id: 111,
                author: "random_user",
                timeAgo: "30 mins ago",
                content: "Do you have a Soundcloud link for it?",
                votes: 5,
                isOp: false,
                replies: []
            }
          ]
        },
        {
           id: 12,
           author: "hater_101",
           timeAgo: "50 mins ago",
           content: "Unpopular opinion: The original was better.",
           votes: -12,
           isOp: false,
           replies: []
        }
      ]
    },
    {
      id: 2,
      author: "coding_wizard",
      timeAgo: "5 hours ago",
      content: "Can someone explain the context of the third song? I feel like I'm missing something.",
      votes: 89,
      isOp: false,
      replies: [
        {
            id: 21,
            author: "lore_master",
            timeAgo: "3 hours ago",
            content: "It's a reference to the 2008 incident where the lead singer...",
            votes: 230,
            isOp: false,
            replies: []
        }
      ]
    }
  ];

  return (
    <div className="bg-white border border-slate-200 rounded-md hover:border-slate-300 transition-colors mb-4 cursor-pointer">
      <div className="flex">
        {/* Vote Sidebar (Left) */}
        <div className="w-12 bg-slate-50 p-2 flex flex-col items-center rounded-l-md border-r border-slate-100">
          <button 
            onClick={(e) => { e.stopPropagation(); setVoteCount(voteCount + 1); }}
            className="text-slate-500 hover:text-orange-500 hover:bg-slate-200 p-1 rounded"
          >
            <ArrowBigUp className="w-6 h-6" />
          </button>
          <span className="text-sm font-bold text-slate-800 my-1">{voteCount}</span>
          <button 
            onClick={(e) => { e.stopPropagation(); setVoteCount(voteCount - 1); }}
            className="text-slate-500 hover:text-indigo-500 hover:bg-slate-200 p-1 rounded"
          >
            <ArrowBigDown className="w-6 h-6" />
          </button>
        </div>

        {/* Post Content */}
        <div className="flex-1 p-3">
          {/* ... Header and Content (Same as before) ... */}
          <div className="flex items-center text-xs text-slate-500 mb-2">
            <img src={post.authorAvatar} alt="avatar" className="w-5 h-5 rounded-full mr-2" />
            {post.community && (
              <>
                <Link 
                  to={`/r/${post.community}`}
                  onClick={(e) => e.stopPropagation()} // Prevent opening post when clicking link
                  className="font-bold text-zinc-900 hover:underline mr-1 flex items-center gap-1"
                >
                  {post.communityIcon && <img src={post.communityIcon} className="w-4 h-4 rounded-full" />}
                  r/{post.community}
                </Link>
                <span>•</span>
                <span className="mx-1">Posted by</span>
              </>
            )}
            <span className="font-bold text-slate-700 hover:underline mr-1">u/{post.author}</span>
            <span>•</span>
            <span className="mx-1">{post.timeAgo}</span>
          </div>

          <h3 className="text-lg font-medium text-zinc-900 mb-2 leading-snug">{post.title}</h3>
          <p className="text-slate-600 text-sm mb-4 line-clamp-3">{post.content}</p>

          {/* Footer Actions */}
          <div className="flex items-center space-x-2 text-slate-500">
            <button 
              onClick={(e) => { e.stopPropagation(); setIsRepliesOpen(!isRepliesOpen); }}
              className={`flex items-center space-x-2 px-2 py-1 rounded text-sm font-medium transition-colors ${isRepliesOpen ? 'bg-slate-200 text-zinc-900' : 'hover:bg-slate-100'}`}
            >
              <MessageSquare className="w-4 h-4" />
              <span>{post.comments} Comments</span>
            </button>
            <button className="flex items-center space-x-2 px-2 py-1 hover:bg-slate-100 rounded text-sm font-medium">
              <Share2 className="w-4 h-4" />
              <span>Share</span>
            </button>
          </div>

          {/* --- REPLIES SECTION --- */}
          {isRepliesOpen && (
            <div className="mt-4 pt-4 border-t border-slate-100 bg-slate-50 -mr-3 -ml-3 px-4 pb-4 rounded-b-md animate-in slide-in-from-top-2 duration-200 cursor-auto" onClick={(e) => e.stopPropagation()}>
              
              {/* Filter / Sort Bar */}
              <div className="flex items-center justify-between mb-4">
                  <h4 className="text-xs font-bold text-slate-400 uppercase">Discussion</h4>
                  <select className="text-xs bg-transparent font-bold text-slate-500 outline-none cursor-pointer">
                      <option>Top Comments</option>
                      <option>Newest</option>
                  </select>
              </div>
              
              {/* RENDER THE COMMENTS */}
              <div className="space-y-2">
                  {mockComments.map((comment) => (
                      <Comment key={comment.id} comment={comment} depth={0} />
                  ))}
              </div>
              
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PostCard;