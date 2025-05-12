import React, { useState } from 'react';
import axios from 'axios';

const OrderForm = ({ customerId }) => {
    const [order, setOrder] = useState({
        orderDate: '',
        description: ''
    });
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post(`/api/customers/${customerId}/orders`, order);
            setOrder({ orderDate: '', description: '' });
            setError(null);
            window.location.reload();
        } catch (error) {
            console.error('Error creating order:', error);
            setError('Failed to create order. Please try again.');
        }
    };

    return (
        <div className="card p-4 mb-4">
            <h3>Add New Order</h3>
            {error && <div className="alert alert-danger">{error}</div>}
            {!customerId && <p className="alert alert-warning">No user selected</p>}
            {customerId && (
                <form onSubmit={handleSubmit}>
                    <div className="mb-3">
                        <label className="form-label">Order Date</label>
                        <input
                            type="date"
                            className="form-control"
                            value={order.orderDate}
                            onChange={(e) => setOrder({ ...order, orderDate: e.target.value })}
                            required
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Description</label>
                        <textarea
                            className="form-control"
                            value={order.description}
                            onChange={(e) => setOrder({ ...order, description: e.target.value })}
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary">Add Order</button>
                </form>
            )}
        </div>
    );
};

export default OrderForm;