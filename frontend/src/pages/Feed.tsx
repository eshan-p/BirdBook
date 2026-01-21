import React, { useEffect, useState } from 'react'
import PostCard from '../components/features/PostCard'
import ProfileIcon from '../components/common/ProfileIcon';
import ProfileCard from '../components/features/ProfileCard';
import { reverseCoordsToCityState } from '../utils/geolocation';
import GroupCard from '../components/features/GroupCard';
import { Group } from '../types/Group';

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

const mockFriends = [
  {
    profilePhoto: "src/assets/profilephoto.jpg",
    name: "Marcus Thompson",
    location: [35.5955, -82.5519],
  },
  {
    groupPhoto: "src/assets/profilephoto.jpg",
    groupName: "Elena Rodriguez",
    location: [32.7153, -117.1573],
  },
  {
    groupPhoto: "src/assets/profilephoto.jpg",
    groupName: "James Mitchell",
    location: [30.2672, -97.7431],
  }
]

function Feed() {
  const [groups, setGroups] = useState<Group[]>([]);

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
            <div className='flex flex-row w-full border-b border-gray-300'>
              <img src="src/assets/person.svg" alt="groups"/>
              <p className='text-lg ml-3'>Friends</p>
            </div>
          </div>
        </div>

        {/* Main Feed */}
        <div className='basis-1/2 m-6'>
          <PostCard {...mockPost}/>
        </div>

        {/* Right Sidebar */}
        <div className='basis-1/4 m-6 ml-0 h-fit w-full bg-white p-4 drop-shadow'>
          <div>Birds</div>
        </div>
    </div>
  )
}

export default Feed
