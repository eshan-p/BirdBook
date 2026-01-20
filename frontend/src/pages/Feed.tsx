import React from 'react'
import PostCard from '../components/features/PostCard'

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
    <div className='flex flex-row h-full'>
        {/* Left Sidebar */}
        <div className='basis-1/4 bg-amber-100'>
          <div></div>
          <div>
            <div>Groups</div>
            <div>Friends</div>
          </div>
        </div>
        {/* Main Feed */}
        <div className='basis-1/2 bg-amber-200'>
          <PostCard {...mockPost}/>
        </div>
        {/* Right Sidebar */}
        <div className='basis-1/4 bg-amber-300'>
          <div>Birds</div>
        </div>
    </div>
  )
}

export default Feed
