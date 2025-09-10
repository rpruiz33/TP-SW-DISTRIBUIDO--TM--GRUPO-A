import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './componentes/navbar/navBar';
import Footer from './componentes/footer/footer';
import Login from './componentes/login/login';
import Home from './componentes/home/home';
import AltaUser from './componentes/user/altaUser';
import UserList from './componentes/user/userList';
import DonationList from './componentes/donation/donationList';
<<<<<<< Updated upstream
import DonationForm from './componentes/donation/donationForm';







=======
import EventosView from './componentes/event/Event';
>>>>>>> Stashed changes
import 'bootstrap/dist/css/bootstrap.min.css';
import Events from './componentes/event/Event';
import Dashboard from './componentes/dashboard/Dashboard';

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
<<<<<<< Updated upstream
            <Route path="/donationlist" element={<DonationList />} />   
            <Route path="/donationform" element={<DonationForm />} />   



           

=======
            <Route path="/donationlist" element={<DonationList />} />
             <Route path="/events" element={<Events/>} />
             <Route path="/dashboard" element={<Dashboard/>} />
>>>>>>> Stashed changes
   
          </Routes>
        </div>
        <Footer />
      </div>
    </Router>
  );
}

export default App;
