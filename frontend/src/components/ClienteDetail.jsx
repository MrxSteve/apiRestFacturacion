import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import ClienteService from "../services/ClienteService";
import FacturaService from "../services/FacturaService";
import Pagination from "./Pagination";
import { showConfirmationAlert, showSuccessAlert, showErrorAlert } from '../utils/alerts';

const ClienteDetail = () => {
  const { id } = useParams(); // Obtener el ID del cliente desde la URL
  const navigate = useNavigate(); // Para redirigir al usuario
  const [cliente, setCliente] = useState(null); // Datos del cliente
  const [facturas, setFacturas] = useState([]); // Lista de facturas
  const [currentPage, setCurrentPage] = useState(0); // Página actual
  const [totalPages, setTotalPages] = useState(0); // Total de páginas
  const [totalFacturas, setTotalFacturas] = useState(0); // Total de facturas
  const [totalDinero, setTotalDinero] = useState(0); // Total en dinero

  useEffect(() => {
    fetchCliente(); // Cargar los datos del cliente
    fetchFacturas(); // Cargar las facturas asociadas al cliente
  }, [id, currentPage]);

  const fetchCliente = async () => {
    try {
      const response = await ClienteService.getById(id);
      setCliente(response.data); // Guardar los datos del cliente en el estado
    } catch (error) {
      console.error("Error fetching cliente:", error);
      navigate("/"); // Redirige si no se encuentra el cliente
    }
  };

  const fetchFacturas = async () => {
    try {
      const response = await FacturaService.getFacturasByCliente(id, currentPage, 5);
      const facturasData = response.data.content;

      // Calcular el total de cada factura y el total general del backend
      const facturasConTotales = facturasData.map((factura) => {
        const totalFactura = factura.items.reduce(
          (total, item) => total + item.cantidad * item.producto.precio,
          0
        );
        return { ...factura, total: totalFactura };
      });

      setFacturas(facturasConTotales);
      setTotalPages(response.data.totalPages);
      setTotalFacturas(response.data.totalElements);

      // Actualizar el total de dinero desde el backend
      setTotalDinero(response.data.totalDinero); // Esto debe venir desde el backend
    } catch (error) {
      console.error("Error fetching facturas:", error);
    }
  };

    const handleDelete = async (id) => {
      const confirmed = await showConfirmationAlert(
          '¿Estás seguro?',
          'Esta acción no se puede deshacer y la factura será eliminado.'
      );

      if (confirmed) {
          try {
              await FacturaService.delete(id); // Llama al servicio para eliminar
              showSuccessAlert('Factura eliminada', 'La factura ha sido eliminado correctamente.');
              fetchFacturas(); // Actualiza la lista
          } catch (error) {
              showErrorAlert('Error al eliminar', 'Ocurrió un error al intentar eliminar la factura.');
              console.error('Error deleting factura:', error);
          }
      }
  }; 

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

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
                <li className="list-group-item">
                  <strong>Total Facturas:</strong> {totalFacturas}
                </li>
                <li className="list-group-item">
                  <strong>Total Dinero:</strong> ${totalDinero.toFixed(2)}
                </li>
              </ul>
              <div className="card-footer d-flex justify-content-end">
                <button
                  className="btn btn-success mt-3 me-3"
                  onClick={() => navigate(`/clientes/${id}/factura`)}
                >
                  Crear Factura
                </button>

                <button
                  className="btn btn-secondary mt-3 me-3"
                  onClick={() => navigate("/")}
                >
                  Volver
                </button>

                <button
                  className="btn btn-primary mt-3 me-3"
                  onClick={() => navigate(`/clientes/formulario/${cliente.id}`)}
                >
                  Editar
                </button>
              </div>
            </div>
          </div>
        </div>
        <div className="card-footer">
          <h4>Facturas</h4>
          <table className="table table-bordered mt-3">
            <thead>
              <tr>
                <th>Folio</th>
                <th>Descripción</th>
                <th>Fecha</th>
                <th>Total</th>
                <th>Ver</th>
                <th>Eliminar</th>
              </tr>
            </thead>
            <tbody>
              {facturas.map((factura) => (
                <tr key={factura.id}>
                  <td>{factura.id}</td>
                  <td>{factura.descripcion}</td>
                  <td>{factura.createAt}</td>
                  <td>${factura.total.toFixed(2)}</td>
                  <td>
                    <button
                      className="btn btn-primary"
                      onClick={() => navigate(`/facturas/ver/${factura.id}`)}
                    >
                      Detalle
                    </button>
                  </td>
                  <td>
                    <button 
                      className="btn btn-danger"
                      onClick={() => handleDelete(factura.id)}>
                      Eliminar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </div>
      </div>
    </div>
  );
};

export default ClienteDetail;
