export interface Bird {
  id: string;          // string
  _id?: never;         // prevent accidental usage
  commonName: string;
  scientificName?: string;
  image?: string;
  location?: [number, number];
}
