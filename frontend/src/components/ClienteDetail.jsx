import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getClienteById } from '../services/ClienteService';

const ClienteDetail = () => {
    const [cliente, setCliente] = useState(null);
    const { id } = useParams();
    const navigate = useNavigate();

    const API_UPLOADS_URL = 'http://localhost:8080/api/uploads/files/clientes';

    useEffect(() => {
        cargarCliente();
    }, []);

    const cargarCliente = async () => {
        try {
            const data = await getClienteById(id);
            setCliente(data);
        } catch (error) {
            console.error('Error al cargar el cliente:', error);
            alert('No se pudo cargar el cliente. Por favor, intenta nuevamente.');
        }
    };

    if (!cliente) {
        return (
            <div className="container mt-5">
                <h1 className="text-primary">Cargando información del cliente...</h1>
            </div>
        );
    }

    return (
        <div className="container mt-5">
            <h1 className="text-primary text-center">Detalle Cliente: {cliente.nombre}</h1>
            <div className="row mt-4 justify-content-center">
                {/* Sección de la foto del cliente */}
                <div className="col-md-4 d-flex justify-content-center">
                    {cliente.foto ? (
                        <img
                            src={`${API_UPLOADS_URL}/${cliente.foto}`}
                            alt={cliente.nombre}
                            className="img-fluid rounded shadow"
                            style={{
                                width: '200px',
                                height: '200px',
                                objectFit: 'cover',
                            }}
                            onError={(e) => {
                                e.target.onerror = null;
                                e.target.src = '/placeholder.jpg'; // Imagen predeterminada
                            }}
                        />
                    ) : (
                        <p className="text-muted">Sin foto</p>
                    )}
                </div>

                {/* Sección de detalles del cliente */}
                <div className="col-md-8">
                    <ul className="list-group">
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
                    </ul>
                    <button
                        className="btn btn-primary mt-3"
                        onClick={() => navigate('/')}
                    >
                        Volver al listado
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ClienteDetail;
