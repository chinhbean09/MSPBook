
import React from 'react';
import { HomeIcon, CompassIcon, BellIcon, MailIcon, UserIcon, BookmarkIcon } from './Icons';

interface NavItemProps {
  icon: React.ReactNode;
  label: string;
  active?: boolean;
}

const NavItem: React.FC<NavItemProps> = ({ icon, label, active = false }) => {
  const activeClasses = active ? 'text-primary font-bold' : 'text-gray-700 hover:bg-gray-200';
  return (
    <a href="#" className={`flex items-center space-x-3 p-3 rounded-lg transition-colors ${activeClasses}`}>
      {icon}
      <span className="text-lg">{label}</span>
    </a>
  );
};


const Sidebar: React.FC = () => {
  return (
    <div className="p-4 sticky top-20">
      <nav className="space-y-2">
        <NavItem icon={<HomeIcon className="h-7 w-7" />} label="Home" active />
        <NavItem icon={<CompassIcon className="h-7 w-7" />} label="Explore" />
        <NavItem icon={<BellIcon className="h-7 w-7" />} label="Notifications" />
        <NavItem icon={<MailIcon className="h-7 w-7" />} label="Messages" />
        <NavItem icon={<BookmarkIcon className="h-7 w-7" />} label="Bookmarks" />
        <NavItem icon={<UserIcon className="h-7 w-7" />} label="Profile" />
      </nav>
      <button className="mt-4 w-full bg-primary text-white font-bold py-3 px-4 rounded-full hover:bg-blue-600 transition-colors">
        Post
      </button>
    </div>
  );
};

export default Sidebar;
