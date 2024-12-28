import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { saveCliente, getClienteById } from '../services/ClienteService';

const ClienteForm = () => {
    const [cliente, setCliente] = useState({
        nombre: '',
        apellido: '',
        email: '',
        fecha: '',
        foto: null, // Campo para manejar la foto
    });

    const navigate = useNavigate();
    const { id } = useParams(); // Detecta si estamos editando

    // Cargar datos si estamos editando
    useEffect(() => {
        if (id) {
            cargarCliente();
        }
    }, [id]);

    const cargarCliente = async () => {
        const data = await getClienteById(id);
        setCliente({
            ...data,
            fecha: data.createAt, // Mapear el campo de fecha desde el backend
        });
    };

    // Maneja el cambio en los campos del formulario
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCliente({ ...cliente, [name]: value });
    };

    // Maneja el cambio en el campo de archivo
    const handleFileChange = (e) => {
        setCliente({ ...cliente, foto: e.target.files[0] });
    };

    // Enviar el formulario
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const formData = new FormData();
            formData.append('nombre', cliente.nombre);
            formData.append('apellido', cliente.apellido);
            formData.append('email', cliente.email);
            formData.append('fecha', cliente.fecha);
            if (cliente.foto) {
                formData.append('foto', cliente.foto); // Agrega la foto si existe
            }

            await saveCliente(formData, id); // Usa el servicio para guardar o editar
            alert('Cliente guardado con éxito');
            navigate('/'); // Redirige al listado de clientes
        } catch (error) {
            console.error('Error al guardar el cliente:', error);
            alert('Hubo un error al guardar el cliente.');
        }
    };

    return (
        <div className="container mt-5">
            <h1 className="text-primary mb-4">
                {id ? 'Editar Cliente' : 'Crear Cliente'}
            </h1>
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label htmlFor="nombre" className="form-label">
                        Nombre
                    </label>
                    <input
                        type="text"
                        className="form-control"
                        id="nombre"
                        name="nombre"
                        value={cliente.nombre}
                        onChange={handleInputChange}
                        required
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="apellido" className="form-label">
                        Apellido
                    </label>
                    <input
                        type="text"
                        className="form-control"
                        id="apellido"
                        name="apellido"
                        value={cliente.apellido}
                        onChange={handleInputChange}
                        required
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="email" className="form-label">
                        Email
                    </label>
                    <input
                        type="email"
                        className="form-control"
                        id="email"
                        name="email"
                        value={cliente.email}
                        onChange={handleInputChange}
                        required
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="fecha" className="form-label">
                        Fecha
                    </label>
                    <input
                        type="date"
                        className="form-control"
                        id="fecha"
                        name="fecha"
                        value={cliente.fecha}
                        onChange={handleInputChange}
                        required
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="foto" className="form-label">
                        Foto
                    </label>
                    <input
                        type="file"
                        className="form-control"
                        id="foto"
                        name="foto"
                        onChange={handleFileChange}
                    />
                </div>
                <button type="submit" className="btn btn-primary">
                    Guardar
                </button>
                <button
                    type="button"
                    className="btn btn-secondary ms-2"
                    onClick={() => navigate('/')}
                >
                    Cancelar
                </button>
            </form>
        </div>
    );
};

export default ClienteForm;
