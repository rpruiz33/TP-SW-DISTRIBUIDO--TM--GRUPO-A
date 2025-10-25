import React, { useEffect, useState } from "react";
import axios from "axios";

const OfferList = () => {
  const [offers, setOffers] = useState([]);
  const [error, setError] = useState("");

  // Se dispara solo al montar el componente
  useEffect(() => {
    fetchOffers();
  }, []);

  const fetchOffers = async () => {
    try {
      const response = await axios.get("http://localhost:5000/api/offerlist");
      const operations = response.data.operations || [];
      setOffers(Array.isArray(operations) ? operations : []);
    } catch (err) {
      console.error("Error obteniendo ofertas:", err);
      setError("Error de conexión con el servidor");
    }
  };

  return (
    <div className="p-6 bg-[#01000F] min-h-screen flex flex-col">
      {/* Título */}
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-4xl font-bold text-white">Ofertas Generales</h1>
      </div>

      {/* Mensaje de error */}
      {error && <div className="text-red-500 mb-4">{error}</div>}

      {/* Tabla */}
      <div className="overflow-x-auto flex-grow">
        <table className="min-w-full border border-gray-700 text-center">
          <thead>
            <tr className="bg-gray-900 text-white">
              <th className="px-4 py-2 border border-gray-700">ID Operación</th>
              <th className="px-4 py-2 border border-gray-700">Organización</th>
              <th className="px-4 py-2 border border-gray-700">Tipo Operación</th>
              <th className="px-4 py-2 border border-gray-700">Activo</th>
              <th className="px-4 py-2 border border-gray-700">Donaciones</th>
            </tr>
          </thead>
          <tbody>
            {offers.map((offer, idx) => (
              <tr key={idx} className="text-gray-200">
                <td className="px-4 py-2 border border-gray-700">{offer.idOperationMessage}</td>
                <td className="px-4 py-2 border border-gray-700">{offer.idOrganization}</td>
                <td className="px-4 py-2 border border-gray-700">{offer.operationType}</td>
                <td className="px-4 py-2 border border-gray-700">
                  {offer.active ? (
                    <span className="text-green-400 font-semibold">Sí</span>
                  ) : (
                    <span className="text-red-400 font-semibold">No</span>
                  )}
                </td>
                <td className="px-4 py-2 border border-gray-700 text-left">
                  {offer.donations && offer.donations.length > 0 ? (
                    <details className="cursor-pointer">
                      <summary className="text-blue-400 hover:underline">Ver Donaciones</summary>
                      <ul className="mt-2">
                        {offer.donations.map((don, i) => (
                          <li key={i} className="py-1 border-b border-gray-700 text-sm">
                            <span className="font-semibold text-white">{don.category}</span> - {don.description} (Cantidad: {don.quantity})
                          </li>
                        ))}
                      </ul>
                    </details>
                  ) : (
                    "Sin donaciones"
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {offers.length === 0 && !error && (
          <p className="text-gray-400 text-center mt-4">
            No se encontraron ofertas.
          </p>
        )}
      </div>
    </div>
  );
};

export default OfferList;
