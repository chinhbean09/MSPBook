
import React from 'react';
import { SearchIcon, BellIcon, ChatIcon } from './Icons';

const Header: React.FC = () => {
  return (
    <header className="bg-card shadow-md fixed top-0 left-0 right-0 z-50">
      <div className="max-w-screen-2xl mx-auto px-4 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Left Section */}
          <div className="flex items-center space-x-4">
            <h1 className="text-2xl font-bold text-primary">MSPBook</h1>
            <div className="relative hidden md:block">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <SearchIcon className="h-5 w-5 text-gray-400" />
              </div>
              <input
                type="text"
                placeholder="Search MSPBook"
                className="bg-background rounded-full py-2 pl-10 pr-4 w-64 focus:outline-none focus:ring-2 focus:ring-primary"
              />
            </div>
          </div>

          {/* Center Section (for larger screens) */}
          <div className="hidden lg:flex items-center space-x-2">
            {/* Add main navigation icons here if needed */}
          </div>

          {/* Right Section */}
          <div className="flex items-center space-x-2">
            <button className="h-10 w-10 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300 transition-colors">
              <ChatIcon className="h-5 w-5 text-gray-700" />
            </button>
            <button className="h-10 w-10 bg-gray-200 rounded-full flex items-center justify-center hover:bg-gray-300 transition-colors">
              <BellIcon className="h-5 w-5 text-gray-700" />
            </button>
            <div className="h-10 w-10 rounded-full bg-gray-300 overflow-hidden">
              <img src="https://picsum.photos/seed/you/40/40" alt="User Avatar" />
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
