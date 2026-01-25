import React, { useState, useEffect } from "react";
import ProfileCard from "../components/features/ProfileCard";
import GroupCard from "../components/features/GroupCard";
import FriendCard from "../components/features/FriendCard";
import BirdCard from "../components/features/BirdCard";
import SearchBar from "../components/common/SearchBar";
import { Group } from "../types/Group";
import { Friend } from "../types/Friend";
import { Bird } from "../types/Bird";
import { getAllBirds } from "../api/Birds";
import { useAuth } from '../context/AuthContext';
import { getUserById } from '../api/Users';
import { User } from '../types/User';
import { getUserGroups } from '../api/Groups';

// ---- MOCK DATA ----
const mockGroups: Group[] = [
  {
    id: "1",
    name: "Carolina Warblers United",
    owner: {userId:"1",username:"TestUser"},
    members: [],
    requests: [],
    groupPhoto: "src/assets/profilephoto.jpg",
    location: [35.5955, -82.5519],
    followers: 814,
  },
];

const mockFriends: Friend[] = [
  {
    id: "1",
    name: "Marcus Thompson",
    profilePhoto: "src/assets/profilephoto.jpg",
    location: [35.5955, -82.5519],
  },
];

export default function Birds() {
  const { user } = useAuth();
  const [userData, setUserData] = useState<User | null>(null);
  const [search, setSearch] = useState("");
  const [birds, setBirds] = useState<Bird[]>([]);
  const [groups, setGroups] = useState<Group[]>([]);
  const [friends, setFriends] = useState<Friend[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const BASE_URL = "http://localhost:8080";

  // Fetch full user data
  useEffect(() => {
    if (user?.id) {
      getUserById(user.id)
        .then(setUserData)
        .catch(console.error);
    }
  }, [user?.id]);

  // Fetch user's groups only
  useEffect(() => {
    if (user?.id) {
      getUserGroups(user.id)
        .then(setGroups)
        .catch(err => console.error("Failed to fetch groups:", err));
    }
  }, [user?.id]);

  // Fetch friends from backend
  useEffect(() => {
    if (user?.id) {
      fetch(`${BASE_URL}/users/${user.id}/friends`, {credentials: 'include'})
        .then(r => r.json())
        .then(setFriends)
        .catch(err => console.error("Failed to fetch friends:", err));
    }
  }, [user?.id]);

  // Fetch birds from backend on component mount
  useEffect(() => {
    const fetchBirds = async () => {
      try {
        setLoading(true);
        const data = await getAllBirds();
        setBirds(data);
        setError(null);
      } catch (err) {
        setError("Failed to load birds. Please try again later.");
        console.error("Error fetching birds:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchBirds();
  }, []);

  const filteredBirds = birds.filter(b =>
    b.commonName.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="flex flex-row min-h-screen bg-[#F7F7F7] px-16">
      {/* LEFT SIDEBAR */}
      <div className="flex flex-col basis-1/4 m-6 mr-0">
        <ProfileCard user={userData || undefined} />
        <div className="h-fit w-full mt-6 bg-white p-4 drop-shadow">
          <div className="flex flex-row w-full border-b border-gray-300 mb-3">
            <img src="src/assets/groups.svg" alt="groups"/>
            <p className="text-lg ml-3 font-bold">Groups</p>
          </div>
          {groups.map(group => (
            <GroupCard key={group.id.toString()} group={group} />
          ))}
          <div className="flex flex-row w-full border-b border-gray-300 mb-3">
            <img src="src/assets/person.svg" alt="friends"/>
            <p className="text-lg ml-3 font-bold">Friends</p>
          </div>
          {friends.map(friend => (
            <FriendCard key={friend.id} friend={friend} />
          ))}
        </div>
      </div>

      {/* CENTER CONTENT */}
      <div className="basis-3/4 m-6">
        <div className="bg-white p-4 drop-shadow mb-4">
          <SearchBar onChange={(e: any) => setSearch(e.target.value)} />
        </div>

        <div className="bg-white p-4 drop-shadow grid grid-cols-1 gap-2">
          {loading && <p className="text-center py-4">Loading birds...</p>}
          {error && <p className="text-center py-4 text-red-600">{error}</p>}
          {!loading && !error && filteredBirds.length === 0 && (
            <p className="text-center py-4 text-gray-500">No birds found</p>
          )}
          {!loading && !error && filteredBirds.map(bird => (
            <BirdCard key={bird.id} bird={bird} />
          ))}
        </div>
      </div>
    </div>
  );
}
