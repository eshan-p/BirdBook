import React from 'react'
import PostCard from '../components/features/PostCard'
import ProfileIcon from '../components/common/ProfileIcon';
import ProfileCard from '../components/features/ProfileCard';

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

function Feed() {
  return (
    <div className='flex flex-row h-full bg-[#F7F7F7] px-16'>
        {/* Left Sidebar */}
        <div className='flex flex-col basis-1/4 m-6 mr-0'>
          <ProfileCard/>
          <div className='h-fit w-full mt-6 bg-white p-4 drop-shadow'>
            <div>Groups</div>
            <div>Friends</div>
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
