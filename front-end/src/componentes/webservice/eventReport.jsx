import React, { useEffect, useState } from "react";
import axios from "axios";

const EventReport = () => {
  const [eventData, setEventData] = useState([]);
  const [error, setError] = useState("");
  const [emailUser, setEmailUser] = useState(localStorage.getItem("usernameOrEmail") || "");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [withDonations, setWithDonations] = useState("AMBOS");
  const [userRole, setUserRole] = useState(localStorage.getItem("userRole") || "");
  const [userData, setUserData] = useState([]);



  // determinar si el input de usuario debe estar habilitado
  const canEditUser = userRole === "COORDINADOR" || userRole === "PRESIDENTE";

  useEffect(() => {
    fetchEventReport();

    if (canEditUser) {
      fetchUsers()
    }

  }, []);

  const fetchEventReport = async () => {
    if (!emailUser) {
      setError("Debes seleccionar un usuario antes de buscar.");
      return;
    }

    setError("");

    try {
      const query = `
        query GetEventReport($emailUser: String!, $startDate: String, $endDate: String, $withDonations: String) {
          eventReport(    
          emailUser: $emailUser,
          startDate: $startDate,
          endDate: $endDate,
          withDonations: $withDonations) 
          {
            month
            events {
              idEvent
              nameEvent
              descriptionEvent
              dateRegistration
              members {
                fullName
                roleName
              }
              donations {
                donation {
                  category
                  description
                }
                quantity
              }
            }
          }
        }
      `;

      const variables = {
        emailUser: emailUser,
        startDate: startDate || null,
        endDate: endDate || null,
        withDonations: withDonations
      };

      const response = await axios.post(
        "http://localhost:8080/graphql",
        { query, variables },
        { headers: { "Content-Type": "application/json" } }
      );

      setEventData(response.data.data.eventReport || []);
    } catch (err) {
      console.error(err);
      setError("Error al obtener el reporte de eventos");
    }
  };


  const fetchUsers = async () => {

    try {
      const query = `
        query GetUserList {
          userList
          {
            fullName
            roleName
            email
          }
        }
      `;

      const response = await axios.post(
        "http://localhost:8080/graphql",
        { query },
        { headers: { "Content-Type": "application/json" } }
      );

      setUserData(response.data.data.userList || []);

      console.log("Todo conseguido :D")
    } catch (err) {
      console.error(err);
      setError("Error al obtener el listado de usuarios");
    }
  }

  return (
    <div className="p-6 bg-[#01000F] min-h-screen flex flex-col">
      <h1 className="text-5xl font-bold text-white mb-6">Reporte de Eventos por Mes</h1>
      {error && <div className="text-red-500 mb-4">{error}</div>}
      <div className="bg-[#232D4F] px-6 py-3 rounded mb-4 mt-4 flex gap-4 items-end w-full">
        <div className="flex-1">
          <label className="text-white block mb-1">Usuario</label>
          <select
            value={emailUser}
            onChange={(e) => setEmailUser(e.target.value)}
            disabled={!canEditUser}
            className="w-full px-2 py-1 rounded text-black"
          >
            <option value="">Seleccionar usuario...</option>
            {userData.map((user, idx) => (
              <option key={idx} value={user.email}>
                {user.fullName} ({user.roleName})
              </option>
            ))}
          </select>
        </div>

        <div className="flex-1">
          <label className="text-white block mb-1">Fecha Inicio</label>
          <input
            type="datetime-local" step="1"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="w-full px-2 py-1 rounded text-black"
          />
        </div>

        <div className="flex-1">
          <label className="text-white block mb-1">Fecha Fin</label>
          <input
            type="datetime-local" step="1"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="w-full px-2 py-1 rounded text-black"
          />
        </div>

        <div className="flex-1">
          <label className="text-white block mb-1">Con Donaciones</label>
          <select
            value={withDonations}
            onChange={(e) => setWithDonations(e.target.value)}
            className="w-full px-2 py-1 rounded text-black"
          >
            <option value="SI">Sí</option>
            <option value="NO">No</option>
            <option value="AMBOS">Ambos</option>
          </select>
        </div>

        <div className="flex-none">
          <button
            onClick={() => fetchEventReport()}
            className="px-6 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Buscar
          </button>

        </div>

        <div className="flex-none">
          <button
            onClick={console.log("Guardado")}
            className="px-6 py-2 bg-green-800 text-white rounded hover:bg-green-900"
          >
            Guardar Filtro
          </button>
        </div>
      </div>
      {eventData.map((group) => (
        <div key={group.month} className="mb-10">
          <h2 className="text-3xl font-semibold text-blue-400 mb-3">{group.month}</h2>

          <table className="min-w-full border border-gray-700 text-center">
            <thead>
              <tr className="bg-gray-900 text-white">
                <th className="px-4 py-2 border border-gray-700">Nombre</th>
                <th className="px-4 py-2 border border-gray-700">Descripción</th>
                <th className="px-4 py-2 border border-gray-700">Fecha</th>
                <th className="px-4 py-2 border border-gray-700">Miembros</th>
                <th className="px-4 py-2 border border-gray-700">Donaciones</th>
              </tr>
            </thead>
            <tbody>
              {group.events.map((event, index) => (
                <tr key={event.idEvent || index} className="text-gray-200">
                  <td className="px-4 py-2 border border-gray-700">{event.nameEvent}</td>
                  <td className="px-4 py-2 border border-gray-700">{event.descriptionEvent}</td>
                  <td className="px-4 py-2 border border-gray-700">{event.dateRegistration}</td>

                  <td className="px-4 py-2 border border-gray-700">
                    {event.members.length > 0 ? (
                      <details>
                        <summary className="text-blue-400 hover:underline cursor-pointer">Ver Miembros</summary>
                        <ul className="mt-2 text-left">
                          {event.members.map((m, i) => (
                            <li key={i} className="border-b border-gray-700 py-1">
                              {m.fullName} ({m.roleName})
                            </li>
                          ))}
                        </ul>
                      </details>
                    ) : (
                      "N/A"
                    )}
                  </td>

                  <td className="px-4 py-2 border border-gray-700">
                    {event.donations.length > 0 ? (
                      <details>
                        <summary className="text-blue-400 hover:underline cursor-pointer">Ver Donaciones</summary>
                        <ul className="mt-2 text-left">
                          {event.donations.map((d, i) => (
                            <li key={i} className="border-b border-gray-700 py-1">
                              {d.donation.category} - {d.donation.description} ({d.quantity})
                            </li>
                          ))}
                        </ul>
                      </details>
                    ) : (
                      "N/A"
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ))}
    </div>
  );
};

export default EventReport;
