import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const DonationList = () => {
  const navigate = useNavigate();
  const [donations, setDonations] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    getDonations();
  }, []);

  const getDonations = async () => {
    try {
      const response = await axios.get("http://localhost:5000/api/donationlist");

      if (Array.isArray(response.data)) {
        setDonations(response.data);
      } else if (response.data.donations && Array.isArray(response.data.donations)) {
        setDonations(response.data.donations);
      } else {
        setDonations([]);
        console.warn("La respuesta no contiene un array:", response.data);
      }
    } catch (err) {
      console.error("Error en la solicitud:", err);
      setError("Error de conexión con el servidor");
    }
  };

 const editDonation = (donation) => {
    navigate("/donationform", { state: { donation } });
  };

  const deleteDonation = ()=>{}




return (
  <div className="p-6">
    <h1 className="text-2xl font-bold mb-4">Inventario de Donaciones</h1>
    {error && <div className="text-red-500 mb-4">{error}</div>}
    <div className="overflow-x-auto">
      <table className="min-w-full bg-white border border-gray-200">
        <thead>
          <tr className="bg-gray-100">
            <th className="px-4 py-2 border">Categoria</th>
            <th className="px-4 py-2 border">Descripcion</th>
            <th className="px-4 py-2 border">Cantidad</th>
            <th className="px-4 py-2 border">Eliminado</th>
            <th className="px-4 py-2 border">Historial de Entregas</th>
            <th className="px-4 py-2 border">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {donations.map((donation, index) => (
            <tr key={donation.id || index} className="text-center">
              <td className="px-4 py-2 border">{donation.category || "N/A"}</td>
              <td className="px-4 py-2 border">{donation.description || "N/A"}</td>
              <td className="px-4 py-2 border">{donation.amount || "N/A"}</td>
              <td className="px-4 py-2 border">
                {donation.removed != null ? (donation.removed ? "Sí" : "No") : "N/A"}
              </td>
              <td className="px-4 py-2 border">
                {donation.events && donation.events.length > 0 ? (
                  <details className="cursor-pointer">
                    <summary className="text-blue-600 hover:underline">
                      Ver Eventos
                    </summary>
                    <ul className="mt-2 text-left">
                      {donation.events.map((event, idx) => (
                        <li key={idx} className="py-1 border-b border-gray-200">
                          <span className="font-semibold">{event.event.nameEvent}</span>{" "}
                          - Entregadas {event.quantityDelivered} unidades
                        </li>
                      ))}
                    </ul>
                  </details>
                ) : (
                  "N/A"
                )}
              </td>
              <td className="px-4 py-2 border space-x-2">
                <button
                  onClick={() => editDonation(donation)}
                  className="px-2 py-1 bg-yellow-500 text-black rounded hover:bg-yellow-600"
                >
                  Modificar
                </button>
                <button
                  onClick={() => deleteDonation(donation)}
                  className="px-2 py-1 bg-red-500 text-black rounded hover:bg-red-600"
                >
                  Eliminar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  </div>
);
}

export default DonationList;
