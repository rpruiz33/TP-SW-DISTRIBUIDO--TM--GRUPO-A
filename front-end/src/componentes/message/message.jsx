import { useState, useEffect } from "react";

const baseURL = "http://localhost:5000/api";
const CATEGORIAS = ["ROPA", "ALIMENTO", "JUGUETE", "UTIL_ESCOLAR"];

export default function Mensajeria() {
  const [pestana, setPestana] = useState("donaciones");

  const [solicitudes, setSolicitudes] = useState([{ categoria: "", descripcion: "" }]);
  const [ofertas, setOfertas] = useState([{ categoria: "", descripcion: "", cantidad: "" }]);

  const [eventos, setEventos] = useState([]);
  const [nuevoEvento, setNuevoEvento] = useState({ nombre: "", descripcion: "", fechaHora: "" });
  const [adhesion, setAdhesion] = useState({ idEvento: "", nombre: "", apellido: "", email: "" });

  /*** FETCH DINÁMICO DE EVENTOS ***/
  useEffect(() => {
    fetchEventos();
  }, []);

  const fetchEventos = async () => {
    try {
      const resp = await fetch(`${baseURL}/eventlist`);
      const data = await resp.json();
      setEventos(data.events || []); // Ajusta según tu JSON
    } catch (e) {
      console.error("Error obteniendo eventos:", e);
    }
  };

  /*** DONACIONES ***/
  const handleAgregarSolicitud = () =>
    setSolicitudes([...solicitudes, { categoria: "", descripcion: "" }]);

  const handleCambioSolicitud = (i, f, v) => {
    const nuevas = [...solicitudes];
    nuevas[i][f] = v;
    setSolicitudes(nuevas);
  };

  const handleEnviarSolicitud = async () => {
    if (solicitudes.some(s => !CATEGORIAS.includes(s.categoria))) return alert("⚠️ Categoría inválida.");
    try {
      const resp = await fetch(`${baseURL}/solicitar-donaciones`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ idOrganizacion: 1, listaDonaciones: solicitudes }),
      });
      const data = await resp.json();
      if (data.success) {
        alert("✅ Solicitud enviada!");
        setSolicitudes([{ categoria: "", descripcion: "" }]);
      } else alert("❌ Error: " + data.message);
    } catch (e) {
      alert("❌ Error enviando solicitud: " + e.message);
    }
  };

  const handleAgregarOferta = () =>
    setOfertas([...ofertas, { categoria: "", descripcion: "", cantidad: "" }]);

  const handleCambioOferta = (i, f, v) => {
    const nuevas = [...ofertas];
    nuevas[i][f] = v;
    setOfertas(nuevas);
  };

  const handleEnviarOferta = async () => {
    if (ofertas.some(o => !CATEGORIAS.includes(o.categoria))) return alert("⚠️ Categoría inválida.");
    try {
      const resp = await fetch(`${baseURL}/ofrecer-donaciones`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ idOrganizacion: 1, listaDonaciones: ofertas }),
      });
      const data = await resp.json();
      if (data.success) {
        alert("✅ Oferta enviada!");
        setOfertas([{ categoria: "", descripcion: "", cantidad: "" }]);
      } else alert("❌ Error: " + data.message);
    } catch (e) {
      alert("❌ Error enviando oferta: " + e.message);
    }
  };
