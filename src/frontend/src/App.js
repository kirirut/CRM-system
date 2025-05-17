import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import CustomersPage from './pages/CustomersPage';
import OrdersPage from './pages/OrdersPage';
import OrderFilterPage from './pages/OrderFilterPage';
import { Navbar, Nav, Container } from 'react-bootstrap';

function App() {
    return (
        <Router>
            <div style={{ minHeight: '100vh', backgroundColor: '#f8f9fa' }}>
                <Navbar bg="dark" variant="dark" expand="lg" sticky="top">
                    <Container>
                        <Navbar.Brand as={Link} to="/">ShopSphere</Navbar.Brand>
                        <Navbar.Toggle aria-controls="basic-navbar-nav" />
                        <Navbar.Collapse id="basic-navbar-nav">
                            <Nav className="me-auto">
                                <Nav.Link as={Link} to="/">Orders</Nav.Link>
                                <Nav.Link as={Link} to="/customers">Customers</Nav.Link>
                                <Nav.Link as={Link} to="/orders/filter">Order Filter</Nav.Link>
                            </Nav>
                            <Nav>
                                <Nav.Link disabled>Developed by: Your Name</Nav.Link>
                            </Nav>
                        </Navbar.Collapse>
                    </Container>
                </Navbar>

                <Container className="py-4">
                    <Routes>
                        <Route path="/" element={<OrdersPage />} />
                        <Route path="/customers" element={<CustomersPage />} />
                        <Route path="/orders/filter" element={<OrderFilterPage />} />
                    </Routes>
                </Container>

                <footer className="bg-light text-center py-3 mt-4">
                    <p className="mb-0">
                        © {new Date().getFullYear()} ShopSphere - E-Commerce Platform
                    </p>
                </footer>
            </div>
        </Router>
    );
}

export default App;