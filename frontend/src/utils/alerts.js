import Swal from 'sweetalert2';

// Mensaje de éxito
export const showSuccessAlert = (title, text) => {
    Swal.fire({
        icon: 'success',
        title,
        text,
        confirmButtonText: 'OK',
    });
};

// Mensaje de error
export const showErrorAlert = (title, text) => {
    Swal.fire({
        icon: 'error',
        title,
        text,
        confirmButtonText: 'OK',
    });
};

// Mensaje de confirmación
export const showConfirmationAlert = async (title, text) => {
    const result = await Swal.fire({
        title,
        text,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar',
    });
    return result.isConfirmed;
};

