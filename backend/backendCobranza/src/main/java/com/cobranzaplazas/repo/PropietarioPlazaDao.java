package com.cobranzaplazas.repo;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cobranzaplazas.model.JBPropietarioPlaza;

public interface PropietarioPlazaDao extends JpaRepository<PropietarioPlaza, String> {
	final String QUERY_BA = "SELECT new com.cobranzaplazas.model.JBPropietarioPlaza(PP.idPropietarioPlaza, PP.plaza.codigoPlaza, PP.tipoPlaza.codigoTipoPlaza, "
			+ " CONCAT(PP.contribuyente.codigoContribuyente, ' ',PP.contribuyente.nombre,' ', PP.contribuyente.apePaterno,' ',PP.contribuyente.apeMaterno), "
			+ "PP.vigenciaInicial, PP.vigenciaFinal, PP.importe) "
			+ " FROM PropietarioPlaza AS PP WHERE ((DATE(:fInicial) BETWEEN DATE(PP.vigenciaInicial) AND DATE(PP.vigenciaFinal)) OR (DATE(:fFinal) BETWEEN DATE(PP.vigenciaInicial) AND DATE(PP.vigenciaFinal))) ";

	String QUERY_CONTRIBUYENTE = QUERY_BA + " AND PP.contribuyente.codigoContribuyente= :codigoContribuyente";
	String QUERY_TIPOPLAZA = QUERY_BA + " AND PP.tipoPlaza.codigoTipoPlaza= :tipoPlaza";
	String QUERY_TIPOPLAZA_CONTRIBUYENTE = QUERY_BA
			+ " AND PP.tipoPlaza.codigoTipoPlaza= :tipoPlaza AND PP.contribuyente.codigoContribuyente= :codigoContribuyente";

	@Query("SELECT F FROM Folio AS F WHERE F.codigoFolio=:codigoFolio ")
	Folio recuperarFolio(@Param("codigoFolio") String codigoFolio);

	@Query("SELECT new com.cobranzaplazas.model.JBPropietarioPlaza(PP.idPropietarioPlaza, PP.plaza.codigoPlaza, PP.tipoPlaza.codigoTipoPlaza,"
			+ "CONCAT(PP.contribuyente.codigoContribuyente, ' ',PP.contribuyente.nombre,' ', PP.contribuyente.apePaterno,' ',PP.contribuyente.apeMaterno),"
			+ "PP.vigenciaInicial, PP.vigenciaFinal,PP.importe)" + " FROM PropietarioPlaza AS PP ")
	List<JBPropietarioPlaza> recuperarPropietarios();

	@Query(QUERY_BA)
	List<JBPropietarioPlaza> recuperarPropietarios(@Param("fInicial") Date fechaInicial,
			@Param("fFinal") Date fechaFinal);

	@Query(QUERY_CONTRIBUYENTE)
	List<JBPropietarioPlaza> recuperarPropietariosContribuyente(@Param("fInicial") Date fechaInicial,
			@Param("fFinal") Date fechaFinal, @Param("codigoContribuyente") String codigoContribuyente);

	@Query(QUERY_TIPOPLAZA)
	List<JBPropietarioPlaza> recuperarPropietariosTipoPlaza(@Param("fInicial") Date fechaInicial,
			@Param("fFinal") Date fechaFinal, @Param("tipoPlaza") String tipoPlaza);

	@Query(QUERY_TIPOPLAZA_CONTRIBUYENTE)
	List<JBPropietarioPlaza> recuperarPropietarios(@Param("fInicial") Date fechaInicial,
			@Param("fFinal") Date fechaFinal, @Param("codigoContribuyente") String codigoContribuyente,
			@Param("tipoPlaza") String tipoPlaza);

}
