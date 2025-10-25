import React, { useEffect, useState } from "react";
import axios from "axios";

const EventReport = () => {
  const [eventData, setEventData] = useState([]);
  const [error, setError] = useState("");
  const [emailUser, setEmailUser] = useState(localStorage.getItem("usernameOrEmail") || "");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [emailUserFilter, setEmailUserFilter] = useState("");
  const [userFilter, setUserFilter] = useState(null);
  const [withDonations, setWithDonations] = useState("AMBOS");
  const [userRole, setUserRole] = useState(localStorage.getItem("userRole") || "");
  const [userData, setUserData] = useState([]);
  const [message, setMessage] = useState("");
  const [filterName, setFilterName] = useState("");
  const [savedFilters, setSavedFilters] = useState([]);

  const canEditUser = userRole === "COORDINADOR" || userRole === "PRESIDENTE";



  useEffect(() => {
    setEmailUserFilter(emailUser);
    fetchEventReport();
    if (canEditUser) fetchUsers();
    fetchSavedFilters();
  }, []);



  useEffect(() => {
    if (message) {
      const timer = setTimeout(() => setMessage(""), 4000);
      return () => clearTimeout(timer);
    }
  }, [message]);

  const fetchSavedFilters = async () => {
    if (!emailUser) return;
    try {

      const resp = await axios.get(
        `http://localhost:8080/api/event-filters/getlist?emailOrUsername=${emailUser}`);


      setSavedFilters(resp.data || []);
    } catch (err) {
      console.error("Error fetching saved filters", err);
    }
  };

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
            withDonations: $withDonations
          ) {
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

      const variables = { emailUser: emailUserFilter, startDate: startDate || null, endDate: endDate || null, withDonations };

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
          userList {
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
    } catch (err) {
      console.error(err);
      setError("Error al obtener el listado de usuarios");
    }
  };

  const saveFilter = async () => {
    const name = filterName.trim();
    if (!name) {
      setMessage("❌ Debes ingresar un nombre para el filtro.");
      return;
    }
    if (!emailUser) {
      setMessage("❌ Selecciona un usuario antes de guardar el filtro.");
      return;
    }

    let activate = null;
    if (withDonations === "SI") activate = true;
    else if (withDonations === "NO") activate = false;

    const input = {
      filterName: name,
      startDate: startDate || null,
      endDate: endDate || null,
      filterUser: userFilter,
      activate,
    };


    if (!emailUser) return;
    try {

      const resp = await axios.post(
        `http://localhost:8080/api/event-filters/save?emailOrUsername=${emailUser}`,
        input,
        { headers: { "Content-Type": "application/json" } })

      await fetchSavedFilters()
    } catch (err) {
      console.error("Error fetching saved filters", err);
    }
  };

  const applyFilter = (filter) => {
    setStartDate(filter.startDate || "");
    setEndDate(filter.endDate || "");
    setWithDonations(filter.activate === true ? "SI" : filter.activate === false ? "NO" : "AMBOS");
    setEmailUserFilter(filter.filterUser.email)
    setUserFilter(filter.filterUser || "");
    setFilterName(filter.filterName);
    fetchEventReport();
    setMessage(`🔎 Filtro "${filter.filterName}" aplicado.`);
  };

  const deleteFilter = async (name) => {
    const nameF = name.trim();

    try {


    const response= await axios.delete("http://localhost:8080/api/event-filters/delete", {
      params: {
        filterName: nameF,
        emailOrUsername: emailUser
      }
    });
      if(response.data){
        alert("Filtro eliminado con exito")
        await fetchSavedFilters()
      }

    } catch (err) {
      console.error("Error fetching saved filters", err);
    }

  };

  return (
    <div className="p-6 bg-[#01000F] min-h-screen flex flex-col">
      <h1 className="text-5xl font-bold text-white mb-6">Reporte de Eventos por Mes</h1>

      {error && <div className="mb-4 px-4 py-2 rounded text-white text-center bg-red-500">{error}</div>}
      {message && <div className="mb-4 px-4 py-2 rounded text-white text-center bg-blue-500">{message}</div>}




      {/* Panel de filtros */}
      <div className="bg-[#232D4F] px-6 py-3 rounded mb-4 mt-4 flex gap-4 items-start w-full flex-wrap">
        <div className="flex-1 min-w-[200px]">
          <label className="text-white block mb-1">Usuario</label>
          <select value={emailUserFilter} onChange={(e) => {

            const selectedEmail = e.target.value;
            setEmailUserFilter(selectedEmail)
            const selectedUser = userData.find(u => u.email === selectedEmail);
            setUserFilter(selectedUser || null);
          }

          } disabled={!canEditUser} className="w-full px-2 py-1 rounded text-black">
            <option value="">Seleccionar usuario...</option>
            {userData.map((u, i) => (
              <option key={i} value={u.email}>{u.fullName} ({u.roleName})</option>
            ))}
          </select>
        </div>

        <div className="flex-1 min-w-[180px]">
          <label className="text-white block mb-1">Fecha Inicio</label>
          <input type="datetime-local" step="1" value={startDate} onChange={(e) => setStartDate(e.target.value)} className="w-full px-2 py-1 rounded text-black" />
        </div>

        <div className="flex-1 min-w-[180px]">
          <label className="text-white block mb-1">Fecha Fin</label>
          <input type="datetime-local" step="1" value={endDate} onChange={(e) => setEndDate(e.target.value)} className="w-full px-2 py-1 rounded text-black" />
        </div>

        <div className="flex-1 min-w-[150px]">
          <label className="text-white block mb-1">Con Donaciones</label>
          <select value={withDonations} onChange={(e) => setWithDonations(e.target.value)} className="w-full px-2 py-1 rounded text-black">
            <option value="SI">Sí</option>
            <option value="NO">No</option>
            <option value="AMBOS">Ambos</option>
          </select>
        </div>

        <div className="flex-1 min-w-[200px] mt-4">

          <button onClick={fetchEventReport} className="px-6 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition">Buscar</button>

        </div>

        <div className="flex-1 min-w-[200px]">
          <label className="text-white block mb-1">Nombre filtro</label>
          <input type="text" value={filterName} onChange={(e) => setFilterName(e.target.value)} placeholder="Nombre del filtro" className="w-full px-2 py-1 rounded text-black" />
          <button onClick={saveFilter} className="px-6 py-2 bg-green-700 text-white rounded hover:bg-green-800 transition mt-4">Guardar Filtro</button>
        </div>
      </div>

      {/* Filtros guardados */}
      {savedFilters.length > 0 && (
        <div className="bg-[#232D4F] px-6 py-4 rounded mb-6">
          <h2 className="text-2xl text-white mb-3 font-semibold">Filtros guardados</h2>
          <div className="flex flex-wrap gap-3">
            {savedFilters.map((f) => (
              <div key={f.filterName} className="bg-[#1B2440] text-white px-4 py-3 rounded-lg shadow-md flex flex-col gap-2 w-[250px]">
                <div className="font-semibold text-lg">{f.filterName}</div>
                <div className="text-sm opacity-80">
                  <div>Desde: {f.startDate ? new Date(f.startDate).toLocaleString() : "—"}</div>
                  <div>Hasta: {f.endDate ? new Date(f.endDate).toLocaleString() : "—"}</div>
                  <div>
                    Usuario:{" "}
                    {f.filterUser
                      ? `${f.filterUser.fullName || "Sin nombre"} (${f.filterUser.roleName || "Sin rol"})`
                      : "—"}
                  </div>                  <div>Donaciones: {f.activate === true ? "Sí" : f.activate === false ? "No" : "Ambos"}</div>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => applyFilter(f)} className="flex-1 bg-blue-500 hover:bg-blue-600 text-white rounded py-1 text-sm">Aplicar</button>
                  <button onClick={() => deleteFilter(f.filterName)} className="flex-1 bg-red-600 hover:bg-red-700 text-white rounded py-1 text-sm">Eliminar</button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Reporte */}
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
              {group.events.map((event, i) => (
                <tr key={event.idEvent || i} className="text-gray-200">
                  <td className="px-4 py-2 border border-gray-700">{event.nameEvent}</td>
                  <td className="px-4 py-2 border border-gray-700">{event.descriptionEvent}</td>
                  <td className="px-4 py-2 border border-gray-700">{event.dateRegistration}</td>
                  <td className="px-4 py-2 border border-gray-700">
                    {event.members.length > 0 ? (
                      <details>
                        <summary className="text-blue-400 hover:underline cursor-pointer">Ver Miembros</summary>
                        <ul className="mt-2 text-left">
                          {event.members.map((m, j) => (
                            <li key={j} className="border-b border-gray-700 py-1">{m.fullName} ({m.roleName})</li>
                          ))}
                        </ul>
                      </details>
                    ) : "N/A"}
                  </td>
                  <td className="px-4 py-2 border border-gray-700">
                    {event.donations.length > 0 ? (
                      <details>
                        <summary className="text-blue-400 hover:underline cursor-pointer">Ver Donaciones</summary>
                        <ul className="mt-2 text-left">
                          {event.donations.map((d, j) => (
                            <li key={j} className="border-b border-gray-700 py-1">{d.donation.category} - {d.donation.description} ({d.quantity})</li>
                          ))}
                        </ul>
                      </details>
                    ) : "N/A"}
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
