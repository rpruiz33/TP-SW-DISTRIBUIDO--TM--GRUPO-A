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

    const protoData ={
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
    <div className="p-6">
  <div className="flex justify-between items-center mb-4">
    <h1 className="text-2xl font-bold">Inventario de Donaciones</h1>
    <button
      onClick={() => navigate("/donationform")}
      className="px-3 py-1 bg-green-500 text-black rounded hover:bg-green-600"
    >
      Nueva Donación
    </button>
  </div>

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
              {donation.removed != null
                ? donation.removed
                  ? "Sí"
                  : "No"
                : "N/A"}
            </td>
            <td className="px-4 py-2 border">
              {donation.events && donation.events.length > 0 ? (
                <details className="cursor-pointer">
                  <summary className="text-blue-600 hover:underline">
                    Ver Eventos
                  </summary>
                  <ul className="mt-2 text-left">
                    {donation.events.map((event, idx) => (
                      <li
                        key={idx}
                        className="py-1 border-b border-gray-200"
                      >
                        <span className="font-semibold">
                          {event.event.nameEvent}
                        </span>{" "}
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
                 {donation.removed != null ? (donation.removed? "Activar" : "Eliminar") : "N/A"}
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
