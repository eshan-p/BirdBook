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
    </div>
  );
}

export default GroupCard