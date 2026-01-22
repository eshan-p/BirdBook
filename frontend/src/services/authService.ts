const API_URL = "http://localhost:8080/auth/login";

export async function login(username: string, role: string) {
  const response = await fetch(API_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ username, role })
  });

  if (!response.ok) {
    throw new Error("Login failed");
  }

  return response.json();
}
