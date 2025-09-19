import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";


//Componente dedicado a gestionar las donaciones YA ASIGNADAS al evento
const DonationManagment = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [donations, setDonations] = useState([]);
    const [error, setError] = useState("");

    // si no hay evento, inicializamos con donations vacío para evitar errores
    const mainEvent = location.state?.event || { donations: [] };

    //  estado local para manejar cantidades ingresadas en cada fila
    const [quantities, setQuantities] = useState({});

    useEffect(() => {
        getAlreadyAssignedDonations();
    }, []);



    const getAlreadyAssignedDonations = async () => {

        try {
            const response = await axios.get(`http://localhost:5000/api/assigneddonationlist/${mainEvent.id}`);
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

    //UPDATE DE LA CANTIDAD DE LA NUEVA DONACION ASIGNADA
    const assignDonation = async (donation) => {
        const quantity = quantities[donation.id] || 0;

        if (!quantity || quantity < 1) {
            alert("Debes ingresar una cantidad válida antes de asignar.");
            return;
        }


        const protoPayload = {
            idEvent: mainEvent.id,
            description: donation.description,
            quantityDelivered: quantity,
            username: localStorage.getItem("usernameOrEmail")
        }

        try {
            const response = await axios.post("http://localhost:5000/api/updatedonationatevent", protoPayload);

            if (response.data.success) {
                alert("Se actualizo la cantidad asignada al evento")
                window.location.reload();

            } else {
                alert(response.data.message);
            }

        } catch {
            console.error(error);
            alert("❌ Error en la operación");
        }

    };


    const handleNewDonationAssignment = (mainEvent) => {
        navigate("/assignnewdonation", { state: { mainEvent } });
    };

    return (
        <div className="p-6">
            <h1 className="text-2xl text-white font-bold mb-4">
                Gestión de Donaciones del Evento {mainEvent.nameEvent}
            </h1>
            {error && <div className="text-red-500 mb-4">{error}</div>}
            <button
                onClick={() => handleNewDonationAssignment(mainEvent)}
                className="mb-4 px-4 py-2 bg-blue-500 text-black rounded hover:bg-blue-600"
            >
                Asignar una nueva donación
            </button>
            <div className="overflow-x-auto">
                <table className="min-w-full bg-white border border-gray-200">
                    <thead>
                        <tr className="bg-gray-100">
                            <th className="px-4 py-2 border">Categoría</th>
                            <th className="px-4 py-2 border">Descripción</th>
                            <th className="px-4 py-2 border">Cantidad disponible</th>
                            <th className="px-4 py-2 border">Cantidad asignada</th>

                            <th className="px-4 py-2 border">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {donations.map((donation, index) => {
                            return (
                                <tr key={donation.id || index} className="text-center">
                                    <td className="px-4 py-2 border">{donation.donation.category || "N/A"}</td>
                                    <td className="px-4 py-2 border">{donation.donation.description || "N/A"}</td>
                                    <td className="px-4 py-2 border">{donation.donation.amount || 0}</td>
                                    <td className="px-4 py-2 border">{donation.quantityDelivered || 0}</td>

                                    <td className="px-4 py-2 border">
                                        <input
                                            type="number"
                                            value={quantities[donation.donation.id] || ""}
                                            onChange={(e) => {
                                                let value = parseInt(e.target.value, 10);

                                                // Si no es número válido → lo dejamos vacío
                                                if (isNaN(value)) {
                                                    setQuantities({
                                                        ...quantities,
                                                        [donation.donation.id]: ""
                                                    });
                                                    return;
                                                }

                                                // Forzar mínimo 1
                                                if (value < 1) {
                                                    value = 1;
                                                }
                                                const maxValue = donation.donation.amount != null 
                                                    ? donation.donation.amount + donation.quantityDelivered
                                                    : donation.quantityDelivered; // máximo permitido

                                                // Forzar máximo (amount +  cantidad ya asignada )
                                                if (value > maxValue) {
                                                    value = maxValue
                                                }

                                                setQuantities({
                                                    ...quantities,
                                                    [donation.donation.id]: value
                                                });
                                            }}
                                            placeholder="Cantidad"
                                            className="w-24 p-1 border rounded"
                                        />




                                        <button
                                            onClick={() => assignDonation(donation.donation)}
                                            className="px-2 py-1 bg-green-500 text-black rounded hover:bg-green-600"
                                        >
                                            Actualizar cantidad
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
