// ============================
// AUTENTICACIÓN - LOGIN Y REGISTRO
// ============================

document.addEventListener('DOMContentLoaded', function () {
    const loginFormElement = document.getElementById('loginFormElement');
    if (loginFormElement) {
        loginFormElement.addEventListener('submit', handleLogin);
    }

    const registerFormElement = document.getElementById('registerFormElement');
    if (registerFormElement) {
        registerFormElement.addEventListener('submit', handleRegister);
    }

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    }
});

function goToDashboard(username) {
    const authContainer = document.getElementById('authContainer');
    const dashboardContainer = document.getElementById('dashboardContainer');
    const userDisplay = document.getElementById('userDisplay');

    if (authContainer) {
        authContainer.style.display = 'none';
    }

    if (dashboardContainer) {
        dashboardContainer.style.display = 'block';
    }

    if (userDisplay) {
        userDisplay.textContent = username;
    }

    if (typeof switchTab === 'function') {
        switchTab('overview');
    }

    if (typeof loadSensors === 'function') {
        loadSensors();
    }

    if (typeof loadAlerts === 'function') {
        loadAlerts();
    }

    if (typeof loadAccessLogs === 'function') {
        loadAccessLogs();
    }
}

async function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    const errorDiv = document.getElementById('loginError');
    const submitBtn = event.target.querySelector('button[type="submit"]');
    const loader = submitBtn.querySelector('.loader');

    submitBtn.querySelector('span:first-child').style.display = 'none';
    loader.style.display = 'block';
    errorDiv.textContent = '';

    try {
        const response = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        submitBtn.querySelector('span:first-child').style.display = 'inline';
        loader.style.display = 'none';

        if (!response.ok) {
            errorDiv.textContent = '❌ Usuario o contraseña inválidos';
            return;
        }

        const data = await response.json();

        localStorage.setItem('authToken', data.token || 'token_' + Date.now());
        localStorage.setItem('currentUser', JSON.stringify({
            username: username,
            roles: ['USER']
        }));

        goToDashboard(username);

    } catch (error) {
        submitBtn.querySelector('span:first-child').style.display = 'inline';
        loader.style.display = 'none';
        errorDiv.textContent = '⚠️ Error de conexión: ' + error.message;
        console.error('Login error:', error);
    }
}

async function handleRegister(event) {
    event.preventDefault();

    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const passwordConfirm = document.getElementById('regPasswordConfirm').value;
    const errorDiv = document.getElementById('registerError');
    const submitBtn = event.target.querySelector('button[type="submit"]');
    const loader = submitBtn.querySelector('.loader');

    errorDiv.style.color = '';
    errorDiv.textContent = '';

    if (password !== passwordConfirm) {
        errorDiv.textContent = '❌ Las contraseñas no coinciden';
        return;
    }

    if (password.length < 8) {
        errorDiv.textContent = '❌ La contraseña debe tener al menos 8 caracteres';
        return;
    }

    if (!isValidEmail(email)) {
        errorDiv.textContent = '❌ Email no válido';
        return;
    }

    submitBtn.querySelector('span:first-child').style.display = 'none';
    loader.style.display = 'block';

    try {
        const response = await fetch('http://localhost:8080/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                email: email,
                password: password
            })
        });

        submitBtn.querySelector('span:first-child').style.display = 'inline';
        loader.style.display = 'none';

        if (!response.ok) {
            errorDiv.textContent = '❌ Error al registrar. Código: ' + response.status;
            return;
        }

        localStorage.setItem('currentUser', JSON.stringify({
            username: username,
            roles: ['USER']
        }));

        errorDiv.style.color = 'var(--success-color)';
        errorDiv.textContent = '✅ Registro exitoso. Entrando al dashboard...';

        setTimeout(() => {
            goToDashboard(username);
        }, 700);

    } catch (error) {
        submitBtn.querySelector('span:first-child').style.display = 'inline';
        loader.style.display = 'none';
        errorDiv.textContent = '⚠️ Error en el registro: ' + error.message;
        console.error('Register error:', error);
    }
}

function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');

    const dashboardContainer = document.getElementById('dashboardContainer');
    const authContainer = document.getElementById('authContainer');

    if (dashboardContainer) {
        dashboardContainer.style.display = 'none';
    }

    if (authContainer) {
        authContainer.style.display = 'block';
    }
}

function isValidEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}