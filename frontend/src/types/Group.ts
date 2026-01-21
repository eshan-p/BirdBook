export interface Group {
    id: string;
    name: string;
    ownerId: string;
    members: string[];
    requests: string[];
    groupPhoto?: string;
    location?: [number, number];
    followers?: number;
}