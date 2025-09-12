import React, { useEffect, useState } from "react";

// --- COMPONENTES UI SIMPLES ---
const Button = ({ children, className = "", ...props }) => (
  <button
    className={`px-3 py-1.5 rounded-lg font-medium text-sm shadow-sm transition-all 
    bg-[#232D4F] text-white hover:bg-[#1B2440] hover:shadow-md ${className}`}
    {...props}
  >
    {children}
  </button>
);

const Card = ({ children, className = "" }) => (
  <div
    className={`rounded-2xl shadow-md border border-gray-200 p-5 bg-white ${className}`}
  >
    {children}
  </div>
);

const Input = (props) => (
  <input
    className="border border-gray-300 rounded-lg p-2 w-full text-black focus:outline-none focus:ring-2 focus:ring-[#4F6EE4] transition"
    {...props}
  />
);

const Textarea = (props) => (
  <textarea
    className="border border-gray-300 rounded-lg p-2 w-full text-black focus:outline-none focus:ring-2 focus:ring-[#4F6EE4] transition"
    {...props}
  />
);

const Select = (props) => (
  <select
    className="border border-gray-300 rounded-lg p-2 w-full text-black focus:outline-none focus:ring-2 focus:ring-[#4F6EE4] transition"
    {...props}
  />
);

// --- ROLES ---
const roles = {
  PRESIDENTE: "PRESIDENTE",
  COORDINADOR: "COORDINADOR",
  VOLUNTARIO: "VOLUNTARIO",
};

