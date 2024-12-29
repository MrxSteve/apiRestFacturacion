import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import ClienteList from "./components/ClienteList";
import ClienteDetail from "./components/ClienteDetail";
import ClienteForm from "./components/ClienteForm";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";

const App = () => {
  return (
    <Router>
      <Navbar />
      <div className="container mt-4">
        <Routes>
        <Route path="/" element={<ClienteList />} />
          <Route path="/clientes" element={<ClienteList />} />
          <Route path="/clientes/formulario" element={<ClienteForm />} />
          <Route path="/clientes/formulario/:id" element={<ClienteForm />} />
          <Route path="/clientes/:id" element={<ClienteDetail />} />
        </Routes>
      </div>
      <Footer />
    </Router>
  );
};

export default App;
