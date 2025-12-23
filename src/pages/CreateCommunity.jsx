import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Upload, X, Plus, Trash2, Image as ImageIcon, Shield } from 'lucide-react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';

const CreateCommunity = () => {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();
  
  // State for Image Previews
  const [avatarPreview, setAvatarPreview] = useState(null);
  const [bannerPreview, setBannerPreview] = useState(null);
  
  // State for Rules
  const [rules, setRules] = useState([]);
  const [currentRule, setCurrentRule] = useState('');

  // Handle Image Selection
  const handleImageChange = (e, setPreview) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  // Handle Rule Addition
  const handleAddRule = (e) => {
    e.preventDefault(); // Prevent form submission
    if (currentRule.trim()) {
      setRules([...rules, currentRule.trim()]);
      setCurrentRule('');
    }
  };

  // Handle Rule Deletion
  const handleDeleteRule = (index) => {
    const newRules = rules.filter((_, i) => i !== index);
    setRules(newRules);
  };

  const onSubmit = async (data) => {
    try {
      // Construct the final payload
      const payload = {
        ...data,
        rules: rules,
        // In a real app, you'd upload the images to S3/Cloudinary here first
        // and send the URLs to your backend.
        avatar: avatarPreview, 
        banner: bannerPreview
      };

      console.log('Submitting Community:', payload);

      // Simulate API Call
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      toast.success(`r/${data.name} created successfully!`);
      navigate(`/r/${data.name}`); // Redirect to new community
    } catch (error) {
      toast.error('Failed to create community.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-300 py-12 px-4 sm:px-6 lg:px-8 animate-in fade-in duration-300">
      <div className="max-w-3xl mx-auto">
        
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold text-zinc-900">Create a Community</h1>
          <p className="text-slate-500 mt-2">Build a new home for your interests</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
          
          {/* 1. DETAILS SECTION */}
          <div className="bg-white shadow-sm border border-slate-200 rounded-xl p-6">
            <h2 className="text-lg font-semibold text-zinc-800 mb-4 border-b border-slate-100 pb-2">Details</h2>
            
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Community Name</label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <span className="text-slate-400 font-bold">r/</span>
                  </div>
                  <input
                    {...register('name', { 
                      required: 'Name is required',
                      minLength: { value: 3, message: 'Name must be at least 3 characters' },
                      pattern: { value: /^[a-zA-Z0-9_]+$/, message: 'Only letters, numbers, and underscores' }
                    })}
                    type="text"
                    className="block w-full pl-8 pr-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none"
                    placeholder="community_name"
                  />
                </div>
                {errors.name && <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>}
                <p className="mt-1 text-xs text-slate-400">Community names cannot be changed.</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Description</label>
                <textarea
                  {...register('description', { required: 'Description is required' })}
                  rows={3}
                  className="block w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none resize-none"
                  placeholder="What is this community about?"
                />
                {errors.description && <p className="mt-1 text-sm text-red-600">{errors.description.message}</p>}
              </div>
            </div>
          </div>

          {/* 2. VISUALS SECTION */}
          <div className="bg-white shadow-sm border border-slate-200 rounded-xl p-6">
            <h2 className="text-lg font-semibold text-zinc-800 mb-4 border-b border-slate-100 pb-2">Visuals</h2>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Avatar Upload */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">Community Icon</label>
                <div className="flex items-center space-x-4">
                  <div className="h-20 w-20 rounded-full border-2 border-dashed border-slate-300 flex items-center justify-center overflow-hidden bg-slate-50 relative group">
                    {avatarPreview ? (
                      <img src={avatarPreview} alt="Preview" className="h-full w-full object-cover" />
                    ) : (
                      <ImageIcon className="h-8 w-8 text-slate-400" />
                    )}
                    <label className="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 cursor-pointer transition-opacity text-white font-xs font-bold">
                        Upload
                       <input type="file" className="hidden" accept="image/*" onChange={(e) => handleImageChange(e, setAvatarPreview)} />
                    </label>
                  </div>
                  <div className="text-sm text-slate-500">
                    <p>Must be an image.</p>
                    <p>Recommended 256x256px.</p>
                  </div>
                </div>
              </div>

              {/* Banner Upload */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">Banner Image</label>
                <div className="h-20 w-full rounded-lg border-2 border-dashed border-slate-300 flex items-center justify-center overflow-hidden bg-slate-50 relative group">
                    {bannerPreview ? (
                      <img src={bannerPreview} alt="Preview" className="h-full w-full object-cover" />
                    ) : (
                      <Upload className="h-8 w-8 text-slate-400" />
                    )}
                    <label className="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 cursor-pointer transition-opacity text-white font-bold">
                        Upload Banner
                       <input type="file" className="hidden" accept="image/*" onChange={(e) => handleImageChange(e, setBannerPreview)} />
                    </label>
                </div>
              </div>
            </div>
          </div>

          {/* 3. RULES SECTION */}
          <div className="bg-white shadow-sm border border-slate-200 rounded-xl p-6">
            <h2 className="text-lg font-semibold text-zinc-800 mb-4 border-b border-slate-100 pb-2">Community Rules</h2>
            
            <div className="space-y-4">
              <div className="flex gap-2">
                <input
                  type="text"
                  value={currentRule}
                  onChange={(e) => setCurrentRule(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleAddRule(e)}
                  className="flex-1 px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none"
                  placeholder="e.g. Be respectful to others"
                />
                <button 
                  type="button"
                  onClick={handleAddRule}
                  className="px-4 py-2 bg-slate-100 text-slate-700 font-medium rounded-lg hover:bg-slate-200 transition-colors"
                >
                  Add
                </button>
              </div>

              {/* Rules List */}
              <div className="space-y-2">
                {rules.length === 0 && (
                  <p className="text-center text-slate-400 py-4 text-sm italic">No rules added yet.</p>
                )}
                {rules.map((rule, index) => (
                  <div key={index} className="flex items-center justify-between p-3 bg-slate-50 rounded-lg border border-slate-100 group">
                    <div className="flex items-center gap-3">
                        <div className="w-6 h-6 rounded-full bg-indigo-100 text-indigo-600 flex items-center justify-center text-xs font-bold">
                            {index + 1}
                        </div>
                        <span className="text-zinc-700 text-sm">{rule}</span>
                    </div>
                    <button 
                        type="button"
                        onClick={() => handleDeleteRule(index)}
                        className="text-slate-400 hover:text-red-500 opacity-0 group-hover:opacity-100 transition-all"
                    >
                        <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* SUBMIT */}
          <div className="flex justify-end gap-4 pt-4">
            <button
              type="button"
              onClick={() => navigate(-1)}
              className="px-6 py-3 border border-slate-300 text-slate-700 font-medium rounded-xl hover:bg-slate-50 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-8 py-3 bg-indigo-600 text-white font-medium rounded-xl hover:bg-indigo-700 transition-colors shadow-sm disabled:opacity-50"
            >
              {isSubmitting ? 'Creating...' : 'Create Community'}
            </button>
          </div>

        </form>
      </div>
    </div>
  );
};

export default CreateCommunity;