import axios from "axios";
import { useState } from "react";

function OperationForm() {
  const [operation, setOperation] = useState({
    idOrganization: 1,
    operationType: "OFERTA",
    operationDonations: [
      { category: "ALIMENTO", description: "Arroz", quantity: 10 },
    ],
  });
  const [message, setMessage] = useState("");

  const sendOperation = async (operation) => {
    try {
      const res = await axios.post(
        "http://localhost:5000/api/sendOperation",
        operation
      );
      setMessage(res.data.message);
      console.log(res.data.message);
    } catch (err) {
      console.error(err);
      setMessage("Error enviando operación");
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    sendOperation(operation);
  };

  return (
    <div>
      <h2>Enviar Operación</h2>
      <form onSubmit={handleSubmit}>
        {/* Aquí podés agregar inputs para modificar operation si querés */}
        <button type="submit">Enviar</button>
      </form>
      {message && <p>{message}</p>}
    </div>
  );
}

export default OperationForm;
