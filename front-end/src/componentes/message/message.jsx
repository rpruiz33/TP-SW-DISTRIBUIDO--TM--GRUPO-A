import { useState, useEffect } from "react";
import { generarOperacion } from '../services/kafkaService'; // función ya existente

export default function Mensajeria() {
  const [pestana, setPestana] = useState("donaciones");

  const CATEGORIAS = ["ROPA", "ALIMENTO", "JUGUETE", "UTIL_ESCOLAR"];

  const [solicitudes, setSolicitudes] = useState([{ categoria: "", descripcion: "" }]);
  const [ofertas, setOfertas] = useState([{ categoria: "", descripcion: "", cantidad: "" }]);

  const [eventos, setEventos] = useState([]);
  const [nuevoEvento, setNuevoEvento] = useState({ nombre: "", descripcion: "", fechaHora: "" });

  const [adhesion, setAdhesion] = useState({ idEvento: "", nombre: "", apellido: "", email: "" });

  /*** FUNCIONES DONACIONES ***/
  const handleAgregarSolicitud = () => setSolicitudes([...solicitudes, { categoria: "", descripcion: "" }]);
  const handleCambioSolicitud = (index, field, value) => {
    const nuevas = [...solicitudes];
    nuevas[index][field] = value;
    setSolicitudes(nuevas);
  };
  const handleEnviarSolicitud = async () => {
    if (solicitudes.some(s => !CATEGORIAS.includes(s.categoria))) {
      alert("⚠️ Categoría inválida.");
      return;
    }
    const mensaje = { tipo: "SOLICITUD", idOrganizacion: 1, listaDonaciones: solicitudes };
    await generarOperacion(mensaje);
    alert("Solicitud enviada!");
  };

  const handleAgregarOferta = () => setOfertas([...ofertas, { categoria: "", descripcion: "", cantidad: "" }]);
  const handleCambioOferta = (index, field, value) => {
    const nuevas = [...ofertas];
    nuevas[index][field] = value;
    setOfertas(nuevas);
  };
  const handleEnviarOferta = async () => {
    if (ofertas.some(o => !CATEGORIAS.includes(o.categoria))) {
      alert("⚠️ Categoría inválida.");
      return;
    }
    const mensaje = { tipo: "OFERTA", idOrganizacion: 1, listaDonaciones: ofertas };
    await generarOperacion(mensaje);
    alert("Oferta enviada!");
  };
const handleCrearEvento = async () => {
  try {
    const response = await fetch("http://localhost:5000/api/evento", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ ...nuevoEvento, idOrganizacion: 1 })
    });
    const data = await response.json();

    if (data.success) {
      alert("Evento creado!");
      // Actualizamos la lista de eventos agregando el nuevo
      setEventos(prev => [...prev, { ...nuevoEvento, idEvento: data.idEvento }]);
      setNuevoEvento({ nombre: "", descripcion: "", fechaHora: "" });
    } else {
      alert("Error al crear evento: " + data.message);
    }
  } catch (e) {
    alert("Error al conectar con la API: " + e.message);
  }
};


  const handleBajaEvento = async (idEvento) => {
    try {
      const response = await fetch("http://localhost:5000/api/baja-evento", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ idEvento, idOrganizacion: 1 })
      });
      const data = await response.json();
      if (data.success) {
        setEventos(eventos.filter(e => e.idEvento !== idEvento));
        alert("Evento dado de baja");
      } else {
        alert("Error al dar de baja: " + data.message);
      }
    } catch (e) {
      alert("Error al conectar con la API: " + e.message);
    }
  };

  /*** FUNCIONES ADHESIÓN ***/
  const handleAdherir = async () => {
    try {
      const response = await fetch(`http://localhost:5000/api/adhesion/1`, { // idOrganizador=1
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(adhesion)
      });
      const data = await response.json();
      if (data.success) {
        alert("Adhesión enviada!");
        setAdhesion({ idEvento: "", nombre: "", apellido: "", email: "" });
      } else {
        alert("Error al adherirse: " + data.message);
      }
    } catch (e) {
      alert("Error al conectar con la API: " + e.message);
    }
  };

  /*** RENDER PESTANAS ***/
  const renderPestana = () => {
    switch (pestana) {
      case "donaciones":
        return (
          <div>
            <h2 className="text-xl font-semibold mb-2">Solicitar Donaciones</h2>
            {solicitudes.map((d, i) => (
              <div key={i} className="flex gap-2 mb-2">
                <select
                  value={d.categoria}
                  onChange={e => handleCambioSolicitud(i, "categoria", e.target.value)}
                  className="border px-2 py-1"
                >
                  <option value="">Seleccionar categoría</option>
                  {CATEGORIAS.map(cat => <option key={cat} value={cat}>{cat}</option>)}
                </select>
                <input
                  placeholder="Descripción"
                  value={d.descripcion}
                  onChange={e => handleCambioSolicitud(i, "descripcion", e.target.value)}
                  className="border px-2 py-1"
                />
              </div>
            ))}
            <button onClick={handleAgregarSolicitud} className="bg-blue-500 text-white px-3 py-1 mr-2 rounded">Agregar</button>
            <button onClick={handleEnviarSolicitud} className="bg-green-500 text-white px-3 py-1 rounded">Enviar Solicitud</button>

            <hr className="my-4"/>

            <h2 className="text-xl font-semibold mb-2">Ofrecer Donaciones</h2>
            {ofertas.map((o, i) => (
              <div key={i} className="flex gap-2 mb-2">
                <select
                  value={o.categoria}
                  onChange={e => handleCambioOferta(i, "categoria", e.target.value)}
                  className="border px-2 py-1"
                >
                  <option value="">Seleccionar categoría</option>
                  {CATEGORIAS.map(cat => <option key={cat} value={cat}>{cat}</option>)}
                </select>
                <input
                  placeholder="Descripción"
                  value={o.descripcion}
                  onChange={e => handleCambioOferta(i, "descripcion", e.target.value)}
                  className="border px-2 py-1"
                />
                <input
                  placeholder="Cantidad"
                  value={o.cantidad}
                  onChange={e => handleCambioOferta(i, "cantidad", e.target.value)}
                  className="border px-2 py-1"
                />
              </div>
            ))}
            <button onClick={handleAgregarOferta} className="bg-blue-500 text-white px-3 py-1 mr-2 rounded">Agregar</button>
            <button onClick={handleEnviarOferta} className="bg-green-500 text-white px-3 py-1 rounded">Enviar Oferta</button>
          </div>
        );

      case "eventos":
        return (
          <div>
            <h2 className="text-xl font-semibold mb-2">Eventos Externos</h2>
            <div className="mb-4">
              <input
                placeholder="Nombre"
                value={nuevoEvento.nombre}
                onChange={e => setNuevoEvento({ ...nuevoEvento, nombre: e.target.value })}
                className="border px-2 py-1 mr-2"
              />
              <input
                placeholder="Descripción"
                value={nuevoEvento.descripcion}
                onChange={e => setNuevoEvento({ ...nuevoEvento, descripcion: e.target.value })}
                className="border px-2 py-1 mr-2"
              />
              <input
                type="datetime-local"
                value={nuevoEvento.fechaHora}
                onChange={e => setNuevoEvento({ ...nuevoEvento, fechaHora: e.target.value })}
                className="border px-2 py-1"
              />
              <button onClick={handleCrearEvento} className="bg-green-500 text-white px-3 py-1 ml-2 rounded">Crear Evento</button>
            </div>

            <ul>
              {eventos.map(e => (
                <li key={e.idEvento} className="flex justify-between border p-2 mb-2 rounded">
                  <div>
                    <strong>{e.nombre}</strong> - {e.descripcion} - {e.fechaHora}
                  </div>
                  <button onClick={() => handleBajaEvento(e.idEvento)} className="bg-red-500 text-white px-2 py-1 rounded">Baja</button>
                </li>
              ))}
            </ul>
          </div>
        );

      case "adhesion":
        return (
          <div>
            <h2 className="text-xl font-semibold mb-2">Adhesión a Evento</h2>
            <input
              placeholder="ID Evento"
              value={adhesion.idEvento}
              onChange={e => setAdhesion({ ...adhesion, idEvento: e.target.value })}
              className="border px-2 py-1 mr-2"
            />
            <input
              placeholder="Nombre"
              value={adhesion.nombre}
              onChange={e => setAdhesion({ ...adhesion, nombre: e.target.value })}
              className="border px-2 py-1 mr-2"
            />
            <input
              placeholder="Apellido"
              value={adhesion.apellido}
              onChange={e => setAdhesion({ ...adhesion, apellido: e.target.value })}
              className="border px-2 py-1 mr-2"
            />
            <input
              placeholder="Email"
              value={adhesion.email}
              onChange={e => setAdhesion({ ...adhesion, email: e.target.value })}
              className="border px-2 py-1 mr-2"
            />
            <button onClick={handleAdherir} className="bg-green-500 text-white px-3 py-1 rounded">Adherir</button>
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <div className="max-w-5xl mx-auto mt-6 p-4 bg-white rounded shadow">
      <h1 className="text-2xl font-semibold mb-4">Mensajería de ONGs</h1>

      <div className="flex space-x-4 mb-6 border-b">
        <button
          className={`px-4 py-2 font-medium ${pestana === "donaciones" ? "border-b-2 border-blue-600 text-blue-600" : "text-gray-600"}`}
          onClick={() => setPestana("donaciones")}
        >
          Donaciones
        </button>
        <button
          className={`px-4 py-2 font-medium ${pestana === "eventos" ? "border-b-2 border-blue-600 text-blue-600" : "text-gray-600"}`}
          onClick={() => setPestana("eventos")}
        >
          Eventos
        </button>
        <button
          className={`px-4 py-2 font-medium ${pestana === "adhesion" ? "border-b-2 border-blue-600 text-blue-600" : "text-gray-600"}`}
          onClick={() => setPestana("adhesion")}
        >
          Adhesión
        </button>
      </div>

      {renderPestana()}
    </div>
  );
}
