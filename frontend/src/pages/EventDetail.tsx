import { useCallback, useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { bookingApi, eventApi, ApiError } from "../api/client";
import type { BookingResponse, BookingSummaryResponse, EventResponse, EventSeatResponse } from "../api/types";

type BookingPhase = "idle" | "pending" | "confirmed" | "timed-out" | "error";

const POLL_INTERVAL_MS = 2000;
const POLL_TIMEOUT_MS = 20000;

function sameSeatSet(a: number[], b: number[]): boolean {
  if (a.length !== b.length) return false;
  const sortedA = [...a].sort();
  const sortedB = [...b].sort();
  return sortedA.every((v, i) => v === sortedB[i]);
}

export function EventDetail() {
  const { id } = useParams<{ id: string }>();
  const eventId = Number(id);

  const [event, setEvent] = useState<EventResponse | null>(null);
  const [seats, setSeats] = useState<EventSeatResponse[]>([]);
  const [selected, setSelected] = useState<number[]>([]);
  const [phase, setPhase] = useState<BookingPhase>("idle");
  const [message, setMessage] = useState<string | null>(null);

  const pollTimer = useRef<ReturnType<typeof setInterval> | null>(null);
  const pollDeadline = useRef<number>(0);

  const loadSeats = useCallback(async () => {
    const seatData = await eventApi.request<EventSeatResponse[]>(`/events/${eventId}/seats`);
    setSeats(seatData);
  }, [eventId]);

  useEffect(() => {
    eventApi.request<EventResponse>(`/events/${eventId}`).then(setEvent).catch(() => setMessage("Could not load event"));
    loadSeats().catch(() => setMessage("Could not load seats"));
  }, [eventId, loadSeats]);

  useEffect(() => {
    return () => {
      if (pollTimer.current) clearInterval(pollTimer.current);
    };
  }, []);

  const toggleSeat = (seat: EventSeatResponse) => {
    if (seat.status !== "AVAILABLE" || phase === "pending") return;
    setSelected((prev) =>
      prev.includes(seat.eventSeatId) ? prev.filter((s) => s !== seat.eventSeatId) : [...prev, seat.eventSeatId]
    );
  };

  const startPolling = (seatIds: number[]) => {
    pollDeadline.current = Date.now() + POLL_TIMEOUT_MS;
    pollTimer.current = setInterval(async () => {
      try {
        const bookings = await bookingApi.request<BookingSummaryResponse[]>("/bookings");
        const match = bookings.find((b) => b.eventId === eventId && sameSeatSet(b.eventSeatIds, seatIds));
        if (match) {
          clearPoll();
          setPhase("confirmed");
          setMessage(`Booking confirmed — $${match.totalAmount.toFixed(2)}`);
          setSelected([]);
          loadSeats();
          return;
        }
      } catch {
        // transient poll failure, keep trying until the deadline
      }
      if (Date.now() >= pollDeadline.current) {
        clearPoll();
        setPhase("timed-out");
        setMessage("No confirmation yet — the payment may have failed. Seats have been released, feel free to try again.");
        loadSeats();
      }
    }, POLL_INTERVAL_MS);
  };

  const clearPoll = () => {
    if (pollTimer.current) {
      clearInterval(pollTimer.current);
      pollTimer.current = null;
    }
  };

  const handleBook = async () => {
    if (selected.length === 0) return;
    setPhase("pending");
    setMessage("Submitting booking...");
    try {
      await bookingApi.request<BookingResponse>("/bookings", {
        method: "POST",
        body: JSON.stringify({ eventId, eventSeatIds: selected }),
      });
      setMessage("Payment processing — waiting for confirmation...");
      startPolling(selected);
    } catch (err) {
      setPhase("error");
      setMessage(err instanceof ApiError ? err.message : "Booking failed");
    }
  };

  if (!event) return <p className="page-status">Loading...</p>;

  const total = seats
    .filter((s) => selected.includes(s.eventSeatId))
    .reduce((sum, s) => sum + s.price, 0);

  const sections = Array.from(new Set(seats.map((s) => s.section)));

  return (
    <div className="event-detail-page">
      <h1>{event.name}</h1>
      <p className="event-detail-sub">
        {event.performer}
        <span className="event-tag">{event.category}</span>
        {new Date(event.startTime).toLocaleString()}
      </p>

      <div className="stage-indicator">
        <div className="stage-indicator-arc" />
        <span className="stage-label">STAGE</span>
      </div>

      {sections.map((section) => (
        <div key={section} className="seat-section">
          <h3>{section}</h3>
          <div className="seat-grid">
            {seats
              .filter((s) => s.section === section)
              .map((seat) => (
                <button
                  key={seat.eventSeatId}
                  className={`seat-btn ${seat.status.toLowerCase()} ${selected.includes(seat.eventSeatId) ? "selected" : ""}`}
                  disabled={seat.status !== "AVAILABLE" || phase === "pending"}
                  onClick={() => toggleSeat(seat)}
                  title={`Seat ${seat.seatNumber}, ${seat.tier.toLowerCase()} tier, $${seat.price}`}
                >
                  {seat.seatNumber}
                </button>
              ))}
          </div>
        </div>
      ))}

      <div className="booking-bar">
        <div className="ticket-main">
          <span className="ticket-seat-count">
            {selected.length === 0
              ? "No seats selected"
              : `${selected.length} seat${selected.length === 1 ? "" : "s"} selected`}
          </span>
          <span className="ticket-total">${total.toFixed(2)}</span>
        </div>
        <div className="ticket-stub">
          <span className="ticket-perforation" />
          <button onClick={handleBook} disabled={selected.length === 0 || phase === "pending"}>
            {phase === "pending" ? "Processing" : "Book"}
          </button>
        </div>
      </div>

      {message && (
        <p className={`booking-status ${phase === "error" || phase === "timed-out" ? "form-error" : ""}`}>
          {message}
        </p>
      )}
    </div>
  );
}
