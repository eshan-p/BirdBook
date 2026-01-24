import React, { useEffect, useState } from 'react'
import SearchBar from '../common/SearchBar'
import ProfileIcon from '../common/ProfileIcon'
import { useNavigate } from "react-router-dom";
import { useAuth } from '../../context/AuthContext';
import { getUserById } from '../../api/Users';
import { User } from '../../types/User';

function Header() {
  const navigate = useNavigate();
  const { user: authUser } = useAuth();
  const [userData, setUserData] = useState<User | null>(null);

  useEffect(() => {
    if (authUser?.id) {
      getUserById(authUser.id)
        .then(setUserData)
        .catch(console.error);
    }
  }, [authUser?.id]);
  return (
    <div className='bg-white flex flex-row justify-between items-center h-12 px-24 drop-shadow sticky top-0 z-50'>
      <div className='basis-1/3 flex flex-row justify-start min-w-0'>
        <div className='w-8 h-8 flex flex-row justify-center items-center border-2 border-gray-500 text-gray-500 text-center font-bold mr-4'>BB</div>
        <SearchBar searchType='all' placeholder='Search birds, users, groups...'/>
      </div>
            <button onClick={() => navigate(`/feed`)}
                  className="mt-2">
                    Global Feed
            </button>
            <button onClick={() => navigate(`/groups`)}
                  className="mt-2">
                    Groups
            </button>
            <button onClick={() => navigate(`/friends`)}
                  className="mt-2">
                    Friends
            </button>
            <button onClick={() => navigate(`/birds`)}
                  className="mt-2">
                    Birds
            </button>
            <button onClick={() => navigate(`/profile`)}
                  className="mt-2">
                    Profile
            </button>
      <div className='basis-1/3 flex flex-row justify-center shrink-0'></div>
      <div className='basis-1/3 flex flex-row justify-end shrink-0'>
        <ProfileIcon size='sm' src={userData?.profilePic ? `http://localhost:8080${userData.profilePic}` : undefined}/>
      </div>
    </div>
  )
}

export default Header
