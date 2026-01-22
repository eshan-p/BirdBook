import React from 'react'
import ProfileIcon from '../components/common/ProfileIcon'

function Profile() {
  return (
    <div className='flex flex-row h-full bg-[#F7F7F7] px-16'>
      <div className='basis-2/3 m-6'>
        <div className='bg-white h-fit w-full p-4 drop-shadow'>
            <div className='flex flex-row py-8 border-b border-gray-300 mb-3'>
                <ProfileIcon size="lg"/>
                <div>
                    <h2 className='text-xl mt-1 ml-4'>Peyton Barre</h2>
                    <div className='flex flex-row ml-4 mb-2'>
                        <img src="src/assets/pin.svg" alt="location"/>
                        <p className='text-base/4 opacity-65 ml-1'>Plano, TX</p>
                    </div>
                    <div className='flex flex-row items-center w-full justify-between px-3 gap-4'>
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
            </div>
        </div>
      </div>
      <div className='basis-1/3 m-6 ml-0'>
        <div className='bg-white h-fit w-full p-4 drop-shadow mb-6'>
            Posts
        </div>
        <div className='bg-white h-fit w-full p-4 drop-shadow'>
            Badges
        </div>
      </div>
    </div>
  )
}

export default Profile
