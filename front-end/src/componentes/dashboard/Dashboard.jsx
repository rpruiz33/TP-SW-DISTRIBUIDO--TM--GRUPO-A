import React from "react";
import { useNavigate } from "react-router-dom";

const Dashboard = () => {
  const navigate = useNavigate();
  const role = localStorage.getItem("userRole"); // 🔹 Ya está en mayúsculas

  if (role !== "PRESIDENTE") {
    return <p className="text-center text-red-600 mt-5">Acceso denegado</p>;
  }

  return (
    <div className="p-6 flex flex-col items-center gap-6">
      <h1 className="text-2xl font-bold text-[#232D4F]">Panel del Presidente</h1>
      <div className="flex gap-4">
        <button
          onClick={() => navigate("/userlist")}
          className="px-4 py-2 bg-[#232D4F] text-black rounded-lg hover:opacity-90"
        >
          Gestion de Usuarios
        </button>
        <button
          onClick={() => navigate("/eventlist")}
          className="px-4 py-2 bg-[#232D4F] text-black rounded-lg hover:opacity-90"
        >
          Gestion de Eventos
        </button>

        <button           
        className="px-4 py-2 bg-[#232D4F] text-black rounded-lg hover:opacity-90"
        onClick={()=>{navigate("/donationlist")}}   
>
          Gestión de Inventario
        </button>
      </div>
    </div>
  );
};

export default Dashboard;
