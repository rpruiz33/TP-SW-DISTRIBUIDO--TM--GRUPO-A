import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

export default function EventForm() {
  const location = useLocation();
  const navigate = useNavigate();
  const event = location.state?.event || null ;

  const [formData, setFormData] = useState({
    id: "",
    nameEvent: "",
    descriptionEvent:  "",
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
      if (event){ //Si existe el evento, editamos

        response= await axios.put(`http://localhost:5000/api/updateevent`, formData);


      } else{ //Sino creamos

        response=await axios.post(`http://localhost:5000/api/createevent`,formData);

      }

      if (response.data.success){
        alert("Evento guardado correctamente");
        navigate("/eventlist"); // Volver a la lista
      }else{
        alert(response.data.message);
      }

    } catch (err) {
      console.error("Error al guardar evento:", err);
      alert("No se pudo guardar el evento");
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl text-white font-bold mb-4">Modificar Evento</h1>
      <form
        onSubmit={handleSubmit}
        className="max-w-lg bg-white shadow-md rounded px-8 pt-6 pb-8 mb-4"
      >
        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Nombre del Evento
          </label>
          <input
            type="text"
            name="nameEvent"
            value={formData.nameEvent}
            onChange={handleChange}
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700"
            required
          />
        </div>

        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Descripción
          </label>
          <textarea
            name="descriptionEvent"
            value={formData.descriptionEvent}
            onChange={handleChange}
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700"
            required
          />
        </div>

        <div className="mb-4">
          <label className="block text-gray-700 text-sm font-bold mb-2">
            Fecha y Hora del Evento
          </label>
          <input
            type="datetime-local"
            name="dateRegistration"
            value={formData.dateRegistration}
            onChange={handleChange}
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700"
            required
          />
        </div>

        <div className="flex items-center justify-between">
          <button
            type="submit"
            className="px-4 py-2 bg-blue-500 text-black rounded hover:bg-blue-600"
          >
            Guardar cambios
          </button>
          <button
            type="button"
            onClick={() => navigate("/eventlist")}
            className="px-4 py-2 bg-gray-400 text-black rounded hover:bg-gray-500"
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
}

