import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

export default function EventForm() {
  const location = useLocation();
  const navigate = useNavigate();
  const event = location.state?.event || null;

  const [formData, setFormData] = useState({
    id: "",
    nameEvent: "",
    descriptionEvent: "",
    dateRegistration: "",
  });

  // Si venimos a editar, cargamos los valores existentes
  useEffect(() => {
    if (event) {
      setFormData({
        id: event.id || "",
        nameEvent: event.nameEvent || "",
        descriptionEvent: event.descriptionEvent || "",
        dateRegistration: event.dateRegistration || "",
      });
    }
  }, [event]);



  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  //Reutilizamos segun se quiera editar o crear 
  const handleSubmit = async (e) => {

    e.preventDefault();

    try {
      let response;
      if (event) { //Si existe el evento, editamos

        response = await axios.put(`http://localhost:5000/api/updateevent`, formData);


      } else { //Sino creamos

        response = await axios.post(`http://localhost:5000/api/createevent`, formData);

      }

      if (response.data.success) {
        alert("Evento guardado correctamente");
        navigate("/eventlist"); // Volver a la lista
      } else {
        alert(response.data.message);
      }

    } catch (err) {
      console.error("Error al guardar evento:", err);
      alert("No se pudo guardar el evento");
    }
  };


  return (
    <div className="flex justify-center items-center min-h-screen bg-[#01000F] p-6">
      <form
        onSubmit={handleSubmit}
        className="bg-white p-6 rounded shadow-md w-full max-w-lg"
      >
        <h1 className="text-4xl font-bold text-center mb-6">
          Modificar Evento
        </h1>

        {/* Nombre del Evento */}
        <div className="mb-4">
          <label className="font-bold block mb-2">Nombre del Evento</label>
          <input
            type="text"
            name="nameEvent"
            value={formData.nameEvent}
            onChange={handleChange}
            className="w-full p-2 border rounded"
            required
          />
        </div>

        {/* Descripción */}
        <div className="mb-4">
          <label className="font-bold block mb-2">Descripción</label>
          <textarea
            name="descriptionEvent"
            value={formData.descriptionEvent}
            onChange={handleChange}
            className="w-full p-2 border rounded"
            required
          />
        </div>

        {/* Fecha y Hora */}
        <div className="mb-4">
          <label className="font-bold block mb-2">Fecha y Hora del Evento</label>
          <input
            type="datetime-local"
            name="dateRegistration"
            value={formData.dateRegistration}
            onChange={handleChange}
            className="w-full p-2 border rounded"
            required
          />
        </div>

        {/* Botones */}
        <div className="flex justify-between">
          <button
            type="submit"
            className="w-full bg-blue-600 text-white p-2 rounded mb-2"
          >
            {event ? "Guardar Cambios" : "Dar de Alta"}
          </button>

        </div>

        <div className="flex justify-between">

          <button
            type="button"
            onClick={() => navigate("/eventlist")}
            className="w-full bg-gray-400 text-white p-2 rounded mb-2"
          >
            Volver al listado
          </button>

        </div>

      </form>
    </div>
  );
}

