import React, { useEffect, useState } from "react";
import axios from "axios";
import { useLocation } from "react-router-dom";

const DonationReportComponent = () => {
  const [reportData, setReportData] = useState([]);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [category, setCategory] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [activate, setActivate] = useState("AMBOS");
  const [filterName, setFilterName] = useState("");

  const location = useLocation();
  const isOther = location.state?.isOther || false; // true si son donaciones externas

  const categoryOptions = ["ROPA", "ALIMENTO", "JUGUETE", "UTIL_ESCOLAR"];
  const booleanOptions = ["SI", "NO", "AMBOS"];

  useEffect(() => {
    fetchDonationReport();
  }, [location]);

  const parseBoolean = (val) => (val === "SI" ? true : val === "NO" ? false : null);

  // 🔹 Obtener reporte de donaciones
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

      const variables = {
        category: category || null,
        startDate: startDate || null,
        endDate: endDate || null,
        activate: parseBoolean(activate),
        isExternal: isOther
      };

      const response = await axios.post(
        "http://localhost:8080/graphql",
        { query, variables },
        { headers: { "Content-Type": "application/json" } }
      );

      setReportData(response.data.data.donationReport || []);
      setError("");
    } catch (err) {
      console.error(err);
      setError("❌ Error al obtener el reporte de donaciones");
    }
  };

  // 🔹 Generar Excel
  const generateExcel = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/excel/donaciones", {
        params: { isExternal: isOther },
        responseType: "blob",
      });

      const blob = new Blob([response.data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });

      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", "ReporteDonaciones.xlsx");
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Error en la generación de Excel:", err);
      setError("❌ Error generando el archivo Excel");
    }
  };

  // 🔹 Guardar filtro en DB
  const saveDonationFilter = async () => {
    if (!filterName || filterName.trim() === "") {
      setError("❌ Ingrese un nombre para el filtro");
      return;
    }

    const input = {
      filterName,
      startDate: startDate || null,
      endDate: endDate || null,
      activate: parseBoolean(activate),
      category: category || null,
    };

    const emailOrUsername = localStorage.getItem("usernameOrEmail") || "";
    if (!emailOrUsername) {
      setError("❌ No se encontró usuario en sesión");
      return;
    }

    const mutation = `
      mutation SaveDonationFilter($input: DonationFilterDTO!, $emailOrUsername: String!) {
        saveDonationFilter(input: $input, emailOrUsername: $emailOrUsername)
      }
    `;

    try {
      const resp = await axios.post(
        "http://localhost:8080/graphql",
        { query: mutation, variables: { input, emailOrUsername } },
        { headers: { "Content-Type": "application/json" } }
      );

      const saved = resp.data?.data?.saveDonationFilter;
      if (saved) {
        setError("");
        setSuccessMessage("✅ Filtro guardado correctamente.");
        setTimeout(() => setSuccessMessage(""), 4000);
      } else {
        setError("❌ No se pudo guardar el filtro");
      }
    } catch (err) {
      console.error("Error guardando filtro:", err);
      setError("❌ Error al guardar el filtro en el servidor");
    }
  };

  return (
    <div className="p-6 bg-[#01000F] min-h-screen flex flex-col">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-5xl font-bold text-white">
          {isOther ? "Reporte de Donaciones Externas" : "Reporte de Donaciones Propias"}
        </h1>
        <button
          onClick={generateExcel}
          className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          Generar reporte en Excel
        </button>
      </div>

      {/* Mensajes unificados estilo EventReport */}
      {error && (
        <div className="mb-4 px-4 py-2 rounded text-white text-center bg-red-500">{error}</div>
      )}
      {successMessage && (
        <div className="mb-4 px-4 py-2 rounded text-white text-center bg-green-500">{successMessage}</div>
      )}

      {/* Filtros */}
      <div className="bg-[#232D4F] px-6 py-3 rounded mb-4 mt-4 flex gap-4 items-end w-full flex-wrap">
        <div className="flex-1 min-w-[200px]">
          <label className="text-white block mb-1">Categoría</label>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="w-full px-2 py-1 rounded text-black"
          >
            <option value="">Todas</option>
            {categoryOptions.map((cat) => (
              <option key={cat} value={cat}>
                {cat}
              </option>
            ))}
          </select>
        </div>

        <div className="flex-1 min-w-[200px]">
          <label className="text-white block mb-1">Fecha Inicio</label>
          <input
            type="datetime-local"
            step="1"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="w-full px-2 py-1 rounded text-black"
          />
        </div>

        <div className="flex-1 min-w-[200px]">
          <label className="text-white block mb-1">Fecha Fin</label>
          <input
            type="datetime-local"
            step="1"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="w-full px-2 py-1 rounded text-black"
          />
        </div>

        <div className="flex-1 min-w-[200px]">
          <label className="text-white block mb-1">Activo</label>
          <select
            value={activate}
            onChange={(e) => setActivate(e.target.value)}
            className="w-full px-2 py-1 rounded text-black"
          >
            {booleanOptions.map((opt) => (
              <option key={opt} value={opt}>
                {opt}
              </option>
            ))}
          </select>
        </div>

        <div className="flex-none">
          <button
            onClick={fetchDonationReport}
            className="px-6 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Buscar
          </button>
        </div>

        {/* Guardar filtro */}
        <div className="flex items-end gap-2">
          <div>
            <label className="text-white block mb-1">Nombre del filtro</label>
            <input
              type="text"
              value={filterName}
              onChange={(e) => setFilterName(e.target.value)}
              placeholder="Nombre del filtro"
              className="px-2 py-1 rounded text-black"
            />
          </div>

          <div className="flex-none">
            <button
              onClick={saveDonationFilter}
              className="px-6 py-2 bg-green-700 text-white rounded hover:bg-green-900"
            >
              Guardar Filtro
            </button>
          </div>
        </div>
      </div>

      {/* Tabla de resultados */}
      {reportData.map((report, idx) => (
        <div key={idx} className="mb-10">
          <h2 className="text-3xl font-semibold text-blue-400 mb-3">
            {report.category || "Sin categoría"}
          </h2>
          <p className="text-gray-300 mb-2">
            Activo: {report.activate ? "Sí" : "No"} | Total Cantidad: {report.totalQuantity}
          </p>

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
