import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Spinner, Alert } from 'react-bootstrap';
import { getCustomers, getCustomerById, createCustomer, updateCustomer, deleteCustomer } from '../services/api';

const CustomersPage = () => {
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [editingCustomerId, setEditingCustomerId] = useState(null);
    const [formData, setFormData] = useState({ username: '', password: '', email: '', phone: '', address: '', companyName: '' });
    const [error, setError] = useState('');
    const [formErrors, setFormErrors] = useState({});

    useEffect(() => {
        fetchCustomers();
    }, []);

    const fetchCustomers = async () => {
        try {
            setLoading(true);
            const data = await getCustomers();
            setCustomers(data || []);
        } catch (err) {
            setError(err.response?.data?.message || 'Не удалось загрузить клиентов');
        } finally {
            setLoading(false);
        }
    };

    const validateForm = () => {
        const errors = {};
        if (!formData.username.trim()) errors.username = 'Имя пользователя обязательно';
        else if (formData.username.length < 3 || formData.username.length > 20) errors.username = 'Имя пользователя должно быть от 3 до 20 символов';
        if (!formData.password.trim() && !editingCustomerId) errors.password = 'Пароль обязателен';
        else if (formData.password && formData.password.length < 6) errors.password = 'Пароль должен быть минимум 6 символов';
        if (!formData.email.trim()) errors.email = 'Email обязателен';
        else if (!/\S+@\S+\.\S+/.test(formData.email)) errors.email = 'Неверный формат email';
        setFormErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
        setFormErrors({ ...formErrors, [name]: '' });
    };

    const handleCreateOrUpdate = async () => {
        if (!validateForm()) return;

        const confirmMessage = editingCustomerId
            ? 'Вы уверены, что хотите обновить этого клиента?'
            : 'Вы уверены, что хотите создать нового клиента?';
        if (!window.confirm(confirmMessage)) return;

        try {
            setError('');
            const customerData = { ...formData };
            if (editingCustomerId) {
                await updateCustomer(editingCustomerId, customerData);
                alert('Клиент успешно обновлен');
            } else {
                await createCustomer(customerData);
                alert('Клиент успешно создан');
            }
            resetModal();
            fetchCustomers();
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка при сохранении клиента');
        }
    };

    const handleEdit = async (id) => {
        try {
            setLoading(true);
            const customer = await getCustomerById(id);
            setFormData({
                username: customer.username || '',
                password: '', // Не заполняем пароль при редактировании для безопасности
                email: customer.email || '',
                phone: customer.phone || '',
                address: customer.address || '',
                companyName: customer.companyName || ''
            });
            setEditingCustomerId(id);
            setShowModal(true);
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка при загрузке данных клиента');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Вы уверены, что хотите удалить этого клиента?')) {
            try {
                await deleteCustomer(id);
                alert('Клиент успешно удален');
                fetchCustomers();
            } catch (err) {
                setError(err.response?.data?.message || 'Ошибка при удалении клиента');
            }
        }
    };

    const resetModal = () => {
        setFormData({ username: '', password: '', email: '', phone: '', address: '', companyName: '' });
        setEditingCustomerId(null);
        setShowModal(false);
        setError('');
        setFormErrors({});
    };

    return (
        <div className="py-3">
            <h2 className="mb-4">Клиенты</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            <Button variant="primary" onClick={() => setShowModal(true)} className="mb-3">
                Создать клиента
            </Button>

            {loading ? (
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Загрузка...</span>
                </Spinner>
            ) : (
                <Table striped bordered hover responsive>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Имя пользователя</th>
                        <th>Email</th>
                        <th>Телефон</th>
                        <th>Адрес</th>
                        <th>Название компании</th>
                        <th>Действия</th>
                    </tr>
                    </thead>
                    <tbody>
                    {customers.map((customer) => (
                        <tr key={customer.id}>
                            <td>{customer.id}</td>
                            <td>{customer.username}</td>
                            <td>{customer.email}</td>
                            <td>{customer.phone}</td>
                            <td>{customer.address}</td>
                            <td>{customer.companyName}</td>
                            <td>
                                <Button
                                    variant="warning"
                                    size="sm"
                                    onClick={() => handleEdit(customer.id)}
                                    className="me-2"
                                >
                                    Редактировать
                                </Button>
                                <Button
                                    variant="danger"
                                    size="sm"
                                    onClick={() => handleDelete(customer.id)}
                                >
                                    Удалить
                                </Button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            )}

            <Modal show={showModal} onHide={resetModal} centered>
                <Modal.Header closeButton>
                    <Modal.Title>{editingCustomerId ? 'Редактировать клиента' : 'Создать клиента'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3">
                            <Form.Label>Имя пользователя</Form.Label>
                            <Form.Control
                                type="text"
                                name="username"
                                value={formData.username}
                                onChange={handleInputChange}
                                placeholder="Введите имя пользователя"
                                isInvalid={!!formErrors.username}
                            />
                            <Form.Control.Feedback type="invalid">{formErrors.username}</Form.Control.Feedback>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Пароль {editingCustomerId && '(оставьте пустым, чтобы не менять)'}</Form.Label>
                            <Form.Control
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleInputChange}
                                placeholder="Введите пароль"
                                isInvalid={!!formErrors.password}
                            />
                            <Form.Control.Feedback type="invalid">{formErrors.password}</Form.Control.Feedback>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Email</Form.Label>
                            <Form.Control
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleInputChange}
                                placeholder="Введите email"
                                isInvalid={!!formErrors.email}
                            />
                            <Form.Control.Feedback type="invalid">{formErrors.email}</Form.Control.Feedback>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Телефон</Form.Label>
                            <Form.Control
                                type="text"
                                name="phone"
                                value={formData.phone}
                                onChange={handleInputChange}
                                placeholder="Введите телефон (опционально)"
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Адрес</Form.Label>
                            <Form.Control
                                type="text"
                                name="address"
                                value={formData.address}
                                onChange={handleInputChange}
                                placeholder="Введите адрес (опционально)"
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Название компании</Form.Label>
                            <Form.Control
                                type="text"
                                name="companyName"
                                value={formData.companyName}
                                onChange={handleInputChange}
                                placeholder="Введите название компании (опционально)"
                            />
                        </Form.Group>
                    </Form>
                    {error && <Alert variant="danger">{error}</Alert>}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={resetModal}>
                        Отмена
                    </Button>
                    <Button variant="primary" onClick={handleCreateOrUpdate}>
                        {editingCustomerId ? 'Обновить' : 'Создать'}
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default CustomersPage;