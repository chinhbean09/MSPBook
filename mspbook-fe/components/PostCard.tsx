
import React from 'react';
import { Post } from '../types';
import { ThumbUpIcon, ChatAltIcon, ShareIcon } from './Icons';

interface PostCardProps {
  post: Post;
}

const PostCard: React.FC<PostCardProps> = ({ post }) => {
    const timeAgo = (dateString: string) => {
        const date = new Date(dateString);
        const seconds = Math.floor((new Date().getTime() - date.getTime()) / 1000);
        let interval = seconds / 31536000;
        if (interval > 1) return Math.floor(interval) + "y";
        interval = seconds / 2592000;
        if (interval > 1) return Math.floor(interval) + "mo";
        interval = seconds / 86400;
        if (interval > 1) return Math.floor(interval) + "d";
        interval = seconds / 3600;
        if (interval > 1) return Math.floor(interval) + "h";
        interval = seconds / 60;
        if (interval > 1) return Math.floor(interval) + "m";
        return Math.floor(seconds) + "s";
    };

  return (
    <div className="bg-card p-4 rounded-lg shadow-sm">
      {/* Post Header */}
      <div className="flex items-center mb-3">
        <img src={post.author.avatarUrl} alt={post.author.name} className="w-10 h-10 rounded-full" />
        <div className="ml-3">
          <p className="font-bold text-text-primary">{post.author.name}</p>
          <p className="text-xs text-text-secondary">{timeAgo(post.timestamp)} ago</p>
        </div>
      </div>

      {/* Post Content */}
      <p className="mb-3 text-text-primary whitespace-pre-wrap">{post.text}</p>
      {post.imageUrl && (
        <div className="my-3 -mx-4">
          <img src={post.imageUrl} alt="Post content" className="w-full object-cover" />
        </div>
      )}

      {/* Post Stats */}
      <div className="flex justify-between items-center text-text-secondary text-sm py-2">
        <div>{post.likes > 0 && `${post.likes} likes`}</div>
        <div>{post.comments.length > 0 && `${post.comments.length} comments`}</div>
      </div>
      
      <hr className="border-divider" />

      {/* Post Actions */}
      <div className="flex justify-around items-center pt-2 text-text-secondary font-semibold">
        <button className="flex items-center space-x-2 py-2 px-4 rounded-md hover:bg-gray-100 w-full justify-center">
          <ThumbUpIcon className="h-5 w-5" />
          <span>Like</span>
        </button>
        <button className="flex items-center space-x-2 py-2 px-4 rounded-md hover:bg-gray-100 w-full justify-center">
          <ChatAltIcon className="h-5 w-5" />
          <span>Comment</span>
        </button>
        <button className="flex items-center space-x-2 py-2 px-4 rounded-md hover:bg-gray-100 w-full justify-center">
          <ShareIcon className="h-5 w-5" />
          <span>Share</span>
        </button>
      </div>

      <hr className="border-divider my-2" />
      
      {/* Comment Section */}
      <div className="space-y-2">
          {post.comments.map(comment => (
              <div key={comment.id} className="flex space-x-2">
                  <img src={comment.author.avatarUrl} alt={comment.author.name} className="w-8 h-8 rounded-full mt-1" />
                  <div className="bg-background rounded-xl p-2">
                      <p className="font-bold text-sm">{comment.author.name}</p>
                      <p className="text-sm">{comment.text}</p>
                  </div>
              </div>
          ))}
          <div className="flex space-x-2 items-center pt-2">
            <img src="https://picsum.photos/seed/you/32/32" alt="Your avatar" className="w-8 h-8 rounded-full" />
            <input type="text" placeholder="Write a comment..." className="w-full bg-background rounded-full py-1.5 px-4 focus:outline-none" />
          </div>
      </div>

    </div>
  );
};

export default PostCard;
