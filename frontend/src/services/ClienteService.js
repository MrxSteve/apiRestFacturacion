import axios from "axios";

const API_URL = "http://localhost:8080/api/clientes";

const ClienteService = {
  getAll: (page, size) => axios.get(`${API_URL}/find-all?page=${page}&size=${size}`),
  getById: (id) => axios.get(`${API_URL}/find-one/${id}`),
  searchByName: (name, page, size) => {
    return axios.get(`${API_URL}/find-by-nombre/${name}?page=${page}&size=${size}`);
  },
  save: (cliente) => axios.post(`${API_URL}/save`, cliente),
  update: (id, cliente) => axios.put(`${API_URL}/update/${id}`, cliente),
  delete: (id) => axios.delete(`${API_URL}/delete/${id}`),
  uploadPhoto: (id, file) => {
    const formData = new FormData();
    formData.append("file", file);
    return axios.post(`${API_URL}/upload-photo/${id}`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },
};

export default ClienteService;
