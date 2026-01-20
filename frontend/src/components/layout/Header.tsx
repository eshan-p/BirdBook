import React from 'react'

function Header() {
  return (
    <div className='bg-white flex flex-row justify-between items-center h-12 px-24'>
      <div className='basis-1/3 flex flex-row justify-start min-w-0'>
        {/* Logo */}
        <div className='w-8 h-8 flex flex-row justify-center items-center border-2 border-gray-500 text-gray-500 text-center font-bold mr-4'>BB</div>
        {/* Searchbar */}
        <div className='flex-1 flex flex-row items-center min-w-0 px-4 py-1 border border-gray-300 rounded'>
            <img src="/src/assets/search.svg" alt="search" className='w-4 h-4 shrink mr-3 opacity-80'/>
            <input type='text' placeholder='Search' className='flex-1 min-w-0 text-sm outline-transparent bg-transparent w-full'/>
        </div>
      </div>
      {/* TODO, routing icons */}
      <div className='basis-1/3 flex flex-row justify-center shrink-0'></div>
      {/* Profile icon */}
      <div className='basis-1/3 flex flex-row justify-end shrink-0'>right</div>
    </div>
  )
}

export default Header
