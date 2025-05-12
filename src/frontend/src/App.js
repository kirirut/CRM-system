import React, { useState } from 'react';
import CustomerList from './components/CustomerList';
import CustomerOrders from './components/CustomerOrders';
import CustomerForm from './components/CustomerForm';
import UserDetails from './components/UserDetails';

const App = () => {
    const [activeSection, setActiveSection] = useState('my-orders');
    const [selectedUserId, setSelectedUserId] = useState(null);
    const currentUserId = 1; // Replace with actual authentication logic

    const handleSelectUser = (userId) => {
        setSelectedUserId(userId);
        setActiveSection('user-details');
    };

    return (
        <div className="container-fluid">
            <div className="row">
                <nav className="col-md-3 col-lg-2 d-md-block bg-light sidebar">
                    <div className="position-sticky pt-3">
                        <h4 className="px-3">Menu</h4>
                        <ul className="nav flex-column">
                            <li className="nav-item">
                                <button
                                    className={`nav-link ${activeSection === 'my-orders' ? 'active' : ''}`}
                                    onClick={() => setActiveSection('my-orders')}
                                >
                                    My Orders
                                </button>
                            </li>
                            <li className="nav-item">
                                <button
                                    className={`nav-link ${activeSection === 'all-users' ? 'active' : ''}`}
                                    onClick={() => setActiveSection('all-users')}
                                >
                                    All Users
                                </button>
                            </li>
                        </ul>
                    </div>
                </nav>
                <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                    <div className="pt-3">
                        {activeSection === 'my-orders' && (
                            <div>
                                <h2>My Orders</h2>
                                <CustomerOrders customerId={currentUserId} isEditable={true} />
                            </div>
                        )}
                        {activeSection === 'all-users' && (
                            <div>
                                <h2>All Users</h2>
                                <CustomerForm />
                                <CustomerList onSelectUser={handleSelectUser} currentUserId={currentUserId} />
                            </div>
                        )}
                        {activeSection === 'user-details' && selectedUserId && (
                            <div>
                                <h2>User Details</h2>
                                <UserDetails
                                    userId={selectedUserId}
                                    isEditable={selectedUserId === currentUserId}
                                />
                            </div>
                        )}
                    </div>
                </main>
            </div>
        </div>
    );
};

export default App;