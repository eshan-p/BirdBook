import React, { ChangeEvent, useEffect, useState } from 'react'
import { useAuth } from '../../context/AuthContext';
import { Bird } from '../../types/Bird';
import { Group } from '../../types/Group'
import { reverseCoordsToCityState, arrayToCoords } from '../../utils/geolocation'

function PostFormCard({onClose} : {onClose: () => void}) {
  const { user } = useAuth();
  const [header, setHeader] = useState('');
  const [textBody, setTextBody] = useState('');
  const [selectedBird, setSelectedBird] = useState<Bird | null>(null);
  const [selectedGroup, setSelectedGroup] = useState<Group | null>(null);
  const [location, setLocation] = useState<[Number, number] | null>(null);
  const [locationName, setLocationName] = useState('');
  const [help, setHelp] = useState(false);
  const [image, setImage] = useState<File | null>(null);
  const [tags, setTags] = useState<{ [key: string]: string }>({});
  const [tagKey, setTagKey] = useState('');
  const [tagValue, setTagValue] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const [birdSearchQuery, setBirdSearchQuery] = useState('');
  const [birdSearchResults, setBirdSearchResults] = useState<Bird[]>([]);
  const [showBirdDropdown, setShowBirdDropdown] = useState(false);
  const [loadingBirds, setLoadingBirds] = useState(false);

  const [groupSearchQuery, setGroupSearchQuery] = useState('');
  const [groupSearchResults, setGroupSearchResults] = useState<Group[]>([]);
  const [showGroupDropdown, setShowGroupDropdown] = useState(false);
  const [loadingGroups, setLoadingGroups] = useState(false);

  const BASE_URL = "http://localhost:8080";

  useEffect(() => {
    if(navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            (position) => {
                const coords: [number, number] = [
                    position.coords.latitude,
                    position.coords.longitude
                ];
                setLocation(coords);
                //set name
                reverseCoordsToCityState(arrayToCoords(coords)).then(setLocationName).catch(error => console.error("Error geocoding:" + error));
            },
            (error) => {
                console.error("Error getting location:" + error);
            }
        );
    }
  }, []);

  useEffect(() => {
    if (birdSearchQuery.length < 2) {
        setBirdSearchResults([]);
        return;
    }

    const timer = setTimeout(async () => {
        setLoadingBirds(true);
        try{
            const response = await fetch(
                `${BASE_URL}/birds/search?query=${encodeURIComponent(birdSearchQuery)}`,
                {credentials: 'include'}
            );
            if(response.ok) {
                console.error("Welp..: ");
                const birds = await response.json();
                setBirdSearchResults(birds);
            }
        } catch (error) {
            console.error("Error searching birds: " + error);
        } finally {
            setLoadingBirds(false);
        }
    }, 300);
    return () => clearTimeout(timer);
  }, [birdSearchQuery])

  const handleSelectBird = (bird: Bird) => {
    setSelectedBird(bird);
    setBirdSearchQuery(bird.commonName);
    setShowBirdDropdown(false);
  };

  useEffect(() => {
    if(!user?.id) return;
    const timer = setTimeout(async () => {
        setLoadingGroups(true);
        try{
            const response = await fetch(
                `${BASE_URL}/users/${user.id}/groups`,
                {credentials: 'include'}
            );
            if(response.ok) {
                const groups = await response.json();
                const filtered = groupSearchQuery ? groups.filter((g: Group) =>
                    g.name.toLowerCase().includes(groupSearchQuery.toLowerCase())) : groups;
                setGroupSearchResults(filtered);
            }
        } catch (error) {
            console.error("Error searching groups: " + error)
        } finally {
            setLoadingGroups(false);
        }
    }, 300);
    return () => clearTimeout(timer);
  }, [groupSearchQuery,  user?.id])

  const handleSelectGroup = (group: Group) => {
    setSelectedGroup(group);
    setGroupSearchQuery(group.name);
    setShowGroupDropdown(false);
  }

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    if(!user?.id) {
        setError('You must be logged in to create a post');
        setLoading(false);
        return;
    }
    try{
        const postData = {
            header,
            textBody,
            bird: selectedBird?.id || null,
            group: selectedGroup?.id || null,
            help,
            tags: {
                ...tags,
                ...(location && {
                    latitude: location[0].toString(),
                    longitude: location[1].toString()
                })
            },
            flagged: false,
            likes: [],
            comments: []
        };
        const formData = new FormData();
        formData.append('post', JSON.stringify(postData));
        formData.append('userId', user.id);
        if(image) {
            formData.append('image', image);
        }
        const response = await fetch(`${BASE_URL}/sightings`, {
            method: 'POST',
            credentials: 'include',
            body: formData
        });
        if(!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to create post');
        }
        console.log("Post created successfully");
        onClose();
    } catch(error) {
        setError(error instanceof Error ? error.message : 'An error occured');
    } finally {
        setLoading(false);
    }
  };

    function handleImageChange(event: ChangeEvent<HTMLInputElement>): void {
        if (event.target.files && event.target.files[0]) {
            setImage(event.target.files[0])
        }
    };

    function handleAddTag(): void {
        if(tagKey && tagValue) {
            setTags({...tags, [tagKey]:tagValue});
            setTagKey('');
            setTagValue('');
        }
    };

    function handleRemoveTag(key: string): void {
        const newTags = {...tags};
        delete newTags[key];
        setTags(newTags);
    };

  return (
    <div className='fixed inset-0 z-49 flex items-center justify-center bg-black/20 backdrop-blur-sm"'>
        <div className='bg-white backdrop-blur-md p-8 rounded-2xl drop-shadow w-full max-w-xl m-4'>
            <h2 className='text-2xl mb-6'>Create New Sighting</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label className='block text-sm mb-1'>
                        Title <span className='text-red-500'>*</span>
                    </label>

                    <input
                        type='text'
                        value={header}
                        onChange={(e) => setHeader(e.target.value)}
                        maxLength={100}
                        required
                        className='w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'
                        placeholder='Enter sighting title'
                    />
                    <p className='text-xs text-gray-500 mt-1 mb-1'>{header.length}/100 characters</p>

                    <div>
                        <label className='block text-sm mb-1'>
                            Description <span className='text-red-500'>*</span>
                        </label>
                        <textarea
                            value={textBody}
                            onChange={(e) => setTextBody(e.target.value)}
                            maxLength={300}
                            required
                            rows={4}
                            className='w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'
                            placeholder='Sighting description'
                        />
                        <p className='text-xs text-gray-500 mb-1'>{textBody.length}/300 characters</p>
                    </div>

                    <div className='relative'>
                        <label className='block text-sm mb-1'>
                            Bird
                        </label>
                        <input 
                            type='text'
                            value={birdSearchQuery}
                            onChange={(e) => {
                                setBirdSearchQuery(e.target.value);
                                setShowBirdDropdown(true);
                                if(!e.target.value) setSelectedBird(null);
                            }}
                            onFocus={() => setShowBirdDropdown(true)}
                            className='w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'
                            placeholder='Enter bird'
                        />
                        {showBirdDropdown && birdSearchQuery.length >= 2 && (
                            <div className='absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-48 overflow-y-auto'>
                                {loadingBirds ? (
                                    <div className='px-3 py-2 text-sm text-gray-500'>Searching...</div>
                                ) : birdSearchResults.length > 0 ? (
                                birdSearchResults.map((bird) => (
                                    <button
                                    key={bird.id}
                                    type='button'
                                    onClick={() => handleSelectBird(bird)}
                                    className='w-full text-left px-3 py-2 hover:bg-gray-100 border-b last:border-b-0'
                                    >
                                    <div className='font-medium'>{bird.commonName}</div>
                                    {bird.scientificName && (
                                        <div className='text-xs text-gray-500 italic'>{bird.scientificName}</div>
                                    )}
                                    </button>
                                ))
                                ) : (
                                    <div className='px-3 py-2 text-sm text-gray-500'>No birds found</div>
                                )}
                                {selectedBird && (
                                    <div className='mt-1 text-xs'>Selected: {selectedBird.commonName}</div>
                                )}
                            </div>
                        )}
                    </div>

                    {/*TODO: Make this search all groups user is a part of */}
                    <div className='relative'>
                        <label className='block text-sm mb-1'>
                            Group
                        </label>
                        <input 
                            type='text'
                            value={groupSearchQuery}
                            onChange={(e) => {
                                setGroupSearchQuery(e.target.value);
                                setShowGroupDropdown(true);
                            }}
                            onFocus={() => setShowGroupDropdown(true)}
                            className='w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'
                            placeholder='Enter group'
                        />
                        {showGroupDropdown && (
                            <div className='absolute z-10 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-lg max-h-48 overflow-y-auto'>
                                {loadingGroups ? (
                                <div className='px-3 py-2 text-sm text-gray-500'>Loading...</div>
                                ) : groupSearchResults.length > 0 ? (
                                groupSearchResults.map((group) => (
                                    <button
                                    key={group.id}
                                    type='button'
                                    onClick={() => handleSelectGroup(group)}
                                    className='w-full text-left px-3 py-2 hover:bg-gray-100 border-b last:border-b-0'
                                    >
                                    {group.name}
                                    </button>
                                ))
                                ) : (
                                <div className='px-3 py-2 text-sm text-gray-500'>
                                    {user?.id ? 'No groups found' : 'Please log in to see your groups'}
                                </div>
                                )}
                            </div>
                            )}
                            {selectedGroup && (
                            <div className='mt-1 text-xs'> Selected: {selectedGroup.name}</div>
                        )}
                    </div>

                    <div>
                        <label className='block text-sm mb-1'>
                            location
                        </label>
                        <div className='px-3 py-2 bg-gray-50 border border-gray-300 rounded-lg text-sm'>
                            {location ? (
                                <div>
                                <div>{locationName}</div>
                                <div className='text-xs text-gray-500'>
                                    {location[0].toFixed(6)}, {location[1].toFixed(6)}
                                </div>
                                </div>
                            ) : (
                                <div className='text-gray-500'>Getting location...</div>
                            )}
                        </div>
                    </div>

                    <div>
                        <label className='block text-sm mb-1'>
                            Image
                        </label>
                        <input
                            type='file'
                            accept='image/*'
                            onChange={handleImageChange}
                            className='w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'
                        />
                        {image && (
                            <p className='text-xs text-gray-600 mt-1'>Selected: {image.name}</p>
                        )}
                    </div>

                    <div>
                        <label className='block text-sm mb-1'>
                            Tags
                        </label>
                        <div className='flex gap-2 mb-2'>
                            <input
                                type='text'
                                value={tagKey}
                                onChange={(e) => setTagKey(e.target.value)}
                                placeholder='Key (e.g., habitat)'
                                className='flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'
                            />
                            <input
                                type='text'
                                value={tagValue}
                                onChange={(e) => setTagValue(e.target.value)}
                                placeholder='Value (e.g., forest)'
                                className='flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'
                            />
                            <button
                                type='button'
                                onClick={handleAddTag}
                                className='px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600'
                            >
                                Add
                            </button>
                        </div>
                        {Object.entries(tags).length > 0 && (
                            <div className='flex flex-wrap gap-2'>
                                {Object.entries(tags).map(([key, value]) => (
                                    <span
                                        key={key}
                                        className='px-3 py-1 bg-gray-100 rounded-full text-sm flex items-center gap-2'
                                    >
                                        {key}: {value}
                                        <button
                                            type='button'
                                            onClick={() => handleRemoveTag(key)}
                                            className='text-red-500 hover:text-red-700'
                                        >
                                            *
                                        </button>
                                    </span>
                                ))}
                            </div>
                        )}
                    </div>

                    <div className='flex items-center gap-2'>
                        <input
                            type='checkbox'
                            id='help'
                            checked={help}
                            onChange={(e) => setHelp(e.target.checked)}
                            className='w-4 h-4 text-blue-500 rounded focus:ring-2 focus:ring-blue-500'
                        />
                        <label htmlFor='help' className='text-sm'>
                            Request help for identifying this bird
                        </label>
                    </div>

                    {error && (
                        <div className='p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm'>
                            {error}
                        </div>
                    )}

                    <div className='flex gap-3 pt-4'>
                        <button
                            type='button'
                            onClick={onClose}
                            className='flex-1 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50'
                            disabled={loading}
                        >
                            Cancel
                        </button>
                        <button
                            type='submit'
                            className='flex-1 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:bg-gray-300 disabled:cursor-not-allowed'
                            disabled={loading}
                        >
                            {loading ? 'Creating...' : 'Create Sighting'}
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
  )
}

export default PostFormCard
