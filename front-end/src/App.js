import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './componentes/navbar/navBar';
import Footer from './componentes/footer/footer';
import Login from './componentes/login/login';
import Home from './componentes/home/home';
import AltaUser from './componentes/altaUser/altaUser';
import UserList from './componentes/userList/userList';





import 'bootstrap/dist/css/bootstrap.min.css';
function App() {
  return (
    <Router>
      <div >
        <Navbar />
 
        <div >
          <Routes>
            
            <Route path="/home" element={<Home />} /> 
            <Route path="/login" element={<Login />} />
            <Route path="/userlist" element={<UserList/>} />
            <Route path="/altauser" element={<AltaUser />} />



           

   
          </Routes>
        </div>
        <Footer />
      </div>
    </Router>
  );
}

export default App;
