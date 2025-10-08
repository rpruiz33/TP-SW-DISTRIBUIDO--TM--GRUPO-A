import { useState } from "react";
import { generarOperacion } from '../services/kafkaService';

export default function Mensajeria() {
  const [pestana, setPestana] = useState("donaciones");
  const CATEGORIAS = ["ROPA", "ALIMENTO", "JUGUETE", "UTIL_ESCOLAR"];

  const [solicitudes, setSolicitudes] = useState([{ categoria: "", descripcion: "" }]);
  const [ofertas, setOfertas] = useState([{ categoria: "", descripcion: "", cantidad: "" }]);
  const [eventos, setEventos] = useState([]);
  const [nuevoEvento, setNuevoEvento] = useState({ nombre: "", descripcion: "", fechaHora: "" });
  const [adhesion, setAdhesion] = useState({ idEvento: "", nombre: "", apellido: "", email: "" });

  /*** DONACIONES ***/
  const handleAgregarSolicitud = () => setSolicitudes([...solicitudes, { categoria: "", descripcion: "" }]);
  const handleCambioSolicitud = (i, f, v) => {
    const nuevas = [...solicitudes]; nuevas[i][f] = v; setSolicitudes(nuevas);
  };
  const handleEnviarSolicitud = async () => {
    if (solicitudes.some(s => !CATEGORIAS.includes(s.categoria))) return alert("⚠️ Categoría inválida.");
    await generarOperacion({ tipo: "SOLICITUD", idOrganizacion: 1, listaDonaciones: solicitudes });
    alert("Solicitud enviada!");
  };

  const handleAgregarOferta = () => setOfertas([...ofertas, { categoria: "", descripcion: "", cantidad: "" }]);
  const handleCambioOferta = (i, f, v) => {
    const nuevas = [...ofertas]; nuevas[i][f] = v; setOfertas(nuevas);
  };
  const handleEnviarOferta = async () => {
    if (ofertas.some(o => !CATEGORIAS.includes(o.categoria))) return alert("⚠️ Categoría inválida.");
    await generarOperacion({ tipo: "OFERTA", idOrganizacion: 1, listaDonaciones: ofertas });
    alert("Oferta enviada!");
  };

  /*** EVENTOS ***/
  const handleCrearEvento = async () => {
    try {
      const r = await fetch("http://localhost:5000/api/evento", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ...nuevoEvento, idOrganizacion: 1 })
      });
      const data = await r.json();
      if (data.success) {
        setEventos(prev => [...prev, { ...nuevoEvento, idEvento: data.idEvento }]);
        setNuevoEvento({ nombre: "", descripcion: "", fechaHora: "" });
        alert("Evento creado!");
      } else alert("Error: " + data.message);
    } catch (e) { alert("Error: " + e.message); }
  };

  const handleBajaEvento = async (idEvento) => {
    try {
      const r = await fetch("http://localhost:5000/api/baja-evento", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ idEvento, idOrganizacion: 1 })
      });
      const data = await r.json();
      if (data.success) {
        setEventos(eventos.filter(e => e.idEvento !== idEvento));
        alert("Evento dado de baja");
      } else alert("Error: " + data.message);
    } catch (e) { alert("Error: " + e.message); }
  };

  /*** ADHESIÓN ***/
  const handleAdherir = async () => {
    try {
      const r = await fetch(`http://localhost:5000/api/adhesion/1`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(adhesion)
      });
      const data = await r.json();
      if (data.success) {
        alert("Adhesión enviada!");
        setAdhesion({ idEvento: "", nombre: "", apellido: "", email: "" });
      } else alert("Error: " + data.message);
    } catch (e) { alert("Error: " + e.message); }
  };

  /*** UI PESTAÑAS ***/
  const renderPestana = () => {
    const inputClass = "border border-gray-600 bg-gray-800 text-white px-2 py-1 rounded focus:outline-none focus:ring-2 focus:ring-blue-500";
    const selectClass = inputClass + " cursor-pointer";
    const buttonClass = "bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded mr-2 transition";

    switch (pestana) {
      case "donaciones":
        return (
          <div>
            <h2 className="text-xl font-semibold mb-3 text-white">Solicitar Donaciones</h2>
            {solicitudes.map((d, i) => (
              <div key={i} className="flex gap-2 mb-2">
                <select value={d.categoria} onChange={e => handleCambioSolicitud(i, "categoria", e.target.value)} className={selectClass}>
                  <option value="">Seleccionar categoría</option>
                  {CATEGORIAS.map(cat => <option key={cat} value={cat}>{cat}</option>)}
                </select>
                <input value={d.descripcion} onChange={e => handleCambioSolicitud(i, "descripcion", e.target.value)} placeholder="Descripción" className={inputClass} />
              </div>
            ))}
            <button onClick={handleAgregarSolicitud} className={buttonClass}>Agregar</button>
            <button onClick={handleEnviarSolicitud} className="bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded transition">Enviar Solicitud</button>

            <hr className="my-6 border-gray-700" />

            <h2 className="text-xl font-semibold mb-3 text-white">Ofrecer Donaciones</h2>
            {ofertas.map((o, i) => (
              <div key={i} className="flex gap-2 mb-2">
                <select value={o.categoria} onChange={e => handleCambioOferta(i, "categoria", e.target.value)} className={selectClass}>
                  <option value="">Seleccionar categoría</option>
                  {CATEGORIAS.map(cat => <option key={cat} value={cat}>{cat}</option>)}
                </select>
                <input value={o.descripcion} onChange={e => handleCambioOferta(i, "descripcion", e.target.value)} placeholder="Descripción" className={inputClass} />
                <input value={o.cantidad} onChange={e => handleCambioOferta(i, "cantidad", e.target.value)} placeholder="Cantidad" className={inputClass} />
              </div>
            ))}
            <button onClick={handleAgregarOferta} className={buttonClass}>Agregar</button>
            <button onClick={handleEnviarOferta} className="bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded transition">Enviar Oferta</button>
          </div>
        );

      case "eventos":
        return (
          <div>
            <h2 className="text-xl font-semibold mb-3 text-white">Eventos Externos</h2>
            <div className="mb-4 flex flex-wrap gap-2">
              <input placeholder="Nombre" value={nuevoEvento.nombre} onChange={e => setNuevoEvento({ ...nuevoEvento, nombre: e.target.value })} className={inputClass} />
              <input placeholder="Descripción" value={nuevoEvento.descripcion} onChange={e => setNuevoEvento({ ...nuevoEvento, descripcion: e.target.value })} className={inputClass} />
              <input type="datetime-local" value={nuevoEvento.fechaHora} onChange={e => setNuevoEvento({ ...nuevoEvento, fechaHora: e.target.value })} className={inputClass} />
              <button onClick={handleCrearEvento} className="bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded transition">Crear Evento</button>
            </div>

            <ul>
              {eventos.map(e => (
                <li key={e.idEvento} className="flex justify-between items-center border border-gray-700 p-2 mb-2 rounded bg-gray-800 text-white">
                  <div><strong>{e.nombre}</strong> - {e.descripcion} - {e.fechaHora}</div>
                  <button onClick={() => handleBajaEvento(e.idEvento)} className="bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded">Baja</button>
                </li>
              ))}
            </ul>
          </div>
        );

      case "adhesion":
        return (
          <div>
            <h2 className="text-xl font-semibold mb-3 text-white">Adhesión a Evento</h2>
            <div className="flex flex-wrap gap-2 mb-3">
              <input placeholder="ID Evento" value={adhesion.idEvento} onChange={e => setAdhesion({ ...adhesion, idEvento: e.target.value })} className={inputClass} />
              <input placeholder="Nombre" value={adhesion.nombre} onChange={e => setAdhesion({ ...adhesion, nombre: e.target.value })} className={inputClass} />
              <input placeholder="Apellido" value={adhesion.apellido} onChange={e => setAdhesion({ ...adhesion, apellido: e.target.value })} className={inputClass} />
              <input placeholder="Email" value={adhesion.email} onChange={e => setAdhesion({ ...adhesion, email: e.target.value })} className={inputClass} />
              <button onClick={handleAdherir} className="bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded transition">Adherir</button>
            </div>
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen bg-black flex items-center justify-center p-5">
      <div className="w-full max-w-5xl bg-gray-900 p-8 rounded-lg shadow-md">
        <h1 className="text-3xl font-bold text-white mb-6 text-center">Mensajería de ONGs</h1>

        <div className="flex space-x-4 mb-6 border-b border-gray-700">
          {["donaciones", "eventos", "adhesion"].map(tab => (
            <button
              key={tab}
              onClick={() => setPestana(tab)}
              className={`px-4 py-2 font-medium ${
                pestana === tab
                  ? "border-b-2 border-blue-600 text-blue-400"
                  : "text-gray-400 hover:text-gray-200"
              }`}
            >
              {tab === "donaciones" ? "Donaciones" : tab === "eventos" ? "Eventos" : "Adhesión"}
            </button>
          ))}
        </div>

        {renderPestana()}
      </div>
    </div>
  );
}
