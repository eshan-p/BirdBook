import React, { useEffect, useState } from 'react';
import ProfileIcon from '../components/common/ProfileIcon';
import TopBirds from '../components/features/TopBirds';
import MapView from '../components/features/MapView';
import { Bird } from '../types/Bird';
import { Post } from '../types/Post';
import { useAuth } from '../context/AuthContext';
import { useNavigate, useParams } from 'react-router-dom';
import { User } from '../types/User';
import { getUserById } from '../api/Users';
import { arrayToCoords, reverseCoordsToCityState } from '../utils/geolocation';
import { addFriend } from '../api/Users';

function OtherProfile() {
  const [posts, setPosts] = useState<Post[]>([]);
  const [profileUser, setUser] = useState<Post[]>([]);
  const {userId} = useParams<{userId:string}>();
  const [pageLoading, setPageLoading] = useState<boolean>(true);
  const [locationName, setLocationName] = useState<string>("");
  const [userInfo, setUserInfo] = useState<User | null>(null);
  const [topBirds, setTopBirds] = useState<any[]>([]); //TODO: make typing more specific
  const navigate = useNavigate();
  const BASE_URL = "http://localhost:8080";
  const { user, loading } = useAuth();
  //console.log(userId);

  useEffect(() => {
    if(userId){
      fetch(`${BASE_URL}/users/${userId}/top-birds`, {credentials: 'include'})
        .then(r => r.json())
        .then(setTopBirds)
        .catch(err => console.error("Failed to fetch birds: ", err))
    }
  }, [userId])

  const topBirdsMapped: Bird[] = topBirds.map((b, i) => ({
    id: String(i),
    commonName: b.bird,
    scientificName: "", //TODO: implement scientific name
    image: "src/assets/examplebird.png",
    location: [0, 0]
  }))

  useEffect(() => {
    if(userId){
        fetch(`${BASE_URL}/users/${userId}/posts`, {credentials: 'include'})
            .then(r => r.json())
            .then(setPosts)
            .catch(err => console.error("Failed to fetch posts:", err))
            .finally(() => setPageLoading(false));
    }
  }, [userId]);

  useEffect(() => {
    if(!userId && !pageLoading){
      navigate('/login')
      console.error("Not signed in!")
    }
  }, [userId, pageLoading, navigate]);

  useEffect(() => {
    if(userId) {
        getUserById(userId)
            .then(setUserInfo)
            .catch(err => console.error("Failed to fetch user: " + err));
    }
  }, [userId])

  useEffect(() => {
    if(userInfo?.location){
        reverseCoordsToCityState(arrayToCoords(userInfo.location)).then(setLocationName)
    }
  }, [userInfo?.location])

  if (pageLoading) return <div>loading page</div>
  if (!userInfo) return <div>loading user data</div>

  return (
    <div className='flex flex-row h-full bg-[#F7F7F7] px-16'>
      <div className='basis-2/3 m-6'>
        <div className='bg-white h-fit w-full p-4 drop-shadow flex flex-col'>
            <div className='flex flex-row py-8 border-b border-gray-300 mb-3 px-3'>
                <ProfileIcon size="lg"/>
                <div>
                    <h2 className='text-xl mt-1 ml-4'>{userInfo.username}</h2>
                    <div className='flex flex-row ml-4 mb-2'>
                        <img src="src/assets/pin.svg" alt="location"/>
                        <p className='text-base/4 opacity-65 ml-1'>{locationName || 'Location unkown'}</p>
                    </div>
                    <div className='flex flex-row items-center w-full justify-between px-3 gap-4'>
                        <div className='flex flex-col items-center'>
                            <p className='text-xl font-light text-[#0700D3]'>{userInfo.posts.length}</p>
                            <p className='text-sm font-extralight'>Spottings</p>
                        </div>
                        <div className='flex flex-col items-center'>
                            <p className='text-xl font-light text-[#0700D3]'>{userInfo.friends.length}</p>
                            <p className='text-sm font-extralight'>Friends</p>
                        </div>
                        <div className='flex flex-col items-center'>
                            <p className='text-xl font-light text-[#0700D3]'>{userInfo.groups.length}</p>
                            <p className='text-sm font-extralight'>Groups</p>
                        </div>
                    </div>
                </div>
            </div>
            <div className='px-3 flex flex-col pb-6 border-b border-gray-300 mb-3'>
                <TopBirds birds={topBirdsMapped}/>
            </div>
            <div className='px-3 flex flex-col pb-6'>
                <h2 className='text-xl opacity-70 mb-4'>Sighting Map</h2>
                <MapView posts={posts}/>
            </div>
        </div>
      </div>
      <div className='basis-1/3 m-6 ml-0'>
        <div className='bg-white h-fit w-full p-4 drop-shadow mb-6'>
            <div className='flex flex-row w-full border-b border-gray-300 pb-2'>
                <img src="src/assets/post.svg" alt="posts"/>
                <h3 className='ml-3 text-lg'>Posts</h3>
            </div>
        </div>
        <div className='bg-white h-fit w-full p-4 drop-shadow mb-6'>
            <div className='flex flex-row w-full border-b border-gray-300 pb-2'>
                <img src="src/assets/badge.svg" alt="posts"/>
                <h3 className='ml-3 text-lg'>Badges</h3>
            </div>
            <button
                onClick={() => {
                    if (!user || !userId) return;

                    addFriend(user.id, userId)
                    .then(() => {
                        console.log("Friend added");
                    })
                    .catch(err => {
                        console.error("Failed to add friend:", err);
                    });
                }}
                className="mt-2"
                >
            Add Friend
            </button>

        </div>
      </div>
    </div>
  )
}

export default OtherProfile;
