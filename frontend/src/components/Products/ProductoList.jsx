import React, {useState, useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import ProductoService from '../../services/ProductoService';
import Pagination from '../../components/Pagination';
import { showConfirmationAlert, showSuccessAlert, showErrorAlert } from '../../utils/alerts';

export const ProductoList = () => {
    const navigate = useNavigate();
    const [productos, setProductos] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [searchTerm, setSearchTerm] = useState('');

    useEffect(() => {
        fetchProductos();
    }, [page, searchTerm]);

    const fetchProductos = async () => {
        try {
            const response = searchTerm
                ? await ProductoService.searchByName(searchTerm, page, 5)
                : await ProductoService.getAll(page, 5);
            setProductos(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error('Error fetching productos:', error);
        }
    }; 

    const handleDelete = async (id) => {
        const confirmed = await showConfirmationAlert(
            '¿Estás seguro?',
            'Esta acción no se puede deshacer y el producto será eliminado.'
        );
    
        if (confirmed) {
            try {
                await ProductoService.delete(id); // Llama al servicio para eliminar
                showSuccessAlert('Producto eliminado', 'El producto ha sido eliminado correctamente.');
                fetchProductos(); // Actualiza la lista
            } catch (error) {
                showErrorAlert('Error al eliminar', 'Ocurrió un error al intentar eliminar el producto.');
                console.error('Error deleting producto:', error);
            }
        }
    };    

    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
        setPage(0); // Reiniciar a la primera página cuando se realiza una búsqueda
    };

  return (
    <div className="container mt-4">

        <div className="d-flex justify-content-between align-items-center mb-3">
            <h2>Listado de Productos</h2>
            <button 
                className="btn btn-primary" 
                onClick={() => navigate('/productos/formulario')}>
                Crear Producto
            </button>
        </div>

        <div className="input-group mb-3">
            <input 
                type="text" 
                className="form-control" 
                placeholder="Buscar producto..." 
                value={searchTerm} 
                onChange={handleSearchChange} 
            />
            <button 
                className="btn btn-secondary" 
                type="button" 
                id="button-addon2">
                Buscar
            </button>
        </div>

        <table className="table table-bordered table-striped">
            <thead className="table-primary">
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Precio</th>
                    <th>Fecha</th>
                    <th>Ver Detalle</th>
                    <th>Editar</th>
                    <th>Eliminar</th>
                </tr>
            </thead>
            <tbody>
                {productos.length > 0 ? (
                    productos.map((producto) => (
                        <tr key={producto.id}>
                            <td>{producto.id}</td>
                            <td>{producto.nombre}</td>
                            <td>{producto.precio}</td>
                            <td>{producto.createAt}</td>
                            <td>
                                <button 
                                    className="btn btn-info " 
                                    onClick={() => navigate(`/productos/detalle/${producto.id}`)}>
                                    Ver Detalle
                                </button>
                            </td>
                            <td>
                                <button 
                                    className="btn btn-warning" 
                                    onClick={() => navigate(`/productos/formulario/${producto.id}`)}>
                                    Editar
                                </button>
                            </td>
                            <td>
                                <button 
                                    className="btn btn-danger" 
                                    onClick={() => handleDelete(producto.id)}>
                                    Eliminar
                                </button>
                            </td>
                        </tr>
                    ))
                ) : (
                    <tr>
                        <td colSpan="9" className="text-center">No hay productos disponibles</td>
                    </tr>
                )}
            </tbody>
        </table>

        <Pagination 
            currentPage={page} 
            totalPages={totalPages} 
            onPageChange={(newPage) => setPage(newPage)}
        />
    </div>
  )
}
