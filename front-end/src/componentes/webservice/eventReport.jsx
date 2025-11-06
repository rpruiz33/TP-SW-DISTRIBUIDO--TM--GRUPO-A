import React, { useEffect, useState, useCallback } from "react";
import axios from "axios";

const EventReport = () => {
  const [eventData, setEventData] = useState([]);
  const [error, setError] = useState("");
  const emailUser = localStorage.getItem("usernameOrEmail") || "";
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [emailUserFilter, setEmailUserFilter] = useState("");
  const [userFilter, setUserFilter] = useState(null);
  const [withDonations, setWithDonations] = useState("AMBOS");
  const userRole = localStorage.getItem("userRole") || "";
  const [userData, setUserData] = useState([]);
  const [message, setMessage] = useState("");
  const [filterName, setFilterName] = useState("");
  const [originalFilterName, setOriginalFilterName] = useState("");
  const [savedFilters, setSavedFilters] = useState([]);
  const canEditUser = userRole === "COORDINADOR" || userRole === "PRESIDENTE";

  

  useEffect(() => {
    if (message) {
      const timer = setTimeout(() => setMessage(""), 4000);
      return () => clearTimeout(timer);
    }
  }, [message]);

  const fetchSavedFilters = useCallback(async (force = false) => {
    if (!emailUser) return;
    try {
      const resp = await axios.get(
        `http://localhost:8080/api/event-filters/getlist?emailOrUsername=${emailUser}`
      );
  // response shape defensive handling

      // Normalize response: the REST endpoint returns a plain array in resp.data
      // but be defensive in case it's wrapped.
      let list = [];
      if (Array.isArray(resp.data)) list = resp.data;
      else if (Array.isArray(resp.data?.data)) list = resp.data.data;
      else if (Array.isArray(resp.data?.result)) list = resp.data.result;

      // Normalize field names: some DTOs use 'distributionDonations' while UI
      // previously expected 'activate'. Keep both for compatibility.
      list = list.map((f) => ({
        ...f,
        activate: f.activate === undefined ? f.distributionDonations : f.activate,
        filterUser: f.filterUser || f.user || null,
      }));

  // Only replace savedFilters if server returned something, unless force=true
      if (force) {
        setSavedFilters(list || []);
      } else {
        setSavedFilters((prev) => (list && list.length > 0 ? list : prev));
      }
    } catch (err) {
      // If REST fails, do not fall back to the GraphQL donation-filters query because
      // that endpoint returns only donation filters (server GraphQL API is donation-specific).
      // Showing donation filters on the event page is incorrect. Log error and leave
      // savedFilters as-is (or empty).
      console.error("Error fetching saved event filters (REST)", err);
      setSavedFilters([]);
    }
  }, [emailUser]);

  const fetchEventReport = useCallback(async (overrideEmail) => {
    // Defensive: if overrideEmail is provided by accident (e.g. MouseEvent),
    // ignore it unless it's a non-empty string. Otherwise use the selected email.
    const targetEmail = typeof overrideEmail === "string" && overrideEmail.trim()
      ? overrideEmail.trim()
      : (emailUserFilter || "");

    if (!targetEmail) {
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

      const variables = {
        emailUser: targetEmail,
        startDate: startDate || null,
        endDate: endDate || null,
        withDonations,
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
  }, [emailUserFilter, startDate, endDate, withDonations]);

  const fetchUsers = useCallback(async () => {
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
  }, []);

  // Efecto inicial: una vez que las funciones están definidas, disparamos las cargas necesarias
  useEffect(() => {
    // Initialize UI values and saved filters/users, but DO NOT auto-fetch events.
    // The user must click "Buscar" to request the report.
    setEmailUserFilter(emailUser);
    fetchSavedFilters(true);
    if (canEditUser) fetchUsers();
    // NOTE: intentionally not calling fetchEventReport here to avoid automatic searches on mount
  }, [fetchSavedFilters, fetchUsers, canEditUser, emailUser]);

  const saveFilter = async (isUpdate = false) => {
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
      // El backend espera filterUser con al menos email; si no está, lo llenamos
      filterUser: userFilter || { email: emailUserFilter || emailUser },
      // El DTO del backend para eventos usa 'distributionDonations'
      distributionDonations: activate,
    };

    try {
      if (isUpdate) {
        // PUT para actualizar
        // Include originalFilterName so the backend can locate and rename the filter if the user changed the name
        const params = `?emailOrUsername=${encodeURIComponent(emailUser)}${originalFilterName ? `&originalFilterName=${encodeURIComponent(originalFilterName)}` : ""}`;
        const resp = await axios.put(
          `http://localhost:8080/api/event-filters/update${params}`,
          input,
          { headers: { "Content-Type": "application/json" } }
        );
        console.log('UPDATE filter response', resp?.data);
        if (resp?.data) {
        
          if (resp?.data === true || resp?.data === "true") {
            setMessage("🔄 Filtro actualizado correctamente.");
            // optimistic update so user sees change immediately
            const updatedFilter = {
              filterName: name,
              startDate: input.startDate,
              endDate: input.endDate,
              filterUser: input.filterUser,
              distributionDonations: input.distributionDonations,
              activate: input.distributionDonations,
            };
            setSavedFilters((prev) => prev.map((f) => (f.filterName === (originalFilterName || name) ? { ...f, ...updatedFilter } : f)));
          } else {
            setMessage("❌ No se pudo actualizar el filtro (respuesta del servidor)");
            // refresh from server to ensure UI reflects reality
            await fetchSavedFilters(true);
            return;
          }
        } else {
          setMessage("❌ No se pudo actualizar el filtro (respuesta del servidor)");
          await fetchSavedFilters(true);
          setOriginalFilterName("");
          return;
        }
      } else {
        // POST para crear
        const resp = await axios.post(
          `http://localhost:8080/api/event-filters/save?emailOrUsername=${emailUser}`,
          input,
          { headers: { "Content-Type": "application/json" } }
        );
        console.log('SAVE filter response', resp?.data);
        if (resp?.data) {
          
          if (resp?.data === true || resp?.data === "true") {
            setMessage("✅ Filtro guardado correctamente.");
            // optimistic add so user sees the new filter immediately
            const newFilter = {
              filterName: name,
              startDate: input.startDate,
              endDate: input.endDate,
              filterUser: input.filterUser,
              distributionDonations: input.distributionDonations,
              activate: input.distributionDonations,
            };
            setSavedFilters((prev) => [newFilter, ...(prev || [])]);
          } else {
            setMessage("❌ No se pudo guardar el filtro (respuesta del servidor)");
            await fetchSavedFilters(true);
            return;
          }
        } else {
          setMessage("❌ No se pudo guardar el filtro (respuesta del servidor)");
          await fetchSavedFilters(true);
          return;
        }
      }

  setFilterName("");
  await fetchSavedFilters(true);
    } catch (err) {
      console.error("Error saving/updating filter", err);
      setMessage("❌ Error al guardar/actualizar el filtro.");
    }
  };

  const applyFilter = async (filter) => {
    setStartDate(filter.startDate || "");
    setEndDate(filter.endDate || "");
    // Some filters may use 'distributionDonations' instead of 'activate'
    const activeVal = filter.activate === undefined ? filter.distributionDonations : filter.activate;
    setWithDonations(activeVal === true ? "SI" : activeVal === false ? "NO" : "AMBOS");
    const email = filter.filterUser?.email || "";
    setEmailUserFilter(email);
    setUserFilter(filter.filterUser || null);
    setFilterName(filter.filterName);
    setOriginalFilterName(filter.filterName);
    await fetchEventReport(email);
    setMessage(`🔎 Filtro "${filter.filterName}" aplicado.`);
  };

  const deleteFilter = async (name) => {
    const nameF = name.trim();
    try {
      const response = await axios.delete("http://localhost:8080/api/event-filters/delete", {
        params: {
          filterName: nameF,
          emailOrUsername: emailUser,
        },
      });
      if (response.data) {
        setMessage("🗑️ Filtro eliminado con éxito.");
        await fetchSavedFilters(true);
      }
    } catch (err) {
      console.error("Error deleting filter", err);
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
          <select
            value={emailUserFilter}
            onChange={(e) => {
              const selectedEmail = e.target.value;
              setEmailUserFilter(selectedEmail);
              const selectedUser = userData.find((u) => u.email === selectedEmail);
              setUserFilter(selectedUser || null);
            }}
            disabled={!canEditUser}
            className="w-full px-2 py-1 rounded text-black"
          >
            <option value="">Seleccionar usuario...</option>
            {userData.map((u, i) => (
              <option key={i} value={u.email}>
                {u.fullName} ({u.roleName})
              </option>
            ))}
          </select>
        </div>

        <div className="flex-1 min-w-[180px]">
          <label className="text-white block mb-1">Fecha Inicio</label>
          <input
            type="datetime-local"
            step="1"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="w-full px-2 py-1 rounded text-black"
          />
        </div>

        <div className="flex-1 min-w-[180px]">
          <label className="text-white block mb-1">Fecha Fin</label>
          <input
            type="datetime-local"
            step="1"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="w-full px-2 py-1 rounded text-black"
          />
        </div>

        <div className="flex-1 min-w-[150px]">
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

        <div className="flex-1 min-w-[200px] mt-4">
          <button
            // Call fetchEventReport without passing the click event. This prevents the
            // MouseEvent object from being interpreted as an email (overrideEmail).
            onClick={() => fetchEventReport()}
            className="px-6 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
          >
            Buscar
          </button>
        </div>

        <div className="flex-1 min-w-[200px]">
          <label className="text-white block mb-1">Nombre filtro</label>
          <input
            type="text"
            value={filterName}
            onChange={(e) => setFilterName(e.target.value)}
            placeholder="Nombre del filtro"
            className="w-full px-2 py-1 rounded text-black"
          />
          <div className="flex gap-2 mt-4">
            <button
              onClick={() => saveFilter(false)}
              className="flex-1 px-4 py-2 bg-green-700 text-white rounded hover:bg-green-800 transition"
            >
              Guardar
            </button>
            <button
              onClick={() => saveFilter(true)}
              className="flex-1 px-2 py-2 bg-yellow-600 text-white rounded hover:bg-yellow-700 transition"
            >
              Actualizar
            </button>
          </div>
          {/* Botón Borrar colocado en nueva fila para evitar scroll lateral */}
          <div className="w-full mt-3">
            <button
              onClick={async () => {
                if (!filterName || filterName.trim() === "") {
                  setMessage("❌ Debes ingresar el nombre del filtro a borrar.");
                  return;
                }
                if (!window.confirm(`¿Eliminar el filtro '${filterName}'?`)) return;
                await deleteFilter(filterName);
              }}
              className="w-full px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
            >
              Borrar
            </button>
          </div>
        </div>
      </div>

      {/* Filtros guardados (si no hay, se muestra mensaje) */}
      <div className="bg-[#232D4F] px-6 py-4 rounded mb-6">
        <h2 className="text-2xl text-white mb-3 font-semibold">Filtros guardados</h2>
        <div className="flex flex-wrap gap-3">
          {savedFilters && savedFilters.length > 0 ? (
            savedFilters.map((f) => (
              <div
                key={f.filterName}
                className="bg-[#1B2440] text-white px-4 py-3 rounded-lg shadow-md flex flex-col gap-2 w-[250px]"
              >
                <div className="font-semibold text-lg">{f.filterName}</div>
                <div className="text-sm opacity-80">
                  <div>Desde: {f.startDate ? new Date(f.startDate).toLocaleString() : "—"}</div>
                  <div>Hasta: {f.endDate ? new Date(f.endDate).toLocaleString() : "—"}</div>
                  <div>
                    Usuario:{" "}
                    {f.filterUser
                      ? `${f.filterUser.fullName || "Sin nombre"} (${f.filterUser.roleName || "Sin rol"})`
                      : "—"}
                  </div>
                  <div>
                    Donaciones: {f.activate === true ? "Sí" : f.activate === false ? "No" : "Ambos"}
                  </div>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => applyFilter(f)}
                    className="flex-1 bg-blue-500 hover:bg-blue-600 text-white rounded py-1 text-sm"
                  >
                    Aplicar
                  </button>
                  <button
                    onClick={() => deleteFilter(f.filterName)}
                    className="flex-1 bg-red-600 hover:bg-red-700 text-white rounded py-1 text-sm"
                  >
                    Eliminar
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="text-white">No hay filtros guardados</div>
          )}
        </div>
      </div>

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
                        <summary className="text-blue-400 hover:underline cursor-pointer">
                          Ver Miembros
                        </summary>
                        <ul className="mt-2 text-left">
                          {event.members.map((m, j) => (
                            <li key={j} className="border-b border-gray-700 py-1">
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
                        <summary className="text-blue-400 hover:underline cursor-pointer">
                          Ver Donaciones
                        </summary>
                        <ul className="mt-2 text-left">
                          {event.donations.map((d, j) => (
                            <li key={j} className="border-b border-gray-700 py-1">
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
