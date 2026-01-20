import React from 'react'
import SearchBar from '../common/SearchBar'
import ProfileIcon from '../common/ProfileIcon'

function Header() {
  return (
    <div className='bg-white flex flex-row justify-between items-center h-12 px-24'>
      <div className='basis-1/3 flex flex-row justify-start min-w-0'>
        <div className='w-8 h-8 flex flex-row justify-center items-center border-2 border-gray-500 text-gray-500 text-center font-bold mr-4'>BB</div>
        <SearchBar/>
      </div>
      <div className='basis-1/3 flex flex-row justify-center shrink-0'></div>
      <div className='basis-1/3 flex flex-row justify-end shrink-0'>
        <ProfileIcon/>
      </div>
    </div>
  )
}

export default Header
