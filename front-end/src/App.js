import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './componentes/navbar/navBar';
import Footer from './componentes/footer/footer';
import Login from './componentes/login/login';
import UserForm from './componentes/user/userForm';
import UserList from './componentes/user/userList';
import MemberManagment from './componentes/event/memberManagment';
import DonationList from './componentes/donation/donationList';
import DonationForm from './componentes/donation/donationForm';
import EventForm from './componentes/event/eventForm';
import DonationManagment from './componentes/event/donationManagment';
import AssignNewDonation from './componentes/event/assignNewDonation';



import 'bootstrap/dist/css/bootstrap.min.css';
import EventList from './componentes/event/eventList';
import Dashboard from './componentes/dashboard/Dashboard';

function App() {
  return (
    <Router>
      <div >
        <Navbar />
 
        <div >
          <Routes>
            
            <Route path="/dashboard" element={<Dashboard/>} />

            <Route path="/login" element={<Login />} />
            <Route path="/userlist" element={<UserList/>} />
            <Route path="/userform" element={<UserForm />} />

            <Route path="/donationlist" element={<DonationList />} />   
            <Route path="/donationform" element={<DonationForm />} />   

            <Route path="/eventlist" element={<EventList/>} />
            <Route path="/membermanagment" element={<MemberManagment />} />
            <Route path="/donationmanagment" element={<DonationManagment />} />
            <Route path="/assignnewdonation" element={<AssignNewDonation />} />

            <Route path="/eventform" element={<EventForm />} />
   
          </Routes>
        </div>
        <Footer />
      </div>
    </Router>
  );
}

export default App;
