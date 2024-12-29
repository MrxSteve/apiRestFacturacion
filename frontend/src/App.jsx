import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import ClienteList from "./components/ClienteList";
import ClienteDetail from "./components/ClienteDetail";
import ClienteForm from "./components/ClienteForm";

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<ClienteList />} />
        <Route path="/clientes/:id" element={<ClienteDetail />} />
        <Route path="/clientes/formulario/:id?" element={<ClienteForm />} />
      </Routes>
    </Router>
  );
};

export default App;
