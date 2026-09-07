import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { eventApi } from "../api/client";
import type { EventResponse } from "../api/types";

export function Events() {
  const [events, setEvents] = useState<EventResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    eventApi
      .request<EventResponse[]>("/events")
      .then(setEvents)
      .catch(() => setError("Could not load events"))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="page-status">Loading events...</p>;
  if (error) return <p className="page-status form-error">{error}</p>;

  return (
    <div className="events-page">
      <h1>Now Showing</h1>
      {events.length === 0 && <p className="page-status">Nothing on the board yet.</p>}
      <div className="event-grid">
        {events.map((event) => (
          <Link to={`/events/${event.id}`} key={event.id} className="event-card">
            <div className="event-card-swatch" />
            <div className="event-card-body">
              <h2>{event.name}</h2>
              <p className="event-performer">{event.performer}</p>
              <span className="event-tag">{event.category}</span>
              <p className="event-datetime">{new Date(event.startTime).toLocaleString()}</p>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
