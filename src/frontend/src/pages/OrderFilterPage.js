import React, { useState } from 'react';
import { Table, Button, Form, Spinner, Alert } from 'react-bootstrap';
import { getOrdersByCustomerName, getOrdersByDate } from '../services/api';

const OrderFilterPage = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [filterType, setFilterType] = useState('customer'); // 'customer' или 'date'
    const [customerName, setCustomerName] = useState('');
    const [orderDate, setOrderDate] = useState('');

    const handleFilter = async () => {
        try {
            setLoading(true);
            setError('');
            let data;
            if (filterType === 'customer') {
                if (!customerName.trim()) {
                    setError('Введите имя пользователя для фильтрации');
                    return;
                }
                data = await getOrdersByCustomerName(customerName);
            } else {
                if (!orderDate) {
                    setError('Выберите дату для фильтрации');
                    return;
                }
                data = await getOrdersByDate(orderDate);
            }
            setOrders(data || []);
        } catch (err) {
            setError('Не удалось загрузить заказы');
        } finally {
            setLoading(false);
        }
    };

    const handleClear = () => {
        setOrders([]);
        setCustomerName('');
        setOrderDate('');
        setError('');
    };

    return (
        <div>
            {error && <Alert variant="danger">{error}</Alert>}
            <Form>
                <Form.Group className="mb-3">
                    <Form.Label>Тип фильтра</Form.Label>
                    <Form.Select value={filterType} onChange={(e) => setFilterType(e.target.value)}>
                        <option value="customer">По имени клиента</option>
                        <option value="date">По дате</option>
                    </Form.Select>
                </Form.Group>

                {filterType === 'customer' ? (
                    <Form.Group className="mb-3">
                        <Form.Label>Имя пользователя</Form.Label>
                        <Form.Control
                            type="text"
                            value={customerName}
                            onChange={(e) => setCustomerName(e.target.value)}
                            placeholder="Введите имя пользователя"
                        />
                    </Form.Group>
                ) : (
                    <Form.Group className="mb-3">
                        <Form.Label>Дата заказа</Form.Label>
                        <Form.Control
                            type="date"
                            value={orderDate}
                            onChange={(e) => setOrderDate(e.target.value)}
                        />
                    </Form.Group>
                )}

                <Button variant="primary" onClick={handleFilter} className="me-2">
                    Фильтровать
                </Button>
                <Button variant="secondary" onClick={handleClear}>
                    Очистить
                </Button>
            </Form>

            {loading ? (
                <Spinner animation="border" className="mt-3" />
            ) : (
                orders.length > 0 && (
                    <Table striped bordered hover responsive className="mt-3">
                        <thead>
                        <tr>
                            <th>Имя клиента</th>
                            <th>Дата заказа</th>
                            <th>Описание</th>
                        </tr>
                        </thead>
                        <tbody>
                        {orders.map((order) => (
                            <tr key={order.id}>
                                <td>{order.customer?.username || '-'}</td>
                                <td>{order.orderDate ? new Date(order.orderDate).toLocaleString() : '-'}</td>
                                <td>{order.description}</td>
                            </tr>
                        ))}
                        </tbody>
                    </Table>
                )
            )}
        </div>
    );
};

export default OrderFilterPage;