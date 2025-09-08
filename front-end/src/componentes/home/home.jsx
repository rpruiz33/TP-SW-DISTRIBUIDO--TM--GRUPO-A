import React from "react";
import { useNavigate } from "react-router-dom";


const Home = () => {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
      <h1 className="text-3xl font-bold mb-8">Panel de Administración</h1>
      <div className="space-y-4">
        <button className="px-6 py-3 bg-blue-500 text-black rounded-lg shadow hover:bg-blue-600 transition"
         onClick={()=>{navigate("/userlist")}}   
        >
          Gestión de Usuarios
        </button>
        <button className="px-6 py-3 bg-green-500 text-black rounded-lg shadow hover:bg-green-600 transition"
        onClick={()=>{navigate("/donationlist")}}   
>
          Gestión de Inventario
        </button>
      </div>
    </div>
  );
};

export default Home;
