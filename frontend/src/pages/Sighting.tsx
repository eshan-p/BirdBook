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
import { reverseCoordsToCityState } from '../utils/geolocation';

function Sighting() {
  //grabs params from the current url
  const {postId} = useParams<{postId:string}>();

  //these return value and functions to update the values
  const [post, setPost] = useState<Post | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [user,setUser] = useState<User | null>(null);
  const [locationLabel, setLocationLabel] = useState<string | null>(null);

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
  if (!post?.userId) return;

  let userId: any = post.userId;

  // 🔑 Normalize ObjectId-shaped values
  if (typeof userId === "object") {
    userId = userId.id || userId._id || userId.$oid;
  }

  if (typeof userId !== "string") {
    console.error("Invalid post userId:", post.userId);
    return;
  }

  getUserById(userId)
    .then(setUser)
    .catch((err) => {
      console.error(err);
      setUser(null);
    });
}, [post?.userId]);
*/

//finally fetch location
useEffect(() => {
  if (!post?.tags?.location) return;

  let raw = post.tags.location as any;
  //console.log("RAW location tag:", raw);

  // 🔑 FIX: handle stringified JSON
  if (typeof raw === "string") {
    try {
      raw = JSON.parse(raw);
    } catch {
      console.error("Location is not valid JSON:", raw);
      return;
    }
  }

  const latitude = Number(raw.latitude);
  const longitude = Number(raw.longitude);

  //console.log("latitude:", latitude, "type:", typeof latitude);
  //console.log("longitude:", longitude, "type:", typeof longitude);

  if (!Number.isFinite(latitude) || !Number.isFinite(longitude)) {
    console.error("Invalid coordinates AFTER parsing:", raw);
    return;
  }

  reverseCoordsToCityState({ latitude, longitude })
    .then(setLocationLabel)
    .catch((err) => {
      console.error("Reverse geocode failed:", err);
      setLocationLabel(null);
    });
}, [post?.tags?.location]);


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
      {locationLabel && <p>Location: {locationLabel}</p>}
      {post.bird && (<p>Bird: {post.bird}</p>)}
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
          key={`${comment.user.id}-${comment.timestamp}`}
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

  /*
  useEffect(() => {
    if (!comment.user) return;

    getUserById(comment.userId)
      .then(setUser)
      .catch(() => setUser(null));
  }, [comment.userId]); */

  //console.log(comment.timestamp);
  return (
    <li>
      <small>
        {comment.user ? comment.user.username : "Unknown user"}  ·{"  "}
        
        {parseDate(comment.timestamp).toDateString()}
      </small>
      <p>{comment.textBody}</p>
    </li>
  );
}

