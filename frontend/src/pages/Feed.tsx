import React, { useEffect, useState } from 'react'
import PostCard from '../components/features/PostCard'
import ProfileIcon from '../components/common/ProfileIcon';
import ProfileCard from '../components/features/ProfileCard';
import { reverseCoordsToCityState } from '../utils/geolocation';
import GroupCard from '../components/features/GroupCard';
import { Group } from '../types/Group';
import FriendCard from '../components/features/FriendCard';
import { Friend } from '../types/Friend';
import { getSightings } from '../api/Sightings';
import { Post } from '../types/Post';
import { parseDate } from '../utils/dateTime';
import { Bird } from '../types/Bird';
import BirdCard from '../components/features/BirdCard';
import SearchBar from '../components/common/SearchBar';
import { useNavigate } from "react-router-dom";
import { useAuth } from '../context/AuthContext';
import { getUserById } from '../api/Users';

//page logic
const PAGE_SIZE = 5; // easy to tweak later


// TODO: Delete when have real data
const mockPost = {
  description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec hendrerit massa eu orci aliquet, sed tincidunt diam sodales. Etiam lobortis felis eu egestas varius. Nulla eleifend vestibulum lorem vel ultrices. Nunc ut lectus vel massa mollis consectetur non nec magna. Vivamus congue sollicitudin est nec pulvinar...",
  author: "Sarah Mitchell",
  dateTime: new Date('2024-01-18'),
  location: {
    type: 'Point' as const,
    coordinates: [-96.8158, 33.2148] // Plano, TX
  },
  likes: 24,
  comments: 5
};

const mockGroups: Group[] = [
  {
    id: "1",
    name: "Carolina Warblers United",
    owner: { userId: "1", username: "Owner Name" },
    members: [],
    requests: [],
    groupPhoto: "src/assets/profilephoto.jpg",
    location: [35.5955, -82.5519],
    followers: 814
  },
  {
    id: "2",
    name: "Coastal California Seabirds",
    owner: { userId: "1", username: "Owner Name" },
    members: [],
    requests: [],
    groupPhoto: "src/assets/profilephoto.jpg",
    location: [32.7153, -117.1573],
    followers: 3195
  },
  {
    id: "3",
    name: "Texas Hill Country Birding",
    owner: { userId: "1", username: "Owner Name" },
    members: [],
    requests: [],
    groupPhoto: "src/assets/profilephoto.jpg",
    location: [30.2672, -97.7431],
    followers: 720
  }
]

const mockFriends: Friend[] = [
  {
    id: "1",
    name: "Marcus Thompson",
    profilePhoto: "src/assets/profilephoto.jpg",
    location: [35.5955, -82.5519],
  },
  {
    id: "2",
    name: "Elena Rodriguez",
    profilePhoto: "src/assets/profilephoto.jpg",
    location: [32.7153, -117.1573],
  },
  {
    id: "3",
    name: "James Mitchell",
    profilePhoto: "src/assets/profilephoto.jpg",
    location: [30.2672, -97.7431],
  }
]

const mockBirds: Bird[] = [
  {
    id: "1",
    commonName: "Crested Kingfisher",
    scientificName: "Megaceryle lugubris",
    image: "src/assets/crested-kingfisher.jpg",
    location: [-82.5519, 35.5955]
  },
  {
    id: "2",
    commonName: "American Bittern",
    scientificName: "Botaurus lentiginosus",
    image: "src/assets/american-bittern.jpg",
    location: [-95.7129, 37.2688]
  },
  {
    id: "3",
    commonName: "Northern Bobwhite",
    scientificName: "Colinus virginianus",
    image: "src/assets/northern-bobwhite.jpg",
    location: [-78.6382, 35.4676]
  },
  {
    id: "4",
    commonName: "Stellar's Jay",
    scientificName: "Cyanocitta stelleri",
    image: "src/assets/stellars-jay.jpg",
    location: [-120.5, 45.5]
  },
  {
    id: "5",
    commonName: "Mourning Dove",
    scientificName: "Zenaida macroura",
    image: "src/assets/mourning-dove.jpg",
    location: [-96.8158, 33.2148]
  }
];

function Feed() {
  const [groups, setGroups] = useState<Group[]>([]);
  const [posts, setPosts] = useState<Post[]>([]);
  const [loadingPage, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const BASE_URL = "http://localhost:8080";
  const { user, loading } = useAuth();
  const [friends, setFriends] = useState<Friend[]>([]);

  const [page, setPage] = useState(0); // zero-based index

  const navigate = useNavigate();
  
  // Reset page if posts change
  useEffect(() => {
    setPage(0);
  }, [posts]);

useEffect(() => {
  getSightings()
    .then(setPosts)
    .catch(err => setError(err.message))
    .finally(() => {
      setLoading(false);
      console.log(posts);
    });
}, []); //get sightings

  useEffect(() => {
        fetch(`${BASE_URL}/groups`, {credentials: 'include'})
            .then(r => r.json())
            .then(setGroups)
            .catch(err => console.error("Failed to fetch posts:", err))
  },[]); // get groups

  useEffect(() => {
      if(user?.id) {
          fetch(`${BASE_URL}/${user.id}/friends`, {credentials: 'include'})
            .then(r => r.json())
            .then(setFriends)
            .catch(err => console.error("Failed to fetch user: " + err));
      }
    }, [user?.id])

  const totalPages = Math.ceil(posts.length / PAGE_SIZE);

  const pagedPosts = posts.slice(
    page * PAGE_SIZE,
    (page + 1) * PAGE_SIZE
  );

  if (loadingPage) return <p>Loading...</p>;

  //console.log("posts:", posts);
  //console.log("pagedPosts:", pagedPosts);

  

  return (
    <div className='flex flex-row h-full bg-[#F7F7F7] px-16'>
        {/* Left Sidebar */}
        <div className='flex flex-col basis-1/4 m-6 mr-0'>
          <ProfileCard/>
          <div className='h-fit w-full mt-6 bg-white p-4 drop-shadow'>
            <div className='flex flex-row w-full border-b border-gray-300 mb-3'>
              <img src="src/assets/groups.svg" alt="groups"/>
              <p className='text-lg ml-3 font-bold'>Groups</p>
            </div>
            {groups.map((group) => (
              <GroupCard key={group.id.toString()} group={group}/>
            ))}
            <div className='flex flex-row w-full border-b border-gray-300 mb-3'>
              <img src="src/assets/person.svg" alt="friends"/>
              <p className='text-lg ml-3 font-bold'>Friends</p>
            </div>
            {friends.map((friend) => (
              <FriendCard key={friend.id} friend={friend}/>
            ))}
          </div>
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
              No posts yet
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

export default Feed