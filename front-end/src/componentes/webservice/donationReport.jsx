import React, { useEffect, useState } from "react";
import axios from "axios";
import { useLocation } from "react-router-dom";

const DonationReportComponent = () => {
  const [reportData, setReportData] = useState([]);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [category, setCategory] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [activate, setActivate] = useState("AMBOS");
  const [filterName, setFilterName] = useState("");
  const [savedFilters, setSavedFilters] = useState([]);
  const [emailUser, setEmailUser] = useState(localStorage.getItem("usernameOrEmail") || "");

  const location = useLocation();
  const isOther = location.state?.isOther || false;

  const categoryOptions = ["ROPA", "ALIMENTO", "JUGUETE", "UTIL_ESCOLAR"];
  const booleanOptions = ["SI", "NO", "AMBOS"];

  useEffect(() => {
    fetchDonationReport();
    fetchSavedFilters();
  }, [location]);

  useEffect(() => {
    if (message) {
      const timer = setTimeout(() => setMessage(""), 4000);
      return () => clearTimeout(timer);
    }
  }, [message]);

  const fetchSavedFilters = async () => {
    if (!emailUser) return;
    try {
      const query = `
        query GetFilters($email: String!) {
          getUserFiltersByEmail(emailOrUsername: $email) {
            idFilter
            filterName
            startDate
            endDate
            activate
            category
          }
        }
      `;
      const resp = await axios.post(
        "http://localhost:8080/graphql",
        { query, variables: { email: emailUser } },
        { headers: { "Content-Type": "application/json" } }
      );
      setSavedFilters(resp?.data?.data?.getUserFiltersByEmail || []);
    } catch (err) {
      console.error("Error fetching saved filters", err);
    }
  };

  const fetchDonationReport = async () => {
    try {
      const query = `
        query DonationReport($category: Category, $startDate: String, $endDate: String, $activate: Boolean, $isExternal: Boolean) {
          donationReport(category: $category, startDate: $startDate, endDate: $endDate, activate: $activate, isExternal: $isExternal) {
            category
            activate
            totalQuantity
            details {
              description
              quantity
            }
          }
        }
      `;
      const parseBoolean = (val) => (val === "SI" ? true : val === "NO" ? false : null);
      const variables = { category: category || null, startDate: startDate || null, endDate: endDate || null, activate: parseBoolean(activate), isExternal: isOther };
      const response = await axios.post("http://localhost:8080/graphql", { query, variables }, { headers: { "Content-Type": "application/json" } });
      setReportData(response.data.data.donationReport || []);
      setError("");
    } catch (err) {
      console.error(err);
      setError("Error al obtener el reporte de donaciones");
    }
  };

  const saveDonationFilter = async () => {
    if (isOther) return;
    const name = filterName.trim();
    if (!name) {
      setMessage("❌ Debes ingresar un nombre para el filtro.");
      return;
    }
    if (!emailUser) {
      setMessage("❌ No se encontró usuario en sesión.");
      return;
    }

    let act = null;
    if (activate === "SI") act = true;
    else if (activate === "NO") act = false;

    const input = {
      filterName: name,
      startDate: startDate || null,
      endDate: endDate || null,
      activate: act,
      category: category || null,
    };

    const mutation = `
      mutation Save($input: DonationFilterDTO!, $email: String!) {
        saveDonationFilter(input: $input, emailOrUsername: $email)
      }
    `;

    try {
      const resp = await axios.post(
        "http://localhost:8080/graphql",
        { query: mutation, variables: { input, email: emailUser } },
        { headers: { "Content-Type": "application/json" } }
      );
      const ok = resp?.data?.data?.saveDonationFilter;
      if (ok) {
        setMessage("✅ Filtro guardado correctamente.");
        setFilterName("");
        await fetchSavedFilters();
      } else {
        setMessage("❌ Error al guardar el filtro.");
      }
    } catch (err) {
      console.error(err);
      setMessage("❌ Error de conexión con el servidor.");
    }
  };

  const applyFilter = (filter) => {
    setStartDate(filter.startDate || "");
    setEndDate(filter.endDate || "");
    setActivate(filter.activate === true ? "SI" : filter.activate === false ? "NO" : "AMBOS");
    setCategory(filter.category || "");
    setFilterName(filter.filterName);
    fetchDonationReport();
    setMessage(`🔎 Filtro "${filter.filterName}" aplicado.`);
  };

  const deleteFilter = async (id) => {
    const mutation = `
      mutation Delete($id: Int!) {
        deleteFilterById(idFilter: $id)
      }
    `;
    try {
      const resp = await axios.post(
        "http://localhost:8080/graphql",
        { query: mutation, variables: { id } },
        { headers: { "Content-Type": "application/json" } }
      );
      const ok = resp?.data?.data?.deleteFilterById;
      if (ok) {
        setMessage("🗑️ Filtro eliminado correctamente.");
        await fetchSavedFilters();
      } else {
        setMessage("❌ No se pudo eliminar el filtro.");
      }
    } catch (err) {
      console.error(err);
      setMessage("❌ Error al eliminar el filtro.");
    }
  };

  const generateExcel = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/excel/donaciones", { params: { isExternal: isOther }, responseType: "blob" });
      const blob = new Blob([response.data], { type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "ReporteDonaciones.xlsx");
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error(err);
      setMessage("❌ Error generando el archivo Excel");
    }
  };

  return (
    <div className="p-6 bg-[#01000F] min-h-screen flex flex-col">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-5xl font-bold text-white">{isOther ? "Reporte de Donaciones Externas" : "Reporte de Donaciones Propias"}</h1>
        <button onClick={generateExcel} className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">Generar reporte en Excel</button>
      </div>

      {error && <div className="mb-4 px-4 py-2 rounded text-white text-center bg-red-500">{error}</div>}
      {message && <div className="mb-4 px-4 py-2 rounded text-white text-center bg-green-500">{message}</div>}

      {/* Filtros */}
      <div className="bg-[#232D4F] px-6 py-3 rounded mb-4 mt-4 flex gap-4 items-end w-full flex-wrap">
        <div className="flex-1 min-w-[200px]">
          <label className="text-white block mb-1">Categoría</label>
          <select value={category} onChange={(e) => setCategory(e.target.value)} className="w-full px-2 py-1 rounded text-black">
            <option value="">Todas</option>
            {categoryOptions.map((cat) => <option key={cat} value={cat}>{cat}</option>)}
          </select>
        </div>

        <div className="flex-1 min-w-[200px]">
          <label className="text-white block mb-1">Fecha Inicio</label>
          <input type="datetime-local" step="1" value={startDate} onChange={(e) => setStartDate(e.target.value)} className="w-full px-2 py-1 rounded text-black" />
        </div>

        <div className="flex-1 min-w-[200px]">
          <label className="text-white block mb-1">Fecha Fin</label>
          <input type="datetime-local" step="1" value={endDate} onChange={(e) => setEndDate(e.target.value)} className="w-full px-2 py-1 rounded text-black" />
        </div>

        <div className="flex-1 min-w-[150px]">
          <label className="text-white block mb-1">Activo</label>
          <select value={activate} onChange={(e) => setActivate(e.target.value)} className="w-full px-2 py-1 rounded text-black">
            {booleanOptions.map((opt) => <option key={opt} value={opt}>{opt}</option>)}
          </select>
        </div>

        <div className="flex-1 min-w-[200px]">
          <label className="text-white block mb-1">Nombre filtro</label>
          <input type="text" value={filterName} onChange={(e) => setFilterName(e.target.value)} placeholder="Nombre del filtro" className="w-full px-2 py-1 rounded text-black" />
        </div>

        <div className="flex gap-2">
          <button onClick={fetchDonationReport} className="px-6 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition">Buscar</button>
          {!isOther && <button onClick={saveDonationFilter} className="px-6 py-2 bg-green-700 text-white rounded hover:bg-green-800 transition">Guardar Filtro</button>}
        </div>
      </div>

      {/* Filtros guardados */}
      {savedFilters.length > 0 && (
        <div className="bg-[#232D4F] px-6 py-4 rounded mb-6">
          <h2 className="text-2xl text-white mb-3 font-semibold">Filtros guardados</h2>
          <div className="flex flex-wrap gap-3">
            {savedFilters.map((f) => (
              <div key={f.idFilter} className="bg-[#1B2440] text-white px-4 py-3 rounded-lg shadow-md flex flex-col gap-2 w-[250px]">
                <div className="font-semibold text-lg">{f.filterName}</div>
                <div className="text-sm opacity-80">
                  <div>Desde: {f.startDate ? new Date(f.startDate).toLocaleString() : "—"}</div>
                  <div>Hasta: {f.endDate ? new Date(f.endDate).toLocaleString() : "—"}</div>
                  <div>Activo: {f.activate === true ? "Sí" : f.activate === false ? "No" : "Ambos"}</div>
                  <div>Categoría: {f.category || "Todas"}</div>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => applyFilter(f)} className="flex-1 bg-blue-500 hover:bg-blue-600 text-white rounded py-1 text-sm">Aplicar</button>
                  <button onClick={() => deleteFilter(f.idFilter)} className="flex-1 bg-red-600 hover:bg-red-700 text-white rounded py-1 text-sm">Eliminar</button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Tabla de resultados */}
      {reportData.map((report, idx) => (
        <div key={idx} className="mb-10">
          <h2 className="text-3xl font-semibold text-blue-400 mb-3">{report.category || "Sin categoría"}</h2>
          <p className="text-gray-300 mb-2">Activo: {report.activate ? "Sí" : "No"} | Total Cantidad: {report.totalQuantity}</p>
          <table className="min-w-full border border-gray-700 text-center">
            <thead>
              <tr className="bg-gray-900 text-white">
                <th className="px-4 py-2 border border-gray-700">Descripción</th>
                <th className="px-4 py-2 border border-gray-700">Cantidad</th>
              </tr>
            </thead>
            <tbody>
              {report.details.map((d, i) => (
                <tr key={i} className="text-gray-200">
                  <td className="px-4 py-2 border border-gray-700">{d.description}</td>
                  <td className="px-4 py-2 border border-gray-700">{d.quantity}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ))}
    </div>
  );
};

export default DonationReportComponent;
