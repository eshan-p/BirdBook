export interface Bird {
    id: string;
    commonName: string;
    scientificName?: string;
    imageURL?: string; // Can be external URL (Wikipedia) or backend path
    location?: [number, number];
}