import React from 'react'
import { Bird } from '../../types/Bird'

function BirdCard({ bird }: { bird: Bird }) {
  return (
    <a
      onClick={() => {}}
      className="flex items-center gap-3 p-2 rounded hover:bg-gray-100 cursor-pointer"
    >
      {/* Bird Image */}
      <img
        src={bird.image}
        alt={bird.commonName}
        className="w-12 h-12 rounded object-cover"
      />

      {/* Bird Name */}
      <p className="opacity-75 text-base">{bird.commonName}</p>
    </a>
  )
}

export default BirdCard
