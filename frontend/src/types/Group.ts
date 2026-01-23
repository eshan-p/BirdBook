export interface PostUser {
    userId: string;
    username: string;
}

export interface Group {
    id: string;
    name: string;
    owner: PostUser;
    members: PostUser[];
    requests: PostUser[];
    groupPhoto?: string;
    location?: [number, number];
    followers?: number;
}