import React from "react";
const Footer = () => {
return (
    <footer className="bg-[#232D4F] text-gray-100">
      {/* Redes sociales */}
      <div className="flex justify-center space-x-4 py-2">
        <a
          href="#!"
          className="text-gray-100 hover:text-blue-400"
          aria-label="Facebook"
        >
          <i className="fab fa-facebook-f"></i>
        </a>
        <a
          href="#!"
          className="text-gray-100 hover:text-blue-400"
          aria-label="Twitter"
        >
          <i className="fab fa-twitter"></i>
        </a>
        <a
          href="#!"
          className="text-gray-100 hover:text-blue-400"
          aria-label="Instagram"
        >
          <i className="fab fa-instagram"></i>
        </a>
      </div>

      {/* Texto de copyright */}
      <div className="text-center text-sm py-2">
        <p>SISTEMA--EMPUJE--COMUNITARIO</p>
        <p>© 2025 Copyright</p>
      </div>
    </footer>
  );
};

export default Footer; 