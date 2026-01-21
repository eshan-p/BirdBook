import React, { useEffect, useState } from 'react'
import { Group } from '../../types/Group'
import { arrayToCoords, reverseCoordsToCityState } from '../../utils/geolocation';
import ProfileIcon from '../common/ProfileIcon';


function GroupCard({group} : {group: Group}) {
    const [cityState, setCityState] = useState<string>("");

    useEffect(() => {
        if(group.location){
            reverseCoordsToCityState(arrayToCoords(group.location)).then(setCityState);
        }else{
            console.error("Location in groups not valid")
        }
    }, [group.location])

    return (
        <div className='flex flex-row items-center'>
            <div className='mb-5'>
                <ProfileIcon size='sm'/>
            </div>
            <div className='ml-3 mb-4'>
                <h3 className='text-base/4 font-bold'>{group.name}</h3>
                <p className='text-xs opacity-75'>{cityState}</p>
                <p className='text-xs'>{group.followers} followers</p>
            </div>
        </div>
    )
}

export default GroupCard
