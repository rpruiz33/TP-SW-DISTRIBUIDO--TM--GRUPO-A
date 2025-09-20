import React from "react";
import { useNavigate } from "react-router-dom";

const Dashboard = () => {
  const navigate = useNavigate();
  const role = localStorage.getItem("userRole"); // PRESUPUESTO: siempre en mayúsculas

  // 🔹 Lógica de visibilidad de botones
  const showUserButton = role === "PRESIDENTE";
  const showEventButton = role === "PRESIDENTE" || role === "COORDINADOR" || role === "VOLUNTARIO";
  const showInventoryButton = role === "PRESIDENTE" || role === "VOCAL";

  return (
    <div className="min-h-screen flex flex-col bg-[#01000F]">
      <div className="flex-grow flex flex-col items-center justify-center gap-8 p-6">
        <h1 className="text-5xl font-bold text-[#4034ED]">Panel del {role}</h1>

        <div className="flex flex-wrap gap-6 justify-center">
          {showUserButton && (
            <button
              onClick={() => navigate("/userlist")}
              className="px-8 py-4 text-lg bg-[#232D4F] text-white rounded-lg shadow hover:opacity-90 transition"
            >
              Gestión de Usuarios
            </button>
          )}

          {showEventButton && (
            <button
              onClick={() => navigate("/eventlist")}
              className="px-8 py-4 text-lg bg-[#232D4F] text-white rounded-lg shadow hover:opacity-90 transition"
            >
              Gestión de Eventos
            </button>
          )}

          {showInventoryButton && (
            <button
              onClick={() => navigate("/donationlist")}
              className="px-8 py-4 text-lg bg-[#232D4F] text-white rounded-lg shadow hover:opacity-90 transition"
            >
              Gestión de Inventario
            </button>
          )}
        </div>
      </div>
      {/* Footer anclado abajo */}
    </div>
  );
};



export default Dashboard;
