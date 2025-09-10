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

// --- ROLES ---
const roles = {
  PRESIDENTE: "PRESIDENTE",
  COORDINADOR: "COORDINADOR",
  VOLUNTARIO: "VOLUNTARIO",
};

export default function Events({ userRole, userId }) {
  const [eventos, setEventos] = useState([]);
  const [miembros, setMiembros] = useState([]);
  const [openForm, setOpenForm] = useState(false);
  const [openDonaciones, setOpenDonaciones] = useState(false);
  const [eventoActual, setEventoActual] = useState(null);

  // Mock inicial
  useEffect(() => {
    setEventos([
      {
        id: 1,
        nombre: "Visita a la escuela nº 99",
        descripcion: "Se organizarán juegos y repartirán útiles",
        fecha: "2025-09-20T15:00",
        miembros: [1, 2],
        donaciones: [],
      },
    ]);
    setMiembros([
      { id: 1, nombre: "Juan Pérez", activo: true },
      { id: 2, nombre: "Ana López", activo: true },
      { id: 3, nombre: "Roberto Alcocer", activo: false },
    ]);
  }, []);

  const esPasado = (evento) => new Date(evento.fecha) <= new Date();

  // Guardar evento
  const handleGuardar = (e) => {
    e.preventDefault();
    if (new Date(eventoActual.fecha) <= new Date()) {
      alert("La fecha debe ser a futuro");
      return;
    }
    if (eventoActual.id) {
      setEventos((prev) =>
        prev.map((ev) => (ev.id === eventoActual.id ? eventoActual : ev))
      );
    } else {
      setEventos((prev) => [...prev, { ...eventoActual, id: Date.now() }]);
    }
    setOpenForm(false);
    setEventoActual(null);
  };

  // Baja evento
  const handleBaja = (id) => {
    const evento = eventos.find((ev) => ev.id === id);
    if (esPasado(evento)) {
      alert("Solo se pueden eliminar eventos a futuro");
      return;
    }
    setEventos((prev) => prev.filter((ev) => ev.id !== id));
  };

  // Participación voluntario
  const toggleParticipacion = (evento) => {
    if (userRole === roles.VOLUNTARIO) {
      const yaEsta = evento.miembros.includes(userId);
      const nuevos = yaEsta
        ? evento.miembros.filter((m) => m !== userId)
        : [...evento.miembros, userId];
      setEventos((prev) =>
        prev.map((ev) => (ev.id === evento.id ? { ...ev, miembros: nuevos } : ev))
      );
    }
  };

  // Asignar/quitar miembro
  const asignarQuitarMiembro = (evento, miembroId) => {
    if (userRole === roles.PRESIDENTE || userRole === roles.COORDINADOR) {
      const yaEsta = evento.miembros.includes(miembroId);
      const nuevos = yaEsta
        ? evento.miembros.filter((m) => m !== miembroId)
        : [...evento.miembros, miembroId];
      setEventos((prev) =>
        prev.map((ev) => (ev.id === evento.id ? { ...ev, miembros: nuevos } : ev))
      );
    }
  };

  // Guardar donaciones
  const handleGuardarDonaciones = (e) => {
    e.preventDefault();
    const nuevasDonaciones = eventoActual.donaciones || [];
    nuevasDonaciones.push({
      descripcion: e.target.descripcion.value,
      cantidad: e.target.cantidad.value,
      registradoPor: userId,
    });
    setEventos((prev) =>
      prev.map((ev) =>
        ev.id === eventoActual.id
          ? { ...ev, donaciones: nuevasDonaciones }
          : ev
      )
    );
    setOpenDonaciones(false);
    setEventoActual(null);
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
              value={eventoActual?.nombre || ""}
              onChange={(e) =>
                setEventoActual({ ...eventoActual, nombre: e.target.value })
              }
              required
            />
            <Textarea
              placeholder="Descripción"
              value={eventoActual?.descripcion || ""}
              onChange={(e) =>
                setEventoActual({ ...eventoActual, descripcion: e.target.value })
              }
              required
            />
            <Input
              type="datetime-local"
              value={eventoActual?.fecha || ""}
              onChange={(e) =>
                setEventoActual({ ...eventoActual, fecha: e.target.value })
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
              Registrar Donaciones
            </h2>
            <Input name="descripcion" placeholder="Descripción donación" required />
            <Input
              name="cantidad"
              type="number"
              min="1"
              placeholder="Cantidad"
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
              {evento.nombre}
            </h2>
            <p>{evento.descripcion}</p>
            <p className="text-sm text-gray-600">
              {new Date(evento.fecha).toLocaleString()}
            </p>
            <p className="text-sm">
              Participantes:{" "}
              {evento.miembros
                .map((id) => miembros.find((m) => m.id === id)?.nombre)
                .join(", ")}
            </p>

            <div className="flex flex-wrap gap-2 mt-2">
              {(userRole === roles.PRESIDENTE ||
                userRole === roles.COORDINADOR) && (
                <>
                  <Button
                    className="bg-[#F4B400] hover:bg-[#C99700] text-black"
                    onClick={() => {
                      if (esPasado(evento)) {
                        setEventoActual(evento);
                        setOpenDonaciones(true);
                      } else {
                        setEventoActual(evento);
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
              {userRole === roles.VOLUNTARIO && (
                <Button onClick={() => toggleParticipacion(evento)}>
                  {evento.miembros.includes(userId)
                    ? "Salir del evento"
                    : "Participar"}
                </Button>
              )}
            </div>

            {/* Asignación de miembros */}
            {(userRole === roles.PRESIDENTE ||
              userRole === roles.COORDINADOR) && (
              <div className="mt-3">
                <h3 className="font-medium text-sm text-[#232D4F]">
                  Asignar miembros:
                </h3>
                <div className="flex flex-wrap gap-2 mt-1">
                  {miembros
                    .filter((m) => m.activo)
                    .map((miembro) => (
                      <Button
                        key={miembro.id}
                        className={`${
                          evento.miembros.includes(miembro.id)
                            ? "bg-[#4F6EE4]"
                            : "bg-gray-400 hover:bg-gray-500"
                        }`}
                        onClick={() => asignarQuitarMiembro(evento, miembro.id)}
                      >
                        {miembro.nombre}
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
