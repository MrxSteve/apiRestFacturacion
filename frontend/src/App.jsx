import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import ClienteList from './components/ClienteList';
import ClienteForm from './components/ClienteForm';
import ClienteDetail from './components/ClienteDetail';
import './App.css'

function App() {
  return (
    <Router>
        <Routes>
            <Route path="/" element={<ClienteList />} />
            <Route path="/clientes/nuevo" element={<ClienteForm />} />
            <Route path="/clientes/:id" element={<ClienteForm />} />
            <Route path="/clientes/detalle/:id" element={<ClienteDetail />} />
        </Routes>
    </Router>
);
}

export default App
