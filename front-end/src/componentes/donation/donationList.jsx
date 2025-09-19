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

  const deleteDonation = async (donation) => {
    if (!window.confirm("¿Seguro que deseas eliminar esta donación?")) return;

    const protoData = {
      id: donation.id,
      username: localStorage.getItem("usernameOrEmail")
    }

    try {
      const response = await axios.put(
        `http://localhost:5000/api/deletedonation`,
        protoData
      );

      if (response.data.success) {
        alert("Donación eliminada con éxito");

        // 🔄 Recargar lista desde el servidor
        await getDonations();

        // 📌 Alternativa sin recargar toda la lista:
        // setDonations((prev) => prev.filter((d) => d.id !== donation.id));
      } else {
        alert("No se pudo eliminar la donación");
      }
    } catch (error) {
      console.error("Error al eliminar la donación:", error);
      alert("Hubo un error al eliminar. Intente nuevamente.");
    }
  };

  return (
    <div className="p-6 bg-[#01000F] min-h-screen flex flex-col">
      {/* Título y botón */}
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-5xl font-bold text-white">Inventario de Donaciones</h1>
        <button
          onClick={() => navigate("/donationform")}
          className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          Nueva Donación
        </button>
      </div>

      {/* Mensaje de error */}
      {error && <div className="text-red-500 mb-4">{error}</div>}

      {/* Tabla */}
      <div className="overflow-x-auto flex-grow">
        <table className="min-w-full border border-gray-700 text-center">
          <thead>
            <tr className="bg-gray-900 text-white">
              <th className="px-4 py-2 border border-gray-700">Categoria</th>
              <th className="px-4 py-2 border border-gray-700">Descripcion</th>
              <th className="px-4 py-2 border border-gray-700">Cantidad</th>
              <th className="px-4 py-2 border border-gray-700">Eliminado</th>
              <th className="px-4 py-2 border border-gray-700">Historial de Entregas</th>
              <th className="px-4 py-2 border border-gray-700">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {donations.map((donation, index) => (
              <tr key={donation.id || index} className="text-gray-200">
                <td className="px-4 py-2 border border-gray-700">{donation.category || "N/A"}</td>
                <td className="px-4 py-2 border border-gray-700">{donation.description || "N/A"}</td>
                <td className="px-4 py-2 border border-gray-700">{donation.amount || "N/A"}</td>
                <td className={`px-4 py-2 border border-gray-700 ${donation.removed ? "text-red-500" : "text-green-400"}`}>
                  {donation.removed != null ? (donation.removed ? "Sí" : "No") : "N/A"}
                </td>
                <td className="px-4 py-2 border border-gray-700">
                  {donation.events && donation.events.length > 0 ? (
                    <details className="cursor-pointer">
                      <summary className="text-blue-400 hover:underline">Ver Eventos</summary>
                      <ul className="mt-2 text-left">
                        {donation.events.map((event, idx) => (
                          <li key={idx} className="py-1 border-b border-gray-700">
                            <span className="font-semibold text-white">{event.event.nameEvent}</span> - Entregadas {event.quantityDelivered} unidades
                          </li>
                        ))}
                      </ul>
                    </details>
                  ) : (
                    "N/A"
                  )}
                </td>
                <td className="px-4 py-2 border border-gray-700 space-x-2">
                  <button
                    onClick={() => editDonation(donation)}
                    className="px-2 py-1 bg-yellow-500 text-black rounded hover:bg-yellow-600"
                  >
                    Modificar
                  </button>
                  <button
                    onClick={() => deleteDonation(donation)}
                    className={`px-2 py-1 rounded text-black hover:opacity-90 ${donation.removed != null
                        ? donation.removed
                          ? "bg-green-500 hover:bg-green-600" // Si dice "Activar"
                          : "bg-red-500 hover:bg-red-600"     // Si dice "Eliminar"
                        : "bg-gray-400 cursor-not-allowed"   // Caso "N/A"
                      }`}
                  >
                    {donation.removed != null ? (donation.removed ? "Activar" : "Eliminar") : "N/A"}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default DonationList;
