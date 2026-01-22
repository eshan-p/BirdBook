export interface User {
  id: string;
  username: string;
  profilePic?: string;
  friends: string[];
  posts: string[];
  groups: string[];
  role: string;
}