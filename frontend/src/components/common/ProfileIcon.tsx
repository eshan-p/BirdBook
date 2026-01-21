import React from 'react'

type Sizes = 'sm' | 'md' | 'lg';

interface ProfileIconProps {
    size: Sizes;
    src?: string;
}

function ProfileIcon({size, src = "/src/assets/profilephoto.jpg"}: ProfileIconProps) {
    const sizeClasses = {
        sm: 'w-10 h-10',
        md: 'w-14 h-14',
        lg: 'w-16 h-16'
    }
    return (
        <div className={`${sizeClasses[size]} rounded-full bg-gray-500 border border-gray-500 overflow-hidden`}>
            <img src={src} alt="profile" className='w-full h-full object-cover'/>
        </div>
    )
}

export default ProfileIcon
