import React, { useState } from 'react';
import { Send, X, Loader2 } from 'lucide-react';
import { toast } from 'react-toastify';

const ReplyForm = ({ parentId, onCancel, onSuccess }) => {
  const [content, setContent] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!content.trim()) return;

    const token = localStorage.getItem('authToken');
    
    // Basic Auth Check
    if (!token) {
        toast.error("You must be logged in to reply.");
        return;
    }

    setIsSubmitting(true);

    try {
      const response = await fetch('http://localhost:8080/api/SubmitComment', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}` // Sending the JWT
        },
        body: JSON.stringify({
          parentId: parentId, // The ID of the comment we are replying to
          content: content
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to post reply');
      }

      const newCommentData = await response.json();
      toast.success('Reply posted!');
      setContent('');
      
      // Callback to parent to close form and hopefully add the new comment to the list
      if (onSuccess) onSuccess(newCommentData);

    } catch (error) {
      console.error(error);
      toast.error('Failed to send reply. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="mt-4 mb-4 animate-in fade-in slide-in-from-top-2 duration-200">
      <div className="relative">
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="What are your thoughts?"
          className="w-full p-3 pr-12 text-sm border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-transparent outline-none min-h-[80px] resize-y bg-white"
          autoFocus
        />
        <button
            type="button"
            onClick={onCancel}
            className="absolute top-2 right-2 text-slate-400 hover:text-slate-600"
            title="Cancel"
        >
            <X className="w-4 h-4" />
        </button>
      </div>
      
      <div className="flex justify-end mt-2">
        <button
          type="submit"
          disabled={isSubmitting || !content.trim()}
          className="flex items-center gap-2 px-4 py-1.5 bg-indigo-600 text-white text-sm font-bold rounded-full hover:bg-indigo-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isSubmitting ? <Loader2 className="w-4 h-4 animate-spin" /> : <Send className="w-4 h-4" />}
          Reply
        </button>
      </div>
    </form>
  );
};

export default ReplyForm;