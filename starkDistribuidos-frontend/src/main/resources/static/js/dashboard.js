// ============================
// DASHBOARD - DATOS Y GRÁFICOS
// ============================

/**
 * Carga los datos iniciales del dashboard
 */
async function loadDashboardData() {
    await Promise.all([
        loadSensorStats(),
        loadAlertStats(),
        initializeCharts(),
    ]);
}

/**
 * Carga estadísticas de sensores
 */
async function loadSensorStats() {
    const data = await apiCall('/sensors');
    if (data && Array.isArray(data)) {
        const activeSensors = data.filter(s => s.status === 'ACTIVE').length;
        document.getElementById('activeSensorsCount').textContent = data.length;
        updateSensorChart(data);
    }
}

/**
 * Carga estadísticas de alertas
 */
async function loadAlertStats() {
    const data = await apiCall('/alerts');
    if (data && Array.isArray(data)) {
        const activeAlerts = data.filter(a => !a.acknowledged).length;
        const criticalAlerts = data.filter(a => a.level === 'CRITICAL' && !a.acknowledged).length;

        document.getElementById('activeAlertsCount').textContent = activeAlerts;
        document.getElementById('criticalAlertsCount').textContent = criticalAlerts;

        updateAlertChart(data);
    }
}

/**
 * Inicializa los gráficos
 */
function initializeCharts() {
    // Gráfico de sensores
    const sensorCtx = document.getElementById('sensorChart');
    if (sensorCtx) {
        charts.sensor = new Chart(sensorCtx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [
                    {
                        label: 'Sensores Activos',
                        data: [],
                        borderColor: '#FF6B35',
                        backgroundColor: 'rgba(255, 107, 53, 0.1)',
                        tension: 0.3,
                        fill: true,
                    },
                ],
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        labels: {
                            color: '#ECF0F1',
                        },
                    },
                },
                scales: {
                    y: {
                        ticks: {
                            color: '#BDC3C7',
                        },
                        grid: {
                            color: 'rgba(44, 62, 80, 0.5)',
                        },
                    },
                    x: {
                        ticks: {
                            color: '#BDC3C7',
                        },
                        grid: {
                            color: 'rgba(44, 62, 80, 0.5)',
                        },
                    },
                },
            },
        });
    }

    // Gráfico de alertas
    const alertCtx = document.getElementById('alertChart');
    if (alertCtx) {
        charts.alert = new Chart(alertCtx, {
            type: 'bar',
            data: {
                labels: ['Críticas', 'Advertencias', 'Info'],
                datasets: [
                    {
                        label: 'Alertas por Nivel',
                        data: [0, 0, 0],
                        backgroundColor: ['#E74C3C', '#F39C12', '#3498DB'],
                    },
                ],
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                indexAxis: 'y',
                plugins: {
                    legend: {
                        labels: {
                            color: '#ECF0F1',
                        },
                    },
                },
                scales: {
                    x: {
                        ticks: {
                            color: '#BDC3C7',
                        },
                        grid: {
                            color: 'rgba(44, 62, 80, 0.5)',
                        },
                    },
                    y: {
                        ticks: {
                            color: '#BDC3C7',
                        },
                        grid: {
                            display: false,
                        },
                    },
                },
            },
        });
    }
}

/**
 * Actualiza el gráfico de sensores
 */
function updateSensorChart(sensors) {
    if (!charts.sensor) return;

    const hours = [];
    const sensorCounts = [];

    for (let i = 23; i >= 0; i--) {
        const hour = new Date();
        hour.setHours(hour.getHours() - i);
        hours.push(hour.getHours() + ':00');
        // Simular datos
        sensorCounts.push(Math.floor(Math.random() * sensors.length) + 1);
    }

    charts.sensor.data.labels = hours;
    charts.sensor.data.datasets[0].data = sensorCounts;
    charts.sensor.update();
}

/**
 * Actualiza el gráfico de alertas
 */
function updateAlertChart(alerts) {
    if (!charts.alert) return;

    const critical = alerts.filter(a => a.level === 'CRITICAL').length;
    const warning = alerts.filter(a => a.level === 'WARNING').length;
    const info = alerts.filter(a => a.level === 'INFO').length;

    charts.alert.data.datasets[0].data = [critical, warning, info];
    charts.alert.update();
}

/**
 * Carga la lista de sensores
 */
