export interface Bird {
    id: string;
    commonName: string;
    scientificName: string;
    image: string;
    location?: [number, number];
}