import { useState, useEffect } from "react";
import userService from "../services/user.service";
import tourPackageService from "../services/tourPackage.service";
import reservationService from "../services/reservation.service";

const AdminPanel = () => {
    
    const [activeTab, setActiveTab] = useState("usuarios");
    const [usuarios, setUsuarios] = useState([]);
    const [paquetes, setPaquetes] = useState([]);
    
    //Estado inicial exacto para el formulario de paquetes
    const estadoInicialPaquete = {
        id: null, name: "", destination: "", description: "", 
        startDate: "", endDate: "", duration: "", price: "", 
        totalQuotas: "", availableQuotas: "", includedServices: "", 
        conditions: "", Restrictions: "", tripType: "", season: "", 
        status: "AVAILABLE", imagen: ""
    };
    const [paqueteForm, setPaqueteForm] = useState(estadoInicialPaquete);
    const [editando, setEditando] = useState(false);

    //Estados para Reportes 
    const [reportDates, setReportDates] = useState({ start: "", end: "" });
    const [salesReport, setSalesReport] = useState([]);
    const [rankingReport, setRankingReport] = useState([]);
    const [loadingReports, setLoadingReports] = useState(false);
    const [reportesGenerados, setReportesGenerados] = useState(false);

    useEffect(() => {
        if (activeTab === "usuarios") cargarUsuarios();
        if (activeTab === "paquetes") cargarPaquetes();
    }, [activeTab]);

    const cargarUsuarios = () => userService.getAll().then(res => setUsuarios(res.data)).catch(err => console.error(err));
    const cargarPaquetes = () => tourPackageService.getAll().then(res => setPaquetes(res.data)).catch(err => console.error(err));

    const handleGuardarPaquete = (e) => {
        e.preventDefault();
        
        //Convertimos a número por seguridad para que Java no reclame
        const payload = {
            ...paqueteForm,
            price: Number(paqueteForm.price),
            totalQuotas: Number(paqueteForm.totalQuotas),
            availableQuotas: Number(paqueteForm.availableQuotas),
            duration: paqueteForm.duration ? Number(paqueteForm.duration) : null
        };

        if (editando) {
            tourPackageService.update(payload.id, payload)
                .then(() => {
                    alert("✅ Paquete actualizado correctamente");
                    finalizarAccionPaquete();
                })
                .catch(err => alert("❌ Error al actualizar: " + err.response?.data));
        } else {
            tourPackageService.create(payload)
                .then(() => {
                    alert("✅ Paquete creado exitosamente");
                    finalizarAccionPaquete();
                })
                .catch(err => alert("❌ Error al crear: " + err.response?.data));
        }
    };

    const prepararEdicion = (pkg) => {
        setPaqueteForm({
            ...pkg,
            startDate: pkg.startDate ? pkg.startDate.split('T')[0] : "",
            endDate: pkg.endDate ? pkg.endDate.split('T')[0] : ""
        });
        setEditando(true);
        window.scrollTo(0, 0); // Sube la pantalla al formulario
    };

    const finalizarAccionPaquete = () => {
        cargarPaquetes();
        setEditando(false);
        setPaqueteForm(estadoInicialPaquete);
    };

    const generarReportes = async () => {
        if (!reportDates.start || !reportDates.end) return alert("⚠️ Selecciona ambas fechas.");
        if (new Date(reportDates.start) > new Date(reportDates.end)) return alert("⚠️ La fecha de inicio no puede ser posterior a la de término.");
        
        setLoadingReports(true);
        try {
            const start = `${reportDates.start}T00:00:00`;
            const end = `${reportDates.end}T23:59:59`;

            const [salesRes, rankingRes] = await Promise.all([
                reservationService.getSalesReport(start, end),
                reservationService.getRankingReport(start, end)
            ]);

            setSalesReport(salesRes.data);
            setRankingReport(rankingRes.data);
            setReportesGenerados(true);
        } catch (error) {
            alert("❌ Error al conectar con el motor de reportes.");
        } finally {
            setLoadingReports(false);
        }
    };

    return (
        <div className="container mt-4 mb-5">
            <h2 className="fw-bold mb-4">⚙️ Panel de Administración</h2>

            {/* PESTAÑAS DE NAVEGACIÓN */}
            <ul className="nav nav-tabs mb-4">
                <li className="nav-item">
                    <button className={`nav-link ${activeTab === 'usuarios' ? 'active fw-bold' : ''}`} onClick={() => setActiveTab('usuarios')}>Usuarios</button>
                </li>
                <li className="nav-item">
                    <button className={`nav-link ${activeTab === 'paquetes' ? 'active fw-bold' : ''}`} onClick={() => setActiveTab('paquetes')}>Inventario Paquetes</button>
                </li>
                <li className="nav-item">
                    <button className={`nav-link ${activeTab === 'reportes' ? 'active fw-bold' : ''}`} onClick={() => setActiveTab('reportes')}>Reportes</button>
                </li>
            </ul>

            {/* USUARIOS */}
            {activeTab === "usuarios" && (
                <div className="card shadow-sm border-0">
                    <div className="table-responsive">
                        <table className="table table-hover align-middle mb-0">
                            <thead className="table-dark">
                                <tr>
                                    <th>Nombre</th>
                                    <th>Email</th>
                                    <th>Rol</th>
                                    <th>Estado</th>
                                    <th className="text-center">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {usuarios.map(u => (
                                    <tr key={u.id}>
                                        <td className="fw-bold">{u.fullName}</td>
                                        <td>{u.email}</td>
                                        <td><span className="badge bg-secondary">{u.role}</span></td>
                                        <td><span className={`badge ${u.status === 'ACTIVE' ? 'bg-success' : 'bg-danger'}`}>{u.status}</span></td>
                                        <td className="text-center">
                                            <button className={`btn btn-sm ${u.status === 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'}`} onClick={() => userService.deactivate(u.id).then(cargarUsuarios)}>
                                                {u.status === 'ACTIVE' ? 'Desactivar' : 'Activar'}
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* INVENTARIO PAQUETES */}
            {activeTab === "paquetes" && (
                <div>
                    {/* FORMULARIO */}
                    <div className="card shadow-sm border-0 mb-4 p-4 border-top border-primary border-5">
                        <div className="d-flex justify-content-between align-items-center mb-4">
                            <h5 className="fw-bold m-0">{editando ? "📝 Editando Paquete Turístico" : "➕ Crear Nuevo Paquete Turístico"}</h5>
                            {editando && <button type="button" className="btn btn-sm btn-outline-secondary" onClick={finalizarAccionPaquete}>Cancelar Edición</button>}
                        </div>
                        
                        <form onSubmit={handleGuardarPaquete} className="row g-3">
                            <div className="col-md-6">
                                <label className="form-label small fw-bold">Nombre del Paquete *</label>
                                <input type="text" className="form-control" value={paqueteForm.name} onChange={e => setPaqueteForm({...paqueteForm, name: e.target.value})} required maxLength="100" />
                            </div>
                            <div className="col-md-3">
                                <label className="form-label small fw-bold">Destino *</label>
                                <input type="text" className="form-control" value={paqueteForm.destination} onChange={e => setPaqueteForm({...paqueteForm, destination: e.target.value})} required maxLength="50" />
                            </div>
                            <div className="col-md-3">
                                <label className="form-label small fw-bold">Precio *</label>
                                <input type="number" step="0.01" className="form-control" value={paqueteForm.price} onChange={e => setPaqueteForm({...paqueteForm, price: e.target.value})} required />
                            </div>

                            <div className="col-md-4">
                                <label className="form-label small fw-bold">Fecha de Inicio *</label>
                                <input type="date" className="form-control" value={paqueteForm.startDate} onChange={e => setPaqueteForm({...paqueteForm, startDate: e.target.value})} required />
                            </div>
                            <div className="col-md-4">
                                <label className="form-label small fw-bold">Fecha de Término *</label>
                                <input type="date" className="form-control" value={paqueteForm.endDate} onChange={e => setPaqueteForm({...paqueteForm, endDate: e.target.value})} required />
                            </div>
                            <div className="col-md-4">
                                <label className="form-label small fw-bold">Duración (Días)</label>
                                <input type="number" className="form-control" value={paqueteForm.duration} onChange={e => setPaqueteForm({...paqueteForm, duration: e.target.value})} />
                            </div>

                            <div className="col-md-3">
                                <label className="form-label small fw-bold">Cupos Totales *</label>
                                <input type="number" className="form-control" value={paqueteForm.totalQuotas} onChange={e => setPaqueteForm({...paqueteForm, totalQuotas: e.target.value})} required />
                            </div>
                            <div className="col-md-3">
                                <label className="form-label small fw-bold">Cupos Disponibles *</label>
                                <input type="number" className="form-control" value={paqueteForm.availableQuotas} onChange={e => setPaqueteForm({...paqueteForm, availableQuotas: e.target.value})} required />
                            </div>
                            <div className="col-md-2">
                                <label className="form-label small fw-bold">Tipo de Viaje</label>
                                <select className="form-select" value={paqueteForm.tripType} onChange={e => setPaqueteForm({...paqueteForm, tripType: e.target.value})}>
                                    <option value="">Seleccionar...</option>
                                    <option value="Aventura">Aventura</option>
                                    <option value="Familiar">Familiar</option>
                                    <option value="Relajacion">Relajación</option>
                                    <option value="Cultural">Cultural</option>
                                    <option value="Naturaleza">Naturaleza</option>
                                    <option value="Fiesta">Fiesta</option>
                                </select>
                            </div>
                            <div className="col-md-2">
                                <label className="form-label small fw-bold">Temporada</label>
                                <select className="form-select" value={paqueteForm.season} onChange={e => setPaqueteForm({...paqueteForm, season: e.target.value})}>
                                    <option value="">Seleccionar...</option>
                                    <option value="Invierno">Invierno</option>
                                    <option value="Otoño">Otoño</option>
                                    <option value="Primavera">Primavera</option>
                                    <option value="Verano">Verano</option>
                                </select>
                            </div>
                            <div className="col-md-2">
                                <label className="form-label small fw-bold">Estado *</label>
                                <select className="form-select" value={paqueteForm.status} onChange={e => setPaqueteForm({...paqueteForm, status: e.target.value})} required>
                                    <option value="AVAILABLE">Disponible</option>
                                    <option value="UNAVAILABLE">No Disponible</option>
                                    <option value="SOLD_OUT">Agotado</option>
                                    <option value="CANCELLED">Cancelado</option>
                                    <option value="EXPIRED">Vencido</option>

                                </select>
                            </div>

                            <div className="col-md-6">
                                <label className="form-label small fw-bold">Descripción General</label>
                                <textarea className="form-control" rows="2" value={paqueteForm.description} onChange={e => setPaqueteForm({...paqueteForm, description: e.target.value})} maxLength="256"></textarea>
                            </div>
                            <div className="col-md-6">
                                <label className="form-label small fw-bold">Servicios Incluidos</label>
                                <textarea className="form-control" rows="2" value={paqueteForm.includedServices} onChange={e => setPaqueteForm({...paqueteForm, includedServices: e.target.value})} maxLength="300"></textarea>
                            </div>
                            <div className="col-md-6">
                                <label className="form-label small fw-bold">Condiciones</label>
                                <textarea className="form-control" rows="2" value={paqueteForm.conditions} onChange={e => setPaqueteForm({...paqueteForm, conditions: e.target.value})} maxLength="300"></textarea>
                            </div>
                            <div className="col-md-6">
                                <label className="form-label small fw-bold text-danger">Restricciones</label>
                                <textarea className="form-control border-danger" rows="2" value={paqueteForm.Restrictions} onChange={e => setPaqueteForm({...paqueteForm, Restrictions: e.target.value})} maxLength="300"></textarea>
                            </div>

                            <div className="col-12 text-end mt-4 pt-3 border-top">
                                <button type="submit" className={`btn fw-bold px-5 ${editando ? 'btn-warning' : 'btn-success'}`}>
                                    {editando ? "💾 Guardar Cambios" : "➕ Crear Paquete"}
                                </button>
                            </div>
                        </form>
                    </div>

                    {/* TABLA DE PAQUETES */}
                    <div className="card shadow-sm border-0">
                        <div className="table-responsive">
                            <table className="table table-hover align-middle mb-0">
                                <thead className="table-dark">
                                    <tr>
                                        <th>Paquete</th>
                                        <th>Destino</th>
                                        <th>Fechas</th>
                                        <th>Cupos (Disp/Tot)</th>
                                        <th>Precio</th>
                                        <th>Estado</th>
                                        <th className="text-center">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {paquetes.map(p => (
                                        <tr key={p.id}>
                                            <td className="fw-bold">{p.name}</td>
                                            <td>{p.destination}</td>
                                            <td className="small">{p.startDate} <br/> {p.endDate}</td>
                                            <td>{p.availableQuotas} / {p.totalQuotas}</td>
                                            <td className="text-success fw-bold">${p.price}</td>
                                            <td>
                                                <span className={`badge ${
                                                    p.status === 'AVAILABLE' ? 'bg-success' : 
                                                    p.status === 'SOLD_OUT' ? 'bg-warning text-dark' : 
                                                    p.status === 'CANCELLED' ? 'bg-danger' : 
                                                    p.status === 'EXPIRED' ? 'bg-dark' : 
                                                    'bg-secondary'
                                                }`}>
                                                    {p.status === 'AVAILABLE' ? 'Disponible' : 
                                                    p.status === 'SOLD_OUT' ? 'Agotado' : 
                                                    p.status === 'CANCELLED' ? 'Cancelado' : 
                                                    p.status === 'EXPIRED' ? 'Vencido' : 
                                                    'Inactivo'}
                                                </span>
                                            </td>
                                            <td className="text-center">
                                                <button className="btn btn-sm btn-outline-primary" onClick={() => prepararEdicion(p)}>
                                                    <i className="bi bi-pencil-square"></i> Editar
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                    {paquetes.length === 0 && <tr><td colSpan="7" className="text-center py-4 text-muted">No hay paquetes turísticos registrados.</td></tr>}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            )}
            
            {/* REPORTES */}
            {activeTab === "reportes" && (
                <div className="row">
                    <div className="col-12 mb-4">
                        <div className="card p-4 border-0 shadow-sm bg-light">
                            <h5 className="fw-bold text-primary mb-3"><i className="bi bi-funnel-fill me-2"></i>Motor de Reportes</h5>
                            <div className="row g-3 align-items-end">
                                <div className="col-md-4">
                                    <label className="form-label fw-bold small text-muted">Fecha Inicio</label>
                                    <input type="date" className="form-control" value={reportDates.start} onChange={(e) => setReportDates({...reportDates, start: e.target.value})} />
                                </div>
                                <div className="col-md-4">
                                    <label className="form-label fw-bold small text-muted">Fecha Término</label>
                                    <input type="date" className="form-control" value={reportDates.end} onChange={(e) => setReportDates({...reportDates, end: e.target.value})} />
                                </div>
                                <div className="col-md-4">
                                    <button className="btn btn-primary w-100 fw-bold" onClick={generarReportes} disabled={loadingReports}>
                                        {loadingReports ? "Procesando..." : "Generar Reportes"}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    {reportesGenerados ? (
                        <>
                            <div className="col-lg-8 mb-4">
                                <div className="card border-0 shadow-sm h-100">
                                    <div className="card-header bg-dark text-white fw-bold">📋 Listado de Ventas</div>
                                    <div className="card-body p-0">
                                        <div className="table-responsive" style={{ maxHeight: '400px' }}>
                                            <table className="table table-hover table-sm mb-0">
                                                <thead className="table-light position-sticky top-0">
                                                    <tr>
                                                        <th>Fecha</th>
                                                        <th>Cliente</th>
                                                        <th>Paquete</th>
                                                        <th className="text-center">Pasajeros</th>
                                                        <th className="text-end">Monto Total</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    {salesReport.length > 0 ? (
                                                        salesReport.map((venta, idx) => (
                                                            <tr key={idx}>
                                                                <td className="small text-muted">{new Date(venta.date).toLocaleDateString()}</td>
                                                                <td className="small">{venta.clientName}</td>
                                                                <td className="small fw-bold">{venta.packageName}</td>
                                                                <td className="small text-center">{venta.passengers}</td>
                                                                <td className="small text-end text-success fw-bold">${venta.totalAmount}</td>
                                                            </tr>
                                                        ))
                                                    ) : (<tr><td colSpan="5" className="text-center py-4">No se encontraron ventas.</td></tr>)}
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="col-lg-4 mb-4">
                                <div className="card border-0 shadow-sm h-100">
                                    <div className="card-header bg-warning text-dark fw-bold">🏆 Ranking de Destinos</div>
                                    <div className="card-body p-0">
                                        <ul className="list-group list-group-flush">
                                            {rankingReport.length > 0 ? (
                                                rankingReport.map((rank, idx) => (
                                                    <li key={idx} className="list-group-item d-flex justify-content-between align-items-center p-3">
                                                        <div>
                                                            <span className="fw-bold me-2">#{idx + 1}</span>{rank.packageName}
                                                            <div className="small text-muted mt-1">Ingresos: <span className="text-success">${rank.totalRevenue}</span></div>
                                                        </div>
                                                        <div className="text-end">
                                                            <span className="badge bg-primary rounded-pill mb-1 d-block">{rank.totalReservations} Ventas</span>
                                                            <span className="badge bg-info text-dark rounded-pill d-block">{rank.totalPassengers} Pasajeros</span>
                                                        </div>
                                                    </li>
                                                ))
                                            ) : (<li className="list-group-item text-center py-4 text-muted">Sin datos para ranking.</li>)}
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </>
                    ) : (
                        <div className="col-12 text-center text-muted py-5">
                            <i className="bi bi-bar-chart-line" style={{ fontSize: "4rem" }}></i>
                            <p className="mt-3">Selecciona un rango de fechas y haz clic en "Generar Reportes" para visualizar la información.</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default AdminPanel;