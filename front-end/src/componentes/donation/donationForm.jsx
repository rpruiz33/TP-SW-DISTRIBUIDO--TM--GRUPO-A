import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

const DonationForm = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const donationToEdit = location.state?.donation || null;
  const [formData, setFormData] = useState({
    id: "",
    category: "",
    description: "",
    amount: "",
  });

  // Si venimos a editar, cargamos los valores existentes
  useEffect(() => {
    if (donationToEdit) {
      setFormData({
        id: donationToEdit.id || "",
        category: donationToEdit.category || "",
        description: donationToEdit.description || "",
        amount: donationToEdit.amount || "",
      });
    }
  }, [donationToEdit]);

  const handleChange = (e) => {
    
    let value = e.target.value;
    if (e.target.name === "amount") {
       value = parseInt(e.target.value, 10);
      if (value<1){
        value=1;
      }
    }


    
    setFormData({ ...formData, [e.target.name]: value });
  };

  // 👉 CREAR / EDITAR
  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
      username: localStorage.getItem("usernameOrEmail"),
      ...formData,
      id: parseInt(formData.id, 10),
      amount: parseInt(formData.amount, 10),
    };

    try {
      let response;
      if (donationToEdit) {
        // UPDATE
        response = await axios.put(
          "http://localhost:5000/api/updatedonation",
          payload
        );
      } else {
        // CREATE
        response = await axios.post(
          "http://localhost:5000/api/altadonation",
          payload
        );
      }

      if (response.data.success) {
        alert("Donación guardada con éxito");
        navigate("/donationlist");
      }
    } catch (error) {
      console.error("Error al guardar la donación:", error);
      alert("Hubo un error al guardar la donación. Por favor, intente nuevamente.");
    }
  };



  return (
    <div className="flex justify-center items-center min-h-screen bg-[#01000F]">
      <form
        onSubmit={handleSubmit}
        className="bg-white p-6 rounded shadow-md w-full max-w-md"
      >
        <h2 className="text-4xl text-center font-bold mb-4 ">
          {donationToEdit ? "Modificar Donación" : "Alta de Donación"}
        </h2>

        {/* Category */}
        <label className="font-bold">Categoria</label>
        {donationToEdit ? (
          <input
            type="text"
            name="category"
            value={formData.category}
            readOnly
            className="w-full p-2 border mb-2 bg-gray-200"
            required
          />
        ) : (
          <select
            name="category"
            value={formData.category}
            onChange={handleChange}
            className="w-full p-2 border mb-2"
          >
            <option value="">Seleccione una categoría</option>
            <option value="ALIMENTO">ALIMENTO</option>
            <option value="ROPA">ROPA</option>
            <option value="JUGUETE">JUGUETE</option>
            <option value="UTIL_ESCOLAR">UTIL ESCOLAR</option>

          </select>
        )}

        {/* Description */}
        <label className="font-bold" >Descripcion</label>
        <input
          type="text"
          name="description"
          value={formData.description}
          onChange={handleChange}
          className="w-full p-2 border mb-2"
          required
        />

        {/* Amount */}
        <label className="font-bold" >Cantidad</label>
        <input
          type="number"
          name="amount"
          value={formData.amount}
          onChange={handleChange}
          className="w-full p-2 border mb-4"
          required
          min={1}
        />

        {/* Botón Guardar */}
        <button
          type="submit"
          className="w-full bg-blue-600 text-white p-2 rounded mb-2"
        >
          {donationToEdit ? "Guardar Cambios" : "Dar de Alta"}
        </button>


        {/* Botón Volver */}
        <button
          type="button"
          onClick={() => navigate("/donationlist")}
          className="w-full bg-gray-400 text-black p-2 rounded"
        >
          Volver al Listado
        </button>
      </form>
    </div>
  );
};

export default DonationForm;
