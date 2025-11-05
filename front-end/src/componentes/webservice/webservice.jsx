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
        <h1 className="text-3xl font-bold text-white">Web Service Opciones</h1>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-8 justify-items-center items-center w-full">
          {/* Columna 1: Reporte de Eventos */}
          <div className="w-full flex justify-center col-start-1">
            <button
              onClick={() => navigate("/eventreport")}
              className="text-lg bg-blue-700 hover:bg-blue-800 text-white px-8 sm:px-16 py-4 rounded w-full sm:w-auto max-w-md"
            >
              Reporte de Eventos
            </button>
          </div>

          {/* Columna 2: Reporte de Donaciones Enviadas (y Reporte de Presidentes irá también en esta columna) */}
          {role !== "VOLUNTARIO" && role !== "VOCAL" && (
            <div className="w-full flex justify-center col-start-2">
              <button
                onClick={() => navigateDonationReport(false)}
                className="text-lg bg-blue-700 hover:bg-blue-800 text-white px-8 sm:px-16 py-4 rounded w-full sm:w-auto max-w-md"
              >
                Reporte de Donaciones Enviadas
              </button>
            </div>
          )}

          {/* Columna 3: Reporte de Donaciones Recibidas */}
          {role !== "VOLUNTARIO" && role !== "VOCAL" && (
            <div className="w-full flex justify-center col-start-3">
              <button
                onClick={() => navigateDonationReport(true)}
                className="text-lg bg-blue-700 hover:bg-blue-800 text-white px-8 sm:px-16 py-4 rounded w-full sm:w-auto max-w-md"
              >
                Reporte de Donaciones Recibidas
              </button>
            </div>
          )}

          {/* Reporte de Presidentes: forzamos columna 2 para que quede alineado con Donaciones Enviadas */}
          {role === "PRESIDENTE" && (
            <div className="w-full flex justify-center col-start-2 mt-6">
              <button
                onClick={() => navigate("/presidentreport")}
                className="text-lg bg-blue-700 hover:bg-blue-800 text-white px-8 sm:px-16 py-4 rounded w-full sm:w-auto max-w-md"
              >
                Reporte de Presidentes y ONGs
              </button>
            </div>
          )}

        </div>
      </div>
      {/* Footer anclado abajo */}
    </div>
  );
};



export default WebService;
