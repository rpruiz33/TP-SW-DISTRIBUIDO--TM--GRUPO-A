import axios from "axios";

const API_URL = "http://127.0.0.1:5000/api";

// ============================
// OPERACIONES
// ============================
export const generarOperacion = async (mensaje, tipo) => {
  try {
    const res = await axios.post(`${API_URL}/generarOperacion`, {
      category: mensaje.listaDonaciones[0]?.categoria,
      description: mensaje.listaDonaciones[0]?.descripcion,
      amount: mensaje.listaDonaciones[0]?.cantidad || 1,
      username: mensaje.idOrganizacion,
      typeOperation: tipo // "SOLICITUD" o "OFERTA"
    });
    console.log("Operación enviada:", res.data);
    return res.data;
  } catch (err) {
    console.error("Error generando operación:", err);
  }
};
