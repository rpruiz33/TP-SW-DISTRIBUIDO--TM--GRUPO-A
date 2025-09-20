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
        <div className="p-6 bg-[#01000F] min-h-screen flex flex-col">
            {/* Título y botón */}
            <div className="flex justify-between items-center mb-4">
                <h1 className="text-5xl font-bold text-white">
                    Gestión de Donaciones del Evento {mainEvent.nameEvent}
                </h1>
                <button
                    onClick={() => handleNewDonationAssignment(mainEvent)}
                    className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                >
                    Asignar una nueva donación
                </button>
            </div>

            {/* Mensaje de error */}
            {error && <div className="text-red-500 mb-4">{error}</div>}

            {/* Tabla */}
            <div className="overflow-x-auto flex-grow">
                <table className="min-w-full border border-gray-700 text-center">
                    <thead>
                        <tr className="bg-gray-900 text-white">
                            <th className="px-4 py-2 border border-gray-700">Categoría</th>
                            <th className="px-4 py-2 border border-gray-700">Descripción</th>
                            <th className="px-4 py-2 border border-gray-700">Cantidad disponible</th>
                            <th className="px-4 py-2 border border-gray-700">Cantidad asignada</th>
                            <th className="px-4 py-2 border border-gray-700">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {donations.map((donation, index) => (
                            <tr key={donation.id || index} className="text-gray-200">
                                <td className="px-4 py-2 border border-gray-700">
                                    {donation.donation.category || "N/A"}
                                </td>
                                <td className="px-4 py-2 border border-gray-700">
                                    {donation.donation.description || "N/A"}
                                </td>
                                <td className="px-4 py-2 border border-gray-700">
                                    {donation.donation.amount || 0}
                                </td>
                                <td className="px-4 py-2 border border-gray-700">
                                    {donation.quantityDelivered || 0}
                                </td>
                                <td className="px-4 py-2 border border-gray-700 space-x-2">
                                    <input
                                        type="number"
                                        value={quantities[donation.donation.id] || ""}
                                        onChange={(e) => {
                                            let value = parseInt(e.target.value, 10);

                                            if (isNaN(value)) {
                                                setQuantities({
                                                    ...quantities,
                                                    [donation.donation.id]: ""
                                                });
                                                return;
                                            }

                                            if (value < 1) value = 1;

                                            const maxValue =
                                                donation.donation.amount != null
                                                    ? donation.donation.amount + donation.quantityDelivered
                                                    : donation.quantityDelivered;

                                            if (value > maxValue) value = maxValue;

                                            setQuantities({
                                                ...quantities,
                                                [donation.donation.id]: value
                                            });
                                        }}
                                        placeholder="Cantidad"
                                        className="w-24 p-1 border border-gray-700 rounded text-black"
                                    />

                                    <button
                                        onClick={() => assignDonation(donation.donation)}
                                        className="px-2 py-1 bg-green-500 text-black rounded hover:bg-green-600"
                                    >
                                        Actualizar cantidad
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

export default DonationManagment;
