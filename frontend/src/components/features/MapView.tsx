import React from 'react'
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Post } from '../../types/Post';
import 'leaflet/dist/leaflet.css';

function MapView({posts} : {posts: Post[]}) {
    const postsWithLocation = posts.filter(p => p.tags?.location);

    const center: [number, number] = postsWithLocation.length > 0 ? 
    [
        postsWithLocation.reduce((sum, p) => sum + (p.tags?.location?.latitude || 0), 0) / posts.length,
        postsWithLocation.reduce((sum, p) => sum + (p.tags?.location?.longitude || 0), 0) / posts.length
    ]
    : [33.2148, -96.8158];

    return (
        <MapContainer center={center} zoom={13} style={{height: '400px', width: '100%'}}>
            <TileLayer 
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                attribution='&copy; OpenStreetMap contributors'
            /> 
            {postsWithLocation.map((post) => (
                <Marker 
                key={post.id} 
                position={[
                    post.tags!.location!.latitude,
                    post.tags!.location!.longitude
                ] as any}
                >
                <Popup>
                    <p>{post.header}</p>
                    <p className='text-sm'>{post.bird}</p>
                </Popup>
                </Marker>
            ))}
        </MapContainer>
    )
}

export default MapView
