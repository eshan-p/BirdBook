import React, { useEffect, useState } from 'react'
import { Group } from '../../types/Group'
import { arrayToCoords, reverseCoordsToCityState } from '../../utils/geolocation';
import ProfileIcon from '../common/ProfileIcon';

interface GroupCardProps {
    group: Group;
    onJoin?: () => void;
    onLeave?: () => void;
}

function GroupCard({ group, onJoin, onLeave }: GroupCardProps) {
    const [cityState, setCityState] = useState<string>("");

    useEffect(() => {
        if (group.location) {
            reverseCoordsToCityState(arrayToCoords(group.location))
                .then(setCityState)
                .catch((err) => {
                    console.error("Failed to get location name:", err);
                    setCityState("Location unavailable");
                });
        } else {
            setCityState("No location");
        }
    }, [group.location])

    return (
        <div className='flex flex-row items-center justify-between p-4'>
            <div className='flex flex-row items-center'>
                <div>
                    <ProfileIcon size='sm' />
                </div>
                <div className='ml-3'>
                    <h3 className='text-base/4 font-bold'>{group.name}</h3>
                    <p className='text-xs opacity-75'>{cityState}</p>
                    <p className='text-xs'>{group.members?.length || 0} followers</p>
                </div>
            </div>
            <div className='flex gap-2'>
                {onJoin && (
                    <button 
                        onClick={onJoin}
                        className='px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700'
                    >
                        Join
                    </button>
                )}
                {onLeave && (
                    <button 
                        onClick={onLeave}
                        className='px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700'
                    >
                        Leave
                    </button>
                )}
            </div>
        </div>
    )
}

export default GroupCard