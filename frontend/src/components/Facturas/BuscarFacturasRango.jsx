import React, { useState } from "react";
import FacturaService from "../../services/FacturaService";
import { showErrorAlert } from "../../utils/alerts";
import Pagination from "../Pagination";

export const BuscarFacturasRango = () => {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [facturas, setFacturas] = useState([]);
  const [totalFacturas, setTotalFacturas] = useState(0);
  const [cantidadFacturas, setCantidadFacturas] = useState(0); // Nueva variable
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const buscarFacturas = async (page = 0) => {
    if (!startDate || !endDate) {
      showErrorAlert("Error", "Por favor selecciona ambas fechas.");
      return;
    }

    setLoading(true);
    try {
      const response = await FacturaService.getFacturasByDateRange(
        startDate,
        endDate,
        page
      );
      setFacturas(response.data.content || []);
      setTotalFacturas(response.data.totalDinero || 0);
      setCantidadFacturas(response.data.totalElements || 0); // Actualiza cantidad
      setTotalPages(response.data.totalPages || 0);
      setCurrentPage(page);
    } catch (error) {
      console.error("Error al buscar facturas:", error);
      showErrorAlert("Error", "No se pudieron obtener las facturas.");
      setFacturas([]);
      setTotalFacturas(0);
      setCantidadFacturas(0); // Reinicia la cantidad
    } finally {
      setLoading(false);
    }
  };

  const exportarPdf = async () => {
    if (!startDate || !endDate) {
      showErrorAlert(
        "Error",
        "Por favor selecciona ambas fechas antes de exportar el PDF."
      );
      return;
    }

    try {
      await FacturaService.exportFacturasByDateRangeToPDF(startDate, endDate);
    } catch (error) {
      console.error("Error al exportar el PDF:", error);
      showErrorAlert("Error", "No se pudo exportar el PDF.");
    }
  };

  return (
    <div className="container mt-5">
      <h2 className="text-center">Listado de facturas por rango de fechas</h2>

      <div className="d-flex justify-content-center align-items-center mt-4 mb-3">
        <input
          type="date"
          className="form-control me-2"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
        />
        <input
          type="date"
          className="form-control me-2"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
        />
        <button className="btn btn-primary" onClick={() => buscarFacturas(0)}>
          Buscar
        </button>
      </div>

      <button className="btn btn-danger mb-3" onClick={exportarPdf}>
        Exportar a PDF
      </button>

      {loading ? (
        <p className="text-center">Cargando...</p>
      ) : facturas.length > 0 ? (
        <div>
          <table className="table table-bordered">
            <thead>
              <tr>
                <th>ID</th>
                <th>Cliente</th>
                <th>Fecha</th>
                <th>Total</th>
              </tr>
            </thead>
            <tbody>
              {facturas.map((factura) => (
                <tr key={factura.id}>
                  <td>{factura.id}</td>
                  <td>{factura.cliente?.nombre || "Cliente desconocido"}</td>
                  <td>{factura.createAt || "Fecha no disponible"}</td>
                  <td>
                    $
                    {factura.items
                      ? factura.items
                          .reduce(
                            (acc, item) =>
                              acc +
                              (item.producto?.precio || 0) *
                                (item.cantidad || 0),
                            0
                          )
                          .toFixed(2)
                      : "0.00"}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <h4 className="text-end">
            Total de todas las facturas: ${totalFacturas.toFixed(2)}
          </h4>
          <h5 className="text-end text-muted">
            Cantidad de facturas encontradas: {cantidadFacturas}
          </h5>
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={(page) => buscarFacturas(page)}
          />
        </div>
      ) : (
        <p className="text-center text-warning">
          No se encontraron facturas en el rango de fechas seleccionado.
        </p>
      )}
    </div>
  );
};
