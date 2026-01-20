import React, { useEffect, useState } from 'react'
import { Point } from 'geojson'
import { pointToCoords, reverseCoordsToCityState } from '../../utils/geolocation';

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

  useEffect(() => {
    const coords = pointToCoords(location);
    reverseCoordsToCityState(coords).then(setLocationName);
  }, [location.coordinates]);

  return (
    <div className=''>
      <h3>{author}</h3>
      <p>{locationName}</p>
      <p>{dateTime.toLocaleDateString()}</p>
      <p>{description}</p>
      <p>{likes}</p>
      <p>{comments}</p>
    </div>
  )
}

export default PostCard;
