import React, { useState } from 'react'
import ProfileIcon from '../common/ProfileIcon'
import PostFormCard from '../common/PostFormCard';

function CreatePost() {
  const[isFormOpen, setIsFormOpen] = useState<boolean>(false);

  const openForm = () => setIsFormOpen(true);
  const closeForm = () => setIsFormOpen(false);

  return (
    <div>
        <button onClick={openForm}>
            <div className='flex w-full bg-white drop-shadow justify-center items-center'>
                <div className='flex w-full m-4 border p-4 bg-[#F7F7F7] border-gray-400 border-dashed justify-center items-center opacity-60'>
                    Log a new bird sighting
                </div>
            </div>
        </button>
        {isFormOpen && <PostFormCard onClose={closeForm}/>}
    </div>
  )
}

export default CreatePost
