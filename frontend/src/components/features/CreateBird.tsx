import React, { useState } from 'react'
import BirdFormCard from '../common/BirdFormCard';

function CreateBird() {
  const[isFormOpen, setIsFormOpen] = useState<boolean>(false);
  const openForm = () => setIsFormOpen(true);
  const closeForm = () => setIsFormOpen(false);

  return (
    <div>
      <button onClick={openForm} className='w-full mb-6'>
        <div className='flex w-full bg-white drop-shadow justify-center items-center'>
          <div className='flex w-full m-4 border p-4 bg-[#F7F7F7] border-gray-400 border-dashed justify-center items-center opacity-50 font-semibold'>
            Create a new bird
          </div>
        </div>
      </button>
      {isFormOpen && <BirdFormCard onClose={closeForm}/>}
    </div>
  )
}

export default CreateBird
