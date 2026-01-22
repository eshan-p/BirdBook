import { useEffect, useState } from "react";
import { User } from "../types/User";
import { getAllUsers } from "../api/Users";
import SearchBar from "../components/common/SearchBar";
import FriendCard from "../components/features/FriendCard";


export default function Friends() {
    const [friends, setFriends] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        async function fetchFriends() {
            try {
                const data = await getAllUsers(); // fetch all users, no auth required
                setFriends(data);
            } catch (err: any) {
                setError(err.message || "Failed to fetch users.");
            } finally {
                setLoading(false);
            }
        }

        fetchFriends();
    }, []);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <main className="max-w-3xl mx-auto m-6 p-6 bg-white rounded-lg shadow-sm">
            <div className="flex items-center justify-between mb-6 gap-4">
                <h2 className="text-2xl font-bold text-gray-800 shrink-0">
                    Friends
                </h2>

                <div className="w-64">
                    <SearchBar />
                </div>
            </div>

            <ul className="divide-y">
                {friends.map(friend => (
                    <li
                        key={friend.id}
                        className="py-5"
                    >
                        <FriendCard user={friend} />
                    </li>
                ))}
            </ul>
        </main>
    );
}
