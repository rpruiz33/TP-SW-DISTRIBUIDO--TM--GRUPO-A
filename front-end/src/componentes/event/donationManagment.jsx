import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";

const DonationManagment = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [donations, setDonations] = useState([]);
    const [error, setError] = useState("");

    // si no hay evento, inicializamos con donations vacío para evitar errores
    const mainEvent = location.state?.event || { donations: [] };

    // estado local para donaciones asignadas a este evento
    const [assignedDonations, setAssignedDonations] = useState(mainEvent.donations || []);

    //  estado local para manejar cantidades ingresadas en cada fila
    const [quantities, setQuantities] = useState({});

    useEffect(() => {
        getDonations();
    }, []);

    const getDonations = async () => {
        try {
            const response = await axios.get("http://localhost:5000/api/activedonationlist");
            console.log("Respuesta completa:", response.data);
            if (Array.isArray(response.data)) {
                setDonations(response.data);
            } else if (response.data.donations && Array.isArray(response.data.donations)) {
                setDonations(response.data.donations);
            } else {
                setDonations([]);
                console.warn("La respuesta no contiene un array de donaciones:", response.data);
            }
        } catch (err) {
            console.error("Error en la solicitud:", err);
            setError("Error de conexión con el servidor");
        }
    };


    const assignDonation = async (donation) => {
        const quantity = quantities[donation.id] || 0;

        const protoPayload = {
            idEvent: mainEvent.id,
            description: donation.description,
            quantityDelivered: quantity,
            username: localStorage.getItem("usernameOrEmail")
        }

        try{
          const response = await axios.post("http://localhost:5000/api/assignDonation", protoPayload);
          
          if (response.data.success){
            alert("Se asignaron "+quantity+" " + donation.description +" al evento")
          }else{
            alert(response.data.message);
          }

        }catch{
            console.error(error);
            alert("❌ Error en la operación");
        }

    };

    return (
        <div className="p-6">
            <h1 className="text-2xl text-white font-bold mb-4">
                Gestión de Donaciones del Evento {mainEvent.nameEvent}
            </h1>
            {error && <div className="text-red-500 mb-4">{error}</div>}

            <div className="overflow-x-auto">
                <table className="min-w-full bg-white border border-gray-200">
                    <thead>
                        <tr className="bg-gray-100">
                            <th className="px-4 py-2 border">Categoría</th>
                            <th className="px-4 py-2 border">Descripción</th>
                            <th className="px-4 py-2 border">Cantidad disponible</th>
                            <th className="px-4 py-2 border">Cantidad a asignar</th>
                            <th className="px-4 py-2 border">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {donations.map((donation, index) => {
                            return (
                                <tr key={donation.id || index} className="text-center">
                                    <td className="px-4 py-2 border">{donation.category || "N/A"}</td>
                                    <td className="px-4 py-2 border">{donation.description || "N/A"}</td>
                                    <td className="px-4 py-2 border">{donation.amount || 0}</td>

                                    <td className="px-4 py-2 border">
                                        <input
                                            type="number"
                                            value={quantities[donation.id] || ""}
                                            onChange={(e) => {
                                                let value = parseInt(e.target.value, 10);

                                                // Si no es número válido → lo dejamos vacío
                                                if (isNaN(value)) {
                                                    setQuantities({
                                                        ...quantities,
                                                        [donation.id]: ""
                                                    });
                                                    return;
                                                }

                                                // Forzar mínimo 1
                                                if (value < 1) {
                                                    value = 1;
                                                }

                                                // Forzar máximo (amount - 1)
                                                if (value >= donation.amount) {
                                                    value = donation.amount - 1;
                                                }

                                                setQuantities({
                                                    ...quantities,
                                                    [donation.id]: value
                                                });
                                            }}
                                            placeholder="Cantidad"
                                            className="w-24 p-1 border rounded"
                                        />

                                    </td>

                                    <td className="px-4 py-2 border space-x-2">
                                        <button
                                            onClick={() => assignDonation(donation)}
                                            className="px-2 py-1 bg-green-500 text-white rounded hover:bg-green-600"
                                        >
                                            Asignar
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

export default DonationManagment;
