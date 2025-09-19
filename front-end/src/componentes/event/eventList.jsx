import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

export default function EventList() {
  const [events, setEvents] = useState([]);
  const navigate = useNavigate();
  const [error, setError] = useState("");


  useEffect(() => {
    getEvents()
  }, []);

  const getEvents = async () => {
    try {
      const response = await axios.get("http://localhost:5000/api/eventlist");
      console.log("Respuesta completa:", response.data);
      if (Array.isArray(response.data)) {
        setEvents(response.data);
      } else if (response.data.events && Array.isArray(response.data.events)) {
        setEvents(response.data.events);
      } else {
        setEvents([]);
        console.warn("La respuesta no contiene un array de eventos:", response.data);
      }
    } catch (err) {
      console.error("Error en la solicitud:", err);
      setError("Error de conexión con el servidor");
    }
  };

  const memberManagment = (event) => {
    navigate("/membermanagment", { state: { event } });
  };

  const donationManagment = (event) => {
    navigate("/donationmanagment", { state: { event } });
  };

  const deleteEvent = async (event) => {
    if (!window.confirm(`¿Seguro que deseas eliminar el evento "${event.nameEvent}"?`)) return;

    try {
      const response = await axios.delete(`http://localhost:5000/api/deleteevent/${event.id}`);
  
      if (response.data.success) {
        
        alert(response.data.message);
        setEvents((prevEvents) => prevEvents.filter((e) => e.id !== event.id));
      } else {
        alert(response.data.message);
      }
      ////forzado sino me lo borra de la db pero no recarga la lista
      window.location.reload();
    } catch (error) {
      console.error("Error al eliminar el evento:", error);
      alert("Hubo un error al eliminar el evento. Intente nuevamente.");
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl text-white font-bold mb-4">Lista de Eventos</h1>
      {error && <div className="text-red-500 mb-4">{error}</div>}
      <button
        onClick={() => navigate("/eventform")}
        className="mb-4 px-4 py-2 bg-blue-500 text-black rounded hover:bg-blue-600"
      >
        Crear Nuevo Evento
      </button>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-200">
          <thead>
            <tr className="bg-gray-100">
              <th className="px-4 py-2 border">Nombre</th>
              <th className="px-4 py-2 border">Descripcion</th>
              <th className="px-4 py-2 border">Fecha y Hora del Evento</th>
              <th className="px-4 py-2 border">Miembros</th>
              <th className="px-4 py-2 border">Donaciones</th>
              <th className="px-4 py-2 border">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {events.map((event, index) => (
              <tr key={event.id || index} className="text-center">
                <td className="px-4 py-2 border">{event.nameEvent || "N/A"}</td>
                <td className="px-4 py-2 border">{event.descriptionEvent || "N/A"}</td>
                <td className="px-4 py-2 border">{event.dateRegistration || "N/A"}</td>

                <td className="px-4 py-2 border">
                  {event.users && event.users.length > 0 ? (
                    <details className="cursor-pointer">
                      <summary className="text-blue-600 hover:underline">
                        Ver Miembros
                      </summary>
                      <ul className="mt-2 text-left">
                        {event.users.map((user, idx) => (
                          <li
                            key={idx}
                            className="py-1 border-b border-gray-200"
                          >
                            <span className="font-semibold">
                              {user.username}
                            </span>{" "}
                          </li>
                        ))}
                      </ul>
                    </details>
                  ) : (
                    "N/A"
                  )}
                </td>

                <td className="px-4 py-2 border">
                  {event.donations && event.donations.length > 0 ? (
                    <details className="cursor-pointer">
                      <summary className="text-blue-600 hover:underline">
                        Ver Donaciones
                      </summary>
                      <ul className="mt-2 text-left">
                        {event.donations.map((donation, idx) => (
                          <li
                            key={idx}
                            className="py-1 border-b border-gray-200"
                          >
                            <span className="font-semibold">
                              {donation.donation.category + " " + donation.donation.description}
                            </span>{" "}
                            -  {donation.quantityDelivered} unidades entregadas.
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

                    className="px-2 py-1 bg-yellow-500 text-black rounded hover:bg-yellow-600"
                    onClick={() => navigate("/eventform", { state: { event } })}
                  >
                    Modificar
                  </button>


                  <button
                    onClick={() => deleteEvent(event)}
                    className="px-2 py-1 bg-red-500 text-black rounded hover:bg-red-600"
                  >
                    Eliminar
                  </button>
                  {true && (
                    <button
                      className="px-2 py-1 bg-red-500 text-black rounded hover:bg-red-600"
                      onClick={() => memberManagment(event)}
                    >
                      Gestionar miembros
                    </button>
                  )}

                  {new Date(event.dateRegistration) < new Date() && (
                    <button
                      className="px-2 py-1 bg-red-500 text-black rounded hover:bg-red-600"
                      onClick={() => donationManagment(event)}
                    >
                      Asignar Donaciones
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};