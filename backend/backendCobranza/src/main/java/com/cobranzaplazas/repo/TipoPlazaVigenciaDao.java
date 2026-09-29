package com.cobranzaplazas.repo;

import java.util.Collection;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface TipoPlazaVigenciaDao extends JpaRepository<TipoPlazaVigencia,Integer> {
	
	@Query(value = "SELECT TPV FROM TipoPlazaVigencia  AS TPV WHERE TPV.tipoPlaza.codigoTipoPlaza= :codigoTipoPlaza ORDER BY TPV.idTipoPlazaVigencia DESC")
	Collection<TipoPlazaVigencia> historialTipoPlaza(@Param("codigoTipoPlaza") String codigoTipoPlaza);

}
