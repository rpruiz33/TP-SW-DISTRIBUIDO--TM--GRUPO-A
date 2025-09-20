import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

const NavBar = () => {
  const isLoggedIn = localStorage.getItem("usernameOrEmail") !== null;
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/");
  };


  return (
    <nav className="bg-[#232D4F] text-gray-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <div className="flex-shrink-0">
            <Link to={isLoggedIn ? "/dashboard" : "/login"} className="text-white text-xl font-semibold">
              Sistema Empuje Comunitario
            </Link>
          </div>

          {/* Menu */}
          <div className="flex items-center space-x-4">
            {!isLoggedIn && (
              <Link to="/login" className="text-gray-100 hover:text-blue-400 px-3 py-2 rounded-md text-sm font-medium">
                Login
              </Link>
            )}
            {isLoggedIn && (
              <button
                onClick={handleLogout}
                className="bg-red-600 hover:bg-red-700 text-white px-3 py-2 rounded-md text-sm font-medium"
              >
                Logout
              </button>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default NavBar;
