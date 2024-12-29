import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import ClienteService from "../services/ClienteService";

const ClienteDetail = () => {
  const { id } = useParams(); // Obtener el ID del cliente desde la URL
  const navigate = useNavigate(); // Para redirigir al usuario
  const [cliente, setCliente] = useState(null); // Datos del cliente

  // Cargar los datos del cliente al montar el componente
  useEffect(() => {
    fetchCliente();
  }, []);

  const fetchCliente = async () => {
    try {
      const response = await ClienteService.getById(id);
      setCliente(response.data); // Guardar los datos del cliente en el estado
    } catch (error) {
      console.error("Error fetching cliente:", error);
      navigate("/"); // Redirige si no se encuentra el cliente
    }
  };

  // Si los datos aún no están disponibles, mostrar un mensaje de carga
  if (!cliente) {
    return <p className="text-center mt-5">Cargando detalles del cliente...</p>;
  }

  return (
    <div className="container mt-4">
      <div className="card shadow">
        <div className="card-header bg-primary text-white">
          <h3>Detalle Cliente: {cliente.nombre}</h3>
        </div>
        <div className="card-body">
          <div className="row">
            <div className="col-md-4 text-center">
              {cliente.foto ? (
                <img
                  src={`http://localhost:8080/uploads/clientes/${cliente.foto}`}
                  alt={`${cliente.nombre} ${cliente.apellido}`}
                  className="cliente-image me-3"
                />
              ) : (
                <div className="text-muted">
                  <p>No hay foto disponible</p>
                  <i className="bi bi-person-circle" style={{ fontSize: "8rem" }}></i>
                </div>
              )}
            </div>
            <div className="col-md-8">
              <ul className="list-group list-group-flush">
                <li className="list-group-item">
                  <strong>Nombre:</strong> {cliente.nombre}
                </li>
                <li className="list-group-item">
                  <strong>Apellido:</strong> {cliente.apellido}
                </li>
                <li className="list-group-item">
                  <strong>Email:</strong> {cliente.email}
                </li>
                <li className="list-group-item">
                  <strong>Fecha de creación:</strong> {cliente.createAt}
                </li>
              </ul>
            </div>
          </div>
        </div>
        <div className="card-footer d-flex justify-content-end">
          <button
            className="btn btn-secondary me-2"
            onClick={() => navigate("/")}
          >
            Volver
          </button>
          <button
            className="btn btn-primary"
            onClick={() => navigate(`/clientes/formulario/${cliente.id}`)}
          >
            Editar
          </button>
        </div>
      </div>
    </div>
  );
};

export default ClienteDetail;
