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
    ownerId: "1",
    members: [],
    requests: [],
    groupPhoto: "src/assets/profilephoto.jpg",
    location: [35.5955, -82.5519],
    followers: 814
  },
  {
    id: "2",
    name: "Coastal California Seabirds",
    ownerId: "1",
    members: [],
    requests: [],
    groupPhoto: "src/assets/profilephoto.jpg",
    location: [32.7153, -117.1573],
    followers: 3195
  },
  {
    id: "3",
    name: "Texas Hill Country Birding",
    ownerId: "1",
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

function Feed() {
  const [groups, setGroups] = useState<Group[]>([]);
  const [posts, setPosts] = useState<Post[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  

useEffect(() => {
  getSightings()
    .then(setPosts)
    .catch(err => setError(err.message))
    .finally(() => setLoading(false));
}, []);

  useEffect(() => {
    //TODO: Replace with fetch
    setGroups(mockGroups);
  }, [])

  return (
    <div className='flex flex-row h-full bg-[#F7F7F7] px-16'>
        {/* Left Sidebar */}
        <div className='flex flex-col basis-1/4 m-6 mr-0'>
          <ProfileCard/>
          <div className='h-fit w-full mt-6 bg-white p-4 drop-shadow'>
            <div className='flex flex-row w-full border-b border-gray-300 mb-3'>
              <img src="src/assets/groups.svg" alt="groups"/>
              <p className='text-lg ml-3'>Groups</p>
            </div>
            {mockGroups.map((group) => (
              <GroupCard key={group.id} group={group}/>
            ))}
            <div className='flex flex-row w-full border-b border-gray-300 mb-3'>
              <img src="src/assets/person.svg" alt="groups"/>
              <p className='text-lg ml-3'>Friends</p>
            </div>
            {mockFriends.map((friend) => (
              <FriendCard key={friend.id} friend={friend}/>
            ))}
          </div>
        </div>

        {/* Main Feed */}
        <div className='basis-1/2 m-6'>
          {posts.map(post => (
              <PostCard
                key={post.id}
                description={post.header}
                author={post.userId}
                dateTime={parseDate(post.timestamp)}
                location={post.tags?.location}
                likes={post.likes.length}
                comments={post.comments.length}
              />
          ))}
        </div>

        {/* Right Sidebar */}
        <div className='basis-1/4 m-6 ml-0 h-fit w-full bg-white p-4 drop-shadow'>
          <div>Birds</div>
        </div>
    </div>
  )
}

export default Feed
