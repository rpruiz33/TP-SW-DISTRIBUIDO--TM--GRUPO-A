import React from "react";
import './footer.css';
const Footer = () => {
  return (
    
    <footer className="text-center text-white py-3 mt-auto bg-nav">
           
         
        
      <div className="container p-4 pb-1 navbar-dark" style={{  backgroundColor:'#232D4F'}}>
        <section className="mb-4">
          <a className="btn btn-outline-light btn-floating m-1" href="#!" role="button">
            <i className="fab fa-facebook-f"></i>
          </a>
          <a className="btn btn-outline-light btn-floating m-1" href="#!" role="button">
            <i className="fab fa-twitter"></i>
          </a>
          <a className="btn btn-outline-light btn-floating m-1" href="#!" role="button">
            <i className="fab fa-instagram"></i>
          </a>
        </section>
      </div>

      <div className="text-center p-3" style={{  backgroundColor:'#232D4F'}}>
      <a className="text-white" href="#!">
          SISTEMA--EMPUJE--SOLIDARIO
          <br></br>
          <br></br>
        </a>
        © 2025 Copyright: 
       
    

       
      </div>
    </footer>
  );
};

export default Footer; 