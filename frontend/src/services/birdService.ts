import { Bird } from "../types/Bird";

const API_BASE = "http://localhost:8080/api/birds";

export async function fetchAllBirds(): Promise<Bird[]> {
  const res = await fetch(API_BASE);

  if (!res.ok) {
    throw new Error("Failed to fetch birds");
  }

  return res.json();
}

export async function fetchBirdById(id: string): Promise<Bird> {
  console.log("Fetching bird with id:", id);

  const res = await fetch(`${API_BASE}/${id}`);

  if (!res.ok) {
    throw new Error("Failed to fetch bird");
  }

  return res.json();
}
