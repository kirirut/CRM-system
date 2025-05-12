import React, { useState } from 'react';
import axios from 'axios';

const CustomerForm = () => {
    const [customer, setCustomer] = useState({
        username: '',
        email: '',
        phone: '',
        address: '',
        companyName: ''
    });
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post('/api/customers/bulk', [customer]);
            setCustomer({ username: '', email: '', phone: '', address: '', companyName: '' });
            setError(null);
            window.location.reload();
        } catch (error) {
            console.error('Error creating customer:', error);
            if (error.response?.status === 409) {
                setError('Username already exists. Please choose a different username.');
            } else {
                setError('Failed to create user. Please try again.');
            }
        }
    };

    return (
        <div className="card p-4 mb-4">
            <h3>Add New User</h3>
            {error && <div className="alert alert-danger">{error}</div>}
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label className="form-label">Username</label>
                    <input
                        type="text"
                        className="form-control"
                        value={customer.username}
                        onChange={(e) => setCustomer({ ...customer, username: e.target.value })}
                        required
                    />
                </div>
                <div className="mb-3">
                    <label className="form-label">Email</label>
                    <input
                        type="email"
                        className="form-control"
                        value={customer.email}
                        onChange={(e) => setCustomer({ ...customer, email: e.target.value })}
                        required
                    />
                </div>
                <div className="mb-3">
                    <label className="form-label">Phone</label>
                    <input
                        type="text"
                        className="form-control"
                        value={customer.phone}
                        onChange={(e) => setCustomer({ ...customer, phone: e.target.value })}
                    />
                </div>
                <div className="mb-3">
                    <label className="form-label">Address</label>
                    <input
                        type="text"
                        className="form-control"
                        value={customer.address}
                        onChange={(e) => setCustomer({ ...customer, address: e.target.value })}
                    />
                </div>
                <div className="mb-3">
                    <label className="form-label">Company Name</label>
                    <input
                        type="text"
                        className="form-control"
                        value={customer.companyName}
                        onChange={(e) => setCustomer({ ...customer, companyName: e.target.value })}
                    />
                </div>
                <button type="submit" className="btn btn-primary">Add User</button>
            </form>
        </div>
    );
};

export default CustomerForm;