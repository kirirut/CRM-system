import React, { useState, useEffect } from 'react';
import axios from 'axios';
import OrderForm from './OrderForm';

const CustomerOrders = ({ customerId, isEditable }) => {
    const [orders, setOrders] = useState([]);
    const [editingOrder, setEditingOrder] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (customerId) {
            fetchOrders();
        }
    }, [customerId]);

    const fetchOrders = async () => {
        try {
            const response = await axios.get(`/api/customers/${customerId}/orders`);
            setOrders(response.data);
            setError(null);
        } catch (error) {
            console.error('Error fetching orders:', error);
            if (error.response?.status === 204) {
                setOrders([]);
                setError('No orders found for this user.');
            } else if (error.response?.status === 400) {
                setError('Invalid user ID.');
            } else {
                setError('Failed to load orders. Please try again.');
            }
        }
    };

    const handleDelete = async (orderId) => {
        try {
            await axios.delete(`/api/customers/${customerId}/orders/${orderId}`);
            setError(null);
            fetchOrders();
        } catch (error) {
            console.error('Error deleting order:', error);
            setError('Failed to delete order. Please try again.');
        }
    };

    const handleEdit = (order) => {
        setEditingOrder(order);
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        try {
            await axios.put(`/api/customers/${customerId}/orders/${editingOrder.id}`, editingOrder);
            setEditingOrder(null);
            setError(null);
            fetchOrders();
        } catch (error) {
            console.error('Error updating order:', error);
            setError('Failed to update order. Please try again.');
        }
    };

    return (
        <div className="mt-4">
            {isEditable && <OrderForm customerId={customerId} />}
            {error && <div className="alert alert-danger">{error}</div>}
            {editingOrder && isEditable && (
                <div className="card p-4 mb-4">
                    <h3>Edit Order</h3>
                    <form onSubmit={handleUpdate}>
                        <div className="mb-3">
                            <label className="form-label">Order Date</label>
                            <input
                                type="date"
                                className="form-control"
                                value={editingOrder.orderDate ? editingOrder.orderDate.split('T')[0] : ''}
                                onChange={(e) => setEditingOrder({ ...editingOrder, orderDate: e.target.value })}
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label className="form-label">Description</label>
                            <textarea
                                className="form-control"
                                value={editingOrder.description}
                                onChange={(e) => setEditingOrder({ ...editingOrder, description: e.target.value })}
                                required
                            />
                        </div>
                        <button type="submit" className="btn btn-primary">Update Order</button>
                        <button
                            type="button"
                            className="btn btn-secondary ms-2"
                            onClick={() => setEditingOrder(null)}
                        >
                            Cancel
                        </button>
                    </form>
                </div>
            )}
            {!customerId && <p className="alert alert-info">No user selected</p>}
            {customerId && orders.length === 0 && !error && <p className="alert alert-info">No orders found</p>}
            {orders.length > 0 && (
                <table className="table table-striped">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Order Date</th>
                        <th>Description</th>
                        {isEditable && <th>Actions</th>}
                    </tr>
                    </thead>
                    <tbody>
                    {orders.map(order => (
                        <tr key={order.id}>
                            <td>{order.id}</td>
                            <td>{order.orderDate ? order.orderDate.split('T')[0] : '-'}</td>
                            <td>{order.description}</td>
                            {isEditable && (
                                <td>
                                    <button
                                        className="btn btn-warning btn-sm me-2"
                                        onClick={() => handleEdit(order)}
                                    >
                                        Edit
                                    </button>
                                    <button
                                        className="btn btn-danger btn-sm"
                                        onClick={() => handleDelete(order.id)}
                                    >
                                        Delete
                                    </button>
                                </td>
                            )}
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default CustomerOrders;