import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Spinner, Row, Col } from 'react-bootstrap';

import { getCustomers, getCustomerById, createBulkCustomers, updateCustomer, deleteCustomer, getOrdersByCustomer } from '../services/api';

const CustomersPage = () => {
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [editingCustomerId, setEditingCustomerId] = useState(null);
    const [formsData, setFormsData] = useState([{ username: '', password: '', email: '', phone: '', address: '', companyName: '' }]);
    const [error, setError] = useState('');
    const [formErrors, setFormErrors] = useState({});
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [customerToDelete, setCustomerToDelete] = useState(null);
    const [showInfoModal, setShowInfoModal] = useState(false);
    const [customerOrders, setCustomerOrders] = useState([]);
    const [selectedCustomerIdForInfo, setSelectedCustomerIdForInfo] = useState(null);

    useEffect(() => {
        fetchCustomers();
    }, []);

    const fetchCustomers = async () => {
        try {
            setLoading(true);
            setError('');
            const data = await getCustomers();
            setCustomers(data || []);
        } catch (err) {
            setError(err.response?.data?.message || 'Не удалось загрузить клиентов');
        } finally {
            setLoading(false);
        }
    };

    const fetchCustomerOrders = async (customerId) => {
        try {
            setLoading(true);
            setError('');
            const data = await getOrdersByCustomer(customerId);
            setCustomerOrders(data || []);
        } catch (err) {
            setError(err.response?.data?.message || 'Не удалось загрузить заказы клиента');
        } finally {
            setLoading(false);
        }
    };

    const validateForm = (index) => {
        const errors = {};
        const data = formsData[index];
        if (!data.username.trim()) errors[`username-${index}`] = 'Имя пользователя обязательно';
        else if (data.username.length < 3 || data.username.length > 20) errors[`username-${index}`] = 'Имя пользователя должно быть от 3 до 20 символов';
        if (!data.password.trim() && !editingCustomerId) errors[`password-${index}`] = 'Пароль обязателен';
        else if (data.password && data.password.length < 6) errors[`password-${index}`] = 'Пароль должен быть минимум 6 символов';
        if (!data.email.trim()) errors[`email-${index}`] = 'Email обязателен';
        else if (!/\S+@\S+\.\S+/.test(data.email)) errors[`email-${index}`] = 'Неверный формат email';
        const currentErrors = Object.keys(errors).reduce((acc, key) => ({ ...acc, [key]: errors[key] }), {});
        setFormErrors(prev => ({ ...prev, ...currentErrors }));
        console.log(`Validation for index ${index}:`, currentErrors);
        return Object.keys(currentErrors).length === 0;
    };

    const handleInputChange = (index) => (e) => {
        const { name, value } = e.target;
        const newFormsData = [...formsData];
        newFormsData[index] = { ...newFormsData[index], [name]: value };
        setFormsData(newFormsData);
        setFormErrors(prev => {
            const newErrors = { ...prev };
            delete newErrors[`${name}-${index}`];
            return newErrors;
        });
    };

    const handleAddForm = () => {
        setFormsData([...formsData, { username: '', password: '', email: '', phone: '', address: '', companyName: '' }]);
    };

    const handleRemoveForm = (index) => {
        const newFormsData = formsData.filter((_, i) => i !== index);
        setFormsData(newFormsData);
        const newFormErrors = { ...formErrors };
        Object.keys(newFormErrors).forEach(key => {
            if (key.includes(`-${index}`)) delete newFormErrors[key];
        });
        setFormErrors(newFormErrors);
    };

    const handleCreateOrUpdate = async () => {
        console.log('Create button clicked');
        const validationResults = formsData.map((_, index) => validateForm(index));
        console.log('Validation results:', validationResults);
        const isValid = validationResults.every(result => result);
        console.log('All valid:', isValid);
        if (!isValid) {
            setError('Проверьте заполнение всех форм');
            return;
        }

        try {
            setError('');
            const customerData = formsData.map(data => ({ ...data }));
            console.log('Sending data to API:', customerData);
            if (editingCustomerId) {
                await updateCustomer(editingCustomerId, customerData[0]);
            } else {
                await createBulkCustomers(customerData);
            }
            resetModal();
            fetchCustomers();
        } catch (err) {
            console.error('API Error:', err);
            setError(err.response?.data?.message || 'Ошибка при сохранении клиента(ов)');
        }
    };

    const handleEdit = async (id) => {
        try {
            setLoading(true);
            setError('');
            const customer = await getCustomerById(id);
            setFormsData([{
                username: customer.username || '',
                password: '',
                email: customer.email || '',
                phone: customer.phone || '',
                address: customer.address || '',
                companyName: customer.companyName || ''
            }]);
            setEditingCustomerId(id);
            setShowModal(true);
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка при загрузке данных клиента');
        } finally {
            setLoading(false);
        }
    };

    const handleDeletePrompt = (id) => {
        setCustomerToDelete(id);
        setShowDeleteModal(true);
    };

    const handleDeleteConfirm = async () => {
        try {
            setError('');
            await deleteCustomer(customerToDelete);
            setShowDeleteModal(false);
            setCustomerToDelete(null);
            fetchCustomers();
        } catch (err) {
            setError(err.response?.data?.message || 'Ошибка при удалении клиента');
            setShowDeleteModal(false);
        }
    };

    const handleInfoPrompt = async (id) => {
        setSelectedCustomerIdForInfo(id);
        await fetchCustomerOrders(id);
        setShowInfoModal(true);
    };

    const resetModal = () => {
        setFormsData([{ username: '', password: '', email: '', phone: '', address: '', companyName: '' }]);
        setEditingCustomerId(null);
        setShowModal(false);
        setError('');
        setFormErrors({});
    };

    const resetDeleteModal = () => {
        setShowDeleteModal(false);
        setCustomerToDelete(null);
        setError('');
    };

    const resetInfoModal = () => {
        setShowInfoModal(false);
        setSelectedCustomerIdForInfo(null);
        setCustomerOrders([]);
        setError('');
    };

    return (
        <div className="py-3">
            <div className={error ? "text-danger mb-3" : "d-none"}>{error}</div>
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
                            <td>{customer.username}</td>
                            <td>{customer.email}</td>
                            <td>{customer.phone}</td>
                            <td>{customer.address}</td>
                            <td>{customer.companyName}</td>
                            <td>
                                <div className="d-flex gap-2">
                                    <Button
                                        variant="warning"
                                        size="sm"
                                        onClick={() => handleEdit(customer.id)}
                                    >
                                        Редактировать
                                    </Button>
                                    <Button
                                        variant="danger"
                                        size="sm"
                                        onClick={() => handleDeletePrompt(customer.id)}
                                    >
                                        Удалить
                                    </Button>
                                    <Button
                                        variant="info"
                                        size="sm"
                                        onClick={() => handleInfoPrompt(customer.id)}
                                    >
                                        Информация
                                    </Button>
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </Table>
            )}

            <Modal show={showModal} onHide={resetModal} centered>
                <Modal.Header closeButton>
                    <Modal.Title>{editingCustomerId ? 'Редактировать клиента' : 'Создать клиента(ов)'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {formsData.map((data, index) => (
                        <div key={index} className="mb-4 border p-3 rounded">
                            <Row>
                                <Col md={11}>
                                    <Form>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Имя пользователя</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="username"
                                                value={data.username}
                                                onChange={handleInputChange(index)}
                                                placeholder="Введите имя пользователя"
                                                isInvalid={!!formErrors[`username-${index}`]}
                                            />
                                            <Form.Control.Feedback type="invalid">{formErrors[`username-${index}`]}</Form.Control.Feedback>
                                        </Form.Group>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Пароль {editingCustomerId && '(оставьте пустым, чтобы не менять)'}</Form.Label>
                                            <Form.Control
                                                type="password"
                                                name="password"
                                                value={data.password}
                                                onChange={handleInputChange(index)}
                                                placeholder="Введите пароль"
                                                isInvalid={!!formErrors[`password-${index}`]}
                                            />
                                            <Form.Control.Feedback type="invalid">{formErrors[`password-${index}`]}</Form.Control.Feedback>
                                        </Form.Group>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Email</Form.Label>
                                            <Form.Control
                                                type="email"
                                                name="email"
                                                value={data.email}
                                                onChange={handleInputChange(index)}
                                                placeholder="Введите email"
                                                isInvalid={!!formErrors[`email-${index}`]}
                                            />
                                            <Form.Control.Feedback type="invalid">{formErrors[`email-${index}`]}</Form.Control.Feedback>
                                        </Form.Group>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Телефон</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="phone"
                                                value={data.phone}
                                                onChange={handleInputChange(index)}
                                                placeholder="Введите телефон (опционально)"
                                            />
                                        </Form.Group>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Адрес</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="address"
                                                value={data.address}
                                                onChange={handleInputChange(index)}
                                                placeholder="Введите адрес (опционально)"
                                            />
                                        </Form.Group>
                                        <Form.Group className="mb-3">
                                            <Form.Label>Название компании</Form.Label>
                                            <Form.Control
                                                type="text"
                                                name="companyName"
                                                value={data.companyName}
                                                onChange={handleInputChange(index)}
                                                placeholder="Введите название компании (опционально)"
                                            />
                                        </Form.Group>
                                    </Form>
                                </Col>
                                <Col md={1} className="d-flex align-items-center">
                                    {!editingCustomerId && index > 0 && (
                                        <Button variant="danger" size="sm" onClick={() => handleRemoveForm(index)} className="mt-2">
                                            -
                                        </Button>
                                    )}
                                </Col>
                            </Row>
                        </div>
                    ))}
                    {!editingCustomerId && (
                        <Button variant="success" onClick={handleAddForm} className="mb-3">
                            +
                        </Button>
                    )}
                    <div className={error ? "text-danger" : "d-none"}>{error}</div>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={resetModal}>
                        Отмена
                    </Button>
                    <Button variant="primary" onClick={() => { console.log('Create button clicked'); handleCreateOrUpdate(); }}>
                        {editingCustomerId ? 'Обновить' : 'Создать'}
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={showDeleteModal} onHide={resetDeleteModal} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Подтверждение удаления</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Вы уверены, что хотите удалить этого клиента?
                    <div className={error ? "text-danger mt-2" : "d-none"}>{error}</div>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={resetDeleteModal}>
                        Отмена
                    </Button>
                    <Button variant="danger" onClick={handleDeleteConfirm}>
                        Удалить
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={showInfoModal} onHide={resetInfoModal} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Заказы клиента</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {loading ? (
                        <Spinner animation="border" role="status">
                            <span className="visually-hidden">Загрузка...</span>
                        </Spinner>
                    ) : (
                        <Table striped bordered hover responsive>
                            <thead>
                            <tr>
                                <th>Описание</th>
                                <th>Дата заказа</th>
                            </tr>
                            </thead>
                            <tbody>
                            {customerOrders.map((order) => (
                                <tr key={order.id}>
                                    <td>{order.description}</td>
                                    <td>{order.orderDate ? new Date(order.orderDate).toLocaleString() : '-'}</td>
                                </tr>
                            ))}
                            </tbody>
                        </Table>
                    )}
                    <div className={error ? "text-danger mt-2" : "d-none"}>{error}</div>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={resetInfoModal}>
                        Закрыть
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default CustomersPage;