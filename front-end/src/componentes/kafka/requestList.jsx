import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const RequestList = () => {
  const location = useLocation();
  const [requests, setRequests] = useState([]);
  const [error, setError] = useState("");
  const navigate = useNavigate();


  const isExternal = location.state?.isExternal || false;

  // Cada vez que location cambie, se dispara la llamada
  useEffect(() => {

    fetchRequests();
  }, [location]);


  const fetchRequests = async () => {
    try {
      const response = await axios.get(
        "http://localhost:5000/api/requestlist",
        { params: { isExternal } }
      );

      const operations = response.data.operations || [];
      setRequests(Array.isArray(operations) ? operations : []);
    } catch (err) {
      console.error("Error obteniendo solicitudes:", err);
      setError("Error de conexión con el servidor");
    }
  };

  const handleCancelar = async (operation) => {

    const protoData = {
      idOperationMessage: operation.idOperationMessage,
      operationType: "CANCELAR",
    }

    try {
      const response = await axios.post(
        "http://localhost:5000/api/deleterequest", protoData

      );

      if (response.data.success) {
        alert("Solicitud dada de baja", response.data.message);
        fetchRequests();

      } else {
        alert("ERROR:", response.data.message);
      }


    } catch (err) {
      setError("Error de conexión con el servidor");
    }
  };

  const handleTransferir = (operation) => {
    navigate("/requestform", { state: { operation } });
  };


  return (
    <div className="p-6 bg-[#01000F] min-h-screen flex flex-col">
      {/* Título */}
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-4xl font-bold text-white">
          {isExternal ? "Solicitudes Externas" : "Solicitudes Propias"}
        </h1>
      </div>

      {/* Mensaje de error */}
      {error && <div className="text-red-500 mb-4">{error}</div>}

      {/* Tabla */}
      <div className="overflow-x-auto flex-grow">
        <table className="min-w-full border border-gray-700 text-center">
          <thead>
            <tr className="bg-gray-900 text-white">
              <th className="px-4 py-2 border border-gray-700">ID Solicitud</th>
              <th className="px-4 py-2 border border-gray-700">Organización</th>
              <th className="px-4 py-2 border border-gray-700">Donaciones</th>

              {/* Mostrar columna "Activo" solo si NO es externa */}
              {!isExternal && (
                <th className="px-4 py-2 border border-gray-700">Activo</th>
              )}
              <th className="px-4 py-2 border border-gray-700">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {requests.map((req, idx) => (
              <tr key={idx} className="text-gray-200">
                <td className="px-4 py-2 border border-gray-700">{req.idOperationMessage}</td>
                <td className="px-4 py-2 border border-gray-700">{req.idOrganization}</td>
                <td className="px-4 py-2 border border-gray-700 text-left">
                  {req.donations && req.donations.length > 0 ? (
                    <details className="cursor-pointer">
                      <summary className="text-blue-400 hover:underline">Ver Donaciones</summary>
                      <ul className="mt-2">
                        {req.donations.map((don, i) => (
                          <li key={i} className="py-1 border-b border-gray-700 text-sm">
                            <span className="font-semibold text-white">{don.category}</span> - {don.description}
                          </li>
                        ))}
                      </ul>
                    </details>
                  ) : (
                    "Sin donaciones"
                  )}
                </td>


                {/* Mostrar columna "Activo" solo si NO es externa */}
                {!isExternal && (
                  <td className="px-4 py-2 border border-gray-700">
                    {req.active ? (
                      <span className="text-green-400 font-semibold">Sí</span>
                    ) : (
                      <span className="text-red-400 font-semibold">No</span>
                    )}
                  </td>
                )}

                {/* Acciones */}
                <td className="px-4 py-2 border border-gray-700">
                  {isExternal ? (
                    <button
                      onClick={() => handleTransferir(req)}
                      className="px-3 py-1 bg-green-600 hover:bg-green-700 text-white rounded"
                    >
                      Transferir
                    </button>
                  ) : (
                    <button
                      disabled={!req.active}
                      onClick={() => handleCancelar(req)}
                      className={`px-3 py-1 rounded text-white transition 
                        ${req.active
                          ? "bg-red-600 hover:bg-red-700 cursor-pointer"
                          : "bg-gray-600 cursor-not-allowed opacity-70"
                        }`}
                    >
                      Cancelar Solicitud
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {requests.length === 0 && !error && (
          <p className="text-gray-400 text-center mt-4">
            No se encontraron solicitudes.
          </p>
        )}
      </div>
    </div>
  );
};

export default RequestList;
