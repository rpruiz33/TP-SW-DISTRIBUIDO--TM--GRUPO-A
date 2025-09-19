import React from 'react';
import './navBar.css';
import { Link , useNavigate } from 'react-router-dom';

const NavBar = () => {
  const isLoggedIn = localStorage.getItem("usernameOrEmail") !== null;
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/"); 
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-nav">
      <div className="container-fluid">
        <Link className="navbar-brand" to={isLoggedIn ? "/dashboard" : "/"}>
          Sistema empuje Comunitario
        </Link>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon" />
        </button>

        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto">

            {!isLoggedIn && (
              <li className="nav-item">
                <Link className="nav-link" to="/login">
                  Login
                </Link>
              </li>
            )}

            {isLoggedIn && (
              <li className="nav-item">
                <button
                  onClick={handleLogout}
                  className="btn btn-danger nav-link border-0"
                >
                  Logout
                </button>
              </li>
            )}

          </ul>
        </div>
      </div>
    </nav>
  );
};

export default NavBar;
