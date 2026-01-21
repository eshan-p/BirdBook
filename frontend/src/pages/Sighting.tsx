import React from 'react';
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

//Actual page
import { getSightingById } from "../api/Sightings";
import { getUserById } from "../api/Users";
import { Post } from "../types/Post";
import { User } from "../types/User";
import { Comment } from "../types/Comment";
import { parseDate } from '../utils/dateTime';
import { getTimeSince } from '../utils/dateTime';

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
      <small>Posted {parseDate(post.timestamp).toDateString()}  ·{"  "}</small>
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
  //console.log(comments[0].timestamp)
  return (
    <ul>
      {comments.map((comment) => (
        <CommentItem
          key={`${comment.userId}-${comment.timestamp}`}
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

  //console.log(comment.timestamp);
  return (
    <li>
      <small>
        {user ? user.username : "Unknown user"}  ·{"  "}
        
        {parseDate(comment.timestamp).toDateString()}
      </small>
      <p>{comment.textBody}</p>
    </li>
  );
}

