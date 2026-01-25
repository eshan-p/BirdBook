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

  const navItems = [
    { label: 'Global Feed', path: '/feed' },
    { label: 'Groups', path: '/groups' },
    { label: 'Friends', path: '/friends' },
    { label: 'Birds', path: '/birds' },
    { label: 'Profile', path: '/profile' }
    { label: 'Users', path: '/users' }
  ];

  return (
    <div className='bg-white flex flex-row justify-between items-center h-16 px-22 drop-shadow sticky top-0 z-50'>
      <div className='basis-1/3 flex flex-row justify-start min-w-0 gap-4'>
        <button 
          onClick={() => navigate('/feed')}
          className='w-10 h-10 flex items-center justify-center bg-blue-600 text-white font-bold rounded hover:bg-blue-700 transition-colors shrink-0'
        >
          BB
        </button>
        <div className='h-10 w-3/4 flex items-center justify-center'>
          <SearchBar searchType='all' placeholder='Search birds, users, groups...'/>
        </div>
      </div>

      <div className='basis-1/3 flex flex-row justify-center gap-6 shrink-0'>
        {navItems.map((item) => (
          <button 
            key={item.path}
            onClick={() => navigate(item.path)}
            className='text-sm font-medium text-gray-700 hover:text-blue-600 transition-colors'
          >
            {item.label}
          </button>
        ))}
      </div>

      <div className='basis-1/3 flex flex-row justify-end shrink-0'>
        <ProfileIcon size='sm' src={userData?.profilePic ? `http://localhost:8080${userData.profilePic}` : undefined}/>
      </div>
    </div>
  )
}

export default Header