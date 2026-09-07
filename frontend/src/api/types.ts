export interface AuthResponse {
  token: string;
}

export interface EventResponse {
  id: number;
  venueId: number;
  name: string;
  performer: string;
  category: string;
  startTime: string;
}

export interface EventSeatResponse {
  eventSeatId: number;
  seatId: number;
  section: string;
  seatNumber: string;
  tier: "VIP" | "PREMIUM" | "STANDARD" | "ECONOMY";
  price: number;
  status: "AVAILABLE" | "BOOKED";
}

export interface BookingResponse {
  paymentId: number;
  status: string;
}

export interface BookingSummaryResponse {
  id: number;
  eventId: number;
  totalAmount: number;
  status: "CONFIRMED" | "CANCELLED" | "REFUNDED";
  createdAt: string;
  eventSeatIds: number[];
}
