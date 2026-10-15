package com.cobranzaplazas.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CobroMensualDiaDao extends JpaRepository<CobroMensualDia, Integer> {

	@Query("SELECT D FROM CobroMensualDia AS D WHERE D.tipoPlaza.codigoTipoPlaza = :codigoTipoPlaza ORDER BY D.anio, D.mes, D.dia")
	List<CobroMensualDia> porTipoPlaza(@Param("codigoTipoPlaza") String codigoTipoPlaza);
}
