import React, { useState, useRef, useEffect } from 'react';
import { 
  X, Image as ImageIcon, Link as LinkIcon, FileText, 
  ChevronDown, Upload, Trash2, Video, Check, Search, Plus
} from 'lucide-react';
import { toast } from 'react-toastify';

const CreatePostModal = ({ isOpen, onClose, defaultCommunity = 'spotify' }) => {
  // --- MOCK DATA ---
  // In a real app, this would be fetched from an API
  const initialCommunities = [
    { name: 'r/spotify', members: '342k', isJoined: true },
    { name: 'r/gaming', members: '32m', isJoined: true },
    { name: 'r/webdev', members: '1.4m', isJoined: true },
    { name: 'r/askseddit', members: '500k', isJoined: false },
    { name: 'r/news', members: '10m', isJoined: false },
    { name: 'r/pics', members: '20m', isJoined: false },
    { name: 'r/funny', members: '15m', isJoined: false },
  ];

  // --- STATE ---
  const [activeTab, setActiveTab] = useState('post'); 
  // We store the full object of the selected community to track 'isJoined' status
  const [selectedCommunity, setSelectedCommunity] = useState(null);
  
  const [communities, setCommunities] = useState(initialCommunities);
  const [isCommunityDropdownOpen, setIsCommunityDropdownOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  
  const [title, setTitle] = useState('');
  const [body, setBody] = useState('');
  const [mediaFiles, setMediaFiles] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const fileInputRef = useRef(null);
  const dropdownRef = useRef(null); // Ref for click-outside detection

  // --- EFFECTS ---

  // 1. Reset state when modal opens
  useEffect(() => {
    if (isOpen) {
      // Find the default community object, or fallback to the first one
      const defaultComm = initialCommunities.find(c => c.name === `r/${defaultCommunity}`) || initialCommunities[0];
      setSelectedCommunity(defaultComm);
      
      setMediaFiles([]);
      setTitle('');
      setBody('');
      setActiveTab('post');
      setSearchTerm('');
      setCommunities(initialCommunities);
    }
  }, [isOpen, defaultCommunity]);

  // 2. Handle Click Outside to close Dropdown
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsCommunityDropdownOpen(false);
      }
    };

    if (isCommunityDropdownOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isCommunityDropdownOpen]);

  // --- HANDLERS ---

  const handleJoinToggle = (e, communityName) => {
    e.stopPropagation(); // Prevent selecting the community if clicking Join
    
    // Update the list of communities
    const updatedList = communities.map(c => 
      c.name === communityName ? { ...c, isJoined: !c.isJoined } : c
    );
    setCommunities(updatedList);

    // Update the currently selected one if it matches
    if (selectedCommunity.name === communityName) {
      setSelectedCommunity({ ...selectedCommunity, isJoined: !selectedCommunity.isJoined });
    }
  };

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    if (files.length > 0) {
      const newFiles = files.map(file => ({
        file,
        preview: URL.createObjectURL(file),
        type: file.type.startsWith('video') ? 'video' : 'image'
      }));
      setMediaFiles([...mediaFiles, ...newFiles]);
    }
  };

  const removeFile = (index) => {
    const newFiles = mediaFiles.filter((_, i) => i !== index);
    setMediaFiles(newFiles);
  };

  const handleSubmit = async () => {
    // Validation
    if (!title.trim()) return toast.error('Please enter a title');
    if (activeTab === 'image' && mediaFiles.length === 0) return toast.error('Please upload at least one image or video');
    
    // Enforce Join Requirement
    if (selectedCommunity && !selectedCommunity.isJoined) {
        return toast.error(`You must join ${selectedCommunity.name} to post.`);
    }

    setIsSubmitting(true);
    await new Promise(resolve => setTimeout(resolve, 1500));
    toast.success('Post created successfully!');
    setIsSubmitting(false);
    onClose();
  };

  const placeholders = {
    post: 'Post Title',
    image: 'Image Title',
    link: 'Url Title'
  };

  // Filter Logic
  const filteredCommunities = communities.filter(c => 
    c.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-in fade-in duration-200">
      <div className="bg-white w-full max-w-3xl rounded-xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-200">
          <h2 className="text-xl font-bold text-zinc-800">Create a post</h2>
          <button onClick={onClose} className="p-2 hover:bg-slate-100 rounded-full text-slate-500 transition-colors">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6 min-h-[400px]">
          
          {/* COMMUNITY SELECTOR */}
          <div className="relative mb-6 z-20" ref={dropdownRef}>
            <div className="flex items-center gap-3">
                <button 
                onClick={() => setIsCommunityDropdownOpen(!isCommunityDropdownOpen)}
                className="flex items-center gap-2 px-3 py-2 bg-white border border-slate-300 rounded-lg hover:border-slate-400 focus:ring-2 focus:ring-indigo-500/20 transition-all min-w-[200px]"
                >
                    {selectedCommunity && (
                        <>
                            <div className="w-6 h-6 rounded-full bg-slate-200 flex items-center justify-center text-xs font-bold text-slate-600">
                                {selectedCommunity.name.charAt(2).toUpperCase()}
                            </div>
                            <span className="font-bold text-zinc-700 flex-1 text-left">{selectedCommunity.name}</span>
                        </>
                    )}
                    <ChevronDown className={`w-4 h-4 text-slate-500 transition-transform ${isCommunityDropdownOpen ? 'rotate-180' : ''}`} />
                </button>

                {/* THE JOIN REQUIREMENT BUTTON (Visible if selected but not joined) */}
                {selectedCommunity && !selectedCommunity.isJoined && !isCommunityDropdownOpen && (
                    <button 
                        onClick={(e) => handleJoinToggle(e, selectedCommunity.name)}
                        className="px-4 py-2 bg-indigo-50 text-indigo-600 text-sm font-bold rounded-full hover:bg-indigo-100 transition-colors animate-in fade-in"
                    >
                        Join to Post
                    </button>
                )}
            </div>

            {/* DROPDOWN MENU */}
            {isCommunityDropdownOpen && (
              <div className="absolute top-full left-0 mt-2 w-72 bg-white border border-slate-200 rounded-xl shadow-2xl overflow-hidden animate-in fade-in slide-in-from-top-2 duration-150">
                
                {/* Search Bar */}
                <div className="p-3 border-b border-slate-100 bg-slate-50">
                    <div className="relative">
                        <Search className="absolute left-3 top-2.5 w-4 h-4 text-slate-400" />
                        <input 
                            autoFocus
                            type="text"
                            placeholder="Search communities..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full pl-9 pr-3 py-2 bg-white border border-slate-200 rounded-md text-sm outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
                        />
                    </div>
                </div>

                {/* List */}
                <div className="max-h-60 overflow-y-auto py-1">
                  {filteredCommunities.length === 0 ? (
                      <div className="px-4 py-8 text-center text-slate-500 text-sm">No communities found</div>
                  ) : (
                      filteredCommunities.map(comm => (
                        <div
                          key={comm.name}
                          onClick={() => {
                            setSelectedCommunity(comm);
                            setIsCommunityDropdownOpen(false);
                            setSearchTerm('');
                          }}
                          className="w-full text-left px-4 py-2.5 hover:bg-slate-50 flex items-center justify-between cursor-pointer group"
                        >
                          <div className="flex items-center gap-3">
                             <div className="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center text-xs font-bold text-slate-500">
                                {comm.name.charAt(2).toUpperCase()}
                             </div>
                             <div>
                                 <div className="text-sm font-bold text-zinc-700">{comm.name}</div>
                                 <div className="text-xs text-slate-400">{comm.members} members</div>
                             </div>
                          </div>
                          
                          {/* Logic: Show Check if selected, otherwise show Join button if not member */}
                          {selectedCommunity?.name === comm.name ? (
                              <Check className="w-4 h-4 text-indigo-600" />
                          ) : !comm.isJoined ? (
                              <button 
                                onClick={(e) => handleJoinToggle(e, comm.name)}
                                className="px-3 py-1 bg-indigo-600 text-white text-xs font-bold rounded-full hover:bg-indigo-700 transition-colors"
                              >
                                Join
                              </button>
                          ) : null}
                        </div>
                      ))
                  )}
                </div>
              </div>
            )}
          </div>

          {/* Tabs */}
          <div className="flex items-center border-b border-slate-200 mb-6">
            <button 
              onClick={() => setActiveTab('post')}
              className={`flex-1 flex items-center justify-center gap-2 py-3 text-sm font-bold border-b-2 transition-colors ${activeTab === 'post' ? 'border-indigo-600 text-indigo-600' : 'border-transparent text-slate-500 hover:bg-slate-50'}`}
            >
              <FileText className="w-5 h-5" /> Post
            </button>
            <button 
              onClick={() => setActiveTab('image')}
              className={`flex-1 flex items-center justify-center gap-2 py-3 text-sm font-bold border-b-2 transition-colors ${activeTab === 'image' ? 'border-indigo-600 text-indigo-600' : 'border-transparent text-slate-500 hover:bg-slate-50'}`}
            >
              <ImageIcon className="w-5 h-5" /> Images & Video
            </button>
            <button 
              onClick={() => setActiveTab('link')}
              className={`flex-1 flex items-center justify-center gap-2 py-3 text-sm font-bold border-b-2 transition-colors ${activeTab === 'link' ? 'border-indigo-600 text-indigo-600' : 'border-transparent text-slate-500 hover:bg-slate-50'}`}
            >
              <LinkIcon className="w-5 h-5" /> Link
            </button>
          </div>

          {/* Form Content */}
          <div className="space-y-4">
            <div className="relative">
              <input
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                maxLength={300}
                placeholder={placeholders[activeTab]} 
                className="w-full px-4 py-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none font-medium text-zinc-900 placeholder:font-normal"
              />
              <span className="absolute right-3 bottom-3 text-xs text-slate-400 font-bold">
                {title.length}/300
              </span>
            </div>

            {activeTab === 'post' && (
              <textarea
                value={body}
                onChange={(e) => setBody(e.target.value)}
                placeholder="Body (optional)"
                className="w-full h-48 px-4 py-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none resize-none align-top"
              />
            )}

            {activeTab === 'image' && (
              <div className="border border-dashed border-slate-300 rounded-xl p-8 bg-slate-50">
                {mediaFiles.length === 0 ? (
                  <div className="text-center">
                    <div className="mx-auto w-12 h-12 bg-white border border-slate-200 rounded-full flex items-center justify-center mb-4">
                      <Upload className="w-6 h-6 text-indigo-600" />
                    </div>
                    <h3 className="text-lg font-bold text-zinc-900 mb-2">Drag and drop images or videos</h3>
                    <p className="text-slate-500 mb-6 text-sm">Or click to select files from your device</p>
                    <button 
                      onClick={() => fileInputRef.current?.click()}
                      className="px-6 py-2 bg-white border border-slate-300 text-slate-700 font-bold rounded-full hover:bg-slate-50 transition-colors"
                    >
                      Upload
                    </button>
                  </div>
                ) : (
                  <div className="space-y-4">
                    <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                      {mediaFiles.map((item, idx) => (
                        <div key={idx} className="relative group aspect-square bg-black rounded-lg overflow-hidden">
                          {item.type === 'video' ? (
                            <video src={item.preview} className="w-full h-full object-cover opacity-80" />
                          ) : (
                            <img src={item.preview} alt="preview" className="w-full h-full object-cover" />
                          )}
                          <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                            <button onClick={() => removeFile(idx)} className="p-2 bg-white rounded-full hover:bg-red-50 text-red-600">
                              <Trash2 className="w-5 h-5" />
                            </button>
                          </div>
                          {item.type === 'video' && <div className="absolute top-2 right-2 p-1 bg-black/50 rounded"><Video className="w-4 h-4 text-white" /></div>}
                        </div>
                      ))}
                      <button 
                        onClick={() => fileInputRef.current?.click()}
                        className="aspect-square border-2 border-dashed border-slate-300 rounded-lg flex flex-col items-center justify-center text-slate-400 hover:text-indigo-600 hover:border-indigo-400 hover:bg-indigo-50 transition-all"
                      >
                        <Plus className="w-8 h-8 mb-2" />
                        <span className="text-sm font-bold">Add</span>
                      </button>
                    </div>
                  </div>
                )}
                <input 
                  type="file" 
                  ref={fileInputRef} 
                  onChange={handleFileChange} 
                  multiple 
                  accept="image/*,video/*" 
                  className="hidden" 
                />
              </div>
            )}

            {activeTab === 'link' && (
              <textarea
                placeholder="Url"
                className="w-full h-24 px-4 py-3 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none resize-none"
              />
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="px-6 py-4 bg-slate-50 border-t border-slate-200 flex justify-end gap-3">
          <button 
            onClick={onClose}
            className="px-6 py-2 border border-slate-300 text-slate-700 font-bold rounded-full hover:bg-white transition-colors"
          >
            Cancel
          </button>
          
          {/* Post button is disabled if fields empty OR if submitting OR if not joined */}
          <button 
            onClick={handleSubmit}
            disabled={!title || isSubmitting || (selectedCommunity && !selectedCommunity.isJoined)}
            className="px-8 py-2 bg-indigo-600 text-white font-bold rounded-full hover:bg-indigo-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            {isSubmitting ? 'Posting...' : 'Post'}
          </button>
        </div>

      </div>
    </div>
  );
};

export default CreatePostModal;