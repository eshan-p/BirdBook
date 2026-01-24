import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Feed from "./pages/Feed";
import Landing from "./pages/Landing";
import Header from "./components/layout/Header";
import Sighting from "./pages/Sighting";
import Profile from "./pages/Profile";
import Birds from "./pages/Birds"; 
import Groups from "./pages/Groups";
import Friends from "./pages/Friends";
import GroupFeed from "./pages/GroupFeed";
import SearchResults from "./pages/SearchResults";
import { AuthProvider  } from "./context/AuthContext";

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <Header />
        <Routes>
          <Route path="/" element={<Landing />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/feed" element={<Feed />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/birds" element={<Birds />} /> 
          <Route path="/groups" element={<Groups />} />
          <Route path="/groups/:groupId/feed" element={<GroupFeed />} />
          <Route path="/friends" element={<Friends />} />
          <Route path="/sightings/:postId" element={<Sighting />} />
          <Route path="/search" element={<SearchResults />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}
