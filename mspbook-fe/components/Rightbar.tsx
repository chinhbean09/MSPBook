
import React from 'react';

const SuggestedUser: React.FC<{ name: string; seed: string }> = ({ name, seed }) => (
    <div className="flex items-center justify-between py-2">
        <div className="flex items-center space-x-3">
            <img src={`https://picsum.photos/seed/${seed}/40/40`} alt={name} className="w-10 h-10 rounded-full" />
            <div>
                <p className="font-semibold text-sm text-text-primary">{name}</p>
                <p className="text-xs text-text-secondary">Suggested for you</p>
            </div>
        </div>
        <button className="text-sm font-bold text-primary hover:text-blue-700">Follow</button>
    </div>
);


const Rightbar: React.FC = () => {
    const suggestions = [
        { name: 'Alex Doe', seed: 'alex' },
        { name: 'Jane Smith', seed: 'jane' },
        { name: 'Sam Wilson', seed: 'sam' },
        { name: 'Casey Becker', seed: 'casey' }
    ];

  return (
    <div className="p-4 space-y-6 sticky top-20">
      <div className="bg-card p-4 rounded-lg shadow-sm">
        <div className="flex justify-between items-center mb-4">
            <h2 className="font-bold text-text-secondary">Suggestions For You</h2>
            <a href="#" className="text-sm text-primary hover:underline">See All</a>
        </div>
        <div className="space-y-2">
            {suggestions.map(user => <SuggestedUser key={user.seed} {...user} />)}
        </div>
      </div>
      <div className="text-xs text-gray-500 space-x-2">
        <a href="#" className="hover:underline">About</a>
        <a href="#" className="hover:underline">Help</a>
        <a href="#" className="hover:underline">Privacy</a>
        <a href="#" className="hover:underline">Terms</a>
        <span>© 2024 MSPBook</span>
      </div>
    </div>
  );
};

export default Rightbar;