const handleEnviarOperacion = async () => {
  try {
    const resp = await fetch(`${baseURL}/crear-operacion`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        operationType: "SOLICITUD",
        descripcion: "Solicitud desde React con gRPC",
      }),
    });
    const data = await resp.json();
    if (data.success) alert("✅ Operación creada: " + data.message);
    else alert("❌ Error: " + data.message);
  } catch (e) {
    alert("❌ Error: " + e.message);
  }
};

  /*** EVENTOS ***/
  const handleCrearEvento = async () => {
    try {
      const resp = await fetch(`${baseURL}/publicar-evento`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ...nuevoEvento, idOrganizacion: 1 }),
      });
      const data = await resp.json();
      if (data.success) {
        setEventos(prev => [...prev, { ...nuevoEvento, idEvento: data.idEvento }]);
        setNuevoEvento({ nombre: "", descripcion: "", fechaHora: "" });
        alert("✅ Evento creado!");
      } else alert("❌ Error: " + data.message);
    } catch (e) {
      alert("❌ Error creando evento: " + e.message);
    }
  };

  const handleBajaEvento = async (idEvento) => {
    try {
      const resp = await fetch(`${baseURL}/baja-evento`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ idEvento, idOrganizacion: 1 }),
      });
      const data = await resp.json();
      if (data.success) {
        setEventos(eventos.filter(e => e.idEvento !== idEvento));
        alert("✅ Evento dado de baja");
      } else alert("❌ Error: " + data.message);
    } catch (e) {
      alert("❌ Error dando de baja evento: " + e.message);
    }
  };

  /*** ADHESIÓN ***/
  const handleAdherir = async () => {
    try {
      const resp = await fetch(`${baseURL}/adhesion-evento/1`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(adhesion),
      });
      const data = await resp.json();
      if (data.success) {
        alert("✅ Adhesión enviada!");
        setAdhesion({ idEvento: "", nombre: "", apellido: "", email: "" });
      } else alert("❌ Error: " + data.message);
    } catch (e) {
      alert("❌ Error enviando adhesión: " + e.message);
    }
  };

  /*** RENDER PESTAÑAS ***/
  const renderPestana = () => {
    const inputClass = "border border-gray-600 bg-gray-800 text-white px-2 py-1 rounded focus:outline-none focus:ring-2 focus:ring-blue-500";
    const selectClass = inputClass + " cursor-pointer";
    const buttonClass = "bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded mr-2 transition";

    switch (pestana) {
      case "donaciones":
  return (
    <div className="flex flex-col gap-6">
      
      {/* Solicitar Donaciones */}
      <div className="flex flex-col gap-3 items-center">
        <h2 className="text-xl font-semibold mb-2">Solicitar Donaciones</h2>
        {solicitudes.map((d, i) => (
          <div key={i} className="flex flex-col gap-2 w-full">
            <select
              value={d.categoria}
              onChange={e => handleCambioSolicitud(i, "categoria", e.target.value)}
              className="w-full border border-gray-600 bg-gray-800 text-white px-2 py-1 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 cursor-pointer"
            >
              <option value="">Seleccionar categoría</option>
              {CATEGORIAS.map(cat => <option key={cat} value={cat}>{cat}</option>)}
            </select>
            <input
              value={d.descripcion}
              onChange={e => handleCambioSolicitud(i, "descripcion", e.target.value)}
              placeholder="Descripción"
              className="w-full border border-gray-600 bg-gray-800 text-white px-2 py-1 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        ))}
        <div className="flex gap-2 mt-2">
          <button onClick={handleAgregarSolicitud} className="bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded">Agregar</button>
          <button onClick={handleEnviarSolicitud} className="bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded">Enviar Solicitud</button>
        </div>
      </div>

      <hr className="border-gray-700 my-4" />

      {/* Ofrecer Donaciones */}
      <div className="flex flex-col gap-3 items-center">
        <h2 className="text-xl font-semibold mb-2">Ofrecer Donaciones</h2>
        {ofertas.map((o, i) => (
          <div key={i} className="flex flex-col gap-2 w-full">
            <select
              value={o.categoria}
              onChange={e => handleCambioOferta(i, "categoria", e.target.value)}
              className="w-full border border-gray-600 bg-gray-800 text-white px-2 py-1 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 cursor-pointer"
            >
              <option value="">Seleccionar categoría</option>
              {CATEGORIAS.map(cat => <option key={cat} value={cat}>{cat}</option>)}
            </select>
            <input
              value={o.descripcion}
              onChange={e => handleCambioOferta(i, "descripcion", e.target.value)}
              placeholder="Descripción"
              className="w-full border border-gray-600 bg-gray-800 text-white px-2 py-1 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <input
              value={o.cantidad}
              onChange={e => handleCambioOferta(i, "cantidad", e.target.value)}
              placeholder="Cantidad"
              className="w-full border border-gray-600 bg-gray-800 text-white px-2 py-1 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        ))}
        <div className="flex gap-2 mt-2">
          <button onClick={handleAgregarOferta} className="bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded">Agregar</button>
          <button onClick={handleEnviarOferta} className="bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded">Enviar Oferta</button>
        </div>
      </div>

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
             
            </div>
             <button onClick={handleCrearEvento} className="bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded transition">Crear Evento</button>
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

            </div>
              <button onClick={handleAdherir} className="bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded transition">Adherir</button>
          </div>
        );

      default:
        return null;
    }
  };

  return (
  <div className="bg-gray-900 min-h-screen flex justify-center items-start pt-10 text-white">
    <div className="w-full max-w-md p-4">
      {/* Botones de pestañas */}
      <div className="flex gap-4 mb-6 justify-center">
        <button onClick={() => setPestana("donaciones")} className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded">Donaciones</button>
        <button onClick={() => setPestana("eventos")} className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded">Eventos</button>
        <button onClick={() => setPestana("adhesion")} className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded">Adhesión</button>
      </div>

      {/* Contenido de cada pestaña */}
      <div className="flex flex-col gap-4">
        {renderPestana()}
      </div>
    </div>
  </div>
);

}
