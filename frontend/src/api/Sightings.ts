//fetch function - src/api/sightings.ts

import {Post} from "../types/Post";
const BASE_URL = "http://localhost:8080";

export async function getSightingById(postId:string): Promise<Post>{
  const response = await fetch(`${BASE_URL}/sightings/${postId}`,{credentials: 'include'});

  if (!response.ok){
    if (response.status == 404){
      throw new Error("Post not found");
    }
    throw new Error("Failed to fetch Post");
  }// if response not ok

  return response.json();
}//get sighting by Id

export async function getSightings(): Promise<Post[]>{
  const response = await fetch(`${BASE_URL}/sightings`,{credentials: 'include'});

    if (!response.ok){
    if (response.status == 404){
      throw new Error("Posts not found");
    }
    throw new Error("Failed to fetch Post");
  }// if response not ok

  return response.json();
}

export async function getSightingsByGroup(groupId: string): Promise<Post[]> {
  console.log('Calling API:', `${BASE_URL}/sightings/group/${groupId}`);
  console.log('Document cookies:', document.cookie);
  
  const response = await fetch(`${BASE_URL}/sightings/group/${groupId}`, {
    method: 'GET',
    credentials: 'include',
    mode: 'cors',
    headers: {
      'Content-Type': 'application/json',
    }
  });

  console.log('Response status:', response.status);
  console.log('Response headers:', response.headers);

  if (!response.ok) {
    if (response.status === 404) {
      return [];
    }
    const errorText = await response.text();
    console.error('Error response:', errorText);
    throw new Error(`Failed to fetch group posts: ${response.status} - ${errorText}`);
  }

  return response.json();
}