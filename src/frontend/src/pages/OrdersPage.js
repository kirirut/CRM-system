import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Spinner, Dropdown } from 'react-bootstrap';
import { getCustomers, getOrdersByCustomer, getOrderById, createOrder, updateOrder, deleteOrder } from '../services/api';

const OrdersPage = () => {
    const [orders, setOrders] = useState([]);
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [editingOrderId, setEditingOrderId] = useState(null);
    const [selectedCustomerId, setSelectedCustomerId] = useState(null);
    const [formData, setFormData] = useState({ description: '' });
    const [error, setError] = useState('');

    useEffect(() => {
        fetchCustomers();
    }, []);

    const fetchCustomers = async () => {
        try {
            const data = await getCustomers();
            setCustomers(data || []);
        } catch (err) {
            setError('Не удалось загрузить клиентов');
        }
    };

    const fetchOrders = async (customerId) => {
        try {
            setLoading(true);
            setError('');
            console.log('Fetching orders for customerId:', customerId); // Логирование перед запросом
            const data = await getOrdersByCustomer(customerId);
            setOrders(data || []);
        } catch (err) {
            setError('Заказы для этого клиента не найдены');
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleCreateOrUpdate = async () => {
        try {
            setError('');
            const orderData = { ...formData };
            console.log('Sending order data:', { selectedCustomerId, orderData, editingOrderId }); // Логирование перед запросом
            if (editingOrderId) {
                await updateOrder(selectedCustomerId, editingOrderId, orderData);
            } else {
                await createOrder(selectedCustomerId, orderData);
            }
            resetModal();
            fetchOrders(selectedCustomerId);
        } catch (err) {
            setError('Ошибка при сохранении заказа');
        }
    };

    const handleEdit = async (orderId) => {
        try {
            setLoading(true);
            console.log('Fetching order for edit with orderId:', orderId); // Логирование перед запросом
            const order = await getOrderById(selectedCustomerId, orderId);
            setFormData({ description: order.description || '' });
            setEditingOrderId(orderId);
            setShowModal(true);
        } catch (err) {
            setError('Ошибка при загрузке данных заказа');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (orderId) => {
        try {
            console.log('Deleting order with orderId:', { selectedCustomerId, orderId }); // Логирование перед запросом
            await deleteOrder(selectedCustomerId, orderId);
            fetchOrders(selectedCustomerId);
        } catch (err) {
            setError('Ошибка при удалении заказа');
        }
    };

    const handleCustomerSelect =  (customerId) => {
        setSelectedCustomerId(customerId);
        if (customerId) {
            fetchOrders(customerId);
        } else {
            setOrders([]);
        }
    };

    const resetModal = () => {
        setFormData({ description: '' });
        setEditingOrderId(null);
        setShowModal(false);
        setError('');
    };

    return (
        <div>
            <div className={error ? "text-danger mb-3" : "d-none"}>{error}</div>
            <Dropdown onSelect={handleCustomerSelect} className="mb-3">
                <Dropdown.Toggle variant="secondary">
                    {selectedCustomerId ? customers.find(c => c.id === parseInt(selectedCustomerId))?.username : 'Выберите клиента'}
                </Dropdown.Toggle>
                <Dropdown.Menu>
                    <Dropdown.Item eventKey={null}>Выберите клиента</Dropdown.Item>
                    {customers.map((customer) => (
                        <Dropdown.Item key={customer.id} eventKey={customer.id}>
                            {customer.username}
                        </Dropdown.Item>
                    ))}
                </Dropdown.Menu>
            </Dropdown>

            {selectedCustomerId && (
                <>
                    <Button variant="primary" onClick={() => setShowModal(true)} className="mb-3">
                        Создать заказ
                    </Button>

                    {loading ? (
                        <Spinner animation="border" />
                    ) : (
                        <Table striped bordered hover responsive>
                            <thead>
                            <tr>
                                <th>Описание</th>
                                <th>Дата заказа</th>
                                <th>Действия</th>
                            </tr>
                            </thead>
                            <tbody>
                            {orders && orders.map((order) => (
                                <tr key={order.id}>
                                    <td>{order.description}</td>
                                    <td>{order.orderDate ? new Date(order.orderDate).toLocaleString() : '-'}</td>
                                    <td>
                                        <Button variant="warning" size="sm" onClick={() => handleEdit(order.id)} className="me-2">
                                            Редактировать
                                        </Button>
                                        <Button variant="danger" size="sm" onClick={() => handleDelete(order.id)}>
                                            Удалить
                                        </Button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </Table>
                    )}
                </>
            )}

            <Modal show={showModal} onHide={resetModal}>
                <Modal.Header closeButton>
                    <Modal.Title>{editingOrderId ? 'Редактировать заказ' : 'Создать заказ'}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form>
                        <Form.Group className="mb-3">
                            <Form.Label>Описание</Form.Label>
                            <Form.Control
                                type="text"
                                name="description"
                                value={formData.description}
                                onChange={handleInputChange}
                                placeholder="Введите описание заказа"
                                required
                            />
                        </Form.Group>
                    </Form>
                    <div className={error ? "text-danger" : "d-none"}>{error}</div>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={resetModal}>
                        Отмена
                    </Button>
                    <Button variant="primary" onClick={handleCreateOrUpdate}>
                        {editingOrderId ? 'Обновить' : 'Создать'}
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default OrdersPage;