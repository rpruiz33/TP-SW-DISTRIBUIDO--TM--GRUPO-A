import React, { useState } from "react";

export default function AltaUsuario() {
  const [formData, setFormData] = useState({
    username: "",
    nombre: "",
    apellido: "",
    telefono: "",
    email: "",
    rol: "",
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const response = await fetch("http://localhost:8080/api/usuarios", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(formData),
    });
    if (response.ok) {
      alert("✅ Usuario dado de alta con éxito!");
      setFormData({
        username: "",
        nombre: "",
        apellido: "",
        telefono: "",
        email: "",
        rol: "",
      });
    } else {
      alert("❌ Error al dar de alta el usuario");
    }
  };

  return (
  <div
    className="flex justify-center items-center min-h-screen bg-gradient-to-r from-indigo-200 via-purple-200 to-pink-200 p-4"
    style={{
      display: "flex",
      flexDirection: "column",
      flexWrap: "wrap",
      alignContent: "space-around",
      alignItems: "center", // para centrar horizontalmente
    }}
  >
      <form
        onSubmit={handleSubmit}
        className="bg-white shadow-2xl rounded-2xl p-8 w-full max-w-md space-y-5"

        style={{
    display: "flex",
    flexDirection: "column",
         }} >
        <h2 className="text-3xl font-bold text-center text-indigo-700 mb-6">
          Alta de Usuario
        </h2>

        {/* Nombre */}
        <div>
          <label className="block text-sm font-medium text-indigo-600 mb-1">
            Nombre
          </label>
          <input
            type="text"
            name="nombre"
            value={formData.nombre}
            onChange={handleChange}
            required
            className="w-full p-3 border border-indigo-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        {/* Nombre de usuario */}
        <div>
          <label className="block text-sm font-medium text-indigo-600 mb-1">
            Nombre de Usuario
          </label>
          <input
            type="text"
            name="username"
            value={formData.username}
            onChange={handleChange}
            required
            className="w-full p-3 border border-indigo-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        {/* Apellido */}
        <div>
          <label className="block text-sm font-medium text-indigo-600 mb-1">
            Apellido
          </label>
          <input
            type="text"
            name="apellido"
            value={formData.apellido}
            onChange={handleChange}
            required
            className="w-full p-3 border border-indigo-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        {/* Teléfono */}
        <div>
          <label className="block text-sm font-medium text-indigo-600 mb-1">
            Teléfono
          </label>
          <input
            type="text"
            name="telefono"
            value={formData.telefono}
            onChange={handleChange}
            className="w-full p-3 border border-indigo-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        {/* Email */}
        <div>
          <label className="block text-sm font-medium text-indigo-600 mb-1">
            Email
          </label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            className="w-full p-3 border border-indigo-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        {/* Rol */}
        <div>
          <label className="block text-sm font-medium text-indigo-600 mb-1">
            Rol
          </label>
          <select
            name="rol"
            value={formData.rol}
            onChange={handleChange}
            required
            className="w-full p-3 border border-indigo-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
          >
            <option value="">Seleccione un rol</option>
            <option value="PRESIDENTE">VOCAL</option>
            <option value="TESORERO">COORDINADOR</option>
            <option value="SECRETARIO">VOLUNTARIO</option>
          </select>
        </div>

        {/* Botón */}
        <button
          type="submit"
          className="w-full bg-indigo-600 text-blasck py-3 rounded-lg hover:bg-indigo-700 font-semibold transition-all duration-300"
        >
          Dar de Alta
        </button>
      </form>
    </div>
  );
}
