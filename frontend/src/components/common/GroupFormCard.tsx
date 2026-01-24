import React, { ChangeEvent, useEffect, useState } from 'react'
import { useAuth } from '../../context/AuthContext';
import { Bird } from '../../types/Bird';
import { Group } from '../../types/Group'
import { reverseCoordsToCityState, arrayToCoords } from '../../utils/geolocation'

function GroupFormCard({onClose} : {onClose: () => void}) {
  const { user } = useAuth();
  return (
    <div>
      
    </div>
  )
}

export default GroupFormCard
