import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

export default function AltaUsuario() {
  const location = useLocation();
  const navigate = useNavigate();
  const userToEdit = location.state?.user || null;

  const [formData, setFormData] = useState({
    username: "",
    nombre: "",
    apellido: "",
    telefono: "",
    email: "",
    rol: "",
  });

  // 👇 Si venimos con un usuario, llenar el form
  useEffect(() => {
    if (userToEdit) {
      setFormData({
        username: userToEdit.username,
        nombre: userToEdit.name,
        apellido: userToEdit.lastName,
        telefono: userToEdit.phone,
        email: userToEdit.email,
        rol: userToEdit.role,
      });
    }
  }, [userToEdit]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      let response;
      if (userToEdit) {
        // 👉 UPDATE
        response = await axios.put("http://localhost:5000/api/updateuser", formData);
      } else {
        // 👉 ALTA
        response = await axios.post("http://localhost:5000/api/altauser", formData);
      }
      if (response.data.success) {
        alert(userToEdit ? "✅ Usuario actualizado!" : "✅ Usuario creado!");
        navigate("/userlist");
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
      <form onSubmit={handleSubmit} className="bg-white p-6 rounded shadow-md w-full max-w-md">
        <h2 className="text-xl font-bold mb-4">
          {userToEdit ? "Modificar Usuario" : "Alta de Usuario"}
        </h2>

        <label>Nombre</label>
        <input
          type="text"
          name="nombre"
          value={formData.nombre}
          onChange={handleChange}
          className="w-full p-2 border mb-2"
        />

        <label>Username</label>
        <input
          type="text"
          name="username"
          value={formData.username}
          onChange={handleChange}
          disabled={!!userToEdit} // 👈 bloqueado si es update
          className="w-full p-2 border mb-2"
        />

        <label>Apellido</label>
        <input
          type="text"
          name="apellido"
          value={formData.apellido}
          onChange={handleChange}
          className="w-full p-2 border mb-2"
        />

        <label>Teléfono</label>
        <input
          type="text"
          name="telefono"
          value={formData.telefono}
          onChange={handleChange}
          className="w-full p-2 border mb-2"
        />

        <label>Email</label>
        <input
          type="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          className="w-full p-2 border mb-2"
        />

        <label>Rol</label>
        <select
          name="rol"
          value={formData.rol}
          onChange={handleChange}
          className="w-full p-2 border mb-4"
        >
          <option value="">Seleccione un rol</option>
          <option value="Vocal">VOCAL</option>
          <option value="Coordinador">COORDINADOR</option>
          <option value="Voluntario">VOLUNTARIO</option>
          <option value="Presidente">PRESIDENTE</option>
        </select>

        <button
          type="submit"
          className="w-full bg-blue-600 text-white p-2 rounded"
        >
          {userToEdit ? "Guardar Cambios" : "Dar de Alta"}
        </button>
      </form>
    </div>
  );
}