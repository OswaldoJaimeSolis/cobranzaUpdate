package com.cobranzaplazas.repo;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecaudadorTipoPlazaDao extends JpaRepository<RecaudadorTipoPlaza, Integer> {

	@Query(value = "SELECT RTP FROM RecaudadorTipoPlaza  AS RTP WHERE RTP.recaudador.codigoRecaudador= :codigoRecaudador")
	Collection<RecaudadorTipoPlaza> recaudadorTiposPlaza(@Param("codigoRecaudador") String codigoRecaudador);
}
