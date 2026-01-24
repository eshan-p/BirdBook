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
  const [search, setSearch] = useState("");
  const [birds, setBirds] = useState<Bird[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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
    <div className="flex flex-row h-full bg-[#F7F7F7] px-16">
      {/* LEFT SIDEBAR */}
      <div className="basis-1/4 m-6 mr-0">
        <ProfileCard />
        <div className="mt-6 bg-white p-4 drop-shadow">
          <p className="font-semibold mb-2">Groups</p>
          {mockGroups.map(group => (
            <GroupCard key={group.id} group={group} />
          ))}
          <p className="font-semibold mt-4 mb-2">Friends</p>
          {mockFriends.map(friend => (
            <FriendCard key={friend.id} friend={friend} />
          ))}
        </div>
      </div>

      {/* CENTER CONTENT */}
      <div className="basis-1/2 m-6">
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

      {/* RIGHT SIDEBAR */}
      <div className="basis-1/4 m-6 ml-0 h-fit bg-white p-4 drop-shadow">
        <p className="font-semibold mb-3">All Birds</p>
        {loading ? (
          <p className="text-sm">Loading...</p>
        ) : (
          birds.slice(0, 20).map(bird => (
            <p key={bird.id} className="text-sm mb-1">
              {bird.commonName}
            </p>
          ))
        )}
      </div>
    </div>
  );
}
