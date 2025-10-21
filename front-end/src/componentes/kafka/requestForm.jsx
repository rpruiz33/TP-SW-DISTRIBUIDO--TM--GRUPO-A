import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

const RequestForm = () => {
    const location = useLocation();
    const navigate = useNavigate();

    // Operación recibida desde RequestList
    const operation = location.state?.operation;

    const [donations, setDonations] = useState([]);
    const [error, setError] = useState("");

    // Inicializamos el estado con las donaciones originales
    useEffect(() => {
        if (operation?.donations) {
            setDonations(operation.donations);
        }
    }, [operation]);

    // Manejar cambios en la cantidad
    const handleCantidadChange = (index, value) => {
        const updated = [...donations];
        updated[index].quantity = parseInt(value, 10) || 0;
        setDonations(updated);
    };

    const handleSubmitTransfer = async () => {

        const protoData = {
            idOperationMessage: operation.idOperationMessage,
            operationType: "TRANSFERENCIA",
            donations: operation.donations,
            idOrganization: operation.idOrganization
        }

        try {
            const response = await axios.post(
                "http://localhost:5000/api/transferdonations", protoData

            );

            if (response.data.success) {
                alert(response.data.message);
                navigate(-1)
            } else {
                alert(response.data.message);
            }


        } catch (err) {
            setError("Error de conexión con el servidor");
        }
    };

    if (!operation) {
        return (
            <div className="p-6 text-white bg-[#01000F] min-h-screen">
                <p>No se encontró información de la operación.</p>
            </div>
        );
    }

    return (
        <div className="p-6 bg-[#01000F] min-h-screen flex flex-col items-center text-white">
            {/* Título */}
            <h1 className="text-3xl font-bold mb-6 text-center">
                Transferir Donaciones — ID {operation.idOperationMessage}
            </h1>


            {/* Mensaje de error */}
            {error && <div className="text-red-500 mb-4">{error}</div>}
            {/* Información de la organización */}
            <div className="mb-6 text-gray-300">



                <p>
                    <span className="font-semibold text-white">Organización solicitante:</span>{" "}
                    {operation.idOrganization}
                </p>
            </div>

            {/* Lista de donaciones */}
            <div className="flex flex-col gap-4 w-full max-w-xl">
                {donations.map((don, idx) => (
                    <div
                        key={idx}
                        className="flex flex-col gap-2 border border-gray-700 bg-gray-900 p-4 rounded-2xl shadow-md"
                    >
                        <div className="flex justify-between items-center">
                            <span className="text-lg font-semibold">{don.description}</span>
                            <span className="text-sm text-gray-400 italic">
                                {don.category}
                            </span>
                        </div>

                        <div className="flex flex-col">
                            <label className="text-sm mb-1 text-gray-300">
                                Cantidad a transferir:
                            </label>
                            <input
                                type="number"
                                min="0"
                                value={don.quantity}
                                onChange={(e) => handleCantidadChange(idx, e.target.value)}
                                className="w-full border border-gray-600 bg-gray-800 text-white px-2 py-1 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                placeholder="Ingrese cantidad"
                            />
                        </div>
                    </div>
                ))}
            </div>

            {/* Botones de acción */}
            <div className="flex gap-3 mt-8">
                <button
                    onClick={handleSubmitTransfer}
                    className="bg-green-700 hover:bg-green-800 text-white px-6 py-2 rounded"
                >
                    Confirmar Transferencia
                </button>

                <button
                    onClick={() => navigate(-1)}
                    className="bg-gray-600 hover:bg-gray-700 text-white px-6 py-2 rounded"
                >
                    Volver
                </button>
            </div>
        </div>
    );
};

export default RequestForm;
