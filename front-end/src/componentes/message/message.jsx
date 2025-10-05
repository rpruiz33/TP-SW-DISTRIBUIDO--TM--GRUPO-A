import { useState, useEffect } from "react";
import { generarOperacion } from '../services/kafkaService'; // solo la función que existe

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
  const handleEnviarSolicitud = () => {
    if (solicitudes.some(s => !CATEGORIAS.includes(s.categoria))) {
      alert("⚠️ Categoría inválida.");
      return;
    }
    const mensaje = { tipo: "SOLICITUD", idOrganizacion: 1, listaDonaciones: solicitudes };
    generarOperacion(mensaje);
    alert("Solicitud enviada!");
  };

  const handleAgregarOferta = () => setOfertas([...ofertas, { categoria: "", descripcion: "", cantidad: "" }]);
  const handleCambioOferta = (index, field, value) => {
    const nuevas = [...ofertas];
    nuevas[index][field] = value;
    setOfertas(nuevas);
  };
  const handleEnviarOferta = () => {
    if (ofertas.some(o => !CATEGORIAS.includes(o.categoria))) {
      alert("⚠️ Categoría inválida.");
      return;
    }
    const mensaje = { tipo: "OFERTA", idOrganizacion: 1, listaDonaciones: ofertas };
    generarOperacion(mensaje);
    alert("Oferta enviada!");
  };

  /*** FUNCIONES EVENTOS ***/
  const handleCrearEvento = () => {
    alert("Función de crear evento pendiente de implementar");
  };
  const handleBajaEvento = (id) => {
    alert("Función de baja evento pendiente de implementar");
    setEventos(eventos.filter(e => e.idEvento !== id));
  };

  /*** FUNCIONES ADHESIÓN ***/
  const handleAdherir = () => {
    alert("Función de adhesión pendiente de implementar");
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
            <h2 className="text-xl font-semibold mb-2">Eventos (pendiente)</h2>
          </div>
        );

      case "adhesion":
        return (
          <div>
            <h2 className="text-xl font-semibold mb-2">Adhesión a Evento (pendiente)</h2>
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
