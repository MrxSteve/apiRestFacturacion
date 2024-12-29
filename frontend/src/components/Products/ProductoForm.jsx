import React, {useState, useEffect} from 'react';
import {useParams, useNavigate} from 'react-router-dom';
import ProductoService from '../../services/ProductoService';
import { showSuccessAlert, showErrorAlert } from '../../utils/alerts';


export const ProductoForm = () => {
    const {id} = useParams();
    const navigate = useNavigate();

    const [producto, setProducto] = useState({
        nombre: '',
        precio: 0,
        createAt: '',
        foto: null,
    });

    const [fotoFile, setFotoFile] = useState(null);

    useEffect(() => {
        if (id) fetchProducto();
    }, [id]);

    const fetchProducto = async () => {
        try {
            const response = await ProductoService.getById(id);
            setProducto(response.data);
        } catch (error) {
            console.error('Error fetching producto:', error);
        }
    };

    const handleChange = (e) => {
        const {name, value} = e.target;
        setProducto({...producto, [name]: value});
    };

    const handleFileChange = (e) => {
        setFotoFile(e.target.files[0]);
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (id) {
                // Editar producto
                await ProductoService.update(id, producto);
                if (fotoFile) {
                    await ProductoService.uploadPhoto(id, fotoFile);
                }
                showSuccessAlert('Producto actualizado', 'El producto se ha actualizado correctamente.');
            } else {
                // Crear producto
                const response = await ProductoService.save(producto);
                if (fotoFile) {
                    await ProductoService.uploadPhoto(response.data.id, fotoFile);
                }
                showSuccessAlert('Producto creado', 'El producto se ha creado correctamente.');
            }
            navigate('/productos'); // Redirige al listado
        } catch (error) {
            showErrorAlert('Error al guardar', 'Ocurrió un error al intentar guardar el producto.');
            console.error('Error saving producto:', error);
        }
    };
    

  return (
    <form onSubmit={handleSubmit} className="cliente-form container mt-4 p-4 shadow rounded">
        <h2 className="text-primary text-center">
            {id ? 'Editar Producto' : 'Nuevo Producto'}
        </h2>
        <div className="mb-3">
            <label htmlFor="nombre" className="form-label">
                Nombre
            </label>
            <input
                type="text"
                id="nombre"
                name="nombre"
                value={producto.nombre}
                onChange={handleChange}
                className="form-control"
                required
            />
        </div>
        <div className="mb-3">
            <label htmlFor="precio" className="form-label">
                Precio
            </label>
            <input
                type="number"
                id="precio"
                name="precio"
                value={producto.precio}
                onChange={handleChange}
                className="form-control"
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
                value={producto.createAt}
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
          onClick={() => navigate("/productos")}
        >
          Cancelar
        </button>
      </div>
    </form>
  )
}
