import React from "react";
import { useNavigate } from 'react-router-dom';
const Login = () => {
    const navigate = useNavigate();
  
    const handleLogin = (e) => {
      e.preventDefault();
      if (e.target.email.value ==="UNLa@gmail.com" && e.target.password.value === "1234") {
        alert("Bienvenido al sistema empuje solidario");
      //navigate('/recarga');
      navigate('/home2');
      }
    }
    return (
        <div className=" h-100 bg-black mb-90 p-5 pb-1000 "> 
            <div className="row  h-100">
                <div className="col-md-6 offset-md-3">
                    <h2 className="text-light">Iniciar Sesión</h2>
                    <form onSubmit={handleLogin}>
                        <div className="mb-3">
                            <label htmlFor="email" className="form-label">Email:</label>
                            <input type="email" className="form-control" id="email" placeholder="xxxxx@xxxxx">
                            </input>

                        </div>  
                        <div className="mb-3">
                            <label htmlFor="password" className="form-label">Contraseña:</label>
                            <input type="password" className="form-control" id="password" placeholder="xxxxxxxxxx">
                            </input>
                        </div>
                        <button type="submit"  className="btn btn-primary">Iniciar Sesión</button>
                    </form>
                    
                 
                </div>
            </div>
        </div>
    );

}
export default Login;