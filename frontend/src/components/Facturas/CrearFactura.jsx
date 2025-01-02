import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import FacturaService from "../../services/FacturaService";
import ProductoService from "../../services/ProductoService";
import ClienteService from "../../services/ClienteService";
import { showSuccessAlert, showErrorAlert, showConfirmationAlertTrue } from "../../utils/alerts";

export const CrearFactura = () => {
  const { clienteId } = useParams();
  const navigate = useNavigate();

  const [factura, setFactura] = useState({
    descripcion: "",
    observacion: "",
    cliente: { id: clienteId },
    items: [],
  });
  const [cliente, setCliente] = useState(null);
  const [productos, setProductos] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);

  // Cargar cliente cada vez que `clienteId` cambie
  useEffect(() => {
    setLoading(true); // Reiniciar estado de carga
    fetchCliente();
  }, [clienteId]);

  const fetchCliente = async () => {
    try {
      const response = await ClienteService.getById(clienteId);
      setCliente(response.data);
      setFactura((prevFactura) => ({
        ...prevFactura,
        cliente: { id: clienteId },
      }));
    } catch (error) {
      console.error("Error al cargar el cliente:", error);
      navigate("/clientes");
    } finally {
      setLoading(false);
    }
  };

  const agregarProducto = (producto) => {
    const existingProductIndex = factura.items.findIndex(
      (item) => item.producto.id === producto.id
    );

    if (existingProductIndex !== -1) {
      const nuevaLista = [...factura.items];
      nuevaLista[existingProductIndex].cantidad += 1;
      setFactura({ ...factura, items: nuevaLista });
      calcularTotal(nuevaLista);
    } else {
      const nuevoItem = { producto, cantidad: 1 };
      const nuevaLista = [...factura.items, nuevoItem];
      setFactura({ ...factura, items: nuevaLista });
      calcularTotal(nuevaLista);
    }

    setSearchTerm("");
    setProductos([]);
  };

  const eliminarProducto = (index) => {
    const nuevaLista = factura.items.filter((_, i) => i !== index);
    setFactura({ ...factura, items: nuevaLista });
    calcularTotal(nuevaLista);
  };

  const calcularTotal = (items) => {
    const total = items.reduce(
      (acc, item) => acc + item.producto.precio * item.cantidad,
      0
    );
    setTotal(total);
  };

  const buscarProductos = async (term) => {
    if (!term.trim()) {
      setProductos([]);
      return;
    }
    try {
      const response = await ProductoService.searchByName(term);
      setProductos(response.data.content);
    } catch (error) {
      console.error("Error buscando productos:", error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const isConfirmed = await showConfirmationAlertTrue(
      "¿Estás seguro?",
      "¿Deseas guardar esta factura?"
    );

    if (!isConfirmed) return;

    try {
      if (!factura.observacion.trim()) {
        setFactura((prevFactura) => ({
          ...prevFactura,
          observacion: "No hay observaciones",
        }));
      }

      await FacturaService.saveFactura(factura);
      showSuccessAlert("¡Éxito!", "La factura se ha guardado correctamente.");
      navigate("/clientes");
    } catch (error) {
      console.error("Error al guardar la factura:", error);
      showErrorAlert("Error", "Hubo un problema al guardar la factura.");
    }
  };

  // Mostrar un indicador de carga mientras se obtiene el cliente
  if (loading) {
    return (
      <div className="loading-container">
        <p>Cargando...</p>
      </div>
    );
  }

  return (
    <div className="crear-factura-container">
      <div className="header">
        <button
          className="btn btn-danger volver-lista"
          onClick={() => navigate("/")}
        >
          Volver a Clientes
        </button>
      </div>
      <h2 className="text-center">Crear Factura</h2>
      {cliente && cliente.foto && (
        <img
          src={`http://localhost:8080/uploads/clientes/${cliente.foto}`}
          alt={`${cliente.nombre} ${cliente.apellido}`}
          className="cliente-foto me-3"
        />
      )}
      <form onSubmit={handleSubmit} className="crear-factura-form">
        <div className="mb-3">
          <label>Cliente</label>
          <input
            type="text"
            className="form-control"
            value={`${cliente.nombre} ${cliente.apellido}`}
            readOnly
          />
        </div>
        <div className="mb-3">
          <label>Descripción</label>
          <input
            type="text"
            className="form-control"
            value={factura.descripcion}
            onChange={(e) =>
              setFactura({ ...factura, descripcion: e.target.value })
            }
            required
          />
        </div>
        <div className="mb-3">
          <label>Observación</label>
          <textarea
            className="form-control"
            value={factura.observacion}
            onChange={(e) =>
              setFactura({ ...factura, observacion: e.target.value })
            }
          ></textarea>
        </div>
        <div className="mb-3">
          <label>Buscar producto</label>
          <input
            type="text"
            className="form-control"
            placeholder="Escribe para buscar productos..."
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              buscarProductos(e.target.value);
            }}
          />
          {productos.length > 0 && (
            <ul className="list-group mt-2">
              {productos.map((producto) => (
                <li
                  key={producto.id}
                  className="list-group-item d-flex justify-content-between align-items-center"
                  onClick={() => agregarProducto(producto)}
                >
                  {producto.nombre} - ${producto.precio}
                </li>
              ))}
            </ul>
          )}
        </div>
        <table className="table table-bordered mt-4">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Precio</th>
              <th>Cantidad</th>
              <th>Subtotal</th>
              <th>Eliminar</th>
            </tr>
          </thead>
          <tbody>
            {factura.items.map((item, index) => (
              <tr key={index}>
                <td>{item.producto.nombre}</td>
                <td>${item.producto.precio}</td>
                <td>
                  <input
                    type="number"
                    className="form-control"
                    value={item.cantidad}
                    min="1"
                    onChange={(e) => {
                      const nuevaCantidad = parseInt(e.target.value);
                      const nuevaLista = [...factura.items];
                      nuevaLista[index].cantidad = nuevaCantidad;
                      setFactura({ ...factura, items: nuevaLista });
                      calcularTotal(nuevaLista);
                    }}
                  />
                </td>
                <td>${item.producto.precio * item.cantidad}</td>
                <td>
                  <button
                    type="button"
                    className="btn btn-danger"
                    onClick={() => eliminarProducto(index)}
                  >
                    X
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <h4>Total: ${total}</h4>
        <button type="submit" className="btn btn-primary">
          Crear Factura
        </button>
      </form>
    </div>
  );
};