async function loadSensors() {
    const data = await apiCall('/sensors');
    const sensorsGrid = document.getElementById('sensorsGrid');

    if (!data || !Array.isArray(data)) {
        sensorsGrid.innerHTML = '<p class="placeholder">No se pudieron cargar los sensores</p>';
        return;
    }

    if (data.length === 0) {
        sensorsGrid.innerHTML = '<p class="placeholder">No hay sensores registrados</p>';
        return;
    }

    sensorsGrid.innerHTML = data
        .map(sensor => `
        <div class="sensor-card">
            <div class="sensor-header">
                <div>
                    <div class="sensor-name">${sensor.name}</div>
                    <div class="sensor-type">${sensor.type}</div>
                </div>
            </div>
            <div class="sensor-info">
                <div class="sensor-info-row">
                    <span class="sensor-info-label">Ubicación:</span>
                    <span class="sensor-value">${sensor.location || 'N/A'}</span>
                </div>
                <div class="sensor-info-row">
                    <span class="sensor-info-label">Valor:</span>
                    <span class="sensor-value">${sensor.value || '0'}</span>
                </div>
                <div class="sensor-info-row">
                    <span class="sensor-info-label">Última Actualización:</span>
                    <span class="sensor-value">${formatDate(sensor.lastUpdate || new Date())}</span>
                </div>
            </div>
            <div class="sensor-status ${sensor.status === 'ACTIVE' ? 'active' : 'inactive'}">
                ${sensor.status === 'ACTIVE' ? '🟢 Activo' : '🔴 Inactivo'}
            </div>
        </div>
        `)
        .join('');
}

/**
 * Carga la lista de alertas
 */
async function loadAlerts() {
    const levelFilter = document.getElementById('alertFilterLevel').value;
    const data = await apiCall('/alerts/unacknowledged');
    const alertsTableBody = document.getElementById('alertsTableBody');

    if (!data || !Array.isArray(data)) {
        alertsTableBody.innerHTML = '<tr><td colspan="6" class="placeholder">No se pudieron cargar las alertas</td></tr>';
        return;
    }

    let filtered = data;
    if (levelFilter) {
        filtered = data.filter(a => a.level === levelFilter);
    }

    if (filtered.length === 0) {
        alertsTableBody.innerHTML = '<tr><td colspan="6" class="placeholder">No hay alertas</td></tr>';
        return;
    }

    alertsTableBody.innerHTML = filtered
        .map(alert => `
        <tr>
            <td>#${alert.id}</td>
            <td>${alert.sensorId || 'Sistema'}</td>
            <td>
                <span class="alert-level ${alert.level.toLowerCase()}">
                    ${alert.level}
                </span>
            </td>
            <td>${alert.message}</td>
            <td>${formatDate(alert.timestamp)}</td>
            <td>
                <button class="btn-secondary" onclick="acknowledgeAlert(${alert.id})">
                    ✓ Reconocer
                </button>
            </td>
        </tr>
        `)
        .join('');
}

/**
 * Reconoce una alerta
 */
async function acknowledgeAlert(alertId) {
    const result = await apiCall(`/alerts/${alertId}/acknowledge`, 'PUT');
    if (result) {
        loadAlerts();
        loadAlertStats();
    }
}

/**
 * Filtra alertas por nivel
 */
function filterAlerts() {
    loadAlerts();
}

/**
 * Carga los logs de acceso
 */
async function loadAccessLogs() {
    const data = await apiCall('/access/logs?page=0&size=50');
    const accessLogsBody = document.getElementById('accessLogsBody');

    if (!data) {
        accessLogsBody.innerHTML = '<tr><td colspan="5" class="placeholder">No se pudieron cargar los logs</td></tr>';
        return;
    }

    const logs = data.content || data;

    if (!Array.isArray(logs) || logs.length === 0) {
        accessLogsBody.innerHTML = '<tr><td colspan="5" class="placeholder">No hay logs de acceso</td></tr>';
        return;
    }

    accessLogsBody.innerHTML = logs
        .map(log => `
        <tr>
            <td>${log.username}</td>
            <td>${log.sensorId || 'N/A'}</td>
            <td>
                <span class="access-${log.granted ? 'granted' : 'denied'}">
                    ${log.granted ? '✓ Permitido' : '✗ Denegado'}
                </span>
            </td>
            <td>${log.ipAddress || 'N/A'}</td>
            <td>${formatDate(log.timestamp)}</td>
        </tr>
        `)
        .join('');
}

