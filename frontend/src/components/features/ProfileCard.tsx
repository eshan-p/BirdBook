import React from 'react'
import ProfileIcon from '../common/ProfileIcon'

function ProfileCard() {
  return (
    <div className=' h-fit w-full bg-white px-4 py-6 drop-shadow flex flex-col items-center'>
        <ProfileIcon size='lg'/>
        <h3 className='text-xl mt-1'>Peyton Barre</h3>
        <div className='flex flex-row items-center w-full justify-between px-3 mt-4'>
            <div className='flex flex-col items-center'>
                <p className='text-2xl font-light text-[#0700D3]'>311</p>
                <p className='text-base font-extralight'>Spottings</p>
            </div>
            <div className='flex flex-col items-center'>
                <p className='text-2xl font-light text-[#0700D3]'>17</p>
                <p className='text-base font-extralight'>Friends</p>
            </div>
            <div className='flex flex-col items-center'>
                <p className='text-2xl font-light text-[#0700D3]'>4</p>
                <p className='text-base font-extralight'>Groups</p>
            </div>
        </div>
    </div>
  )
}

export default ProfileCard