export default function Events({ userRole, userUsername }) {
  const [eventos, setEventos] = useState([]);
  const [miembros, setMiembros] = useState([]);
  const [donations, setDonations] = useState([]);
  const [openForm, setOpenForm] = useState(false);
  const [openDonaciones, setOpenDonaciones] = useState(false);
  const [eventoActual, setEventoActual] = useState(null);

  useEffect(() => {
    // Fetch eventos
    fetch("/api/eventlist")
      .then((res) => res.json())
      .then((data) => setEventos(JSON.parse(data).events || []))
      .catch((err) => console.error(err));

    // Fetch miembros (usuarios activados)
    fetch("/api/userlist")
      .then((res) => res.json())
      .then((data) => {
        const users = JSON.parse(data).users || [];
        setMiembros(users.filter((u) => u.activated));
      })
      .catch((err) => console.error(err));

    // Fetch donaciones
    fetch("/api/donationlist")
      .then((res) => res.json())
      .then((data) => setDonations(JSON.parse(data).donations || []))
      .catch((err) => console.error(err));
  }, []);

  const esPasado = (evento) => new Date(evento.dateRegistration) <= new Date();

  // Guardar evento
  const handleGuardar = (e) => {
    e.preventDefault();
    const eventData = {
      nameEvent: eventoActual.nameEvent,
      descriptionEvent: eventoActual.descriptionEvent,
      dateRegistration: eventoActual.dateRegistration,
    };
    if (new Date(eventoActual.dateRegistration) <= new Date()) {
      alert("La fecha debe ser a futuro");
      return;
    }
    if (eventoActual.id) {
      // Update
      fetch("/api/updateevent", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ id: eventoActual.id, ...eventData }),
      })
        .then((res) => res.json())
        .then((data) => {
          if (data.success) {
            setEventos((prev) =>
              prev.map((ev) => (ev.id === eventoActual.id ? { ...ev, ...eventData } : ev))
            );
            setOpenForm(false);
            setEventoActual(null);
          } else {
            alert(data.message);
          }
        });
    } else {
      // Create
      fetch("/api/createevent", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(eventData),
      })
        .then((res) => res.json())
        .then((data) => {
          if (data.success) {
            setEventos((prev) => [...prev, { id: data.eventId, ...eventData, participantUsernames: [] }]);
            setOpenForm(false);
            setEventoActual(null);
          } else {
            alert(data.message);
          }
        });
    }
  };

  // Baja evento
  const handleBaja = (id) => {
    fetch(`/api/deleteevent/${id}`, { method: "DELETE" })
      .then((res) => res.json())
      .then((data) => {
        if (data.response.includes("success: true")) {
          setEventos((prev) => prev.filter((ev) => ev.id !== id));
        } else {
          alert("Solo se pueden eliminar eventos a futuro");
        }
      });
  };

  // Participación voluntario
  const toggleParticipacion = (evento) => {
    if (userRole === roles.VOLUNTARIO) {
      const yaEsta = evento.participantUsernames.includes(userUsername);
      const endpoint = yaEsta ? "/api/removemember" : "/api/assignmember";
      fetch(endpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ eventId: evento.id, username: userUsername }),
      })
        .then((res) => res.json())
        .then((data) => {
          if (data.success) {
            setEventos((prev) =>
              prev.map((ev) =>
                ev.id === evento.id
                  ? {
                      ...ev,
                      participantUsernames: yaEsta
                        ? ev.participantUsernames.filter((u) => u !== userUsername)
                        : [...ev.participantUsernames, userUsername],
                    }
                  : ev
              )
            );
          } else {
            alert(data.message);
          }
        });
    }
  };

  // Asignar/quitar miembro
  const asignarQuitarMiembro = (evento, username) => {
    if (userRole === roles.PRESIDENTE || userRole === roles.COORDINADOR) {
      const yaEsta = evento.participantUsernames.includes(username);
      const endpoint = yaEsta ? "/api/removemember" : "/api/assignmember";
      fetch(endpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ eventId: evento.id, username }),
      })
        .then((res) => res.json())
        .then((data) => {
          if (data.success) {
            setEventos((prev) =>
              prev.map((ev) =>
                ev.id === evento.id
                  ? {
                      ...ev,
                      participantUsernames: yaEsta
                        ? ev.participantUsernames.filter((u) => u !== username)
                        : [...ev.participantUsernames, username],
                    }
                  : ev
              )
            );
          } else {
            alert(data.message);
          }
        });
    }
  };

  // Guardar donaciones
  const handleGuardarDonaciones = (e) => {
    e.preventDefault();
    const donationId = parseInt(e.target.donationId.value);
    const quantity = parseInt(e.target.quantity.value);
    if (quantity <= 0) {
      alert("Cantidad debe ser positiva");
      return;
    }
    fetch("/api/registerdelivery", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        donationId,
        eventId: eventoActual.id,
        quantity,
        registeredBy: userUsername,
      }),
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          // Actualizar donaciones localmente (restar cantidad)
          setDonations((prev) =>
            prev.map((d) =>
              d.id === donationId ? { ...d, amount: d.amount - quantity } : d
            )
          );
          setOpenDonaciones(false);
          setEventoActual(null);
        } else {
          alert(data.message);
        }
      });
  };

  return (
    <div className="p-6 space-y-6 bg-[#F9FAFB] min-h-screen">
      {/* HEADER */}
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-[#232D4F]">
          Eventos Solidarios
        </h1>
        {(userRole === roles.PRESIDENTE || userRole === roles.COORDINADOR) && (
          <Button onClick={() => setOpenForm(true)}>➕ Nuevo Evento</Button>
        )}
      </div>

      {/* FORMULARIO ALTA / EDICIÓN */}
      {openForm && (
        <Card className="max-w-md mx-auto">
          <form onSubmit={handleGuardar} className="space-y-4">
            <h2 className="text-lg font-semibold text-[#232D4F]">
              {eventoActual ? "Editar Evento" : "Nuevo Evento"}
            </h2>
            <Input
              placeholder="Nombre del evento"
              value={eventoActual?.nameEvent || ""}
              onChange={(e) =>
                setEventoActual({ ...eventoActual, nameEvent: e.target.value })
              }
              required
            />
            <Textarea
              placeholder="Descripción"
              value={eventoActual?.descriptionEvent || ""}
              onChange={(e) =>
                setEventoActual({ ...eventoActual, descriptionEvent: e.target.value })
              }
              required
            />
            <Input
              type="datetime-local"
              value={eventoActual?.dateRegistration.slice(0, 16) || ""}
              onChange={(e) =>
                setEventoActual({ ...eventoActual, dateRegistration: e.target.value })
              }
              required
            />
            <div className="flex gap-2">
              <Button type="submit">💾 Guardar</Button>
              <Button
                type="button"
                className="bg-gray-500 hover:bg-gray-600"
                onClick={() => {
                  setOpenForm(false);
                  setEventoActual(null);
                }}
              >
                Cancelar
              </Button>
            </div>
          </form>
        </Card>
      )}

      {/* FORMULARIO DONACIONES */}
      {openDonaciones && (
        <Card className="max-w-md mx-auto">
          <form onSubmit={handleGuardarDonaciones} className="space-y-4">
            <h2 className="text-lg font-semibold text-[#232D4F]">
              Registrar Donaciones Repartidas
            </h2>
            <Select name="donationId" required>
              <option value="">Seleccione donación</option>
              {donations.filter((d) => d.amount > 0).map((d) => (
                <option key={d.id} value={d.id}>
                  {d.category} - {d.description} (Disponible: {d.amount})
                </option>
              ))}
            </Select>
            <Input
              name="quantity"
              type="number"
              min="1"
              placeholder="Cantidad repartida"
              required
            />
            <div className="flex gap-2">
              <Button type="submit">Guardar</Button>
              <Button
                type="button"
                className="bg-gray-500 hover:bg-gray-600"
                onClick={() => {
                  setOpenDonaciones(false);
                  setEventoActual(null);
                }}
              >
                Cancelar
              </Button>
            </div>
          </form>
        </Card>
      )}

      {/* LISTADO DE EVENTOS */}
      <div className="grid gap-4 md:grid-cols-2">
        {eventos.map((evento) => (
          <Card key={evento.id}>
            <h2 className="text-lg font-semibold text-[#232D4F]">
              {evento.nameEvent}
            </h2>
            <p>{evento.descriptionEvent}</p>
            <p className="text-sm text-gray-600">
              {new Date(evento.dateRegistration).toLocaleString()}
            </p>
            <p className="text-sm">
              Participantes:{" "}
              {evento.participantUsernames
                .map((username) => miembros.find((m) => m.username === username)?.name || username)
                .join(", ")}
            </p>

            <div className="flex flex-wrap gap-2 mt-2">
              {(userRole === roles.PRESIDENTE ||
                userRole === roles.COORDINADOR) && (
                <>
                  <Button
                    className="bg-[#F4B400] hover:bg-[#C99700] text-black"
                    onClick={() => {
                      setEventoActual(evento);
                      if (esPasado(evento)) {
                        setOpenDonaciones(true);
                      } else {
                        setOpenForm(true);
                      }
                    }}
                  >
                    {esPasado(evento)
                      ? "Registrar Donaciones"
                      : "Modificar Evento"}
                  </Button>
                  <Button
                    className="bg-[#D93025] hover:bg-[#B1271C]"
                    onClick={() => handleBaja(evento.id)}
                  >
                    Dar de baja
                  </Button>
                </>
              )}
              {userRole === roles.VOLUNTARIO && !esPasado(evento) && (
                <Button onClick={() => toggleParticipacion(evento)}>
                  {evento.participantUsernames.includes(userUsername)
                    ? "Salir del evento"
                    : "Participar"}
                </Button>
              )}
            </div>

            {/* Asignación de miembros */}
            {(userRole === roles.PRESIDENTE ||
              userRole === roles.COORDINADOR) && !esPasado(evento) && (
              <div className="mt-3">
                <h3 className="font-medium text-sm text-[#232D4F]">
                  Asignar miembros:
                </h3>
                <div className="flex flex-wrap gap-2 mt-1">
                  {miembros.map((miembro) => (
                    <Button
                      key={miembro.username}
                      className={`${
                        evento.participantUsernames.includes(miembro.username)
                          ? "bg-[#4F6EE4]"
                          : "bg-gray-400 hover:bg-gray-500"
                      }`}
                      onClick={() => asignarQuitarMiembro(evento, miembro.username)}
                    >
                      {miembro.name}
                    </Button>
                  ))}
                </div>
              </div>
            )}
          </Card>
        ))}
      </div>
    </div>
  );
}