import React, { useEffect, useState } from 'react'
import { Friend } from '../../types/Friend'
import { arrayToCoords, reverseCoordsToCityState } from '../../utils/geolocation'
import ProfileIcon from '../common/ProfileIcon'

function FriendCard({friend} : {friend: Friend}) {
    const [cityState, setCityState] = useState<string>("")

    useEffect(() => {
        if(friend.location){
            reverseCoordsToCityState(arrayToCoords(friend.location)).then(setCityState);
        }else{
            console.error("Location in friend not valid")
        }
    }, [friend.location])

    return (
        <div className='flex flex-row items-center'>
            <div className='mb-5'>
                <ProfileIcon size='sm'/>
            </div>
            <div className='ml-3 mb-4'>
                <h3 className='text-base/4 font-bold'>{friend.name}</h3>
                <p className='text-xs opacity-75'>{cityState}</p>
                {/* TODO */}
                {/* <p className='text-xs'>{group.followers} followers</p> */}
            </div>
        </div>
    )
}

export default FriendCard
