
import React, { useState } from 'react';
import { PhotographIcon, UserAddIcon, EmojiHappyIcon } from './Icons';

interface CreatePostProps {
  onPost: (text: string) => void;
}

const CreatePost: React.FC<CreatePostProps> = ({ onPost }) => {
  const [postText, setPostText] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (postText.trim()) {
      onPost(postText.trim());
      setPostText('');
    }
  };

  return (
    <div className="bg-card p-4 rounded-lg shadow-sm mb-4">
      <div className="flex space-x-3">
        <img src="https://picsum.photos/seed/you/40/40" alt="Your avatar" className="w-10 h-10 rounded-full" />
        <textarea
          value={postText}
          onChange={(e) => setPostText(e.target.value)}
          className="w-full bg-background rounded-full py-2 px-4 focus:outline-none placeholder-text-secondary resize-none"
          placeholder="What's on your mind?"
          rows={1}
        ></textarea>
      </div>
      <hr className="my-3 border-divider" />
      <div className="flex justify-between items-center">
        <div className="flex space-x-2">
          <button className="flex items-center space-x-2 px-3 py-2 rounded-md hover:bg-gray-100 text-text-secondary font-semibold">
            <PhotographIcon className="h-6 w-6 text-green-500" />
            <span>Photo/video</span>
          </button>
          <button className="flex items-center space-x-2 px-3 py-2 rounded-md hover:bg-gray-100 text-text-secondary font-semibold">
            <UserAddIcon className="h-6 w-6 text-blue-500" />
            <span>Tag friends</span>
          </button>
          <button className="flex items-center space-x-2 px-3 py-2 rounded-md hover:bg-gray-100 text-text-secondary font-semibold">
            <EmojiHappyIcon className="h-6 w-6 text-yellow-500" />
            <span>Feeling/activity</span>
          </button>
        </div>
        <button
          onClick={handleSubmit}
          disabled={!postText.trim()}
          className="bg-primary text-white font-bold py-2 px-6 rounded-lg hover:bg-blue-600 transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed"
        >
          Post
        </button>
      </div>
    </div>
  );
};

export default CreatePost;
