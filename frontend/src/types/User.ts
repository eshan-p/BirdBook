export interface User {
  id: string;
  username: string;
  profilePic?: string;
  location?: [number, number];
  friends?: string[];
  posts: string[];
  groups: string[];
  role: string;
}