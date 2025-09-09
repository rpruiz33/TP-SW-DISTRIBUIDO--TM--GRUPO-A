import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const UserList = () => {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    getUsers();
  }, []);

  const getUsers = async () => {
    try {
      const response = await axios.get("http://localhost:5000/api/userlist");
      console.log("Respuesta completa:", response.data);
      if (Array.isArray(response.data)) {
        setUsers(response.data);
      } else if (response.data.users && Array.isArray(response.data.users)) {
        setUsers(response.data.users);
      } else {
        setUsers([]);
        console.warn("La respuesta no contiene un array de usuarios:", response.data);
      }
    } catch (err) {
      console.error("Error en la solicitud:", err);
      setError("Error de conexión con el servidor");
    }
  };

  const deleteUser = async (username) => {
    if (!window.confirm("¿Seguro que deseas eliminar este usuario?")) return;

    try {
      const response = await axios.delete(`http://localhost:5000/api/deleteuser/${username}`);
      console.log("Respuesta del servidor:", response.data);
      alert(response.data.message || "✅ Usuario eliminado");
      getUsers(); // Recarga la lista
    } catch (err) {
      console.error("Error al eliminar usuario:", err.response ? err.response.data : err.message);
      alert(err.response?.data?.message || "❌ Error al eliminar usuario");
    }
  };

  const editUser = (user) => {
    navigate("/altauser", { state: { user } });
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Lista de Usuarios</h1>
      {error && <div className="text-red-500 mb-4">{error}</div>}
      <button
        onClick={() => navigate("/altausuario")}
        className="mb-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
      >
        Crear Nuevo Usuario
      </button>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-200">
          <thead>
            <tr className="bg-gray-100">
              <th className="px-4 py-2 border">Username</th>
              <th className="px-4 py-2 border">Nombre</th>
              <th className="px-4 py-2 border">Apellido</th>
              <th className="px-4 py-2 border">Teléfono</th>
              <th className="px-4 py-2 border">Email</th>
              <th className="px-4 py-2 border">Rol</th>
              <th className="px-4 py-2 border">Activo</th>
              <th className="px-4 py-2 border">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user, index) => (
              <tr key={user.username || index} className="text-center">
                <td className="px-4 py-2 border">{user.username || "N/A"}</td>
                <td className="px-4 py-2 border">{user.name || "N/A"}</td>
                <td className="px-4 py-2 border">{user.lastName || "N/A"}</td>
                <td className="px-4 py-2 border">{user.phone || "N/A"}</td>
                <td className="px-4 py-2 border">{user.email || "N/A"}</td>
                <td className="px-4 py-2 border">{user.role || "N/A"}</td>
                <td className="px-4 py-2 border">
                  {user.activated != null ? (user.activated ? "Sí" : "No") : "N/A"}
                </td>
                <td className="px-4 py-2 border space-x-2">
                  <button
                    onClick={() => editUser(user)}
                    className="px-2 py-1 bg-yellow-500 text-black rounded hover:bg-yellow-600"
                  >
                    Modificar
                  </button>
                  <button
                    onClick={() => deleteUser(user.username)}
                    className="px-2 py-1 bg-red-500 text-black rounded hover:bg-red-600"
                  >
                    Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default UserList;