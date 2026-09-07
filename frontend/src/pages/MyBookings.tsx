import { useEffect, useState } from "react";
import { bookingApi } from "../api/client";
import type { BookingSummaryResponse } from "../api/types";

export function MyBookings() {
  const [bookings, setBookings] = useState<BookingSummaryResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    bookingApi
      .request<BookingSummaryResponse[]>("/bookings")
      .then(setBookings)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="page-status">Loading...</p>;

  return (
    <div className="bookings-page">
      <h1>My Tickets</h1>
      {bookings.length === 0 && <p className="page-status">No tickets booked yet.</p>}
      <div className="bookings-list">
        {bookings.map((booking) => (
          <div key={booking.id} className="booking-card">
            <div className="booking-card-main">
              <span className="booking-card-title">Booking #{booking.id}</span>
              <span className="booking-card-meta">
                {booking.eventSeatIds.length} seat{booking.eventSeatIds.length === 1 ? "" : "s"} for event #{booking.eventId}
              </span>
              <span className="booking-card-meta">{new Date(booking.createdAt).toLocaleString()}</span>
            </div>
            <div className="booking-card-right">
              <span className={`status-badge ${booking.status.toLowerCase()}`}>{booking.status}</span>
              <span className="booking-card-price">${booking.totalAmount.toFixed(2)}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
