import React from "react";
import { 
    BrowserRouter as Router, 
    Routes, 
    Route, 
} from "react-router-dom";
import Login from "./pages/Login";
import Landing from "./pages/Landing";
import Sighting from "./pages/Sighting";

export default function App() {
    return(
        <Router>
            <Routes>
                {/* TODO: NAV */}
                <Route path="/" element={<Landing/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/sighting" element={<Sighting/>}/>
            </Routes>
        </Router>
    )
}