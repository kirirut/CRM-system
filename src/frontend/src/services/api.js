import axios from 'axios';

const getBaseUrl = () => {
    return 'http://localhost:8080'; // Адрес вашего бэкенда
};

const api = axios.create({
    baseURL: process.env.REACT_APP_API_URL || getBaseUrl(),
    headers: {
        'Content-Type': 'application/json',
        'Cache-Control': 'no-cache', // Отключаем кэширование
    },
});

// API для клиентов
export const getCustomers = () => api.get('/api/customers', { headers: { 'Cache-Control': 'no-cache' } }).then((res) => res.data);
export const getCustomerById = (id) => api.get(`/api/customers/${id}`, { headers: { 'Cache-Control': 'no-cache' } }).then((res) => res.data);
export const createCustomer = (customer) => api.post('/api/customers', customer).then((res) => res.data);
export const createBulkCustomers = (customers) => api.post('/api/customers/bulk', customers).then((res) => res.data);
export const updateCustomer = (id, customer) => api.put(`/api/customers/${id}`, customer).then((res) => res.data);
export const deleteCustomer = (id) => api.delete(`/api/customers/${id}`);

// API для заказов
export const getOrdersByCustomer = (customerId) => api.get(`/api/customers/${customerId}/orders`, { headers: { 'Cache-Control': 'no-cache' } }).then((res) => res.data);
export const getOrderById = (customerId, orderId) => api.get(`/api/customers/${customerId}/orders/${orderId}`, { headers: { 'Cache-Control': 'no-cache' } }).then((res) => res.data);
export const createOrder = (customerId, order) => api.post(`/api/customers/${customerId}/orders`, order).then((res) => res.data);
export const updateOrder = (customerId, orderId, order) => api.put(`/api/customers/${customerId}/orders/${orderId}`, order).then((res) => res.data);
export const deleteOrder = (customerId, orderId) => api.delete(`/api/customers/${customerId}/orders/${orderId}`);

// API для фильтрации заказов
export const getOrdersByCustomerName = (name) => api.get('/api/orders/filter/customer', { params: { name }, headers: { 'Cache-Control': 'no-cache' } }).then((res) => res.data);
export const getOrdersByDate = (date) => api.get('/api/orders/filter/date', { params: { date }, headers: { 'Cache-Control': 'no-cache' } }).then((res) => res.data);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API Error:', error);
        return Promise.reject(error);
    }
);

export default api;