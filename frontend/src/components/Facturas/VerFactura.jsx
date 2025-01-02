import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import FacturaService from "../../services/FacturaService";

export const VerFactura = () => {
  const { id } = useParams(); // ID de la factura
  const navigate = useNavigate(); // Para redirigir
  const [factura, setFactura] = useState(null); // Datos de la factura
  const [loading, setLoading] = useState(true); // Estado de carga

  useEffect(() => {
    fetchFactura();
  }, [id]);

  const fetchFactura = async () => {
    try {
      const response = await FacturaService.getFacturaById(id);
      setFactura(response.data);
    } catch (error) {
      console.error("Error al cargar la factura:", error);
      navigate("/"); // Redirige si hay un error
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <p className="text-center mt-5">Cargando factura...</p>;
  }

  if (!factura) {
    return <p className="text-center mt-5">Factura no encontrada.</p>;
  }

  return (
    <div className="container mt-5">
      <h2 className="text-center">Factura: {factura.descripcion}</h2>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <button className="btn btn-secondary" onClick={() => navigate(`/clientes/${factura.cliente.id}`)}>
          &laquo; Volver
        </button>
        <button
            className="btn btn-danger"
            onClick={() => window.open(`http://localhost:8080/api/facturas/${factura.id}/pdf`, "_blank")}
            >
            Ver PDF
        </button>


      </div>

      <div className="card">
        <div className="card-header bg-primary text-white">Datos del cliente</div>
        <div className="card-body">
          <p><strong>Nombre:</strong> {factura.cliente.nombre} {factura.cliente.apellido}</p>
          <p><strong>Email:</strong> {factura.cliente.email}</p>
        </div>
      </div>

      <div className="card mt-3">
        <div className="card-header bg-success text-white">Datos de la factura</div>
        <div className="card-body">
          <p><strong>Folio:</strong> {factura.id}</p>
          <p><strong>Descripción:</strong> {factura.descripcion}</p>
          <p><strong>Fecha:</strong> {factura.createAt}</p>
        </div>
      </div>

      <div className="card mt-3">
        <div className="card-header bg-dark text-white">Productos</div>
        <table className="table">
          <thead>
            <tr>
              <th>Producto</th>
              <th>Precio</th>
              <th>Cantidad</th>
              <th>Subtotal</th>
            </tr>
          </thead>
          <tbody>
            {factura.items.map((item, index) => (
              <tr key={index}>
                <td>{item.producto.nombre}</td>
                <td>${item.producto.precio.toFixed(2)}</td>
                <td>{item.cantidad}</td>
                <td>${(item.producto.precio * item.cantidad).toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="mt-3">
        <h4>Total: ${factura.items.reduce((acc, item) => acc + item.producto.precio * item.cantidad, 0).toFixed(2)}</h4>
      </div>

      <div className="card mt-3">
        <div className="card-header bg-info text-white">Observaciones</div>
        <div className="card-body">
          <p>{factura.observacion || "Sin observaciones."}</p>
        </div>
      </div>
    </div>
  );
};
