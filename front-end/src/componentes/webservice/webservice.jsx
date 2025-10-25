import React from "react";
import { useNavigate } from "react-router-dom";

const WebService = () => {
  const navigate = useNavigate();

  const role = localStorage.getItem("userRole");// 👈 obtenemos el rol (ej: "presidente")



  const navigateDonationReport = (isOther) => {

    navigate("/donationreport", { state: { isOther } });
  };

  return (
    <div className="min-h-screen flex flex-col bg-[#01000F]">
      <div className="flex-grow flex flex-col items-center justify-center gap-8 p-6">
        <h1 className="text-3xl font-bold text-white">Web Service Options</h1>

        <div className="flex flex-wrap gap-8 justify-center">
          
            <button
              onClick={() => navigate("/eventreport")}
              className="text-lg bg-blue-700 hover:bg-blue-800 text-white px-16 py-4 rounded "
            >
              Reporte de Eventos
            </button>

          {role !== "VOLUNTARIO" && role !== "VOCAL" && (
            <button
              onClick={() => navigateDonationReport(false)}
              className="text-lg bg-blue-700 hover:bg-blue-800 text-white px-16 py-4 rounded "
            >
              Reporte de Donaciones Enviadas
            </button>
          )}

          {role !== "VOLUNTARIO" && role !== "VOCAL"  && (
            <button
              onClick={() => navigateDonationReport(true)}
              className="text-lg bg-blue-700 hover:bg-blue-800 text-white px-16 py-4 rounded "
            >
              Reporte de Donaciones Recibidas
            </button>
          )}

          {role === "PRESIDENTE"  && (
            <button
              onClick={() => navigate("/presidentreport")}
              className="text-lg bg-blue-700 hover:bg-blue-800 text-white px-16 py-4 rounded "
            >
              Reporte de Presidentes y ONGs
            </button>
          )}

        </div>
      </div>
      {/* Footer anclado abajo */}
    </div>
  );
};



export default WebService;
