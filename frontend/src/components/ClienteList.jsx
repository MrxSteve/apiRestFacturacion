import React, { useEffect, useState } from 'react';
import { getClientes, deleteCliente } from '../services/ClienteService';
import { useNavigate } from 'react-router-dom';

const ClienteList = () => {
    const [clientes, setClientes] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        cargarClientes();
    }, []);

    const cargarClientes = async () => {
        const data = await getClientes();
        setClientes(data.content);
    };

    const eliminarCliente = async (id) => {
        if (window.confirm('¿Estás seguro de eliminar este cliente?')) {
            await deleteCliente(id);
            cargarClientes();
        }
    };

    return (
        <div className="container mt-5">
            <h1 className="text-primary mb-4">Listado de Clientes</h1>
            <button
                className="btn btn-primary mb-3"
                onClick={() => navigate('/clientes/nuevo')}
            >
                Crear Cliente
            </button>
            <table className="table table-striped">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Apellido</th>
                        <th>Email</th>
                        <th>Fecha</th>
                        <th>Detalle</th>
                        <th>Editar</th>
                        <th>Eliminar</th>
                    </tr>
                </thead>
                <tbody>
                    {clientes.map((cliente) => (
                        <tr key={cliente.id}>
                            <td>{cliente.id}</td>
                            <td>{cliente.nombre}</td>
                            <td>{cliente.apellido}</td>
                            <td>{cliente.email}</td>
                            <td>{cliente.createAt}</td>
                            <td>
                                <button
                                    className="btn btn-info"
                                    onClick={() => navigate(`/clientes/detalle/${cliente.id}`)}
                                >
                                    Ver Detalle
                                </button>
                            </td>
                            <td>
                                <button
                                    className="btn btn-primary"
                                    onClick={() => navigate(`/clientes/${cliente.id}`)}
                                >
                                    Editar
                                </button>
                            </td>
                            <td>
                                <button
                                    className="btn btn-danger"
                                    onClick={() => eliminarCliente(cliente.id)}
                                >
                                    Eliminar
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ClienteList;
