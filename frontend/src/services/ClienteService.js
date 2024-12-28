import axios from 'axios';

const API_URL = 'http://localhost:8080/api/clientes';

// Obtener todos los clientes con paginación
export const getClientes = async (page = 0, size = 10) => {
    const response = await axios.get(`${API_URL}/find-all`, {
        params: { page, size },
    });
    return response.data;
};

// Crear o actualizar un cliente
export const saveCliente = async (formData, id = null) => {
    if (id) {
        // Editar cliente
        const response = await axios.put(`${API_URL}/update/${id}`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        return response.data;
    } else {
        // Crear cliente
        const response = await axios.post(`${API_URL}/save`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        return response.data;
    }
};

// Obtener cliente por ID
export const getClienteById = async (id) => {
    const response = await axios.get(`${API_URL}/find-one/${id}`);
    return response.data;
};

// Eliminar cliente por ID
export const deleteCliente = async (id) => {
    await axios.delete(`${API_URL}/delete/${id}`);
};
