import { useEffect, useState } from "react";
import { Group } from "../types/Group";
import { getAllGroups, getUserGroups, requestToJoinGroup, leaveGroup } from "../api/Groups";
import SearchBar from "../components/common/SearchBar";
import GroupCard from "../components/features/GroupCard";

export default function Groups() {
    const [allGroups, setAllGroups] = useState<Group[]>([]);
    const [userGroups, setUserGroups] = useState<Group[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const currentUserId = localStorage.getItem('userId') || '';

    useEffect(() => {
        async function fetchGroups() {
            try {
                const data = await getAllGroups();
                setAllGroups(data);

                if (currentUserId) {
                    const userGroupsData = await getUserGroups(currentUserId);
                    setUserGroups(userGroupsData);
                }
            } catch (err: any) {
                setError(err.message || "Failed to fetch groups.");
            } finally {
                setLoading(false);
            }
        }

        fetchGroups();
    }, [currentUserId]);

    const handleJoin = async (groupId: string) => {
        if (!currentUserId) {
            alert("Please log in to join a group");
            return;
        }
        try {
            await requestToJoinGroup(groupId, currentUserId);
            const group = allGroups.find(g => g.id === groupId);
            if (group) {
                setAllGroups(allGroups.filter(g => g.id !== groupId));
            }
        } catch (err: any) {
            console.error("Failed to join group:", err);
        }
    };

    const handleLeave = async (groupId: string) => {
        if (!currentUserId) {
            alert("Please log in to leave a group");
            return;
        }
        try {
            await leaveGroup(groupId, currentUserId);
            const group = userGroups.find(g => g.id === groupId);
            if (group) {
                setUserGroups(userGroups.filter(g => g.id !== groupId));
                setAllGroups([...allGroups, group]);
            }
        } catch (err: any) {
            console.error("Failed to leave group:", err);
        }
    };

    if (loading) return <p>Loading...</p>;

    return (
        <main className="max-w-3xl mx-auto m-6 p-6 bg-white rounded-lg shadow-sm">
            <div className="flex items-center justify-between mb-6 gap-4">
                <h2 className="text-2xl font-bold text-gray-800 shrink-0">
                    Groups
                </h2>

                <div className="w-64">
                    <SearchBar />
                </div>
            </div>

            {error && (
                <div className="mb-4 p-4 bg-red-100 text-red-700 rounded">
                    Error: {error}
                </div>
            )}

            {currentUserId && userGroups.length > 0 && (
                <div className="mb-8">
                    <h3 className="text-lg font-semibold text-gray-800 mb-3">My Groups</h3>
                    <ul className="divide-y">
                        {userGroups.map(group => (
                            <li key={group.id}>
                                <GroupCard 
                                    group={group}
                                    onLeave={() => handleLeave(group.id)}
                                />
                            </li>
                        ))}
                    </ul>
                </div>
            )}

            <div>
                <h3 className="text-lg font-semibold text-gray-800 mb-3">Available Groups</h3>
                <ul className="divide-y">
                    {allGroups.map(group => (
                        <li key={group.id}>
                            <GroupCard 
                                group={group}
                                onJoin={() => handleJoin(group.id)}
                            />
                        </li>
                    ))}
                </ul>
                {allGroups.length === 0 && (
                    <p className="text-gray-500 py-4">No available groups</p>
                )}
            </div>
        </main>
    );
}