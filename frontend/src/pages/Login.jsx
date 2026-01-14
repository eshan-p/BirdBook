import { useState } from "react";
import { login } from "../services/authService";

export default function Login() {
  const [username, setUsername] = useState("");
  const [role, setRole] = useState("BASIC");
  const [message, setMessage] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();

    try {
      const data = await login(username, role);
      localStorage.setItem("token", data.token);
      setMessage("Login successful. Token saved.");
    } catch (err) {
      setMessage("Login failed.");
    }
  }

  return (
    <div style={{ padding: "2rem" }}>
      <h2>Login</h2>

      <form onSubmit={handleSubmit}>
        <input
          placeholder="Username"
          value={username}
          onChange={e => setUsername(e.target.value)}
        />

        <select value={role} onChange={e => setRole(e.target.value)}>
          <option value="BASIC">Basic User</option>
          <option value="ADMIN">Admin</option>
          <option value="SUPER">Super User</option>
        </select>

        <button type="submit">Login</button>
      </form>

      <p>{message}</p>
    </div>
  );
}
