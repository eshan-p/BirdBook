import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Group } from "../types/Group";
import {
  getAllGroups,
  getUserGroups,
  requestToJoinGroup,
  leaveGroup,
  createGroup,
  deleteGroup,
} from "../api/Groups";
import SearchBar from "../components/common/SearchBar";
import GroupCard from "../components/features/GroupCard";
import { useAuth } from "../context/AuthContext";
import CreateGroup from "../components/features/CreateGroup";

export default function Groups() {
  const [allGroups, setAllGroups] = useState<Group[]>([]);
  const [userGroups, setUserGroups] = useState<Group[]>([]);
  const [pageLoading, setPageLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [newGroupName, setNewGroupName] = useState("");
  const { user, loading } = useAuth();
  const navigate = useNavigate();

  const currentUserId = localStorage.getItem("userId") || "";
  const role = user?.role;
  const canManage = role === "ADMIN_USER" || role === "SUPER_USER";

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
        setPageLoading(false);
      }
    }
    fetchGroups();
  }, [currentUserId]);

  useEffect(() => {
    if (!user && !loading) {
      navigate("/login");
    }
  }, [user, loading, navigate]);

  const refreshLists = async () => {
    const data = await getAllGroups();
    setAllGroups(data);
    if (currentUserId) {
      const userGroupsData = await getUserGroups(currentUserId);
      setUserGroups(userGroupsData);
    }
  };

  const handleJoin = async (groupId: string) => {
    if (!currentUserId) {
      alert("Please log in to join a group");
      return;
    }
    try {
      await requestToJoinGroup(groupId, currentUserId);
      setAllGroups(allGroups.filter((g) => g.id !== groupId));
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
      const group = userGroups.find((g) => g.id === groupId);
      if (group) {
        setUserGroups(userGroups.filter((g) => g.id !== groupId));
        setAllGroups([...allGroups, group]);
      }
    } catch (err: any) {
      console.error("Failed to leave group:", err);
    }
  };

  const handleCreate = async () => {
    if (!currentUserId || !newGroupName.trim()) return;
    try {
      await createGroup(newGroupName.trim(), currentUserId);
      setNewGroupName("");
      await refreshLists();
    } catch (err: any) {
      console.error("Failed to create group:", err);
    }
  };

  const handleDelete = async (groupId: string) => {
    if (!canManage) return;
    try {
      await deleteGroup(groupId);
      setAllGroups(allGroups.filter((g) => g.id !== groupId));
      setUserGroups(userGroups.filter((g) => g.id !== groupId));
    } catch (err: any) {
      console.error("Failed to delete group:", err);
    }
  };

  if (pageLoading) return <p>Loading...</p>;

  return (
    <main className="max-w-3xl mx-auto m-6 p-6 bg-white rounded-lg shadow-sm">
      <div className="flex items-center justify-between mb-6 gap-4">
        <div>
          <h2 className="text-2xl font-bold text-gray-800 shrink-0">Groups</h2>
          <CreateGroup/>
        </div>
        <div className="w-64">
          <SearchBar />
        </div>
      </div>

      {canManage && (
        <div className="mb-6 flex gap-2">
          <input
            className="border rounded px-3 py-2 flex-1"
            placeholder="New group name"
            value={newGroupName}
            onChange={(e) => setNewGroupName(e.target.value)}
          />
          <button
            onClick={handleCreate}
            className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
          >
            Create
          </button>
        </div>
      )}

      {error && (
        <div className="mb-4 p-4 bg-red-100 text-red-700 rounded">Error: {error}</div>
      )}

      {currentUserId && userGroups.length > 0 && (
        <div className="mb-8">
          <h3 className="text-lg font-semibold text-gray-800 mb-3">My Groups</h3>
          <ul className="divide-y">
            {userGroups.map((group) => (
              <li key={group.id}>
                <GroupCard
                  group={group}
                  onLeave={() => handleLeave(group.id)}
                  onDelete={canManage ? () => handleDelete(group.id) : undefined}
                />
              </li>
            ))}
          </ul>
        </div>
      )}

      <div>
        <h3 className="text-lg font-semibold text-gray-800 mb-3">Available Groups</h3>
        <ul className="divide-y">
          {allGroups.map((group) => (
            <li key={group.id}>
              <GroupCard
                group={group}
                onJoin={() => handleJoin(group.id)}
                onDelete={canManage ? () => handleDelete(group.id) : undefined}
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