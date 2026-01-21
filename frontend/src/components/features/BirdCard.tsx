import React from 'react'
import { Bird } from '../../types/Bird'

function BirdCard({bird} : {bird: Bird}) {
  return (
    <a onClick={() => {}}>
        <p className='opacity-75 text-base'>{bird.commonName}</p>
    </a>
  )
}

export default BirdCard
