import React, { useState } from 'react';
import { Search, Plus, Bell, MessageSquare, User, Menu, X } from 'lucide-react';
import { Link } from 'react-router-dom';

const Header = () => {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  return (
    <header className="bg-white border-b border-slate-200 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-12">
          {/* Logo */}
          <div className="flex items-center">
            <Link to="/" className="flex items-center space-x-2">
              <div className="w-8 h-8 bg-indigo-600 rounded-full flex items-center justify-center">
                <span className="text-white font-bold text-sm">R</span>
              </div>
              <span className="text-xl font-bold text-zinc-800 hidden sm:block">Seddit</span>
            </Link>
          </div>

          {/* Search Bar */}
          <div className="flex-1 max-w-2xl mx-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-4 h-4" />
              <input
                type="text"
                placeholder="Search Seddit"
                className="w-full pl-10 pr-4 py-2 bg-slate-100 border border-slate-200 rounded-full text-sm focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:border-transparent"
              />
            </div>
          </div>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-4">
            <Link 
              to="/create-community" 
              className="p-2 text-slate-400 hover:text-zinc-800 rounded-lg hover:bg-slate-100"
              title="Create Community"
            >
              <Plus className="w-5 h-5" />
            </Link>
            <button className="p-2 text-slate-400 hover:text-zinc-800 rounded-lg hover:bg-slate-100">
              <Bell className="w-5 h-5" />
            </button>
            <button className="p-2 text-slate-400 hover:text-zinc-800 rounded-lg hover:bg-slate-100">
              <MessageSquare className="w-5 h-5" />
            </button>
            <Link to="/dashboard" className="p-2 text-slate-400 hover:text-zinc-800 rounded-lg hover:bg-slate-100">
              <User className="w-5 h-5" />
            </Link>
          </div>

          {/* Mobile Menu Button */}
          <button
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            className="md:hidden p-2 text-slate-400 hover:text-zinc-800"
          >
            {isMobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>

        {/* Mobile Menu */}
        {isMobileMenuOpen && (
          <div className="md:hidden py-4 border-t border-slate-200">
            <nav className="flex flex-col space-y-2">
              <Link to="/" className="px-3 py-2 text-zinc-800 hover:bg-slate-100 rounded-lg">Home</Link>
              <Link to="/popular" className="px-3 py-2 text-zinc-800 hover:bg-slate-100 rounded-lg">Popular</Link>
              <Link to="/dashboard" className="px-3 py-2 text-zinc-800 hover:bg-slate-100 rounded-lg">Dashboard</Link>
            </nav>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;
