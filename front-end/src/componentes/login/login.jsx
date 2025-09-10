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

            if (response.data.success) {
                const roleName = response.data.role_name; 
                const normalizedRole = roleName.toUpperCase(); // 🔹 Siempre en mayúsculas
                localStorage.setItem("userRole", normalizedRole);

                if (normalizedRole === "PRESIDENTE") {
                    alert("Bienvenido Presidente");
                    navigate('/dashboard');
                } else {
                    alert("Bienvenido " + roleName);
                    navigate('/dashboard');
                }
            } else {
                setError("Credenciales inválidas");
            }
        } catch (err) {
            console.error(err);
            setError("Error de conexión con el servidor");
        }
    };

    return (
        <div className="h-100 bg-black mb-90 p-5 pb-1000">
            <div className="row h-100">
                <div className="col-md-6 offset-md-3">
                    <h2 className="text-light">Iniciar Sesión</h2>
                    <form onSubmit={handleLogin}>
                        <div className="mb-3">
                            <label htmlFor="email" className="form-label">Email:</label>
                            <input
                                type="email"
                                className="form-control"
                                id="email"
                                placeholder="xxxxx@xxxxx"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="password" className="form-label">Contraseña:</label>
                            <input
                                type="password"
                                className="form-control"
                                id="password"
                                placeholder="xxxxxxxxxx"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>
                        {error && <div className="text-danger mb-3">{error}</div>}
                        <button type="submit" className="btn btn-primary">Iniciar Sesión</button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Login;
