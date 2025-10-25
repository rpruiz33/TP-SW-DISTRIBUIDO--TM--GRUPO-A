import { useState } from "react";
import axios from "axios";

export default function PresidentReport() {
    const [orgIds, setOrgIds] = useState([""]);

    const [presidents, setPresidents] = useState([]);

    const [ongs, setOngs] = useState([]);


    // Agregar un nuevo input vacío
    const addOrgId = () => setOrgIds([...orgIds, ""]);

    // Eliminar un input por índice
    const removeOrgId = (index) => {
        setOrgIds(orgIds.filter((_, i) => i !== index));
    };

    // Actualizar valor de un input
    const updateOrgId = (index, value) => {
        const newIds = [...orgIds];
        newIds[index] = value;
        setOrgIds(newIds);
    };

    const fetchPresidents = async () => {
        try {
            setPresidents([])
            const response = await axios.post("http://localhost:8080/api/ong/presidents", orgIds);

            setPresidents(response.data)

        } catch (err) {
            console.error("Error en la solicitud:", err);
        }
    }

    const fetchOngs = async () => {
        try {
            setOngs([])
            const response = await axios.post("http://localhost:8080/api/ong/ongs", orgIds);

            setOngs(response.data)

        } catch (err) {
            console.error("Error en la solicitud:", err);
        }
    }


    return (
        <div className="p-6 bg-[#01000F] min-h-screen flex flex-col">
            <h1 className="text-5xl font-bold text-white mb-6">
                Consulta de Presidentes y ONGs
            </h1>

            {/* Inputs dinámicos */}
            <div className="bg-[#232D4F] px-6 py-3 rounded mb-4 mt-4 flex flex-col gap-4 max-w-4xl mx-auto">
                <label className="text-white mb-2 text-lg">IDs de las ONGs</label>

                {orgIds.map((id, index) => (
                    <div key={index} className="flex gap-4 items-center">
                        <input
                            type="text"
                            value={id}
                            onChange={(e) => updateOrgId(index, e.target.value)}
                            className="flex-1 px-2 py-1 rounded text-black"
                        />
                        <button
                            onClick={() => removeOrgId(index)}
                            className="px-4 py-1 bg-red-600 text-white rounded hover:bg-red-700"
                        >
                            X
                        </button>
                    </div>
                ))}

                <button
                    onClick={addOrgId}
                    className="px-6 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 self-start "
                >
                    Agregar ID
                </button>
                {/* Botones de consulta */}
                <div className="flex gap-4 mb-6">
                    <button
                        onClick={() => fetchPresidents()}
                        className="px-6 py-2 bg-green-700 text-white rounded hover:bg-green-800"
                    >
                        Consultar Presidentes
                    </button>
                    <button
                        onClick={() => fetchOngs()}
                        className="px-6 py-2 bg-purple-700 text-white rounded hover:bg-purple-800"
                    >
                        Consultar ONGs
                    </button>
                </div>
            </div>



            {/* Bloque de resultados */}
            <div className="w-full mx-auto mt-6 space-y-10">

                {/* Tabla de Presidentes */}
                {presidents.length > 0 && (
                    <div>
                        <h2 className="text-2xl font-semibold text-blue-400 mb-3">Presidentes</h2>
                        <table className="min-w-full border border-gray-700 text-center">
                            <thead>
                                <tr className="bg-gray-900 text-white">
                                    <th className="px-4 py-2 border border-gray-700">ID</th>
                                    <th className="px-4 py-2 border border-gray-700">Nombre</th>
                                    <th className="px-4 py-2 border border-gray-700">Dirección</th>
                                    <th className="px-4 py-2 border border-gray-700">Teléfono</th>
                                    <th className="px-4 py-2 border border-gray-700">ID ONG</th>
                                </tr>
                            </thead>
                            <tbody>
                                {presidents.map((p, index) => (
                                    <tr key={index} className="text-gray-200">
                                        <td className="px-4 py-2 border border-gray-700">{p.id}</td>
                                        <td className="px-4 py-2 border border-gray-700">{p.name}</td>
                                        <td className="px-4 py-2 border border-gray-700">{p.address}</td>
                                        <td className="px-4 py-2 border border-gray-700">{p.phone}</td>
                                        <td className="px-4 py-2 border border-gray-700">{p.organizationId}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {/* Tabla de ONGs */}
                {ongs.length > 0 && (
                    <div>
                        <h2 className="text-2xl font-semibold text-blue-400 mb-3">ONGs</h2>
                        <table className="min-w-full border border-gray-700 text-center">
                            <thead>
                                <tr className="bg-gray-900 text-white">
                                    <th className="px-4 py-2 border border-gray-700">ID</th>
                                    <th className="px-4 py-2 border border-gray-700">Nombre</th>
                                    <th className="px-4 py-2 border border-gray-700">Dirección</th>
                                    <th className="px-4 py-2 border border-gray-700">Teléfono</th>
                                </tr>
                            </thead>
                            <tbody>
                                {ongs.map((o, index) => (
                                    <tr key={index} className="text-gray-200">
                                        <td className="px-4 py-2 border border-gray-700">{o.id}</td>
                                        <td className="px-4 py-2 border border-gray-700">{o.name}</td>
                                        <td className="px-4 py-2 border border-gray-700">{o.address}</td>
                                        <td className="px-4 py-2 border border-gray-700">{o.phone}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

            </div>
        </div>


    );
}
