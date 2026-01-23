import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import PostCard from '../components/features/PostCard'
import ProfileCard from '../components/features/ProfileCard'
import { Post } from '../types/Post'
import { Group } from '../types/Group'
import { parseDate } from '../utils/dateTime'
import { getSightingsByGroup } from '../api/Sightings'
import { getAllGroups } from '../api/Groups'
import { useAuth } from '../context/AuthContext'
import SearchBar from '../components/common/SearchBar'
import BirdCard from '../components/features/BirdCard'
import { Bird } from '../types/Bird'

const PAGE_SIZE = 5;

// Reuse mock birds from Feed if needed
const mockBirds: Bird[] = [
  {
    id: "1",
    commonName: "Crested Kingfisher",
    scientificName: "Megaceryle lugubris",
    image: "src/assets/crested-kingfisher.jpg",
    location: [-82.5519, 35.5955]
  },
  // ... add more as needed
];

function GroupFeed() {
  const { groupId } = useParams<{ groupId: string }>();
  const [group, setGroup] = useState<Group | null>(null);
  const [posts, setPosts] = useState<Post[]>([]);
  const [loadingPage, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const { user, loading } = useAuth();
  const navigate = useNavigate();

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
        <ProfileCard/>
        
        {/* Group Info */}
        {group && (
          <div className='h-fit w-full mt-6 bg-white p-4 drop-shadow'>
            <div className='flex flex-row w-full border-b border-gray-300 mb-3'>
              <img src="src/assets/groups.svg" alt="group"/>
              <p className='text-lg ml-3 font-bold'>{group.name}</p>
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
                <ul className='text-sm text-gray-700 space-y-1'>
                  {group.members.map((member, index) => (
                    <li 
                      key={member.userId || `member-${index}`} 
                      className='truncate hover:bg-gray-50 p-1 rounded cursor-pointer'
                      onClick={() => member.userId && navigate(`/profile/${member.userId}`)}
                    >
                      {member.username || 'Unknown User'}
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
          <>
            {pagedPosts.map(post => (
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
                  className="mt-2 mb-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                  View Details
                </button>
              </div>
            ))}

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="flex justify-center items-center gap-4 mt-6">
                <button
                  disabled={page === 0}
                  onClick={() => setPage(p => p - 1)}
                  className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50"
                >
                  Previous
                </button>
                <span className="text-gray-700">
                  Page {page + 1} of {totalPages}
                </span>
                <button
                  disabled={page + 1 >= totalPages}
                  onClick={() => setPage(p => p + 1)}
                  className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50"
                >
                  Next
                </button>
              </div>
            )}
          </>
        )}
      </div>

      {/* Right Sidebar */}
      <div className='basis-1/4 m-6 ml-0 h-fit w-full bg-white p-4 drop-shadow'>
        <div className='flex flex-row w-full border-b border-gray-300 mb-3 items-center'>
          <img src="src/assets/bird.svg" alt="birds" className='w-5 h-5'/>
          <div className='text-lg ml-3'>Birds</div>
        </div>
        <div className='mb-3'>
          <SearchBar/>
        </div>
        {mockBirds.map((bird) => (
          <BirdCard key={bird.id} bird={bird}/>
        ))}
      </div>
    </div>
  )
}

export default GroupFeed;