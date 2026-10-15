package com.cobranzaplazas.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CobroPeriodoAnualDao extends JpaRepository<CobroPeriodoAnual, Integer> {

	@Query("SELECT P FROM CobroPeriodoAnual AS P WHERE P.tipoPlaza.codigoTipoPlaza = :codigoTipoPlaza ORDER BY P.anio")
	List<CobroPeriodoAnual> porTipoPlaza(@Param("codigoTipoPlaza") String codigoTipoPlaza);
}
