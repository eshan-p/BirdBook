import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import PostCard from '../components/features/PostCard'
import ProfileCard from '../components/features/ProfileCard'
import ProfileIcon from '../components/common/ProfileIcon'
import { Post } from '../types/Post'
import { Group } from '../types/Group'
import { parseDate } from '../utils/dateTime'
import { getSightingsByGroup } from '../api/Sightings'
import { getAllGroups, getUserGroups } from '../api/Groups'
import { useAuth } from '../context/AuthContext'
import { getUserById } from '../api/Users'
import { User } from '../types/User'
import SearchBar from '../components/common/SearchBar'
import BirdCard from '../components/features/BirdCard'
import { Bird } from '../types/Bird'
import CreatePost from '../components/features/CreatePost'
import { getAllBirds } from '../api/Birds'
import FriendCard from '../components/features/FriendCard'
import { Friend } from '../types/Friend'
import GroupCard from '../components/features/GroupCard'

const PAGE_SIZE = 5;

function GroupFeed() {
  const { groupId } = useParams<{ groupId: string }>();
  const { user, loading } = useAuth();
  const [userData, setUserData] = useState<User | null>(null);
  const [group, setGroup] = useState<Group | null>(null);
  const [posts, setPosts] = useState<Post[]>([]);
  const [loadingPage, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [birds, setBirds] = useState<Bird[]>([]);
  const [friends, setFriends] = useState<Friend[]>([]);
  const [groups, setGroups] = useState<Group[]>([]);
  const BASE_URL = "http://localhost:8080";
  const navigate = useNavigate();

  // Fetch full user data
  useEffect(() => {
    if (user?.id) {
      getUserById(user.id)
        .then(setUserData)
        .catch(console.error);
      
      fetch(`${BASE_URL}/${user.id}/friends`, {credentials: 'include'})
        .then(r => r.json())
        .then(setFriends)
        .catch(err => console.error("Failed to fetch user: " + err));
    }
  }, [user?.id]);

  useEffect(() => {
    if(user?.id) {
        getUserGroups(user.id)
            .then(setGroups)
            .catch(err => console.error("Failed to fetch groups:", err));
    }
  }, [user?.id]);

  useEffect(() => {
    getAllBirds()
      .then(setBirds)
      .catch(err => console.error("Failed to fetch birds:", err));
  }, []);

  // Fetch group details
  useEffect(() => {
    if (!groupId) return;
    
    getAllGroups()
      .then(groups => {
        const foundGroup = groups.find(g => g.id === groupId);
        setGroup(foundGroup || null);
      })
      .catch(err => console.error("Failed to fetch group:", err));
  }, [groupId]);

  // Fetch posts for this group
  useEffect(() => {
    if (!groupId) return;

    console.log('Fetching posts for group:', groupId);
    
    getSightingsByGroup(groupId)
      .then(posts => {
        console.log('Fetched posts:', posts);
        setPosts(posts);
      })
      .catch(err => {
        console.error('Error fetching posts:', err);
        setError(err.message);
      })
      .finally(() => setLoading(false));
  }, [groupId]);

  // Reset page when posts change
  useEffect(() => {
    setPage(0);
  }, [posts]);

  // Redirect if not logged in
  useEffect(() => {
    if (!user && !loading) {
      navigate("/login");
    }
  }, [user, loading, navigate]);

  const totalPages = Math.ceil(posts.length / PAGE_SIZE);
  const pagedPosts = posts.slice(
    page * PAGE_SIZE,
    (page + 1) * PAGE_SIZE
  );

  if (loadingPage) return <p>Loading...</p>;

  return (
    <div className='flex flex-row h-full bg-[#F7F7F7] px-16'>
      {/* Left Sidebar */}
      <div className='flex flex-col basis-1/4 m-6 mr-0'>
        <ProfileCard user={userData || undefined}/>
        
        {/* Group Info */}
        {group && (
          <div className='h-fit w-full mt-6 bg-white p-4 drop-shadow'>
            <div className='flex flex-row w-full border-b border-gray-300 mb-3'>
              <p className='text-lg font-bold'>{group.name}</p>
            </div>
            <p className='text-sm text-gray-600 mb-2'>
              {group.members?.length || 0} members
            </p>
            {group.followers && (
              <p className='text-sm text-gray-600 mb-3'>
                {group.followers} followers
              </p>
            )}
            
            {/* Members List */}
            {group.members && group.members.length > 0 && (
              <div className='mt-3 border-t border-gray-300 pt-3'>
                <p className='text-sm font-semibold mb-2'>Members:</p>
                <ul className='text-sm text-gray-700 space-y-2'>
                  {group.members.map((member, index) => (
                    <li 
                      key={member.userId || `member-${index}`} 
                      className='flex items-center gap-2 hover:bg-gray-50 p-1 rounded cursor-pointer'
                      onClick={() => member.userId && navigate(`/profile/${member.userId}`)}
                    >
                      <ProfileIcon size="sm" src={member.profilePic} />
                      <span className='truncate'>{member.username || 'Unknown User'}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Main Feed */}
      <div className='basis-1/2 m-6'>
        <div className='flex flex-col'>
          <CreatePost/>
          {error && (
            <div className="mb-4 p-4 bg-red-100 text-red-700 rounded">
              Error: {error}
            </div>
          )}
          
          {posts.length === 0 ? (
            <div className='bg-white p-6 text-center text-gray-500'>
              No posts yet in this group
            </div>
          ) : (
            pagedPosts.map(post => (
              <div key={post.id?.toString()}>
                <PostCard
                  description={post.header}
                  author={post.user.username}
                  dateTime={parseDate(post.timestamp)}
                  location={post.tags?.location}
                  likes={post.likes.length}
                  comments={post.comments.length}
                />
                <button
                  onClick={() => navigate(`/sightings/${post.id.toString()}`)}
                  className="mt-2"
                >
                  Click
                </button>
              </div>
            ))
          )}
        </div>
      </div>

      {/* Right Sidebar */}
      <div className='basis-1/4 m-6 ml-0 h-fit w-full bg-white p-4 drop-shadow'>
        <div className='flex flex-row w-full border-b border-gray-300 mb-3 items-center'>
          <img src="src/assets/bird.svg" alt="birds" className='w-5 h-5'/>
          <div className='text-lg ml-3 font-bold'>All Birds</div>
        </div>
        {birds.length === 0 ? (
          <p className='text-sm'>Loading...</p>
        ) : (
          birds.slice(0, 20).map(bird => (
            <div key={bird.id} className='flex items-center gap-2 mb-2'>
              {bird.imageURL && (
                <img 
                  src={bird.imageURL} 
                  alt={bird.commonName}
                  className='w-8 h-8 rounded-full object-cover'
                />
              )}
              <p className='text-sm'>
                {bird.commonName}
              </p>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default GroupFeed;