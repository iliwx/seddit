import React, { useState, useRef, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { 
  Shield, Settings, MessageSquare, Check, 
  AlertTriangle, BarChart3, Save, Plus, Trash2, ArrowLeft,
  ChevronDown, ChevronUp
} from 'lucide-react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';

// --- MOCK DATABASE (Shared with CommunityPage) ---
const communityDatabase = {
  spotify: {
    name: 'spotify',
    description: 'This subreddit is mainly for sharing Spotify playlists. We are not a support community.',
    members: '342k',
    online: '1.6k',
    rules: ['No tech support questions', 'No illegal content', 'Be respectful', 'No spamming playlists']
  },
  gaming: {
    name: 'gaming',
    description: 'A subreddit for (almost) anything related to games - video games, board games, card games, etc.',
    members: '32.1m',
    online: '142k',
    rules: ['No piracy', 'No NSFW content', 'No hate speech', 'Original content only']
  },
  webdev: {
    name: 'webdev',
    description: 'A community for web developers. Discussion, help, and news about web development.',
    members: '1.4m',
    online: '2.3k',
    rules: ['Be helpful', 'Show your code', 'No self-promotion']
  }
};

// --- HELPER: CUSTOM DROPDOWN COMPONENT ---
const CustomDropdown = ({ label, options, value, onChange }) => {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const selectedOption = options.find(opt => opt.value === value) || options[0];

  return (
    <div className="relative" ref={dropdownRef}>
      <label className="block text-sm font-medium text-slate-700 mb-2">{label}</label>
      <button
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        className={`w-full flex items-center justify-between px-4 py-2.5 bg-white border rounded-lg text-left transition-all ${
          isOpen 
            ? 'border-indigo-500 ring-2 ring-indigo-500/20' 
            : 'border-slate-300 hover:border-slate-400'
        }`}
      >
        <span className="text-zinc-700 block truncate">{selectedOption.label}</span>
        {isOpen ? <ChevronUp className="w-4 h-4 text-slate-500" /> : <ChevronDown className="w-4 h-4 text-slate-500" />}
      </button>

      {isOpen && (
        <div className="absolute z-50 w-full mt-1 bg-white border border-slate-200 rounded-lg shadow-xl animate-in fade-in slide-in-from-top-2 duration-200">
          <ul className="py-1 max-h-60 overflow-auto">
            {options.map((option) => (
              <li key={option.value}>
                <button
                  type="button"
                  onClick={() => {
                    onChange(option.value);
                    setIsOpen(false);
                  }}
                  className={`w-full text-left px-4 py-2.5 text-sm flex items-center justify-between group transition-colors ${
                    value === option.value 
                      ? 'bg-indigo-50 text-indigo-700 font-medium' 
                      : 'text-zinc-700 hover:bg-slate-50'
                  }`}
                >
                  <span>{option.label}</span>
                  {value === option.value && <Check className="w-4 h-4 text-indigo-600" />}
                </button>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

// --- SUB-COMPONENT: STATISTICS OVERVIEW ---
const ModStats = ({ data }) => (
  <div className="space-y-6 animate-in fade-in duration-300">
    <h2 className="text-2xl font-bold text-zinc-800">Community Overview</h2>
    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
        <div className="flex justify-between items-start">
          <div>
            <p className="text-sm font-medium text-slate-500">Total Members</p>
            {/* Dynamic Data */}
            <h3 className="text-2xl font-bold text-zinc-900 mt-1">{data.members}</h3>
          </div>
          <div className="p-2 bg-indigo-50 rounded-lg text-indigo-600"><BarChart3 className="w-5 h-5" /></div>
        </div>
        <div className="mt-4 text-xs text-green-600 font-medium"> +1.2% this week</div>
      </div>
      
      <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
        <div className="flex justify-between items-start">
          <div>
            <p className="text-sm font-medium text-slate-500">Active Now</p>
             {/* Dynamic Data */}
            <h3 className="text-2xl font-bold text-zinc-900 mt-1">{data.online}</h3>
          </div>
          <div className="p-2 bg-green-50 rounded-lg text-green-600"><BarChart3 className="w-5 h-5" /></div>
        </div>
        <div className="mt-4 text-xs text-slate-500 font-medium"> Current online users</div>
      </div>

      <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
        <div className="flex justify-between items-start">
          <div>
            <p className="text-sm font-medium text-slate-500">Reports Today</p>
            <h3 className="text-2xl font-bold text-zinc-900 mt-1">5</h3>
          </div>
          <div className="p-2 bg-red-50 rounded-lg text-red-600"><Shield className="w-5 h-5" /></div>
        </div>
        <div className="mt-4 text-xs text-red-600 font-medium"> Action required</div>
      </div>
    </div>
  </div>
);

// --- SUB-COMPONENT: MODERATION QUEUE ---
const ModQueue = () => {
  // In a real app, you would fetch this based on the community ID
  const [items, setItems] = useState([
    { id: 1, type: 'post', author: 'spammer_123', content: 'Buy cheap watches here! [link]', reason: 'Spam', time: '2 hours ago' },
    { id: 2, type: 'comment', author: 'angry_guy', content: 'You are an idiot.', reason: 'Harassment', time: '5 hours ago' },
  ]);

  const handleAction = (id, action) => {
    setItems(items.filter(i => i.id !== id));
    toast.success(action === 'approve' ? 'Item approved' : 'Item removed');
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-300">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-zinc-800">Moderation Queue</h2>
        <span className="bg-slate-100 text-slate-600 px-3 py-1 rounded-full text-sm font-medium">{items.length} Pending</span>
      </div>

      {items.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-xl border border-slate-200 border-dashed">
          <Check className="w-12 h-12 text-green-500 mx-auto mb-3" />
          <h3 className="text-lg font-medium text-zinc-900">All caught up!</h3>
          <p className="text-slate-500">The queue is empty.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {items.map((item) => (
            <div key={item.id} className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm">
              <div className="flex justify-between items-start mb-3">
                <div className="flex items-center gap-2">
                  <span className={`px-2 py-0.5 text-xs font-bold rounded uppercase ${item.type === 'post' ? 'bg-blue-100 text-blue-700' : 'bg-slate-100 text-slate-700'}`}>
                    {item.type}
                  </span>
                  <span className="text-sm text-slate-500">u/{item.author} • {item.time}</span>
                </div>
                <div className="flex items-center gap-1 text-red-600 bg-red-50 px-2 py-1 rounded text-xs font-bold">
                  <AlertTriangle className="w-3 h-3" />
                  Reason: {item.reason}
                </div>
              </div>
              <p className="text-zinc-800 font-medium mb-4 p-3 bg-slate-50 rounded-lg border border-slate-100">
                "{item.content}"
              </p>
              <div className="flex gap-3 justify-end">
                <button onClick={() => handleAction(item.id, 'approve')} className="flex items-center gap-2 px-4 py-2 text-green-700 bg-green-50 hover:bg-green-100 rounded-lg font-medium transition-colors">
                  <Check className="w-4 h-4" /> Approve
                </button>
                <button onClick={() => handleAction(item.id, 'remove')} className="flex items-center gap-2 px-4 py-2 text-red-700 bg-red-50 hover:bg-red-100 rounded-lg font-medium transition-colors">
                  <Trash2 className="w-4 h-4" /> Remove
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

// --- SUB-COMPONENT: SETTINGS FORM ---
const CommunitySettings = ({ initialData }) => {
  const { register, handleSubmit, setValue, watch, reset } = useForm({
    defaultValues: {
      description: '',
      type: 'public',
      spamFilter: 'high'
    }
  });

  const [rules, setRules] = useState([]);
  const [newRule, setNewRule] = useState('');

  // 1. DYNAMIC DATA LOADING
  // When 'initialData' changes (user switches community), update form fields
  useEffect(() => {
    if (initialData) {
      reset({
        description: initialData.description,
        type: 'public', // In a real app, this would come from DB
        spamFilter: 'high' // In a real app, this would come from DB
      });
      setRules(initialData.rules || []);
    }
  }, [initialData, reset]);

  const currentType = watch('type');
  const currentFilter = watch('spamFilter');

  const addRule = () => {
    if (newRule.trim()) {
      setRules([...rules, newRule]);
      setNewRule('');
    }
  };

  const removeRule = (idx) => {
    setRules(rules.filter((_, i) => i !== idx));
  };

  const onSubmit = (data) => {
    console.log({ ...data, rules });
    toast.success('Settings updated successfully');
  };

  return (
    <div className="animate-in fade-in duration-300">
      <h2 className="text-2xl font-bold text-zinc-800 mb-6">Community Settings</h2>
      
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-4">
          <h3 className="font-semibold text-lg text-zinc-800 border-b pb-2">Details</h3>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Description</label>
            <textarea 
              {...register('description')}
              rows={3} 
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-indigo-500 outline-none transition-all resize-none"
            />
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
             <CustomDropdown 
                label="Community Type"
                value={currentType}
                onChange={(val) => setValue('type', val, { shouldDirty: true })}
                options={[
                    { value: 'public', label: 'Public (Everyone can view & post)' },
                    { value: 'restricted', label: 'Restricted (Everyone view, approved post)' },
                    { value: 'private', label: 'Private (Invite only)' }
                ]}
             />
             <CustomDropdown 
                label="Spam Filter Strength"
                value={currentFilter}
                onChange={(val) => setValue('spamFilter', val, { shouldDirty: true })}
                options={[
                    { value: 'low', label: 'Low' },
                    { value: 'high', label: 'High (Recommended)' },
                    { value: 'all', label: 'Hold All Posts' }
                ]}
             />
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm space-y-4">
          <h3 className="font-semibold text-lg text-zinc-800 border-b pb-2">Rules</h3>
          <div className="flex gap-2">
            <input 
              value={newRule}
              onChange={(e) => setNewRule(e.target.value)}
              placeholder="Add a new rule..."
              className="flex-1 px-3 py-2 border border-slate-300 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition-all"
            />
            <button type="button" onClick={addRule} className="px-4 py-2 bg-slate-100 font-bold text-slate-600 rounded-lg hover:bg-slate-200 transition-colors">
                <Plus className="w-4 h-4" />
            </button>
          </div>
          <div className="space-y-2">
            {rules.map((rule, i) => (
                <div key={i} className="flex justify-between items-center p-3 bg-slate-50 border border-slate-100 rounded-lg hover:border-slate-200 transition-colors">
                    <span className="text-sm font-medium text-zinc-700">{i+1}. {rule}</span>
                    <button type="button" onClick={() => removeRule(i)} className="text-slate-400 hover:text-red-500 transition-colors">
                        <Trash2 className="w-4 h-4" />
                    </button>
                </div>
            ))}
          </div>
        </div>

        <div className="flex justify-end">
             <button type="submit" className="flex items-center gap-2 px-6 py-3 bg-indigo-600 text-white font-bold rounded-xl hover:bg-indigo-700 shadow-sm hover:shadow transition-all">
                <Save className="w-4 h-4" /> Save Changes
             </button>
        </div>
      </form>
    </div>
  );
};

// --- MAIN PAGE LAYOUT ---
const ModDashboard = () => {
  const { name } = useParams();
  const [activeTab, setActiveTab] = useState('overview');

  // 2. LOAD DATA BASED ON URL PARAM
  const currentCommunityData = communityDatabase[name] || communityDatabase['spotify'];

  const navItems = [
    { id: 'overview', label: 'Overview', icon: BarChart3 },
    { id: 'queue', label: 'Mod Queue', icon: Shield },
    { id: 'settings', label: 'Settings', icon: Settings },
  ];

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col md:flex-row">
      <aside className="w-full md:w-64 bg-white border-r border-slate-200 md:min-h-screen sticky top-0 z-10">
        <div className="p-6 border-b border-slate-100">
            <Link to={`/r/${name}`} className="flex items-center text-sm text-slate-500 hover:text-indigo-600 mb-4 transition-colors">
                <ArrowLeft className="w-4 h-4 mr-1" /> Back to r/{name}
            </Link>
            <h1 className="text-xl font-bold text-zinc-900">Mod Tools</h1>
            <p className="text-xs text-slate-400 mt-1">Manage r/{name}</p>
        </div>
        <nav className="p-4 space-y-1">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => setActiveTab(item.id)}
                className={`w-full flex items-center space-x-3 px-4 py-3 rounded-lg text-sm font-medium transition-all ${
                  isActive ? 'bg-indigo-50 text-indigo-600' : 'text-slate-600 hover:bg-slate-50 hover:text-zinc-900'
                }`}
              >
                <Icon className={`w-5 h-5 ${isActive ? 'text-indigo-600' : 'text-slate-400'}`} />
                <span>{item.label}</span>
              </button>
            );
          })}
        </nav>
      </aside>

      <main className="flex-1 p-6 md:p-8 lg:p-12 overflow-y-auto">
        <div className="max-w-4xl mx-auto">
          {/* 3. PASS DATA TO CHILDREN */}
          {activeTab === 'overview' && <ModStats data={currentCommunityData} />}
          {activeTab === 'queue' && <ModQueue />}
          {activeTab === 'settings' && <CommunitySettings initialData={currentCommunityData} />}
        </div>
      </main>
    </div>
  );
};

export default ModDashboard;