package com.jalpa.cobranza.model.dao;

import com.jalpa.cobranza.model.entidades.Contribuyente;
import com.jalpa.cobranza.model.entidades.PropietarioPlaza;

import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface ContribuyentesDao {
    @Query("SELECT * FROM Contribuyente")
    public abstract List<Contribuyente> getAllContribuyentes();
    @Insert
    void insertarContribuyente(Contribuyente contribuyente);





}