/**
 * Maneja el envío del formulario de nuevo sensor
 */
document.addEventListener('DOMContentLoaded', function () {
    const sensorForm = document.getElementById('sensorForm');
    if (sensorForm) {
        sensorForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            const sensor = {
                name: document.getElementById('sensorName').value,
                type: document.getElementById('sensorType').value,
                location: document.getElementById('sensorLocation').value,
            };

            const result = await apiCall('/sensors', 'POST', sensor);
            if (result) {
                closeSensorModal();
                sensorForm.reset();
                loadSensors();
                loadSensorStats();
                showNotification('✓ Sensor creado exitosamente', 'success');
            } else {
                showNotification('✗ Error al crear el sensor', 'error');
            }
        });
    }
});

// ============================
// LECTURAS EN TIEMPO REAL - MICROSERVICIOS DE SENSORES
// ============================

const GATEWAY_URL = 'http://localhost:8080';

async function fetchSensor(path) {
    try {
        const response = await fetch(GATEWAY_URL + path);
        if (!response.ok) return [];
        return await response.json();
    } catch {
        return [];
    }
}

async function loadSensorReadings() {
    const [movimiento, temperatura, acceso] = await Promise.all([
        fetchSensor('/api/sensores/movimiento/ultimas'),
        fetchSensor('/api/sensores/temperatura/ultimas'),
        fetchSensor('/api/sensores/acceso/ultimos'),
    ]);
    renderMovimientoReadings(movimiento);
    renderTemperaturaReadings(temperatura);
    renderAccesoReadings(acceso);
}

function renderMovimientoReadings(data) {
    const el = document.getElementById('movimientoReadings');
    if (!el) return;
    if (!data.length) {
        el.innerHTML = '<p class="placeholder">Sin lecturas disponibles</p>';
        return;
    }
    el.innerHTML = `
        <table class="readings-table">
            <thead><tr><th>Zona</th><th>Movimiento</th><th>Intensidad</th><th>Hora</th></tr></thead>
            <tbody>
                ${data.map(r => `<tr>
                    <td>${r.zona}</td>
                    <td>${r.movimientoDetectado ? '● Sí' : '○ No'}</td>
                    <td>${r.intensidad}%</td>
                    <td>${formatDate(r.timestamp)}</td>
                </tr>`).join('')}
            </tbody>
        </table>`;
}

function renderTemperaturaReadings(data) {
    const el = document.getElementById('temperaturaReadings');
    if (!el) return;
    if (!data.length) {
        el.innerHTML = '<p class="placeholder">Sin lecturas disponibles</p>';
        return;
    }
    el.innerHTML = `
        <table class="readings-table">
            <thead><tr><th>Ubicación</th><th>Temp °C</th><th>Humedad %</th><th>Hora</th></tr></thead>
            <tbody>
                ${data.map(r => `<tr>
                    <td>${r.ubicacion}</td>
                    <td>${r.temperatura}</td>
                    <td>${r.humedad}</td>
                    <td>${formatDate(r.timestamp)}</td>
                </tr>`).join('')}
            </tbody>
        </table>`;
}

function renderAccesoReadings(data) {
    const el = document.getElementById('accesoReadings');
    if (!el) return;
    if (!data.length) {
        el.innerHTML = '<p class="placeholder">Sin lecturas disponibles</p>';
        return;
    }
    el.innerHTML = `
        <table class="readings-table">
            <thead><tr><th>Puerta</th><th>Tarjeta</th><th>Estado</th><th>Hora</th></tr></thead>
            <tbody>
                ${data.map(r => `<tr>
                    <td>${r.puerta}</td>
                    <td>${r.tarjetaId}</td>
                    <td>${r.autorizado ? '✓ Autorizado' : '✗ Denegado'}</td>
                    <td>${formatDate(r.timestamp)}</td>
                </tr>`).join('')}
            </tbody>
        </table>`;
}

/**
 * Recarga periódica de datos (cada 30 segundos)
 */
setInterval(() => {
    if (currentUser) {
        loadSensorStats();
        loadAlertStats();
        if (document.getElementById('sensorsTab')?.classList.contains('active')) {
            loadSensorReadings();
        }
    }
}, 30000);

