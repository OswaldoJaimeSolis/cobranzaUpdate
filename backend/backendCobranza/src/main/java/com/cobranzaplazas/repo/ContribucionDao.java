package com.cobranzaplazas.repo;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cobranzaplazas.model.JBContribucionReporte;
import com.cobranzaplazas.model.JBContribuyente;

public interface ContribucionDao extends JpaRepository<Contribucion, Integer> {
	final String QUERY_CONTRIBUCIONES_PERIODO = "SELECT new com.cobranzaplazas.model.JBContribucionReporte( c.idContribucion as idContribucion,c.idContribucionLocal as codigoContribucionLocal,"
			+ " c.contribuyente.codigoContribuyente as codigoContribuyente,"
			+ " CONCAT(c.contribuyente.nombre,' ', c.contribuyente.apePaterno,' ',c.contribuyente.apeMaterno) as nombreContribuyente,"
			+ " c.recaudador.codigoRecaudador as codigoRecaudador, c.plaza.codigoPlaza as codigoPlaza, c.fechaContribucion as fechaContribucion, "
			+ " c.fechaModificacion as fechaModificacion, c.importeContribucion as importeContribucion, c.estadoPago as estadoPago,"
			+ " c.equipoRecaudador.codigoEquipoRec as codigoEquipo, c.tipoPlaza.codigoTipoPlaza as codigoTipoPlaza) "
			+ "FROM Contribucion c WHERE DATE(c.fechaContribucion) BETWEEN DATE(:fechaInicial) AND (:fechaFinal) AND  c.estadoPago IN (:estadosPago)  ";

	String QUERY_CONTRIBUCIONES_PERIODO_CONTRIBUYENTE = QUERY_CONTRIBUCIONES_PERIODO
			+ (" AND c.contribuyente.codigoContribuyente= :codigoContribuyente");
	String QUERY_CONTRIBUCIONES_PERIODO_TIPO_PLAZA = QUERY_CONTRIBUCIONES_PERIODO
			+ (" AND c.tipoPlaza.codigoTipoPlaza =:codigoTP");

	final String QUERY_CONTRIBUCIONES_PERIODO_CONTRIBUYENTE_TIPO_PLAZA = QUERY_CONTRIBUCIONES_PERIODO
			+ (" AND c.contribuyente.codigoContribuyente = :codigoContribuyente AND c.tipoPlaza.codigoTipoPlaza= :codigoTP");

	final String QUERY_CONTRIBUCIONES_PAGO_PERIODO = "SELECT new com.cobranzaplazas.model.JBContribucionReporte( c.idContribucion as idContribucion,c.idContribucionLocal as codigoContribucionLocal,"
			+ " c.contribuyente.codigoContribuyente as codigoContribuyente,"
			+ " CONCAT(c.contribuyente.nombre,' ', c.contribuyente.apePaterno,' ',c.contribuyente.apeMaterno) as nombreContribuyente,"
			+ " c.recaudador.codigoRecaudador as codigoRecaudador, c.plaza.codigoPlaza as codigoPlaza, c.fechaContribucion as fechaContribucion, "
			+ " c.fechaModificacion as fechaModificacion, c.importeContribucion as importeContribucion, c.estadoPago as estadoPago,"
			+ " c.equipoRecaudador.codigoEquipoRec as codigoEquipo, c.tipoPlaza.codigoTipoPlaza as codigoTipoPlaza) "
			+ "FROM Contribucion c WHERE DATE(c.fechaModificacion) BETWEEN DATE(:fechaInicial) AND (:fechaFinal) AND  c.estadoPago IN (:estadosPago)  ";

	String QUERY_CONTRIBUCIONES_PAGO_PERIODO_CONTRIBUYENTE = QUERY_CONTRIBUCIONES_PAGO_PERIODO
			+ (" AND c.contribuyente.codigoContribuyente= :codigoContribuyente");
	String QUERY_CONTRIBUCIONES_PAGO_PERIODO_TIPO_PLAZA = QUERY_CONTRIBUCIONES_PAGO_PERIODO
			+ (" AND c.tipoPlaza.codigoTipoPlaza =:codigoTP");

	final String QUERY_CONTRIBUCIONES_PAGO_PERIODO_CONTRIBUYENTE_TIPO_PLAZA = QUERY_CONTRIBUCIONES_PAGO_PERIODO
			+ (" AND c.contribuyente.codigoContribuyente = :codigoContribuyente AND c.tipoPlaza.codigoTipoPlaza= :codigoTP");

