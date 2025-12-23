import React from 'react';
import { User, Settings, Sliders, Shield, Bell, Eye, MessageSquare, Bookmark, Clock, BarChart3 } from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';

const DashboardSidebar = () => {
  const location = useLocation();

  const menuSections = [
    {
      title: 'Account',
      items: [
        { icon: User, label: 'Profile', path: '/dashboard/profile' },
        { icon: Settings, label: 'Account Settings', path: '/dashboard/settings' },
        { icon: Sliders, label: 'Preferences', path: '/dashboard/preferences' },
        { icon: Shield, label: 'Privacy & Safety', path: '/dashboard/privacy' },
        { icon: Bell, label: 'Notifications', path: '/dashboard/notifications' },
      ]
    },
    {
      title: 'Content',
      items: [
        { icon: MessageSquare, label: 'Posts', path: '/dashboard/posts' },
        { icon: MessageSquare, label: 'Comments', path: '/dashboard/comments' },
        { icon: Bookmark, label: 'Saved', path: '/dashboard/saved' },
        { icon: Clock, label: 'History', path: '/dashboard/history' },
      ]
    },
    {
      title: 'Moderation',
      items: [
        { icon: Shield, label: 'Moderated Communities', path: '/dashboard/moderated' },
        { icon: BarChart3, label: 'Mod Tools', path: '/dashboard/mod-tools' },
        { icon: Eye, label: 'Content Policy', path: '/dashboard/content-policy' },
      ]
    }
  ];

  return (
    <aside className="w-64 bg-gray-300 border-r border-slate-200 h-screen sticky top-12 overflow-y-auto">
      <div className="p-4">
        <div className="mb-6">
          <h2 className="text-lg font-semibold text-zinc-800 mb-2">User Settings</h2>
          <p className="text-sm text-slate-500">Manage your account and preferences</p>
        </div>

        {menuSections.map((section) => (
          <div key={section.title} className="mb-8">
            <h3 className="px-3 text-xs font-semibold text-slate-400 uppercase tracking-wider mb-3">
              {section.title}
            </h3>
            <nav className="space-y-1">
              {section.items.map((item) => {
                const Icon = item.icon;
                const isActive = location.pathname === item.path;
                return (
                  <Link
                    key={item.path}
                    to={item.path}
                    className={`flex items-center px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                      isActive
                        ? 'bg-indigo-50 text-indigo-600 border-r-2 border-indigo-600'
                        : 'text-slate-600 hover:bg-slate-100 hover:text-zinc-800'
                    }`}
                  >
                    <Icon className="w-5 h-5 mr-3" />
                    {item.label}
                  </Link>
                );
              })}
            </nav>
          </div>
        ))}
      </div>
    </aside>
  );
};

export default DashboardSidebar;
