import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import { buildMapsUrl } from "../utils/mapshelper";
import { getAreaForRestaurant } from "../utils/locationHelper";

export default function Result() {
  const location = useLocation();
  const navigate = useNavigate();
  const result = location.state?.result;

  const user = JSON.parse(localStorage.getItem("user"));
  const city = user?.location || "Default";

  if (!result) {
    return (
      <section className="result-page">
        <div className="result-card">
          <p>There is nothing to show yet. Try starting from the search page.</p>
          <button className="secondary-btn" onClick={() => navigate("/search")}>
            Go to search
          </button>
        </div>
      </section>
    );
  }

  const { query, bestMatch, alternatives, explanation } = result;
  const [currentImage] = useState(0);
  const imageList = bestMatch?.images ? bestMatch.images.split(",") : [];

  const bestArea = bestMatch
    ? (city === "Coimbatore"
        ? bestMatch.area
        : getAreaForRestaurant(bestMatch.id, city))
    : "";

  const bestCity = bestMatch
    ? (city === "Coimbatore"
        ? bestMatch.location
        : city)
    : "";

  return (
    <section className="result-page">
      <div className="result-card">
        <h2>Here's a suggestion</h2>

        {query && (
          <p className="user-query">
            You said: <span>{query}</span>
          </p>
        )}

        {!bestMatch && explanation && (
          <div style={{
            padding: "20px",
            background: "#fff3cd",
            borderLeft: "4px solid #ffc107",
            borderRadius: "4px",
            marginBottom: "20px"
          }}>
            <p style={{ fontSize: "16px", lineHeight: "1.6", margin: 0 }}>
              {explanation}
            </p>
          </div>
        )}

        {bestMatch && (
          <div className="best-wrapper">
            <div className="best-left">
              <div className="restaurant-main">
                <h3>{bestMatch.name}</h3>

                <p className="restaurant-meta">
                  {bestMatch.cuisine} ·{" "}
                  {bestMatch.priceRange?.toUpperCase()} · ⭐{" "}
                  {bestMatch.rating > 0
                    ? bestMatch.rating.toFixed(1)
                    : "Not rated yet"}
                </p>

                <p className="restaurant-location">
                  📍 {bestArea}, {bestCity}{" "}
                  <a
                    href={buildMapsUrl(bestMatch)}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    Open in Maps
                  </a>
                </p>

                <p className="restaurant-desc">{bestMatch.description}</p>

                {explanation && (
                  <div style={{
                    marginTop: "15px",
                    padding: "12px",
                    background: "#f0f7ff",
                    borderLeft: "4px solid #6b2df8",
                    borderRadius: "4px"
                  }}>
                    <strong>🤖 AI Recommendation:</strong>
                    <p style={{ marginTop: "8px", fontSize: "14px", lineHeight: "1.6" }}>
                      {explanation}
                    </p>
                  </div>
                )}
              </div>

              <button
                className="primary-btn"
                style={{ marginTop: "10px" }}
                onClick={() => navigate(`/restaurant/${bestMatch.id}`)}
              >
                🍽 Book Now
              </button>
            </div>

            {imageList.length > 0 && (
              <img
                src={imageList[currentImage]}
                alt={bestMatch.name}
                className="best-image"
              />
            )}
          </div>
        )}

        {alternatives && alternatives.length > 0 && (
          <div className="alt-list">
            <h4>Other places you could check</h4>

            <div className="alt-container">
              {alternatives.map((r) => {
                const altArea =
                  city === "Coimbatore"
                    ? r.area
                    : getAreaForRestaurant(r.id, city);

                const altCity =
                  city === "Coimbatore"
                    ? r.location
                    : city;

                return (
                  <div className="alt-card" key={r.id}>
                    <div className="alt-info">
                      <h3>{r.name}</h3>

                      <p className="alt-meta">
                        {r.cuisine} ({r.priceRange}) · ⭐{" "}
                        {r.rating > 0
                          ? r.rating.toFixed(1)
                          : "Not rated yet"}
                      </p>

                      <p className="alt-location">
                        📍 {altArea}, {altCity}{" "}
                        <a
                          href={buildMapsUrl(r)}
                          target="_blank"
                          rel="noopener noreferrer"
                        >
                          Open in Maps
                        </a>
                      </p>
                    </div>

                    <button
                      className="primary-btn alt-book-btn"
                      onClick={() => navigate(`/restaurant/${r.id}`)}
                    >
                      🍽 Book Now
                    </button>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        <div className="button-row">
          <button className="secondary-btn" onClick={() => navigate("/search")}>
            New search
          </button>
        </div>
      </div>
    </section>
  );
}
 