	final String QUERY_CONTRIBUYENTES_BASE = "SELECT new com.cobranzaplazas.model.JBContribuyente(PP.contribuyente.codigoContribuyente as codigoContribuyente,"
			+ "CONCAT(PP.contribuyente.nombre,' ', PP.contribuyente.apePaterno,' ',PP.contribuyente.apeMaterno) as nombreContribuyente,"
			+ "PP.contribuyente.rfcContribuyente as rfc, PP.tipoPlaza.codigoTipoPlaza as tipoPlaza) "
			+ "FROM PropietarioPlaza AS PP    ";
	final String QUERY_CONTRIBUYENTES_TODOS = QUERY_CONTRIBUYENTES_BASE
			+ " GROUP BY PP.contribuyente.codigoContribuyente";
	final String QUERY_CONTRIBUYENTES_TP = QUERY_CONTRIBUYENTES_BASE
			+ " WHERE PP.tipoPlaza.codigoTipoPlaza= :codigoTP GROUP BY PP.contribuyente.codigoContribuyente";

	@Query(value = QUERY_CONTRIBUCIONES_PERIODO)
	Collection<JBContribucionReporte> findContribucionesPeriodo(@Param("fechaInicial") Date fechaInicial,
			@Param("fechaFinal") Date fechaFinal, @Param("estadosPago") List<Integer> estadoPagos, Sort sort);

	@Query(value = QUERY_CONTRIBUCIONES_PERIODO_CONTRIBUYENTE_TIPO_PLAZA)
	Collection<JBContribucionReporte> findContribucionesPeriodoTPContribuyentes(
			@Param("fechaInicial") Date fechaInicial, @Param("fechaFinal") Date fechaFinal,
			@Param("estadosPago") List<Integer> estadoPagos, @Param("codigoContribuyente") String codigoContribuyente,
			@Param("codigoTP") String codigoTP, Sort sort);

	@Query(value = QUERY_CONTRIBUCIONES_PERIODO_TIPO_PLAZA)
	Collection<JBContribucionReporte> findContribucionesPeriodoTP(@Param("fechaInicial") Date fechaInicial,
			@Param("fechaFinal") Date fechaFinal, @Param("estadosPago") List<Integer> estadoPagos,
			@Param("codigoTP") String codigoTP, Sort sort);

	@Query(value = QUERY_CONTRIBUCIONES_PERIODO_CONTRIBUYENTE)
	Collection<JBContribucionReporte> findContribucionesPeriodoContribuyentes(@Param("fechaInicial") Date fechaInicial,
			@Param("fechaFinal") Date fechaFinal, @Param("estadosPago") List<Integer> estadoPagos,
			@Param("codigoContribuyente") String codigoContribuyente, Sort sort);

	@Query(value = QUERY_CONTRIBUCIONES_PAGO_PERIODO)
	Collection<JBContribucionReporte> findContribucionesPagoPeriodo(@Param("fechaInicial") Date fechaInicial,
			@Param("fechaFinal") Date fechaFinal, @Param("estadosPago") List<Integer> estadoPagos, Sort sort);

	@Query(value = QUERY_CONTRIBUCIONES_PAGO_PERIODO_CONTRIBUYENTE_TIPO_PLAZA)
	Collection<JBContribucionReporte> findContribucionesPagoPeriodoTPContribuyentes(
			@Param("fechaInicial") Date fechaInicial, @Param("fechaFinal") Date fechaFinal,
			@Param("estadosPago") List<Integer> estadoPagos, @Param("codigoContribuyente") String codigoContribuyente,
			@Param("codigoTP") String codigoTP, Sort sort);

	@Query(value = QUERY_CONTRIBUCIONES_PAGO_PERIODO_TIPO_PLAZA)
	Collection<JBContribucionReporte> findContribucionesPagoPeriodoTP(@Param("fechaInicial") Date fechaInicial,
			@Param("fechaFinal") Date fechaFinal, @Param("estadosPago") List<Integer> estadoPagos,
			@Param("codigoTP") String codigoTP, Sort sort);

	@Query(value = QUERY_CONTRIBUCIONES_PAGO_PERIODO_CONTRIBUYENTE)
	Collection<JBContribucionReporte> findContribucionesPagoPeriodoContribuyentes(
			@Param("fechaInicial") Date fechaInicial, @Param("fechaFinal") Date fechaFinal,
			@Param("estadosPago") List<Integer> estadoPagos, @Param("codigoContribuyente") String codigoContribuyente,
			Sort sort);

	@Query(value = QUERY_CONTRIBUYENTES_TODOS)
	Collection<JBContribuyente> findTodosConribuyentes(Sort sort);

	@Query(value = QUERY_CONTRIBUYENTES_TP)
	Collection<JBContribuyente> findTPConribuyentes(@Param("codigoTP") String codigoTP, Sort sort);

}
