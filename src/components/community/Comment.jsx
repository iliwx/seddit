import React, { useState } from 'react';
import { ArrowBigUp, ArrowBigDown, MessageSquare, PlusSquare, MinusSquare } from 'lucide-react';
import ReplyForm from './ReplyForm'; // Import the form we just made

const Comment = ({ comment, depth = 0 }) => {
  // 1. STATE: Collapse the entire comment (Reddit style)
  const [isCollapsed, setIsCollapsed] = useState(false);
  
  // 2. STATE: Toggle the Reply Input Box
  const [isReplying, setIsReplying] = useState(false);

  const hasReplies = comment.replies && comment.replies.length > 0;

  // Handler for successful reply
  const handleReplySuccess = (newComment) => {
    setIsReplying(false);
    // In a real app, you would append 'newComment' to the 'comment.replies' list here
    // For now, we just close the box.
  };

  // If collapsed, we show a minimal version
  if (isCollapsed) {
    return (
      <div className={`mt-2 ${depth > 0 ? 'ml-4 md:ml-8' : ''} py-1`}>
         <div className="flex items-center gap-2">
             <button onClick={() => setIsCollapsed(false)} className="text-slate-400 hover:text-indigo-600">
                <PlusSquare className="w-4 h-4" />
             </button>
             <span className="text-xs font-bold text-slate-500">u/{comment.author}</span>
             <span className="text-xs text-slate-400 italic">Comment hidden</span>
         </div>
      </div>
    );
  }

  return (
    <div className={`mt-4 ${depth > 0 ? 'ml-4 md:ml-8' : ''}`}>
      <div className="flex gap-2 group">
        
        {/* COLLAPSIBLE THREAD LINE */}
        <div 
            className="flex flex-col items-center cursor-pointer group/line" 
            onClick={() => setIsCollapsed(true)}
        >
            <div className="w-7 h-7 rounded-full bg-slate-200 overflow-hidden flex-shrink-0 relative z-10">
               <img 
                 src={`https://api.dicebear.com/7.x/avataaars/svg?seed=${comment.author}`} 
                 alt="avatar" 
                 className="w-full h-full object-cover"
                />
            </div>
            {/* The Thread Line: Hovering it highlights it to show you can click to collapse */}
            <div className="w-0.5 flex-1 bg-slate-200 my-2 group-hover/line:bg-indigo-400 transition-colors rounded-full"></div>
        </div>

        {/* Comment Content */}
        <div className="flex-1">
          <div className="flex items-center gap-2 mb-1">
            <span className="text-xs font-bold text-zinc-800 hover:underline cursor-pointer">
              u/{comment.author}
            </span>
            <span className="text-xs text-slate-400">{comment.timeAgo}</span>
            {comment.isOp && (
                <span className="text-[10px] font-bold text-indigo-600 bg-indigo-50 px-1 rounded">OP</span>
            )}
          </div>

          <div className="text-sm text-zinc-800 leading-relaxed break-words">
            {comment.content}
          </div>

          {/* Action Bar */}
          <div className="flex items-center gap-4 mt-2 select-none">
            <div className="flex items-center gap-1 text-slate-500">
                <button className="hover:bg-slate-100 p-1 rounded transition-colors">
                    <ArrowBigUp className="w-5 h-5 cursor-pointer hover:text-orange-500" />
                </button>
                <span className="text-xs font-bold">{comment.votes}</span>
                <button className="hover:bg-slate-100 p-1 rounded transition-colors">
                     <ArrowBigDown className="w-5 h-5 cursor-pointer hover:text-indigo-600" />
                </button>
            </div>
            
            <button 
                onClick={() => setIsReplying(!isReplying)}
                className={`flex items-center gap-1 text-xs font-bold px-2 py-1 rounded transition-colors ${isReplying ? 'bg-slate-200 text-zinc-800' : 'text-slate-500 hover:bg-slate-100'}`}
            >
                <MessageSquare className="w-4 h-4" /> Reply
            </button>
          </div>

          {/* REPLY FORM INJECTION */}
          {isReplying && (
             <ReplyForm 
                parentId={comment.id} 
                onCancel={() => setIsReplying(false)}
                onSuccess={handleReplySuccess}
             />
          )}

          {/* RECURSION: Render Children */}
          {hasReplies && (
            <div className="mt-2">
              {comment.replies.map((reply) => (
                <Comment key={reply.id} comment={reply} depth={depth + 1} />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Comment;