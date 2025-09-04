import React, { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import axios from "axios";


const UserList = () => {



    const navigate = useNavigate();
    const [users, setUsers] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        getUsers(); // Llama a la función para traer usuarios cuando se monta
    }, []);

    const getUsers = async () => {
        setError("");  // Limpiar errores previos

        try {
            const response = await axios.get("http://localhost:5000/api/userlist");
            setUsers(response.data.users || []);
            console(users) // Guardar la lista de usuarios en el estado

        } catch (err) {
            console.error(err);
            setError("Error de conexión con el servidor");
        }
    };

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Lista de Usuarios</h1>

            {/* Botón para nuevo usuario */}
            <button className="mb-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
                Nuevo Usuario
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
                            <tr key={index} className="text-center">
                                <td className="px-4 py-2 border">{user.username}</td>
                                <td className="px-4 py-2 border">{user.name}</td>
                                <td className="px-4 py-2 border">{user.lastName}</td>
                                <td className="px-4 py-2 border">{user.phone}</td>
                                <td className="px-4 py-2 border">{user.email}</td>
                                <td className="px-4 py-2 border">{user.role}</td>
                                <td className="px-4 py-2 border">
                                    {user.activated ? "Sí" : "No"}
                                </td>
                                <td className="px-4 py-2 border space-x-2">
                                    <button className="px-2 py-1 bg-yellow-500 text-white rounded hover:bg-yellow-600">
                                        Modificar
                                    </button>
                                    <button className="px-2 py-1 bg-red-500 text-white rounded hover:bg-red-600">
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
