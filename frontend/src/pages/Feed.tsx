import React from 'react'
import PostCard from '../components/features/PostCard'

function Feed() {
  return (
    <div className='flex flex-row h-full'>
        {/* Left Sidebar */}
        <div className='basis-1/4 bg-amber-100'>
          <div>Profile</div>
          <div>
            <div>Groups</div>
            <div>Friends</div>
          </div>
        </div>
        {/* Main Feed */}
        <div className='basis-1/2 bg-amber-200'>
          <PostCard/>
        </div>
        {/* Right Sidebar */}
        <div className='basis-1/4 bg-amber-300'>
          <div>Birds</div>
        </div>
    </div>
  )
}

export default Feed
