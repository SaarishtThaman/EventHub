import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function NavBar() {
  const { isAuthenticated, userId, role, logout } = useAuth();

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">EventHub</Link>
      <div className="navbar-links">
        <Link to="/">Now Showing</Link>
        {isAuthenticated && <Link to="/bookings">My Tickets</Link>}
      </div>
      <div className="navbar-user">
        {isAuthenticated ? (
          <>
            <span className="navbar-user-info">
              Signed in as {userId}
              {role && <span className="role-tag">{role}</span>}
            </span>
            <button className="ghost" onClick={logout}>Log out</button>
          </>
        ) : (
          <Link to="/login">Log in</Link>
        )}
      </div>
    </nav>
  );
}
