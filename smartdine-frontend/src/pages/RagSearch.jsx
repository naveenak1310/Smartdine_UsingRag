import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Search.css";
import Lottie from "lottie-react";
import chef from "../assets/chef.json";
import MicrophoneAnim from "../assets/Microphone.json";
import ChefAnim from "../assets/Chefanim.json";

const API_BASE = "http://localhost:8080/api";

export default function RagSearch() {
  const [query, setQuery] = useState("");
  const [location, setLocation] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [changingLocation, setChangingLocation] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showAnim, setShowAnim] = useState(null);
  const [listening, setListening] = useState(false);

  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user"));

  const fetchSuggestions = async (text) => {
    setLocation(text);
    if (!text.trim()) return setSuggestions([]);

    const res = await fetch(
      `https://nominatim.openstreetmap.org/search?format=json&addressdetails=1&countrycodes=in&limit=8&q=${text}`
    );
    const data = await res.json();
    setSuggestions(data);
  };

  const selectLocation = async (place) => {
    const city =
      place.address.city ||
      place.address.town ||
      place.address.state ||
      place.address.county ||
      place.address.village ||
      "Unknown";

    setLocation(city);
    setSuggestions([]);
    setChangingLocation(false);

    await fetch("http://localhost:8080/api/user/update-location", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ id: user.id, location: city }),
    });

    const updated = { ...user, location: city };
    localStorage.setItem("user", JSON.stringify(updated));
  };

  const startVoiceInput = () => {
    const SpeechRecognition =
      window.SpeechRecognition || window.webkitSpeechRecognition;

    if (!SpeechRecognition) {
      setError("Voice recognition is not supported on this browser.");
      return;
    }

    const recognition = new SpeechRecognition();
    recognition.lang = "en-IN";
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    setListening(true);
    recognition.start();

    recognition.onresult = (e) => {
      const text = e.results[0][0].transcript;
      setQuery(text);
    };

    recognition.onerror = () => setError("Voice recognition failed.");
    recognition.onend = () => setListening(false);
  };

  const handleRagSearch = async () => {
    setError("");

    if (!query.trim()) {
      setError("Tell what food you feel like eating.");
      return;
    }

    if (!user?.location) {
      setError("Please select your city first.");
      return;
    }

    setShowAnim("chef");
    setLoading(true);

    try {
      const res = await fetch(`${API_BASE}/rag/recommend`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query }),
      });

      const data = await res.json();
      const city = user.location;

      if (data.bestRestaurant) data.bestRestaurant.location = city;
      if (data.alternatives)
        data.alternatives.forEach((r) => (r.location = city));

      setTimeout(() => {
        navigate("/result", { 
          state: { 
            result: {
              bestMatch: data.bestRestaurant,
              alternatives: data.alternatives,
              explanation: data.explanation
            }
          } 
        });
      }, 2200);
    } catch (err) {
      setError("RAG search failed. Make sure backend is running.");
      setShowAnim(null);
    } finally {
      setLoading(false);
    }
  };

  if (listening) {
    return (
      <div
        style={{
          position: "fixed",
          top: 0,
          left: 0,
          width: "100%",
          height: "100%",
          background: "rgba(0,0,0,0.5)",
          backdropFilter: "blur(5px)",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          color: "white",
          zIndex: 9999,
        }}
      >
        <Lottie
          animationData={MicrophoneAnim}
          loop={true}
          style={{ height: 260 }}
        />
        <p style={{ fontSize: "22px", marginTop: 10 }}>Listening…</p>
      </div>
    );
  }

  if (showAnim) {
    return (
      <div style={{ textAlign: "center", paddingTop: 100 }}>
        <Lottie
          animationData={chef}
          loop={false}
          style={{ height: 260 }}
        />
        <p style={{ marginTop: 10, fontSize: "18px" }}>
          🤖 AI is analyzing restaurants with RAG...
        </p>
      </div>
    );
  }

  return (
    <section className="search-page">
      <div className="search-card">
        <h2 className="chef-title">
          <span className="chef-text">🤖 RAG-Powered Search</span>
          <span className="chef-icon">
            <Lottie
              animationData={ChefAnim}
              loop={false}
              autoplay={true}
              style={{ height: 70 }}
            />
          </span>
        </h2>

        <p style={{ fontSize: "13px", color: "#666", marginBottom: "15px" }}>
          Using local embeddings + OpenRouter for intelligent recommendations
        </p>

        {user?.location && !changingLocation && (
          <p style={{ fontSize: "15px", marginBottom: "12px" }}>
            📍 <b>{user.location}</b>{" "}
            <button
              style={{
                border: "none",
                background: "none",
                color: "#6b2df8",
                cursor: "pointer",
                fontWeight: "600",
              }}
              onClick={() => setChangingLocation(true)}
            >
              Change
            </button>
          </p>
        )}

        {changingLocation && (
          <>
            <input
              className="location-input"
              placeholder="Search for a city..."
              value={location}
              onChange={(e) => fetchSuggestions(e.target.value)}
            />

            {suggestions.length > 0 && (
              <ul className="suggestions-box">
                {suggestions.map((s) => (
                  <li key={s.place_id} onClick={() => selectLocation(s)}>
                    {s.display_name}
                  </li>
                ))}
              </ul>
            )}
          </>
        )}

        <textarea
          rows={3}
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder='Describe your craving... ("spicy authentic biryani", "budget-friendly pizza", "romantic italian")'
        />

        {error && <p className="error-text">{error}</p>}

        <div className="button-row">
          <button
            className="primary-btn"
            disabled={loading}
            onClick={handleRagSearch}
          >
            {loading ? "AI Searching..." : "🤖 RAG Search"}
          </button>

          <button className="mic-btn" onClick={startVoiceInput}>
            Speak
          </button>
        </div>
      </div>
    </section>
  );
}
