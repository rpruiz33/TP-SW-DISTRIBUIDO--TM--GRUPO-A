import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

const DonationForm = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const donationToEdit = location.state?.donation || null;

  const [formData, setFormData] = useState({
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
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

     // Convertimos amount a número
    const payload = {
      ...formData,
      id: parseInt(formData.id,10),
      amount: parseInt(formData.amount, 10),
    };

    try {
      let response;
      if (donationToEdit) {
        // 👉 UPDATE
        response = await axios.put(
          "http://localhost:5000/api/updatedonation",
          payload
        );
      } else {
        // 👉 CREATE
        response = await axios.post(
          "http://localhost:5000/api/altadonation",
          payload
        );
      }

      if (response.data.success) {
        alert(donationToEdit ? "✅ Donación actualizada!" : "✅ Donación creada!");
        navigate("/donationlist");
      } else {
        alert("❌ " + response.data.message);
      }
    } catch (error) {
      console.error(error);
      alert("❌ Error en la operación");
    }
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100">
      <form
        onSubmit={handleSubmit}
        className="bg-white p-6 rounded shadow-md w-full max-w-md"
      >
        <h2 className="text-xl font-bold mb-4">
          {donationToEdit ? "Modificar Donación" : "Alta de Donación"}
        </h2>

         {/* Category: editable solo si es creación */}
        <label>Categoria</label>
        {donationToEdit ? (
          <input
            type="text"
            name="category"
            value={formData.category}
            readOnly
            className="w-full p-2 border mb-2 bg-gray-200"
          />
        ) : (
          <select
            name="category"
            value={formData.category}
            onChange={handleChange}
            className="w-full p-2 border mb-2"
          >
            <option value="">Seleccione una categoría</option>
            <option value="ALIMENTOS">ALIMENTOS</option>
            <option value="ROPA">ROPA</option>
            <option value="MEDICAMENTOS">MEDICAMENTOS</option>
          </select>
        )}


        {/* Description: editable */}
        <label>Descripcion</label>
        <input
          type="text"
          name="description"
          value={formData.description}
          onChange={handleChange}
          className="w-full p-2 border mb-2"
        />

        {/* Amount: editable */}
        <label>Cantidad</label>
        <input
          type="number"
          name="amount"
          value={formData.amount}
          onChange={handleChange}
          className="w-full p-2 border mb-4"
        />

        <button
          type="submit"
          className="w-full bg-blue-600 text-black p-2 rounded"
        >
          {donationToEdit ? "Guardar Cambios" : "Dar de Alta"}
        </button>
      </form>
    </div>
  );
};

export default DonationForm;
