import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import ClienteService from "../services/ClienteService";

const ClienteForm = () => {
  const { id } = useParams(); // Obtiene el id de los parámetros de la URL
  const navigate = useNavigate(); // Navegación para redirigir al usuario

  const [cliente, setCliente] = useState({
    nombre: "",
    apellido: "",
    email: "",
    createAt: "",
    foto: null,
  });

  const [fotoFile, setFotoFile] = useState(null); // Almacena la foto seleccionada

  // Cargar datos del cliente si está en modo edición
  useEffect(() => {
    if (id) fetchCliente();
  }, [id]);

  const fetchCliente = async () => {
    try {
      const response = await ClienteService.getById(id);
      setCliente(response.data);
    } catch (error) {
      console.error("Error fetching cliente:", error);
    }
  };

  // Manejo de cambios en los inputs
  const handleChange = (e) => {
    const { name, value } = e.target;
    setCliente({ ...cliente, [name]: value });
  };

  const handleFileChange = (e) => {
    setFotoFile(e.target.files[0]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (id) {
        // Actualizar cliente
        await ClienteService.update(id, cliente);
        if (fotoFile) {
          await ClienteService.uploadPhoto(id, fotoFile); // Subir la nueva foto
        }
      } else {
        // Crear nuevo cliente
        const response = await ClienteService.save(cliente);
        if (fotoFile) {
          await ClienteService.uploadPhoto(response.data.id, fotoFile); // Subir la foto asociada
        }
      }
      navigate("/"); // Redirige a la página principal
    } catch (error) {
      console.error("Error saving cliente:", error);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="cliente-form container mt-4 p-4 shadow rounded">
      <h2 className="text-primary text-center">
        {id ? "Editar Cliente" : "Crear Cliente"}
      </h2>
      <div className="mb-3">
        <label htmlFor="nombre" className="form-label">
          Nombre
        </label>
        <input
          type="text"
          id="nombre"
          name="nombre"
          value={cliente.nombre}
          onChange={handleChange}
          className="form-control"
          placeholder="Nombre"
          required
        />
      </div>
      <div className="mb-3">
        <label htmlFor="apellido" className="form-label">
          Apellido
        </label>
        <input
          type="text"
          id="apellido"
          name="apellido"
          value={cliente.apellido}
          onChange={handleChange}
          className="form-control"
          placeholder="Apellido"
        />
      </div>
      <div className="mb-3">
        <label htmlFor="email" className="form-label">
          Email
        </label>
        <input
          type="email"
          id="email"
          name="email"
          value={cliente.email}
          onChange={handleChange}
          className="form-control"
          placeholder="Email"
          required
        />
      </div>
      <div className="mb-3">
        <label htmlFor="createAt" className="form-label">
          Fecha de Creación
        </label>
        <input
          type="date"
          id="createAt"
          name="createAt"
          value={cliente.createAt}
          onChange={handleChange}
          className="form-control"
        />
      </div>
      <div className="mb-3">
        <label htmlFor="foto" className="form-label">
          Foto
        </label>
        <input
          type="file"
          id="foto"
          name="foto"
          onChange={handleFileChange}
          className="form-control"
          accept="image/*"
        />
      </div>
      <div className="d-flex justify-content-between">
        <button type="submit" className="btn btn-primary">
          Guardar
        </button>
        <button
          type="button"
          className="btn btn-secondary"
          onClick={() => navigate("/")}
        >
          Cancelar
        </button>
      </div>
    </form>
  );
};

export default ClienteForm;
