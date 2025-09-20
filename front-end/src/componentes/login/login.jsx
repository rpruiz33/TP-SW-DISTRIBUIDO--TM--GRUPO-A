import { useNavigate } from 'react-router-dom';
import React, { useState } from "react";
import axios from "axios";

const Login = () => {
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");

        try {
            const response = await axios.post("http://localhost:5000/api/login", {
                username: email,
                password: password
            });

            console.log(response);
            
            if (response.data.success) {
                const roleName = response.data.role_name;
                const normalizedRole = roleName.toUpperCase(); // 🔹 Siempre en mayúsculas
                localStorage.setItem("userRole", normalizedRole);
                localStorage.setItem("usernameOrEmail", email);


                alert("Bienvenido " + roleName);
                navigate('/dashboard');

            } else {
                setError(response.data.message);
            }
        } catch (err) {
            console.error(err);
            setError("Error de conexión con el servidor");
        }
    };

    return (
        <div className="min-h-screen bg-black flex items-center justify-center p-5">
            <div className="w-full max-w-md bg-gray-900 p-8 rounded-lg shadow-md">
                <h2 className="text-2xl font-bold text-white mb-6 text-center">Iniciar Sesión</h2>

                <form onSubmit={handleLogin} className="space-y-4">
                    <div>
                        <label htmlFor="email" className="block text-white font-medium mb-1">
                            Email:
                        </label>
                        <input
                            type="text"
                            id="email"
                            placeholder="xxxxx@xxxxx"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            className="w-full px-3 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-gray-800 text-white placeholder-gray-400"
                        />
                    </div>

                    <div>
                        <label htmlFor="password" className="block text-white font-medium mb-1">
                            Contraseña:
                        </label>
                        <input
                            type="password"
                            id="password"
                            placeholder="xxxxxxxxxx"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            className="w-full px-3 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-gray-800 text-white placeholder-gray-400"
                        />
                    </div>

                    {error && <div className="text-red-500 text-sm">{error}</div>}

                    <button
                        type="submit"
                        className="w-full bg-blue-700 hover:bg-blue-800 text-white font-medium py-2 px-4 rounded-md transition-colors"
                    >
                        Iniciar Sesión
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;
