const ContactoAyuda = () => {
    return (
        <div className="container mt-5 mb-5">
            {/* Cabecera */}
            <div className="text-center mb-5">
                <h1 className="display-4 fw-bold text-primary mb-3">Contacto y Ayuda</h1>
                <p className="lead text-muted mx-auto" style={{ maxWidth: '700px' }}>
                    ¿Tienes dudas sobre tu próximo viaje o necesitas asistencia con tu cuenta? 
                    Estamos aquí para ayudarte. Contáctanos o revisa nuestras preguntas frecuentes.
                </p>
            </div>

            <div className="row g-5 mb-5">
                {/* Columna de Información de Contacto */}
                <div className="col-lg-5">
                    <h3 className="fw-bold mb-4">Ponte en contacto</h3>
                    
                    <div className="card shadow-sm border-0 bg-light mb-4">
                        <div className="card-body p-4">
                            <div className="d-flex align-items-center mb-4">
                                <div className="bg-primary text-white rounded-circle d-flex justify-content-center align-items-center" style={{ width: '50px', height: '50px' }}>
                                    <i className="bi bi-telephone-fill fs-4"></i>
                                </div>
                                <div className="ms-3">
                                    <h6 className="mb-0 fw-bold text-muted">Llámanos o escríbenos (WhatsApp)</h6>
                                    <p className="mb-0 fs-5 fw-bold">+56 9 8452 9892</p>
                                </div>
                            </div>

                            <div className="d-flex align-items-center mb-4">
                                <div className="bg-success text-white rounded-circle d-flex justify-content-center align-items-center" style={{ width: '50px', height: '50px' }}>
                                    <i className="bi bi-envelope-fill fs-4"></i>
                                </div>
                                <div className="ms-3">
                                    <h6 className="mb-0 fw-bold text-muted">Correo Electrónico</h6>
                                    <p className="mb-0 fs-5 fw-bold">soporte@travelagency.cl</p>
                                </div>
                            </div>

                            <div className="d-flex align-items-center">
                                <div className="bg-danger text-white rounded-circle d-flex justify-content-center align-items-center" style={{ width: '50px', height: '50px' }}>
                                    <i className="bi bi-geo-alt-fill fs-4"></i>
                                </div>
                                <div className="ms-3">
                                    <h6 className="mb-0 fw-bold text-muted">Ubicación Principal</h6>
                                    <p className="mb-0 fs-5 fw-bold">Santiago, Región Metropolitana, Chile</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Columna del Formulario de Contacto */}
                <div className="col-lg-7">
                    <div className="card shadow border-0">
                        <div className="card-body p-5">
                            <h3 className="fw-bold mb-4">Envíanos un mensaje</h3>
                            <form>
                                <div className="row">
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold">Nombre</label>
                                        <input type="text" className="form-control" placeholder="Tu nombre" />
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold">Correo Electrónico</label>
                                        <input type="email" className="form-control" placeholder="tu@correo.com" />
                                    </div>
                                </div>
                                <div className="mb-3">
                                    <label className="form-label fw-bold">Asunto</label>
                                    <select className="form-select">
                                        <option value="">Selecciona un motivo...</option>
                                        <option value="reserva">Dudas sobre una reserva</option>
                                        <option value="cuenta">Problemas con mi cuenta</option>
                                        <option value="pagos">Información de pagos</option>
                                        <option value="otros">Otros</option>
                                    </select>
                                </div>
                                <div className="mb-4">
                                    <label className="form-label fw-bold">Mensaje</label>
                                    <textarea className="form-control" rows="4" placeholder="Escribe tu consulta aquí..."></textarea>
                                </div>
                                <button type="button" className="btn btn-primary w-100 fw-bold">
                                    <i className="bi bi-send-fill me-2"></i> Enviar Mensaje
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            {/* Sección de Ayuda / FAQ */}
            <div className="mt-5 pt-5 border-top">
                <div className="text-center mb-5">
                    <h2 className="fw-bold">Centro de Ayuda (FAQ)</h2>
                    <p className="text-muted">Respuestas rápidas a las consultas más comunes de nuestros viajeros.</p>
                </div>

                <div className="row justify-content-center">
                    <div className="col-lg-8">
                        <div className="accordion shadow-sm" id="accordionFAQ">
                            
                            <div className="accordion-item border-0 border-bottom">
                                <h2 className="accordion-header" id="headingOne">
                                    <button className="accordion-button fw-bold bg-light" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                                        ¿Cómo puedo reservar un paquete turístico?
                                    </button>
                                </h2>
                                <div id="collapseOne" className="accordion-collapse collapse show" aria-labelledby="headingOne" data-bs-parent="#accordionFAQ">
                                    <div className="accordion-body text-muted">
                                        Para reservar, primero debes registrarte o iniciar sesión en tu cuenta. Luego, dirígete a nuestro catálogo de paquetes, selecciona el destino que deseas y haz clic en "Ver Detalles" para proceder con la reserva según los cupos disponibles.
                                    </div>
                                </div>
                            </div>

                            <div className="accordion-item border-0 border-bottom">
                                <h2 className="accordion-header" id="headingTwo">
                                    <button className="accordion-button collapsed fw-bold bg-light" type="button" data-bs-toggle="collapse" data-bs-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
                                        ¿Qué sucede si necesito cancelar mi viaje?
                                    </button>
                                </h2>
                                <div id="collapseTwo" className="accordion-collapse collapse" aria-labelledby="headingTwo" data-bs-parent="#accordionFAQ">
                                    <div className="accordion-body text-muted">
                                        Las políticas de cancelación varían según el paquete y la anticipación con la que se solicite. Te recomendamos revisar la sección de "Condiciones y Restricciones" específica del paquete que compraste o contactar directamente a nuestro equipo a través de WhatsApp.
                                    </div>
                                </div>
                            </div>

                            <div className="accordion-item border-0">
                                <h2 className="accordion-header" id="headingThree">
                                    <button className="accordion-button collapsed fw-bold bg-light" type="button" data-bs-toggle="collapse" data-bs-target="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
                                        ¿Tengo que ingresar todos mis datos personales en "Mi Cuenta"?
                                    </button>
                                </h2>
                                <div id="collapseThree" className="accordion-collapse collapse" aria-labelledby="headingThree" data-bs-parent="#accordionFAQ">
                                    <div className="accordion-body text-muted">
                                        Sí, para emitir pasajes y reservas de hotel de forma legal, necesitamos tus datos completos y verídicos (RUT, Nombre Completo, Nacionalidad y Teléfono). Puedes actualizarlos de forma segura en la sección "Mi Cuenta".
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ContactoAyuda;