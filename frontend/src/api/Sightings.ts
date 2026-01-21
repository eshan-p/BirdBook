//fetch function - src/api/sightings.ts

import {Post} from "../types/Post";
const BASE_URL = "http://localhost:8080";

export async function getSightingById(postId:string): Promise<Post>{
  const response = await fetch(`${BASE_URL}/sightings/${postId}`);

  if (!response.ok){
    if (response.status == 404){
      throw new Error("Post not found");
    }
    throw new Error("Failed to fetch Post");
  }// if response not ok

  return response.json();
}//get sighting by Id

export async function getSightings(): Promise<Post[]>{
  const response = await fetch(`${BASE_URL}/sightings`);

    if (!response.ok){
    if (response.status == 404){
      throw new Error("Posts not found");
    }
    throw new Error("Failed to fetch Post");
  }// if response not ok

  return response.json();
}