import React, { useEffect, useState } from 'react'
import { Bird } from '../../types/Bird'
import { reverseCoordsToRegion } from '../../utils/geolocation'

function TopBirds({birds} : {birds: Bird[]}) {
  const [region, setRegion] = useState<string>("");
//   useEffect(() => {

//   }, bird)
  return (
    <div className='w-full'>
        <h2 className='text-xl opacity-70 mb-4'>Top birds spotted this month</h2>
        <div className='flex justify-between'>
            {birds.map((bird) => (
                <div key={bird.id} className='flex flex-col shrink-0 w-38 bg-[#F9F9F9] drop-shadow'>
                    <img src="src/assets/examplebird.png" alt="bird"/>
                    <div className='px-3'>
                        <p className='mt-1 text-xs opacity-65'>{bird.commonName}</p>
                        <p className='text-[10px] opacity-65 italic mb-2'>{bird.scientificName}</p>
                        {/* <div>  
                            <img src="src/assets/pin.svg" alt="location"/>
                            <p>{reverseCoordsToRegion(bird.location)}</p>
                        </div> */}
                    </div>
                </div>
            ))}
        </div>
    </div>
  )
}

export default TopBirds
