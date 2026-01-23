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

export default function App() {
  return (
    <Router>
      <Header />
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/feed" element={<Feed />} />
        <Route path="/birds" element={<Birds />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/sightings/:postId" element={<Sighting />} />
      </Routes>
    </Router>
  );
}
