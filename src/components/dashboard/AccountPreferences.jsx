import React from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { Globe, Moon, Eye, Bell, Shield, Smartphone, Save } from 'lucide-react';

const AccountPreferences = () => {
  // Initialize form with default values (simulating existing user settings)
  const { register, handleSubmit, formState: { isDirty, isSubmitting } } = useForm({
    defaultValues: {
      language: 'en',
      theme: 'light',
      showOnlineStatus: true,
      allowSearchIndexing: false,
      autoplayMedia: true,
      emailDigest: 'weekly',
      pushNotifications: true,
      marketingEmails: false
    }
  });

  const onSubmit = async (data) => {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 800));
    console.log('Preferences Updated:', data);
    toast.success('Preferences saved successfully');
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6 animate-in fade-in duration-300">
      
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-zinc-800">Preferences</h2>
          <p className="text-slate-500">Customize how Seddit looks and behaves for you.</p>
        </div>
        {/* Save button appears at top too for easy access */}
        <button
          onClick={handleSubmit(onSubmit)}
          disabled={!isDirty || isSubmitting}
          className="hidden md:flex items-center space-x-2 px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <Save className="w-4 h-4" />
          <span>{isSubmitting ? 'Saving...' : 'Save Changes'}</span>
        </button>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        
        {/* Display & Language */}
        <div className="bg-gray-300 border border-slate-200 rounded-xl overflow-hidden shadow-sm">
          <div className="p-4 border-b border-slate-100 bg-slate-50 flex items-center gap-3">
            <Globe className="w-5 h-5 text-indigo-600" />
            <h3 className="font-semibold text-zinc-800">Display & Language</h3>
          </div>
          <div className="p-6 space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">Interface Language</label>
                <select 
                  {...register('language')}
                  className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none bg-white"
                >
                  <option value="en">English (US)</option>
                  <option value="es">Español</option>
                  <option value="fr">Français</option>
                  <option value="de">Deutsch</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">Color Theme</label>
                <div className="relative">
                  <select 
                    {...register('theme')}
                    className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none bg-white"
                  >
                    <option value="light">Light Mode</option>
                    <option value="dark">Dark Mode</option>
                    <option value="system">Sync with System</option>
                  </select>
                  <Moon className="absolute right-3 top-2.5 w-4 h-4 text-slate-400 pointer-events-none" />
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Content Visibility */}
        <div className="bg-gray-300 border border-slate-200 rounded-xl overflow-hidden shadow-sm">
          <div className="p-4 border-b border-slate-100 bg-slate-50 flex items-center gap-3">
            <Eye className="w-5 h-5 text-indigo-600" />
            <h3 className="font-semibold text-zinc-800">Content Visibility</h3>
          </div>
          <div className="p-6 space-y-4 divide-y divide-slate-100">
            {/* Toggle Item */}
            <div className="flex items-center justify-between py-2">
              <div>
                <p className="font-medium text-zinc-800">Online Status</p>
                <p className="text-sm text-slate-500">Let others know when you are active.</p>
              </div>
              <label className="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" {...register('showOnlineStatus')} className="sr-only peer" />
                <div className="w-11 h-6 bg-slate-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-indigo-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-indigo-600"></div>
              </label>
            </div>

            {/* Toggle Item */}
            <div className="flex items-center justify-between py-2 pt-4">
              <div>
                <p className="font-medium text-zinc-800">Search Engine Indexing</p>
                <p className="text-sm text-slate-500">Allow search engines like Google to show your profile.</p>
              </div>
              <label className="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" {...register('allowSearchIndexing')} className="sr-only peer" />
                <div className="w-11 h-6 bg-slate-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-indigo-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-indigo-600"></div>
              </label>
            </div>

            {/* Toggle Item */}
            <div className="flex items-center justify-between py-2 pt-4">
              <div>
                <p className="font-medium text-zinc-800">Autoplay Media</p>
                <p className="text-sm text-slate-500">Videos and GIFs will play automatically.</p>
              </div>
              <label className="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" {...register('autoplayMedia')} className="sr-only peer" />
                <div className="w-11 h-6 bg-slate-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-indigo-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-indigo-600"></div>
              </label>
            </div>
          </div>
        </div>

        {/* Notifications */}
        <div className="bg-gray-300 border border-slate-200 rounded-xl overflow-hidden shadow-sm">
          <div className="p-4 border-b border-slate-100 bg-slate-50 flex items-center gap-3">
            <Bell className="w-5 h-5 text-indigo-600" />
            <h3 className="font-semibold text-zinc-800">Notifications</h3>
          </div>
          <div className="p-6 space-y-4">
             <div className="grid grid-cols-1 md:grid-cols-2 gap-6 items-center">
                <div>
                   <p className="font-medium text-zinc-800 mb-1">Email Digest</p>
                   <p className="text-sm text-slate-500">How often do you want to receive summaries?</p>
                </div>
                <select 
                  {...register('emailDigest')}
                  className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none bg-white"
                >
                  <option value="daily">Daily</option>
                  <option value="weekly">Weekly</option>
                  <option value="never">Never</option>
                </select>
             </div>
             
             <div className="border-t border-slate-100 pt-4 flex items-center justify-between">
                <div>
                    <p className="font-medium text-zinc-800">Push Notifications</p>
                    <p className="text-sm text-slate-500">Receive notifications on this device.</p>
                </div>
                 <label className="relative inline-flex items-center cursor-pointer">
                <input type="checkbox" {...register('pushNotifications')} className="sr-only peer" />
                <div className="w-11 h-6 bg-slate-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-indigo-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-indigo-600"></div>
              </label>
             </div>
          </div>
        </div>

        {/* Mobile Save Button (Visible only on small screens) */}
        <div className="md:hidden">
          <button
            type="submit"
            disabled={!isDirty || isSubmitting}
            className="w-full flex justify-center items-center space-x-2 px-4 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors shadow-sm disabled:opacity-50"
          >
            <Save className="w-4 h-4" />
            <span>Save Preferences</span>
          </button>
        </div>

      </form>
    </div>
  );
};

export default AccountPreferences;