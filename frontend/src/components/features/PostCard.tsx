import React, { useEffect, useState } from 'react'
import { Point } from 'geojson'
import { pointToCoords, reverseCoordsToCityState } from '../../utils/geolocation';
import ProfileIcon from '../common/ProfileIcon';
import { getTimeSince } from '../../utils/dateTime';

interface PostCardProps {
  description: string;
  author: string;
  dateTime: Date;
  location: Point;
  likes: number;
  comments: number;
}

interface NominatimResponse {
  display_name: string;
  address: {
    city?: string;
    state?: string;
  }
}

function PostCard({description, author, dateTime, location, likes, comments}: PostCardProps) {
  const [locationName, setLocationName] = useState<String>('Loading...');
  const [timeSince, setTimeSince] = useState<String>('');

  useEffect(() => {
    const coords = pointToCoords(location);
    reverseCoordsToCityState(coords).then(setLocationName);
  }, [location.coordinates]);

  useEffect(() => {
    setTimeSince(getTimeSince(dateTime));
    const interval = setInterval(() => {
      setTimeSince(getTimeSince(dateTime));
    }, 60000);
    return () => clearInterval(interval)
  }, [dateTime])

  return (
    <div className='w-full bg-white p-4 drop-shadow'>
      <div className='flex flex-row mb-3'>
        <ProfileIcon size="md"/>
        <div className='h-14 w-full ml-3'>
          <h3 className='font-bold text-base'>{author}</h3>
          <p className='text-sm/3 opacity-85'>{locationName}</p>
          <p className='text-sm/6 opacity-85'>{timeSince}</p>
        </div>
      </div>
      <p className='text-md/5'>{description}</p>
      <div className='flex flex-row mt-2'>
        <div className='flex flex-row items-center mr-3'>
          <img src="/src/assets/heart.png" alt="like" className='w-5 h-5 mr-1'/>
          <p>{likes}</p>
        </div>
        <div className='flex flex-row items-center'>
          <img src="/src/assets/comment.png" alt="like" className='w-5 h-5 mr-1'/>
          <p>{comments}</p>
        </div>
      </div>
    </div>
  )
}

export default PostCard;
