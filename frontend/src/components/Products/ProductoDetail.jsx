import React, {useEffect, useState} from 'react'
import {useParams, useNavigate} from 'react-router-dom'
import ProductoService from '../../services/ProductoService';

export const ProductoDetail = () => {
    const {id} = useParams();
    const navigate = useNavigate();
    const [producto, setProducto] = useState(null);

    useEffect(() => {
        fetchProducto();
    }, []);

    const fetchProducto = async () => {
        try {
            const response = await ProductoService.getById(id);
            setProducto(response.data);
        } catch (error) {
            console.error("Error fetching producto:", error);
            navigate("/productos");
        }
    };

    if (!producto) {
        return <p className="text-center mt-5">Cargando detalles del producto...</p>;
    }

  return (
    <div className="container mt-4">
        <div className="card shadow">
            <div className="card-header bg-primary text-white">
                <h3>Detalle Producto: {producto.nombre}</h3>
            </div>
            <div className="card-body">
                <div className="row">
                    <div className="col-md-4 text-center">
                        {producto.foto ? (
                            <img
                                src={`http://localhost:8080/uploads/productos/${producto.foto}`}
                                alt={`${producto.nombre}`}
                                className="cliente-image me-3"
                            />
                        ) : (
                            <div className="text-muted">
                                <p>No hay foto disponible</p>
                                <i className="bi bi-image" style={{ fontSize: "8rem" }}></i>
                            </div>
                        )}
                    </div>
                    <div className="col-md-8">
                        <ul className="list-group list-group-flush">
                            <li className="list-group-item">
                                <strong>Nombre:</strong> {producto.nombre}
                            </li>
                            <li className="list-group-item">
                                <strong>Precio:</strong> ${producto.precio}
                            </li>
                            <li className="list-group-item">
                                <strong>Fecha de creacion:</strong> {producto.createAt}
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

            <div className="card-footer d-flex justify-content-end">
                <button
                className="btn btn-secondary me-2"
                onClick={() => navigate("/productos")}
                >
                Volver
            </button>
            <button
                className="btn btn-primary"
                onClick={() => navigate(`/productos/formulario/${producto.id}`)}
            >
                Editar
            </button>

            </div>

        </div>
    </div>
  )
}
