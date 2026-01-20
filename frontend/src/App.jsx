import React from "react";
import { 
    BrowserRouter as Router, 
    Routes, 
    Route, 
} from "react-router-dom";

import Login from "./pages/Login";
import Landing from "./pages/Landing";
import Sighting from "./pages/Sighting";
import Feed from "./pages/Feed";
import Header from "./components/layout/Header";

export default function App() {
    return(
        <Router>
            <Header/>
            <Routes>
                <Route path="/" element={<Landing/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/feed" element={<Feed/>}/>
                <Route path="/sightings/:postId" element={<Sighting/>}/>
            </Routes>
        </Router>
    )
}