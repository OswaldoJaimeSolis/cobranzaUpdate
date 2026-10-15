package com.cobranzaplazas.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CondonacionDao extends JpaRepository<Condonacion, Integer> {

	@Query("SELECT C FROM Condonacion AS C WHERE C.fechaCondonacion >= :desde AND C.fechaCondonacion < :hasta ORDER BY C.fechaCondonacion DESC")
	List<Condonacion> enPeriodo(@Param("desde") java.util.Date desde, @Param("hasta") java.util.Date hasta);

	boolean existsByIdCondonacionLocal(String idCondonacionLocal);
}
