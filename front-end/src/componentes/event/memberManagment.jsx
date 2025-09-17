import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";

const MemberManagment = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [users, setUsers] = useState([]);
  const [error, setError] = useState("");

  // si no hay evento, inicializamos con users vacío para evitar errores
  const mainEvent = location.state?.event || { users: [] };

  // NUEVO: estado local para los miembros asignados
  const [members, setMembers] = useState(mainEvent.users || []);

  useEffect(() => {
    getUsers();
  }, []);

  const getUsers = async () => {
    try {
      const response = await axios.get("http://localhost:5000/api/activeuserlist");
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

  const toggleMember = async (user) => {
    // NUEVO: calcular si el usuario ya está asignado usando members local
    const exist = members.some(u => u.username === user.username);

    const protoPayload = {
      eventId: mainEvent.id,
      username: user.username,
      alreadyAssigned: exist
    };

    try {
      console.log("llamando al endpoint con los datos: ", protoPayload);
      const response = await axios.put("http://localhost:5000/api/togglemember", protoPayload);
      console.log(response);

      if (response.data.success) {
        alert(response.data.message);

        // NUEVO: actualizar members localmente en lugar de mainEvent.users
        if (exist) {
          // Usuario estaba asignado → eliminar
          setMembers(members.filter(u => u.username !== user.username));
        } else {
          // Usuario no estaba asignado → agregar
          setMembers([...members, user]);
        }

        // COMENTADO: ya no necesitamos modificar mainEvent.users ni forzar re-render con setUsers
        // mainEvent.users = exist ? (mainEvent.users || []).filter(u => u.username !== user.username) : [...(mainEvent.users || []), user];
        // setUsers([...users]);

      } else {
        alert("❌ " + response.data.message);
      }
    } catch (error) {
      console.error(error);
      alert("❌ Error en la operación");
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl text-white font-bold mb-4">
        Gestion de Miembros del Evento {mainEvent.nameEvent}
      </h1>
      {error && <div className="text-red-500 mb-4">{error}</div>}

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
              <th className="px-4 py-2 border">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user, index) => {
              // NUEVO: calcular isAssigned usando members local
              const isAssigned = members.some(u => u.username === user.username);

              return (
                <tr key={user.username || index} className="text-center">
                  <td className="px-4 py-2 border">{user.username || "N/A"}</td>
                  <td className="px-4 py-2 border">{user.name || "N/A"}</td>
                  <td className="px-4 py-2 border">{user.lastName || "N/A"}</td>
                  <td className="px-4 py-2 border">{user.phone || "N/A"}</td>
                  <td className="px-4 py-2 border">{user.email || "N/A"}</td>
                  <td className="px-4 py-2 border">{user.role || "N/A"}</td>

                  <td className="px-4 py-2 border space-x-2">
                    <button
                      onClick={() => toggleMember(user)}
                      className="px-2 py-1 bg-yellow-500 text-black rounded hover:bg-yellow-600"
                    >
                      {isAssigned ? "Desafectar" : "Asignar"}
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default MemberManagment;
