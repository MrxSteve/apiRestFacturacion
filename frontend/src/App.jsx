import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import ClienteList from "./components/ClienteList";
import ClienteDetail from "./components/ClienteDetail";
import ClienteForm from "./components/ClienteForm";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import { ProductoList } from "./components/Products/ProductoList";
import { ProductoDetail } from "./components/Products/ProductoDetail";
import { ProductoForm } from "./components/Products/ProductoForm";

const App = () => {
  return (
    <Router>
      <Navbar />
      <div className="container mt-4">
        <Routes>

          {/* Rutas de Cliente */}
          <Route path="/" element={<ClienteList />} />
          <Route path="/clientes" element={<ClienteList />} />
          <Route path="/clientes/formulario" element={<ClienteForm />} />
          <Route path="/clientes/formulario/:id" element={<ClienteForm />} />
          <Route path="/clientes/:id" element={<ClienteDetail />} />

          {/* Rutas de Producto */}
          <Route path="/productos" element={<ProductoList />} />
          <Route path="/productos/detalle/:id" element={<ProductoDetail />} />
          <Route path="/productos/formulario" element={<ProductoForm />}/>
          <Route path="/productos/formulario/:id" element={<ProductoForm />}/>
        </Routes>
      </div>
      <Footer />
    </Router>
  );
};

export default App;
