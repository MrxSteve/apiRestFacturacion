import React from "react";

const Pagination = ({ currentPage, totalPages, onPageChange }) => {
  return (
    <div className="d-flex justify-content-between align-items-center mt-3">
      <button
        className="btn btn-secondary"
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 0}
      >
        Anterior
      </button>

      <span>
        Página {currentPage + 1} de {totalPages}
      </span>

      <button
        className="btn btn-secondary"
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage + 1 === totalPages}
      >
        Siguiente
      </button>
    </div>
  );
};

export default Pagination;
