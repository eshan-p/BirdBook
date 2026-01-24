import React, { useState } from 'react'
import GroupFormCard from '../common/GroupFormCard';

function CreateGroup() {
  const[isFormOpen, setIsFormOpen] = useState<boolean>(false);
  const openForm = () => setIsFormOpen(true);
  const closeForm = () => setIsFormOpen(false);

  return (
    <div>
      <button onClick={openForm}>
        <h2 className='ml-2'>Create new group</h2>
      </button>
      {isFormOpen && <GroupFormCard onClose={closeForm}/>}
    </div>
  )
}

export default CreateGroup
