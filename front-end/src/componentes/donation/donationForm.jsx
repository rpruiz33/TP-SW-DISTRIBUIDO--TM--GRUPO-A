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

  // 👉 CREAR / EDITAR
  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
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

  // 👉 ELIMINAR
  const handleDelete = async () => {
    if (!formData.id) {
      alert("No se puede eliminar, falta el ID de la donación");
      return;
    }

    if (!window.confirm("¿Seguro que desea eliminar esta donación?")) return;

    try {
      const response = await axios.delete(
        `http://localhost:5000/api/deletedonation/${formData.id}`
      );

      if (response.data.success) {
        alert("Donación eliminada con éxito");
        navigate("/donationlist");
      } else {
        alert("Error al eliminar la donación");
      }
    } catch (error) {
      console.error("Error al eliminar la donación:", error);
      alert("Hubo un error al eliminar. Intente nuevamente.");
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

        {/* Category */}
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

        {/* Description */}
        <label>Descripcion</label>
        <input
          type="text"
          name="description"
          value={formData.description}
          onChange={handleChange}
          className="w-full p-2 border mb-2"
        />

        {/* Amount */}
        <label>Cantidad</label>
        <input
          type="number"
          name="amount"
          value={formData.amount}
          onChange={handleChange}
          className="w-full p-2 border mb-4"
        />

        {/* Botón Guardar */}
        <button
          type="submit"
          className="w-full bg-blue-600 text-white p-2 rounded mb-2"
        >
          {donationToEdit ? "Guardar Cambios" : "Dar de Alta"}
        </button>

        {/* Botón Eliminar (solo si estoy editando) */}
        {donationToEdit && (
          <button
            type="button"
            onClick={handleDelete}
            className="w-full bg-red-600 text-white p-2 rounded mb-2"
          >
            Eliminar Donación
          </button>
        )}

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
