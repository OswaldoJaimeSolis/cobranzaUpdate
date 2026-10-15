package com.cobranzaplazas.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConfiguracionCobroDao extends JpaRepository<ConfiguracionCobro, Integer> {

	@Query("SELECT C FROM ConfiguracionCobro AS C WHERE C.tipoPlaza.codigoTipoPlaza = :codigoTipoPlaza")
	ConfiguracionCobro porTipoPlaza(@Param("codigoTipoPlaza") String codigoTipoPlaza);
}
