import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './componentes/navbar/navBar';
import Footer from './componentes/footer/footer';
import Login from './componentes/login/login';


import 'bootstrap/dist/css/bootstrap.min.css';
function App() {
  return (
    <Router>
      <div >
        <Navbar />
 
        <div >
          <Routes>
         
            <Route path="/login" element={<Login />} />
           

   
          </Routes>
        </div>
        <Footer />
      </div>
    </Router>
  );
}

export default App;
