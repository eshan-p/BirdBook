import React from 'react';
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

//src/types/Post.ts
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
  
  timeStamp: string;

  comments:Comment[];

  userId:string;
}

//src/types/User.ts
export interface User {
  id: string;

  username: string;
}

//src/types/Comment.ts
export interface Comment {
  id: string;
  userId:string;
  textBody:string;
  timeStamp:string;//iso date string
}

//fetch function - src/api/sightings.ts
//import {Post} from "../types/Post";
const BASE_URL = "http://localhost:8080";

export async function getSightingById(postId:string): Promise<Post>{
  const response = await fetch(`${BASE_URL}/sightings/${postId}`);

  if (!response.ok){
    if (response.status == 404){
      throw new Error("Post not found");
    }
    throw new Error("Failed to fetch Post");
  }// if response not ok

  return response.json();
}//get sighting by Id

//second fetch function - src/api/users.ts
//import {User} from "../types/User";
//const BASE_URL = "http://localhost:8080";

export async function getUserById(userId:string): Promise<User>{
  const response = await fetch(`${BASE_URL}/users/${userId}`);

  if (!response.ok){
    if (response.status == 404){
      throw new Error("User not found");
    }
    throw new Error("Failed to fetch User");
  }// if response not ok

  return response.json();
}


//Actual page
//import { getSightingById } from "../api/sightings";
//import { Post } from "../types/Post";


function Sighting() {
  //grabs params from the current url
  const {postId} = useParams<{postId:string}>();

  //these return value and functions to update the values
  const [post, setPost] = useState<Post | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [user,setUser] = useState<User | null>(null);

  //first fetch post
  useEffect(() => {
    if (!postId) return;

    setLoading(true);
    

    getSightingById(postId)
      .then(setPost)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));

  }, [postId]);
/*
  //then fetch user
  useEffect(() => {
  if (!post || !post.userId) return;

  //setLoading(true);
  post.userId = '695c1b84f0e6716530e00b4c';

  console.log(post.userId);

  getUserById(post.userId)
    .then(setUser)
    .catch((err) => {
      console.error(err);
      setUser(null);
    });
}, [post]);
*/

useEffect(() => {
  const testUserId = "695c1b84f0e6716530e00b4c";

  getUserById(testUserId)
    .then(setUser)
    .catch((err) => {
      console.error("User fetch failed:", err);
      setUser(null);
    });
}, []);


  if (loading) return <p>Loading...</p>;
  if (error) return <p>Error: {error}</p>;
  if (!post) return <p>Post not found</p>;

  return (
    <div>
      <h1>{post.header}</h1>
      <small>Likes: {post.likes.length}</small>
      <p>{post.textBody}</p>

      {post.imageUrl && (
        <img src={post.imageUrl} alt={post.header} />
      )}

      <p>Posted by: {user? user.username:"Unknown user"}</p>
      {post.tags?.location && (<p>Location: {post.tags.location}</p>)}
      {post.tags?.bird && (<p>Bird: {post.tags.bird}</p>)}
      {post && <CommentsList comments={post.comments} />}
    </div>
  );
}

export default Sighting;

//keep this nested here in the sighting page, as its only used here
//TODO, add usernames given userIds
function CommentsList({comments}: {comments:Comment[]}){
  
  if(comments.length ===0){
    return <p>No Comments yet...</p>
  }
  //console.log(comments[0].timeStamp)
  return (
    <ul>
      {comments.map((comment) => (
        <CommentItem
          key={`${comment.userId}-${comment.timeStamp}`}
          comment={comment}
        />
      ))}
    </ul>
  );
}

//comment item
//new src/pages/commentItem
function CommentItem({ comment }: { comment: Comment }) {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    if (!comment.userId) return;

    getUserById(comment.userId)
      .then(setUser)
      .catch(() => setUser(null));
  }, [comment.userId]);

  return (
    <li>
      <small>
        {user ? user.username : "Unknown user"} ·{" "}
        {new Date(comment.timeStamp).toLocaleString()}
      </small>
      <p>{comment.textBody}</p>
    </li>
  );
}

