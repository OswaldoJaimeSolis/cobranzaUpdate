package com.cobranzaplazas.seguridad;

import java.util.Set;

public final class Roles {
	/** Todo el sistema web, incluidos catálogos y usuarios. */
	public static final String ADMIN = "ADMIN";
	/** Sólo lectura: reportes y consultas. */
	public static final String CONSULTA = "CONSULTA";
	/** App móvil: sincronización de catálogos y envío de cobros. */
	public static final String RECAUDADOR = "RECAUDADOR";

	/** Roles que se pueden asignar a un usuario web. */
	public static final Set<String> WEB = Set.of(ADMIN, CONSULTA);

	private Roles() {
	}
}
