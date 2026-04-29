// ============================
// CONFIGURACIÓN GLOBAL
// ============================

const API_BASE_URL = '/stark-security/api';
const AUTH_HEADER = 'Authorization';

let currentUser = null;
let authToken = null;
let stompClient = null;
let charts = {};

// ============================
// UTILIDADES
// ============================

/**
 * Realiza una solicitud HTTP con autenticación
 */
async function apiCall(endpoint, method = 'GET', body = null) {
    const headers = {
        'Content-Type': 'application/json',
    };

    if (authToken) {
        // Usar Basic Auth con el token guardado
        headers['Authorization'] = 'Basic ' + authToken;
    }

    const options = {
        method,
        headers,
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(API_BASE_URL + endpoint, options);

        if (response.status === 401) {
            // No autorizado, volver al login
            logout();
            return null;
        }

        if (!response.ok) {
            throw new Error(`HTTP Error: ${response.status}`);
        }

        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        return null;
    } catch (error) {
        console.error('API Error:', error);
        return null;
    }
}

/**
 * Codifica las credenciales a Base64 para Basic Auth
 */
function encodeBasicAuth(username, password) {
    return btoa(`${username}:${password}`);
}

/**
 * Decodifica Basic Auth
 */
function decodeBasicAuth(token) {
    const decoded = atob(token);
    const [username, password] = decoded.split(':');
    return { username, password };
}

/**
 * Formatea fechas
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
    });
}

/**
 * Alterna entre formularios
 */
function toggleForm(event) {
    event.preventDefault();
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    loginForm.classList.toggle('active');
    registerForm.classList.toggle('active');
}

/**
 * Cambia de pestaña en el dashboard
 */
function switchTab(tabName) {
    event.preventDefault();

    // Ocultar todas las pestañas
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => tab.classList.remove('active'));

    // Desactivar todos los enlaces de navegación
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => link.classList.remove('active'));

    // Mostrar la pestaña seleccionada
    const selectedTab = document.getElementById(tabName + 'Tab');
    if (selectedTab) {
        selectedTab.classList.add('active');
    }

    // Activar el enlace de navegación
    event.target.classList.add('active');

    // Cargar datos según la pestaña
    if (tabName === 'sensors') {
        loadSensors();
        loadSensorReadings();
    } else if (tabName === 'alerts') {
        loadAlerts();
    } else if (tabName === 'access') {
        loadAccessLogs();
    }
}

/**
 * Muestra un mensaje de error temporal
 */
function showNotification(message, type = 'info', duration = 3000) {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, duration);
}

/**
 * Obtiene parámetro de URL
 */
function getQueryParam(param) {
    const searchParams = new URLSearchParams(window.location.search);
    return searchParams.get(param);
}

/**
 * Abre el modal de crear sensor
 */
function openSensorModal() {
    document.getElementById('sensorModal').classList.add('show');
}

/**
 * Cierra el modal de crear sensor
 */
function closeSensorModal() {
    document.getElementById('sensorModal').classList.remove('show');
}

// Cerrar modal si se hace clic fuera
window.onclick = function (event) {
    const modal = document.getElementById('sensorModal');
    if (event.target == modal) {
        modal.classList.remove('show');
    }
}

/**
 * Inicializa la aplicación
 */
document.addEventListener('DOMContentLoaded', function () {
    // Verificar si hay sesión guardada
    const savedAuth = localStorage.getItem('authToken');
    const savedUser = localStorage.getItem('currentUser');

    if (savedAuth && savedUser) {
        authToken = savedAuth;
        currentUser = JSON.parse(savedUser);
        showDashboard();
    } else {
        showAuthContainer();
    }
});

/**
 * Muestra el contenedor de autenticación
 */
function showAuthContainer() {
    document.getElementById('authContainer').style.display = 'flex';
    document.getElementById('dashboardContainer').style.display = 'none';
}

/**
 * Muestra el dashboard
 */
function showDashboard() {
    document.getElementById('authContainer').style.display = 'none';
    document.getElementById('dashboardContainer').style.display = 'flex';
    document.getElementById('userDisplay').textContent = '👤 ' + currentUser.username;

    // Cargar datos iniciales
    loadDashboardData();
    connectWebSocket();
}

/**
 * Logout del usuario
 */
function logout() {
    authToken = null;
    currentUser = null;
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    disconnectWebSocket();
    showAuthContainer();
    document.getElementById('loginForm').classList.add('active');
    document.getElementById('registerForm').classList.remove('active');
    // Limpiar formularios
    document.getElementById('loginFormElement').reset();
    document.getElementById('registerFormElement').reset();
}

// Event listener para logout
document.addEventListener('DOMContentLoaded', function () {
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
});

