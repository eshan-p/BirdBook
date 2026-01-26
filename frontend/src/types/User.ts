export interface User {
  id: string;
  username: string;
  firstName?: string;
  lastName?: string;
  profilePic?: string;
  location?: string;
  friends: string[];
  posts: string[];
  groups: string[];
  role: string;
}