import React, { useEffect, useState } from 'react'
import { Group } from '../../types/Group'
import { arrayToCoords, reverseCoordsToCityState } from '../../utils/geolocation';
import ProfileIcon from '../common/ProfileIcon';
import { useNavigate } from 'react-router-dom';

interface GroupCardProps {
  group: Group;
  onJoin?: () => void;
  onLeave?: () => void;
  onDelete?: () => void;
}

function GroupCard({ group, onJoin, onLeave, onDelete }: GroupCardProps) {
    const navigate = useNavigate();

    return (
    <div className="flex flex-row items-center justify-between p-4">
      <div className="flex flex-row items-center">
        <div>
          <ProfileIcon size='sm' />
        </div>
        <div className='ml-3'>
          <button 
            onClick={() => navigate(`/groups/${group.id}`)}
            className="mt-2 text-blue-600 hover:underline">
            {group.name}
          </button>
          <p className='text-xs'>{group.members?.length || 0} followers</p>
        </div>
      </div>
      <div className='flex gap-2'>
        {onJoin && (
          <button onClick={onJoin} className='px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700'>
            Join
          </button>
        )}
        {onLeave && (
          <button onClick={onLeave} className='px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700'>
            Leave
          </button>
        )}
        {onDelete && (
          <button onClick={onDelete} className='px-3 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300'>
            Delete
          </button>
        )}
      </div>
    </div>
  );
}

export default GroupCard