import {Group} from "../types/Group";
const BASE_URL = "http://localhost:8080";

function getHeaders() {
    const token = localStorage.getItem("token");
    return {
        "Content-Type": "application/json",
        ...(token && { "Authorization": `Bearer ${token}` })
    };
}

export async function getAllGroups(): Promise<Group[]>{
    try {
        const response = await fetch(`${BASE_URL}/groups`, {
            headers: getHeaders()
        });
        
        if (!response.ok){
            if (response.status == 404){
                throw new Error("Groups not found");
            }
            throw new Error(`Failed to fetch Groups: ${response.status}`);
        }

        const data = await response.json();
        return data;
    } catch (error: any) {
        console.error("getAllGroups error:", error);
        throw error;
    }
}

export async function getUserGroups(userId:string): Promise<Group[]>{
    const response = await fetch(`${BASE_URL}/users/${userId}/groups`, {
        headers: getHeaders()
    });

    if (!response.ok){
        if (response.status == 404){
            return [];
        }
        throw new Error("Failed to fetch user's groups");
    }

    return response.json();
}

export async function requestToJoinGroup(groupId: string, userId: string): Promise<void> {
    const response = await fetch(`${BASE_URL}/groups/${groupId}/join-requests`, {
        method: "POST",
        headers: getHeaders(),
        body: JSON.stringify({ id: userId }),
    });

    if (!response.ok) {
        throw new Error("Failed to send join request");
    }
}

export async function leaveGroup(groupId: string, userId: string): Promise<void> {
    const response = await fetch(`${BASE_URL}/groups/${groupId}/members/${userId}`, {
        method: "DELETE",
        headers: getHeaders()
    });

    if (!response.ok) {
        throw new Error("Failed to leave group");
    }
}