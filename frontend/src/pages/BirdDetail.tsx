import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { Bird } from "../types/Bird";
import { fetchBirdById } from "../services/birdService";

export default function BirdDetail() {
  const { birdId } = useParams<{ birdId: string }>();
  const [bird, setBird] = useState<Bird | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!birdId) return;

    fetchBirdById(birdId)
      .then(setBird)
      .catch(() => setBird(null))
      .finally(() => setLoading(false));
  }, [birdId]);

  if (loading) {
    return <p className="p-6">Loading bird...</p>;
  }

  if (!bird) {
    return (
      <div className="p-6">
        <p className="text-gray-600">Bird not found.</p>
        <Link to="/birds" className="text-blue-600 underline">
          Back to Birds
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto p-6 bg-white drop-shadow">
      <Link to="/birds" className="text-blue-600 underline">
        ← Back to Birds
      </Link>

      <img
        src={bird.image || "/placeholder-bird.png"}
        alt={bird.commonName}
        className="w-full h-64 object-cover rounded mt-4"
      />

      <h1 className="text-3xl font-semibold mt-4">
        {bird.commonName}
      </h1>

      {bird.scientificName && (
        <p className="text-gray-500 italic">
          {bird.scientificName}
        </p>
      )}

      {bird.location && (
        <p className="mt-4 text-sm text-gray-600">
          Location: {bird.location[1]}, {bird.location[0]}
        </p>
      )}
    </div>
  );
}
