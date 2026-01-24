import { User } from "../types/User";
const BASE_URL = "http://localhost:8080";

export async function getAllUsers(): Promise<User[]> {
  const response = await fetch(`${BASE_URL}/users`);

  if (!response.ok) {
    throw new Error("Failed to fetch users");
  }

  return response.json();
}

export async function getUserById(userId: string): Promise<User> {
  const response = await fetch(`${BASE_URL}/users/${userId}`);

  if (!response.ok) {
    if (response.status === 404) {
      throw new Error("User not found");
    }
    throw new Error("Failed to fetch User");
  }

  return response.json();
}

export async function getFriends(userId: string): Promise<User[]> {
  const response = await fetch(`${BASE_URL}/users/${userId}/friends`);

  if (!response.ok) {
    throw new Error("Failed to fetch friends");
  }

  return response.json();
}

export async function addFriend(userId:string,friendId:string): Promise<void> {
  const response = await fetch(
    `${BASE_URL}/users/${userId}/friends/${friendId}`,
    {
      method: "PUT",
      credentials: "include", // REQUIRED
    }
  );

  if (!response.ok) {
    throw new Error("Failed to fetch friends");
  }
}