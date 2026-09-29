package com.jalpa.cobranza.model.dao;

import com.jalpa.cobranza.model.ConverterDate;
import com.jalpa.cobranza.model.entidades.Contribucion;
import com.jalpa.cobranza.model.entidades.Contribuyente;
import com.jalpa.cobranza.model.entidades.EquipoRecaudador;
import com.jalpa.cobranza.model.entidades.Folio;
import com.jalpa.cobranza.model.entidades.Plaza;
import com.jalpa.cobranza.model.entidades.PropietarioPlaza;
import com.jalpa.cobranza.model.entidades.Recaudador;
import com.jalpa.cobranza.model.entidades.RecaudadorTipoPlaza;
import com.jalpa.cobranza.model.entidades.TipoPlaza;
import com.jalpa.cobranza.model.entidades.TipoPlazaHistorial;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Contribucion.class, Contribuyente.class, Plaza.class, PropietarioPlaza.class, Recaudador.class,  TipoPlaza.class, TipoPlazaHistorial.class, RecaudadorTipoPlaza.class, EquipoRecaudador.class, Folio.class}, version = 11)
@TypeConverters({ConverterDate.class})
public abstract class AppDataBase extends RoomDatabase {
    public abstract OperacionesDao operacionesDao();
    public abstract  ContribuyentesDao contribuyentesDao();
}
