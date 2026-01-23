import { useEffect, useState } from "react";
import ProfileCard from "../components/features/ProfileCard";
import GroupCard from "../components/features/GroupCard";
import FriendCard from "../components/features/FriendCard";
import BirdCard from "../components/features/BirdCard";
import SearchBar from "../components/common/SearchBar";
import { Group } from "../types/Group";
import { Friend } from "../types/Friend";
import { Bird } from "../types/Bird";
import { fetchAllBirds } from "../services/birdService";

// ---- MOCK DATA (SIDEBARS ONLY) ----
const mockGroups: Group[] = [
  {
    id: "1",
    name: "Carolina Warblers United",
    ownerId: "1",
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

  useEffect(() => {
    fetchAllBirds()
      .then(setBirds)
      .catch(() => setBirds([]))
      .finally(() => setLoading(false));
  }, []);

  const filteredBirds = birds.filter(b =>
    b.commonName.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) {
    return <p className="p-6">Loading birds...</p>;
  }

  return (
    <div className="flex flex-row min-h-screen bg-[#F7F7F7] px-16">
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

        {/* SCROLLABLE BIRD LIST */}
        <div className="bg-white p-4 drop-shadow grid grid-cols-1 gap-2 max-h-[70vh] overflow-y-auto">
          {filteredBirds.map(bird => (
            <BirdCard key={bird._id} bird={bird} />
          ))}
        </div>
      </div>

      {/* RIGHT SIDEBAR */}
      <div className="basis-1/4 m-6 ml-0 bg-white p-4 drop-shadow max-h-[70vh] overflow-y-auto">
        <p className="font-semibold mb-3">All Birds</p>
        {birds.map(bird => (
          <p key={bird._id} className="text-sm mb-1">
            {bird.commonName}
          </p>
        ))}
      </div>
    </div>
  );
}
