import React, { useState, useEffect } from 'react';
import axios from 'axios';

const CustomerList = ({ onSelectUser, currentUserId }) => {
    const [customers, setCustomers] = useState([]);
    const [editingCustomer, setEditingCustomer] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchCustomers();
    }, []);

    const fetchCustomers = async () => {
        try {
            const response = await axios.get('/api/customers');
            setCustomers(response.data);
            setError(null);
        } catch (error) {
            console.error('Error fetching customers:', error);
            setError('Failed to load users. Please try again.');
        }
    };

    const handleEdit = (customer) => {
        setEditingCustomer(customer);
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        try {
            await axios.put(`/api/customers/${editingCustomer.id}`, editingCustomer);
            setEditingCustomer(null);
            setError(null);
            fetchCustomers();
        } catch (error) {
            console.error('Error updating customer:', error);
            setError('Failed to update user. Please try again.');
        }
    };

    return (
        <div className="mt-4">
            <h3>Users</h3>
            {error && <div className="alert alert-danger">{error}</div>}
            {editingCustomer && (
                <form onSubmit={handleUpdate} className="mb-4">
                    <div className="mb-3">
                        <label className="form-label">Username</label>
                        <input
                            type="text"
                            className="form-control"
                            value={editingCustomer.username}
                            onChange={(e) => setEditingCustomer({ ...editingCustomer, username: e.target.value })}
                            required
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Email</label>
                        <input
                            type="email"
                            className="form-control"
                            value={editingCustomer.email}
                            onChange={(e) => setEditingCustomer({ ...editingCustomer, email: e.target.value })}
                            required
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Phone</label>
                        <input
                            type="text"
                            className="form-control"
                            value={editingCustomer.phone || ''}
                            onChange={(e) => setEditingCustomer({ ...editingCustomer, phone: e.target.value })}
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Address</label>
                        <input
                            type="text"
                            className="form-control"
                            value={editingCustomer.address || ''}
                            onChange={(e) => setEditingCustomer({ ...editingCustomer, address: e.target.value })}
                        />
                    </div>
                    <div className="mb-3">
                        <label className="form-label">Company Name</label>
                        <input
                            type="text"
                            className="form-control"
                            value={editingCustomer.companyName || ''}
                            onChange={(e) => setEditingCustomer({ ...editingCustomer, companyName: e.target.value })}
                        />
                    </div>
                    <button type="submit" className="btn btn-primary">Update</button>
                    <button
                        type="button"
                        className="btn btn-secondary ms-2"
                        onClick={() => setEditingCustomer(null)}
                    >
                        Cancel
                    </button>
                </form>
            )}
            <table className="table table-striped">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Company</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {customers.map(customer => (
                    <tr key={customer.id}>
                        <td>{customer.id}</td>
                        <td>{customer.username}</td>
                        <td>{customer.email}</td>
                        <td>{customer.phone || '-'}</td>
                        <td>{customer.companyName || '-'}</td>
                        <td>
                            <button
                                className="btn btn-primary btn-sm me-2"
                                onClick={() => onSelectUser(customer.id)}
                            >
                                View Details
                            </button>
                            {customer.id === currentUserId && (
                                <button
                                    className="btn btn-warning btn-sm"
                                    onClick={() => handleEdit(customer)}
                                >
                                    Edit
                                </button>
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default CustomerList;