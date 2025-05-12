import React, { useState, useEffect } from 'react';
import axios from 'axios';
import CustomerOrders from './CustomerOrders';

const UserDetails = ({ userId, isEditable }) => {
    const [user, setUser] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (userId) {
            fetchUser();
        }
    }, [userId]);

    const fetchUser = async () => {
        try {
            const response = await axios.get(`/api/customers/${userId}`);
            setUser(response.data);
            setError(null);
        } catch (error) {
            console.error('Error fetching user:', error);
            setError('Failed to load user details. Please try again.');
        }
    };

    if (!user) {
        return <p className="alert alert-info">Loading user details...</p>;
    }

    return (
        <div className="mt-4">
            <h3>User Information</h3>
            {error && <div className="alert alert-danger">{error}</div>}
            <div className="card p-4 mb-4">
                <p><strong>Username:</strong> {user.username}</p>
                <p><strong>Email:</strong> {user.email}</p>
                <p><strong>Phone:</strong> {user.phone || '-'}</p>
                <p><strong>Address:</strong> {user.address || '-'}</p>
                <p><strong>Company Name:</strong> {user.companyName || '-'}</p>
                <p><strong>Created At:</strong> {user.createdAt ? user.createdAt.split('T')[0] : '-'}</p>
                <p><strong>Updated At:</strong> {user.updatedAt ? user.updatedAt.split('T')[0] : '-'}</p>
            </div>
            <h3>Orders</h3>
            <CustomerOrders customerId={userId} isEditable={isEditable} />
        </div>
    );
};

export default UserDetails;