import axios from "axios";

const API_URL = "http://localhost:8080/api/facturas";

const FacturaService = {
  saveFactura: async (factura) => {
    return await axios.post(`${API_URL}/save`, factura);
  },
  getFacturaById: async (id) => {
    return await axios.get(`${API_URL}/${id}`);
  },
  downloadPdf: async (id) => {
    const response = await axios.get(`${API_URL}/${id}/pdf`, {
      responseType: "blob",
    });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", `Factura_${id}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
  },
  getFacturasByCliente: async (clienteId, page = 0, size = 5) => {
    return await axios.get(`${API_URL}/cliente/${clienteId}`, {
      params: { page, size },
    });
  },
  delete: (id) => axios.delete(`${API_URL}/delete/${id}`),  
  getFacturasByDateRange: async (startDate, endDate, page = 0, size = 5) => {
    return await axios.get(`${API_URL}/by-date-range`, {
      params: {
        startDate,
        endDate,
        page,
        size,
      },
    });
  },
  exportFacturasByDateRangeToPDF: async (startDate, endDate) => {
    const response = await axios.get(
        `${API_URL}/by-date-range/pdf`,
        {
            params: { startDate, endDate },
            responseType: "blob", // Para descargar el archivo
        }
    );
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "facturas_rango.pdf"); // Nombre del archivo descargado
    document.body.appendChild(link);
    link.click();
    link.remove();
  },

};

export default FacturaService;
