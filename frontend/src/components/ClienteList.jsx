import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ClienteService from "../services/ClienteService";
import Pagination from "./Pagination";
import { showConfirmationAlert, showSuccessAlert, showErrorAlert } from '../utils/alerts';

const ClienteList = () => {
  const navigate = useNavigate();
  const [clientes, setClientes] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    fetchClientes();
  }, [page, searchTerm]);

  const fetchClientes = async () => {
    try {
      const response = searchTerm
        ? await ClienteService.searchByName(searchTerm, page, 5)
        : await ClienteService.getAll(page, 5);
      setClientes(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error("Error fetching clientes:", error);
    }
  };

  const handleDelete = async (id) => {
      const confirmed = await showConfirmationAlert(
          '¿Estás seguro?',
          'Esta acción no se puede deshacer y el Cliente será eliminado.'
      );

      if (confirmed) {
          try {
              await ClienteService.delete(id); // Llama al servicio para eliminar
              showSuccessAlert('Cliente eliminado', 'El Cliente ha sido eliminado correctamente.');
              fetchClientes(); // Actualiza la lista
          } catch (error) {
              showErrorAlert('Error al eliminar', 'Ocurrió un error al intentar eliminar el Cliente.');
              console.error('Error deleting Cliente:', error);
          }
      }
  };  

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
    setPage(0); // Reiniciar a la primera página cuando se realiza una búsqueda
  };

  const handleCrearFactura = (clienteId) => {
    navigate(`/clientes/${clienteId}/factura`);
  };

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Listado de Clientes</h2>
        <button
          className="btn btn-primary"
          onClick={() => navigate("/clientes/formulario")}
        >
          Crear Cliente
        </button>
      </div>

      <div className="input-group mb-3">
        <input
          type="text"
          className="form-control"
          placeholder="Buscar por nombre"
          value={searchTerm}
          onChange={handleSearchChange}
        />
        <button className="btn btn-secondary" onClick={fetchClientes}>
          Buscar
        </button>
      </div>

      <table className="table table-bordered table-striped">
        <thead className="table-primary">
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Apellido</th>
            <th>Email</th>
            <th>Fecha</th>
            <th>Ver Detalle</th>
            <th>Editar</th>
            <th>Eliminar</th>
            <th>Crear Factura</th>
          </tr>
        </thead>
        <tbody>
          {clientes.length > 0 ? (
            clientes.map((cliente) => (
              <tr key={cliente.id}>
                <td>{cliente.id}</td>
                <td>{cliente.nombre}</td>
                <td>{cliente.apellido}</td>
                <td>{cliente.email}</td>
                <td>{cliente.createAt}</td>
                <td>
                  <button
                    className="btn btn-info btn-sm"
                    onClick={() => navigate(`/clientes/${cliente.id}`)}
                  >
                    Ver Detalle
                  </button>
                </td>
                <td>
                  <button
                    className="btn btn-warning btn-sm"
                    onClick={() =>
                      navigate(`/clientes/formulario/${cliente.id}`)
                    }
                  >
                    Editar
                  </button>
                </td>
                <td>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => handleDelete(cliente.id)}
                  >
                    Eliminar
                  </button>
                </td>
                <td>
                  <button
                    className="btn btn-success btn-sm"
                    onClick={() => handleCrearFactura(cliente.id)}
                  >
                    Crear Factura
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="9" className="text-center">
                No se encontraron clientes.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <Pagination
        currentPage={page}
        totalPages={totalPages}
        onPageChange={(newPage) => setPage(newPage)}
      />
    </div>
  );
};

export default ClienteList;
