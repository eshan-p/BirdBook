import React from 'react'

function SearchBar() {
  return (
    <div className='flex-1 flex flex-row items-center min-w-0 px-4 py-1 border border-gray-300 rounded'>
            <img src="/src/assets/search.svg" alt="search" className='w-4 h-4 shrink mr-3 opacity-80'/>
            <input type='text' placeholder='Search' className='flex-1 min-w-0 text-sm outline-transparent bg-transparent w-full'/>
    </div>
  )
}

export default SearchBar
