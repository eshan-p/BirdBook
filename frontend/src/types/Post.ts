import { Comment } from "../types/Comment";

export interface Post {
  //mongo userid must be treated as  a string in ts
  id: string;

  header:string;
  tags:Record<string,string>;

  bird:string;
  flagged:string;

  group?:string|null;
  help:boolean;

  likes:string[];

  imageUrl?:string|null;
  textBody:string;
  
  timestamp: string;

  comments:Comment[];

  userId:string;
}