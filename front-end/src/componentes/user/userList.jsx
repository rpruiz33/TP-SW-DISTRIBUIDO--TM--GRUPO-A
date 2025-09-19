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
        setError(response.data.message)
        console.warn("La respuesta no contiene un array de usuarios:", response.data);
      }

    } catch (err) {
      console.error("Error en la solicitud:", err);
      setError("Error de conexión con el servidor");
    }
  };

  const deleteUser = async (user) => {

    if (user.activated) {
      if (!window.confirm("¿Seguro que deseas eliminar este usuario?")) return;
    } else {
      if (!window.confirm("¿Seguro que deseas activar este usuario?")) return;
    }

    try {
      const response = await axios.put(`http://localhost:5000/api/deleteuser/${user.username}`);
      console.log("Respuesta del servidor:", response.data.message);

      alert(response.data.message);

      if (response.data.success) {
        //Si se hicieron cambios, recargar
        await getUsers();
      }


    } catch (err) {
      console.error("Error al eliminar usuario:", err.response ? err.response.data : err.message);
      alert(err.response?.data?.message || "❌ Error al eliminar usuario");
    }
  };

  const editUser = (user) => {
    navigate("/userform", { state: { user } });
  };

  return (
    <div className="p-6 bg-[#01000F] min-h-screen flex flex-col">

      <div className="flex justify-between items-center mb-4">
        <h1 className="text-5xl font-bold text-white mb-4">Lista de Usuarios</h1>

        <button
          onClick={() => navigate("/userform")}
          className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          Crear Nuevo Usuario
        </button>

      </div>


      {error && <div className="text-red-500 mb-4">{error}</div>}

      <div className="overflow-x-auto flex-grow">
        <table className="min-w-full border border-gray-700">
          <thead>
            <tr className="bg-gray-800 text-white">
              <th className="px-4 py-2 border border-gray-700 text-center">Username</th>
              <th className="px-4 py-2 border border-gray-700 text-center">Nombre</th>
              <th className="px-4 py-2 border border-gray-700 text-center">Apellido</th>
              <th className="px-4 py-2 border border-gray-700 text-center">Teléfono</th>
              <th className="px-4 py-2 border border-gray-700 text-center">Email</th>
              <th className="px-4 py-2 border border-gray-700 text-center">Rol</th>
              <th className="px-4 py-2 border border-gray-700 text-center">Activo</th>
              <th className="px-4 py-2 border border-gray-700 text-center">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user, index) => (
              <tr key={user.username || index} className="text-center text-gray-200">
                <td className="px-4 py-2 border border-gray-700">{user.username || "N/A"}</td>
                <td className="px-4 py-2 border border-gray-700">{user.name || "N/A"}</td>
                <td className="px-4 py-2 border border-gray-700">{user.lastName || "N/A"}</td>
                <td className="px-4 py-2 border border-gray-700">{user.phone || "N/A"}</td>
                <td className="px-4 py-2 border border-gray-700">{user.email || "N/A"}</td>
                <td className="px-4 py-2 border border-gray-700">{user.role || "N/A"}</td>
                <td className={`px-4 py-2 border border-gray-700 ${user.activated ? "text-green-400" : "text-red-500"}`}>
                  {user.activated != null ? (user.activated ? "Sí" : "No") : "N/A"}
                </td>
                <td className="px-4 py-2 border border-gray-700 space-x-2">
                  <button
                    onClick={() => editUser(user)}
                    className="px-2 py-1 bg-yellow-500 text-black rounded hover:bg-yellow-600"
                  >
                    Modificar
                  </button>
                  <button
                    onClick={() => deleteUser(user)}
                    className={`px-2 py-1 rounded text-black hover:opacity-90 ${user.activated != null
                      ? user.activated
                        ? "bg-red-500 hover:bg-red-600" // Si dice "Activar"
                        : "bg-green-500 hover:bg-green-600"     // Si dice "Eliminar"
                      : "bg-gray-400 cursor-not-allowed"   // Caso "N/A"
                      }`}
                  >
                    {user.activated != null ? (user.activated ? "Eliminar" : "Activar") : "N/A"}
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