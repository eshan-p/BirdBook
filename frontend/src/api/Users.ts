//second fetch function - src/api/users.ts
import {User} from "../types/User";
const BASE_URL = "http://localhost:8080";

export async function getUserById(userId:string): Promise<User>{
  const response = await fetch(`${BASE_URL}/users/${userId}`);

  if (!response.ok){
    if (response.status == 404){
      throw new Error("User not found");
    }
    throw new Error("Failed to fetch User");
  }// if response not ok

  return response.json();
}