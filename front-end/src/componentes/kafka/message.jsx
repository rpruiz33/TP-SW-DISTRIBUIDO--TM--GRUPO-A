import { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";


const baseURL = "http://localhost:5000/api";
const CATEGORIAS = ["ROPA", "ALIMENTO", "JUGUETE", "UTIL_ESCOLAR"];

export default function Mensajeria() {
  const [pestana, setPestana] = useState("donaciones");

  const [solicitudes, setSolicitudes] = useState([{ categoria: "", descripcion: "" }]);
  const [ofertas, setOfertas] = useState([{ categoria: "", descripcion: "", cantidad: "" }]);
  const [events, setEvents] = useState([]);

  const [donations, setDonations] = useState([]);


  const [error, setError] = useState("");


  const navigate = useNavigate();

  /* Operacion */

  //Generamos un id para la operacion de solicitud y oferta, que no dependen de nada
  //Lo generamos automaticamente con año mes dia hora y minutos para que sea unico y lo asignamos a la llamada a la api
  const generarIdOperacion = () => {
    const ahora = new Date();
    const anio = ahora.getFullYear();
    const mes = String(ahora.getMonth() + 1).padStart(2, '0');
    const dia = String(ahora.getDate()).padStart(2, '0');
    const hora = String(ahora.getHours()).padStart(2, '0');
    const minutos = String(ahora.getMinutes()).padStart(2, '0');

    var idOp = parseInt(`${anio}${mes}${dia}${hora}${minutos}`)
    return (idOp - 202000000000); // le sacamos los primeros 3 numeros del año, para que entre en un int 202.510.171.024 -> 510.171.024
  };

  useEffect(() => {
    if (pestana === "adhesion") {
      getExternalEvents();
    } else {
      fetchDonations();
    }
  }, [pestana]);


  /*** DONACIONES ***/
  const handleAgregarSolicitud = () =>
    setSolicitudes([...solicitudes, { categoria: "", descripcion: "" }]);


  const handleCambioSolicitud = (i, f, v) => {
    const nuevas = [...solicitudes];
    nuevas[i][f] = v;
    setSolicitudes(nuevas);
  };

  const requestList = (isExternal) => {

    navigate("/requestlist", { state: { isExternal } });
  };


  const fetchDonations = async () => {
    try {
      const response = await axios.get("http://localhost:5000/api/activedonationlist");

      if (Array.isArray(response.data)) {
        setDonations(response.data);
      } else if (response.data.donations && Array.isArray(response.data.donations)) {
        setDonations(response.data.donations);
      } else {
        setDonations([]);
        console.warn("La respuesta no contiene un array:", response.data);
      }
    } catch (err) {
      console.error("Error en la solicitud:", err);
      setError("Error de conexión con el servidor");
    }
  };

  const handleEnviarSolicitud = async () => {
    const idSolicitud = generarIdOperacion();

    const protoData = {
      idOperationMessage: idSolicitud,
      operationType: "SOLICITUD",
      donations: solicitudes
    }

    try {

      const response = await axios.post("http://localhost:5000/api/requestdonation", protoData);

      if (response.data.success) {

        alert("✅ Solicitud enviada!");
        setSolicitudes([{ categoria: "", descripcion: "" }]);
      } else alert("❌ Error: " + response.data.message);


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

    const idOferta = generarIdOperacion();

    const donationsProto = ofertas.map(o => ({
      category: o.categoria,
      description: o.descripcion,
      quantity: parseInt(o.cantidad, 10) || 0, // aseguramos que sea int
    }));

    const protoData = {
      idOperationMessage: idOferta,
      operationType:"OFERTA",
      donations: donationsProto,
    };

    try {

      const response = await axios.post("http://localhost:5000/api/offerdonation", protoData);

      if (response.data.success) {

        alert("✅ Oferta enviada!");
        setOfertas([{ categoria: "", descripcion: "", cantidad: "" }]);
      } else alert("❌ Error: " + response.data.message);


    } catch (e) {
      alert("❌ Error enviando oferta: " + e.message);
    }


  };


  const getExternalEvents = async () => {
    try {
      const response = await axios.get("http://localhost:5000/api/externalevents");
      const data = response.data.externalEvents || [];
      setEvents(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Error obteniendo eventos externos:", err);
      setError("Error al cargar los eventos externos");
    }
  };

  const handleAdhesion = async (event) => {

    const protoData = {
      idExternalEvent: event.id,
      emailVolunteer: localStorage.getItem("usernameOrEmail")
    }
    console.log(protoData)
    try {

      const response = await axios.post("http://localhost:5000/api/eventadhesion", protoData);

      alert(response.data.message);


    } catch (err) {
      console.error("Error adhiriendose al evento:", err);
      setError("Error al adherirse al evento");
    }
  };



  /*** RENDER PESTAÑAS ***/
  const renderPestana = () => {

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

              <div className="flex gap-2 mt-2">
                <button onClick={() => requestList(true)} className="bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded">Solicitudes Externas</button>
                <button onClick={() => requestList(false)} className="bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded">Solicitudes Propias</button>

              </div>

            </div>

            <hr className="border-gray-700 my-4" />

            {/* Ofrecer Donaciones */}
            <div className="flex flex-col gap-3 items-center">
              <h2 className="text-xl font-semibold mb-2">Ofrecer Donaciones</h2>
              {ofertas.map((o, i) => {
                const donacionSeleccionada = donations.find(d => d.id === o.id);

                return (
                  <div key={i} className="flex flex-col gap-2 w-full">
                    {/* Selector dinámico de donaciones */}
                    <select
                      value={o.id || ""}
                      onChange={e => {
                        const selectedId = parseInt(e.target.value);
                        const selectedDon = donations.find(d => d.id === selectedId);

                        handleCambioOferta(i, "id", selectedId);
                        handleCambioOferta(i, "categoria", selectedDon?.category || "");
                        handleCambioOferta(i, "descripcion", selectedDon?.description || "");
                        handleCambioOferta(i, "maxCantidad", selectedDon?.amount || 0);
                        handleCambioOferta(i, "cantidad", ""); // reset cantidad al cambiar selección
                      }}
                      className="w-full border border-gray-600 bg-gray-800 text-white px-2 py-1 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 cursor-pointer"
                    >
                      <option value="">Seleccionar donación</option>
                      {donations.map(d => (
                        <option key={d.id} value={d.id}>
                          {d.category} - {d.description}
                        </option>
                      ))}
                    </select>

                    {/* Input de cantidad */}
                    <input
                      type="number"
                      value={o.cantidad || ""}
                      onChange={e => {
                        let val = parseInt(e.target.value) || 0;
                        if (donacionSeleccionada && val > donacionSeleccionada.amount) {
                          val = donacionSeleccionada.amount; // no puede superar el disponible
                        }
                        handleCambioOferta(i, "cantidad", val);
                      }}
                      placeholder={`Cantidad (máx: ${donacionSeleccionada?.amount || 0})`}
                      className="w-full border border-gray-600 bg-gray-800 text-white px-2 py-1 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                );
              })}

              <div className="flex gap-2 mt-2">
                <button
                  onClick={handleAgregarOferta}
                  className="bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded"
                >
                  Agregar
                </button>
                <button
                  onClick={handleEnviarOferta}
                  className="bg-green-700 hover:bg-green-800 text-white px-4 py-2 rounded"
                >
                  Enviar Oferta
                </button>
              </div>
              <div className="flex gap-2 mt-2">
                <button onClick={() => navigate("/offerlist")} className="bg-blue-700 hover:bg-blue-800 text-white px-4 py-2 rounded">Lista de Ofertas</button>

              </div>
            </div>

          </div>
        );

      case "adhesion":
        return (
          <div className="p-8 bg-[#01000F] min-h-screen flex flex-col items-center">
            {/* Título */}
            <h1 className="text-3xl font-bold text-white mb-8">Eventos Externos</h1>

            {/* Mensaje de error */}
            {error && <div className="text-red-500 mb-4">{error}</div>}

            {/* Contenedor de tarjetas: max-w y padding para centrar contenido */}
            <div className="w-full max-w-5xl">
              <div className="grid gap-6 grid-cols-1 ">
                {events.map((event, idx) => (
                  <div
                    key={idx}
                    className="w-full h-full flex flex-col items-stretch bg-gradient-to-b from-gray-800 to-gray-900 border border-gray-700 rounded-xl shadow-lg p-6 hover:shadow-2xl transition-all duration-300"
                  >
                    <div className="flex flex-col gap-3">
                      <h2 className="text-xl font-semibold text-white mb-1">{event.name}</h2>

                      <div className="text-sm text-gray-400">
                        <div>
                          <span className="font-semibold text-gray-300">Organización:</span>{" "}
                          {event.idOrganization}
                        </div>
                        <div className="mt-2 text-gray-300">
                          {event.description}
                        </div>
                      </div>

                      <div className="text-gray-400 text-sm mt-2">
                        <span className="font-semibold text-gray-300">Fecha:</span>{" "}
                        {event.date}
                      </div>
                    </div>

                    {/* Este spacer empuja el botón al fondo, alineando todos los botones */}
                    <div className="mt-auto flex justify-center">
                      <button
                        className="bg-blue-700 hover:bg-blue-800 text-white font-medium px-5 py-2 rounded-lg transition-all"
                        onClick={() => handleAdhesion(event)}
                      >
                        Adherirse
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Sin eventos */}
            {events.length === 0 && !error && (
              <p className="text-gray-400 text-center mt-6">
                No se encontraron eventos externos disponibles.
              </p>
            )}
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
          <button onClick={() => setPestana("adhesion")} className="bg-blue-700 hover:bg-blue-800 px-4 py-2 rounded">Eventos</button>
        </div>

        {/* Contenido de cada pestaña */}
        <div className="flex flex-col gap-4">
          {renderPestana()}
        </div>
      </div>
    </div>
  );

}
