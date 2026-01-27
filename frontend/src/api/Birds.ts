import { Bird } from "../types/Bird";

const BASE_URL = "http://localhost:8080";

export async function getAllBirds(): Promise<Bird[]> {
  const response = await fetch(`${BASE_URL}/birds`);

  if (!response.ok) {
    throw new Error("Failed to fetch birds");
  }

  return response.json();
}

export async function searchBirds(query: string): Promise<Bird[]> {
  const response = await fetch(`${BASE_URL}/birds/search?query=${encodeURIComponent(query)}`);

  if (!response.ok) {
    throw new Error("Failed to search birds");
  }

  return response.json();
}

export async function getBirdByCommonName(commonName: string): Promise<Bird> {
  const response = await fetch(`${BASE_URL}/birds/${encodeURIComponent(commonName)}`);

  if (!response.ok) {
    if (response.status === 404) {
      throw new Error("Bird not found");
    }
    throw new Error("Failed to fetch bird");
  }

  return response.json();
}

export async function addBird(bird: Partial<Bird>, imageFile?: File): Promise<Bird> {
  const formData = new FormData();
  
  // Create bird JSON without the image field
  const birdData = {
    commonName: bird.commonName,
    scientificName: bird.scientificName,
    imageURL: bird.imageURL // This can be a Wikipedia URL if no file is uploaded
  };
  
  formData.append("bird", JSON.stringify(birdData));
  
  if (imageFile) {
    formData.append("image", imageFile);
  }

  const response = await fetch(`${BASE_URL}/birds`, {
    method: "POST",
    credentials: "include",
    body: formData,
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error("Failed to add bird");
  }

  return response.json();
}

export async function updateBird(id: string, bird: Partial<Bird>, imageFile?: File): Promise<Bird> {
  const formData = new FormData();
  
  const birdData = {
    commonName: bird.commonName,
    scientificName: bird.scientificName,
    imageURL: bird.imageURL
  };
  
  formData.append("bird", JSON.stringify(birdData));
  
  if (imageFile) {
    formData.append("image", imageFile);
  }

  const response = await fetch(`${BASE_URL}/birds/${id}`, {
    method: "PATCH",
    body: formData,
  });

  if (!response.ok) {
    throw new Error("Failed to update bird");
  }

  return response.json();
}

export async function deleteBird(id: string): Promise<void> {
  const response = await fetch(`${BASE_URL}/birds/${id}`, {
    method: "DELETE",
  });

  if (!response.ok) {
    throw new Error("Failed to delete bird");
  }
}
