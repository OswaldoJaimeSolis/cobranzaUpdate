package com.jalpa.cobranza.model.dao;

import com.jalpa.cobranza.model.AuxActualizacionRemota;
import com.jalpa.cobranza.model.entidades.ConfiguracionCobro;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public abstract class OperacionesDao {
    @Query("DELETE FROM Recaudador")
    abstract void eliminarRecaudadores();
    @Insert
    abstract  void insertarRecaudador(Recaudador recaudador);
    @Update
    abstract  void actualizaRecaudador(Recaudador recaudador);

    @Query("SELECT * FROM Recaudador WHERE codigoRecaudador=:codigoRecaudador AND passRecaudador=:passRecaudador")
    abstract  Recaudador obtenerRecaudador(String codigoRecaudador, String passRecaudador);


    @Query("DELETE FROM Contribuyente")
    public abstract  void eliminarContribuyentes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract  void insertarContribuyentes(List<Contribuyente> contribuyentes);

    @Update
    public abstract  void actualizarContribuyente(Contribuyente contribuyente);

    @Query("SELECT * FROM Contribuyente")
    public abstract List<Contribuyente> getAllContribuyentes();





    @Insert
    public abstract  void insertarContribuyentePlazaPropietario(Contribuyente contribuyente, Plaza plaza, PropietarioPlaza propietarioPlaza);

    @Insert
    public abstract void insertarContribuyente(Contribuyente contribuyente);

    @Insert
    public abstract void insertarPlaza(Plaza plaza);

    @Insert
    public abstract void insertarPlazaPropietario(PropietarioPlaza plazaPropietario);

    @Transaction
    public void insertarContribuyenteNuevo(Contribuyente contribuyente, Plaza plaza, PropietarioPlaza propietarioPlaza){
        String idCodigoFolio="CONL".concat(propietarioPlaza.getTipoPlaza().getCodigoTipoPlaza());

        Folio folio = getFolio(idCodigoFolio);
        if (folio != null) {
            folio.setFolio(folio.getFolio() + 1);

        } else {
            folio = new Folio();
            folio.setCodigoFolio(idCodigoFolio);
            folio.setFolio(1);

        }
        insertarActualizarFolio(folio);
        contribuyente.setCodigoContribuyente(contribuyente.getCodigoContribuyente()+"_"+String.format("%04d",folio.getFolio()));
        contribuyente.setInServerContribuyente(false);
        insertarContribuyente(contribuyente);

        plaza.setCodigoPlaza(contribuyente.getCodigoContribuyente());
        insertarPlaza(plaza);

        propietarioPlaza.setCodigoPropietarioPlaza(contribuyente.getCodigoContribuyente());
        propietarioPlaza.setPlaza(plaza);
        insertarPlazaPropietario(propietarioPlaza);



    }


    @Transaction
    public void borrarInsertarTiposPlazaWithHistorial(List<TipoPlaza> tiposPlaza){
        borrarTiposPlazaHistorial();
        insertarTiposPlaza(tiposPlaza);
        for(TipoPlaza tp : tiposPlaza){
            if(tp.getImporteGlobal()){
                insertarTiposPlazaHistorial(tp.getHistorial());
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract  void insertarTiposPlaza(List<TipoPlaza> tiposPlaza);
    @Insert
    abstract void insertarTiposPlazaHistorial(List<TipoPlazaHistorial> historial);

   @Transaction
    public  List<TipoPlaza> getAllTiposPlaza(){
       List<TipoPlaza> tiposPlazas= getTiposPlaza();
       agregarHistorialTipoPlazaToTipoPlaza(tiposPlazas, recuperarHistorialPlazas(getStringTipoPlazasFromTipoPlaza(tiposPlazas), new Date()));
       return  tiposPlazas;
    }

    private List<String> getStringTipoPlazasFromTipoPlaza(List<TipoPlaza> tipoPlazas){
        List<String> codesTipoPlaza= new ArrayList<>();
        for(TipoPlaza pp: tipoPlazas){
            codesTipoPlaza.add(pp.getCodigoTipoPlaza());
        }
        return  codesTipoPlaza;
    }

   @Query("SELECT * FROM TipoPlaza")
    abstract List<TipoPlaza> getTiposPlaza();


    @Query("DELETE FROM TipoPlaza")
    abstract  void borrarTiposPlaza();
    @Query("DELETE FROM TipoPlazaHistorial")
    abstract void borrarTiposPlazaHistorial();

    @Query("SELECT * FROM PropietarioPlaza ")
    public abstract List<PropietarioPlaza> getPlazasaLLPropietario();

    @Query("SELECT * FROM PropietarioPlaza AS pp WHERE pp.codigoContribuyente== :contribuyente AND :fecha BETWEEN pp.vigenciaInicial AND vigenciaFinal")
    public abstract List<PropietarioPlaza> getPlazasVigentesPropietario(Date fecha, String contribuyente);

    @Query("SELECT * FROM TipoPlazaHistorial WHERE tipoPlazaH IN (:nombreTipoPlazas) AND :fecha BETWEEN vigenciaInicial AND vigenciaFinal ")
    public abstract List<TipoPlazaHistorial> recuperarHistorialPlazas(List<String> nombreTipoPlazas, Date fecha);

    @Transaction
    public  List<PropietarioPlaza> recuperaPlazasPropietario(Date fecha, String codigoContribuyente){
        //List<PropietarioPlaza> propietarios= getPlazasaLLPropietario();
        List<PropietarioPlaza> propietarioPlaza= getPlazasVigentesPropietario(fecha, codigoContribuyente);
        //también las vigentes hoy: si la fecha en pantalla es otra (p. ej. el fin del período anual de una
        //plaza) el contribuyente no debe perder de la lista sus demás plazas
        Calendar hoy= Calendar.getInstance();
        hoy.set(Calendar.HOUR_OF_DAY, 0);
        hoy.set(Calendar.MINUTE, 0);
        hoy.set(Calendar.SECOND, 0);
        hoy.set(Calendar.MILLISECOND, 0);
        for(PropietarioPlaza vigenteHoy: getPlazasVigentesPropietario(hoy.getTime(), codigoContribuyente)){
            boolean yaEsta= false;
            for(PropietarioPlaza pp: propietarioPlaza){
                if(pp.getCodigoPropietarioPlaza().equals(vigenteHoy.getCodigoPropietarioPlaza())){
                    yaEsta= true;
                    break;
                }
            }
            if(!yaEsta){
                propietarioPlaza.add(vigenteHoy);
            }
        }
        agregarHistorialTipoPlazaToPropietarioPlaza(propietarioPlaza, recuperarHistorialPlazas(getStringTiposPlazaFromPropietarioPlaza(propietarioPlaza),fecha));


        return  propietarioPlaza;
    }

    private void agregarHistorialTipoPlazaToPropietarioPlaza(List<PropietarioPlaza> propietarioPlazas, List<TipoPlazaHistorial> historial){
        for(PropietarioPlaza pp: propietarioPlazas){
            for(TipoPlazaHistorial tph: historial){
                if(pp.getTipoPlaza().getCodigoTipoPlaza().equals(tph.getTipoPlazaH())){
                    pp.getTipoPlaza().getHistorial().add(tph);
                }
            }
        }
    }
    private void agregarHistorialTipoPlazaToTipoPlaza(List<TipoPlaza> tipoPlazas, List<TipoPlazaHistorial> historial){
        for(TipoPlaza tp: tipoPlazas){
            for(TipoPlazaHistorial tph: historial){
                if(tp.getCodigoTipoPlaza().equals(tph.getTipoPlazaH())){
                    tp.getHistorial().add(tph);
                }
            }
        }
    }

    private List<String> getStringTiposPlazaFromPropietarioPlaza(List<PropietarioPlaza> propietarioPlazas){
        List<String> nombresPlazas= new ArrayList<>();
        for(PropietarioPlaza pp: propietarioPlazas){
            nombresPlazas.add(pp.getTipoPlaza().getCodigoTipoPlaza());
        }
        return  nombresPlazas;
    }

    @Query("DELETE FROM PropietarioPlaza")
    abstract  void borrarPropietaroPlaza();

    @Query("DELETE FROM Plaza")
    abstract  void borrarPlaza();

    /**
     * Recupera las contribuciones de un contribuyente de una plaza en un rango de fechas
     * @param codigoContribuyente
     * @param codigoPlaza
     * @param fechaContribucionInicial
     * @param fechaContribucionFinal
     * @return
     */
    @Query("SELECT * FROM Contribucion WHERE codigoContribuyente=:codigoContribuyente AND codigoPlaza=:codigoPlaza AND fecha BETWEEN :fechaContribucionInicial AND :fechaContribucionFinal ")
    public abstract List<Contribucion> getContribuciones(String codigoContribuyente, String codigoPlaza, Date fechaContribucionInicial, Date fechaContribucionFinal);

    /**
     * Recupera la contribución dado el contribuyente, el código de plaza y la fecha
     * @param codigoContribuyente
     * @param codigoPlaza
     * @param fechaContribucion
     * @return
     */
    @Query("SELECT * FROM Contribucion WHERE codigoContribuyente=:codigoContribuyente AND codigoPlaza=:codigoPlaza AND fecha =:fechaContribucion ")
    public abstract Contribucion getContribucion(String codigoContribuyente, String codigoPlaza, Date fechaContribucion);

    @Query("SELECT * FROM Contribucion WHERE codigoContribuyente=:codigoContribuyente AND codigoTipoPlaza=:tipoPlaza ")
    public abstract List<Contribucion> getContribucion(String codigoContribuyente, String tipoPlaza);

    @Query("SELECT codigoContribuyente  FROM Contribucion  GROUP BY  codigoContribuyente, codigoTipoPlaza, fecha HAVING count(codigoContribuyente)>1 ")
    public abstract List<String> getContribucionDuplicadas();

    /**
     * Recupera la contribución dado el contribuyente, el código de plaza y la fecha
     * @param codigoContribuyente
     * @param codigoPlaza
     * @param fechaContribucion
     * @return
     */
    @Query("SELECT * FROM Contribucion WHERE codigoContribuyente=:codigoContribuyente AND codigoPlaza=:codigoPlaza AND estadoPago=:estado AND fecha <:fechaContribucion ORDER BY fecha ASC ")
    public abstract List<Contribucion> getContribucionesAnteriores(String codigoContribuyente, String codigoPlaza, Integer estado, Date fechaContribucion);

    /**
     * Recupera la contribuciones de acuerdo al estado, el tipo de plaza y la fecha de contribución
     * @param codigoTipoPlaza
     * @param estado
     * @param fechaContribucion
     * @return
     */
    @Query("SELECT * FROM Contribucion WHERE  codigoTipoPlaza=:codigoTipoPlaza AND estadoPago=:estado AND fecha=:fechaContribucion ")
    public abstract List<Contribucion> getContribuciones(String codigoTipoPlaza,Integer estado, Date fechaContribucion);

    @Query("SELECT * FROM Contribucion ")
    public abstract List<Contribucion> getContribucionesAll();

    @Query("SELECT * FROM Contribucion WHERE codigoTipoPlaza=:codigoTipoPlaza ")
    public abstract List<Contribucion> getContribucionesAll(String codigoTipoPlaza);

    /*@Query("SELECT * FROM PropietarioPlaza AS PP INNER JOIN Contribucion AS CON ON PP.codigoPlaza=CON.codigoPlaza AND PP.codigoContribuyente=CON.codigoContribuyente AND PP.codigoTipoPlaza" +
            "=CON.codigoTipoPlaza   WHERE  PP.codigoTipoPlaza=:codigoTipoPlaza  AND :fechaContribucionInicial BETWEEN PP.vigenciaInicial AND PP.vigenciaFinal AND  " +
            "  ")*/
    @Query("SELECT * FROM PropietarioPlaza AS PP   WHERE  PP.codigoTipoPlaza=:codigoTipoPlaza  AND :fechaContribucionInicial BETWEEN PP.vigenciaInicial AND PP.vigenciaFinal AND PP.codigoPlaza " +
            " NOT IN ( SELECT CON.codigoPlaza FROM Contribucion AS CON WHERE CON.fecha=:fechaContribucionInicial AND CON.codigoTipoPlaza=:codigoTipoPlaza  )")
    public abstract List<PropietarioPlaza> getPlazasSinRevisar(String codigoTipoPlaza, Date fechaContribucionInicial);

    @Query("Update Contribucion SET estadoServidor=:estado WHERE  codigoTipoPlaza=:codigoPlaza")
    public abstract void establecerEstadoServerSinUpload(boolean estado, String codigoPlaza);

    @Query("Update Contribucion SET estadoServidor=:estado ")
    public abstract void establecerEstadoServerSinUpload(boolean estado);

    @Query("Update Contribucion SET estadoServidor=:estado WHERE idContribucion>:numFolio AND idContribucion<:numFolio2 ")
    public abstract void establecerEstadoServerSinUpload(boolean estado, Integer numFolio, Integer numFolio2);

    @Query("SELECT * FROM Contribuyente WHERE codigoContribuyente=:codigoContribuyente ")
    public abstract Contribuyente getContribuyente(String codigoContribuyente);

    @Query("UPDATE Contribuyente SET inServerContribuyente=:estado WHERE codigoContribuyente=:codigoContribuyente ")
    public abstract void actualizarEstadoServerContribuyente(Boolean estado, String codigoContribuyente);

    @Query("DELETE FROM Contribuyente WHERE codigoContribuyente=:codigoContribuyente ")
    public abstract void deleteContribuyente(String codigoContribuyente);

    @Query("Update Contribucion SET codigoContribuyente=:codigoContribuyente WHERE codigoContribuyente=:codigoContribuyenteOld ")
    public abstract void actualizarContribuyenteContribucion(String codigoContribuyente, String codigoContribuyenteOld);

    @Query("Update Contribucion SET codigoPlaza=:codigo WHERE codigoPlaza=:codigoOld ")
    public abstract void actualizarPlazaContribucion(String codigo, String codigoOld);

    @Query("Update Plaza SET codigoPlaza=:codigo WHERE codigoPlaza=:codigoOld ")
    public abstract void actualizarPlaza(String codigo, String codigoOld);

    @Query("Update PropietarioPlaza SET codigoContribuyente=:codigo WHERE codigoContribuyente=:codigoOld ")
    public abstract void actualizarContribuyentePP(String codigo, String codigoOld);

    @Query("Update PropietarioPlaza SET codigoPropietarioPlaza=:codigo, codigoPlaza=:codigo WHERE codigoPropietarioPlaza=:codigoOld ")
    public abstract void actualizarPP(String codigo, String codigoOld);

    @Query("SELECT * FROM  Plaza  WHERE codigoPlaza=:codigoPlaza")
    public abstract Plaza recuperaPlazaxCodigo(String codigoPlaza);

    @Query("DELETE FROM  Plaza  WHERE codigoPlaza=:codigoPlaza")
    public abstract void deletePlazaxCodigo(String codigoPlaza);

    @Query("SELECT * FROM  PropietarioPlaza  WHERE codigoContribuyente=:cc")
    public abstract PropietarioPlaza recuperaPPxContribuyente(String cc);

    @Query("DELETE FROM  PropietarioPlaza  WHERE codigoContribuyente=:cc")
    public abstract void deletePPxContribuyente(String cc);

    @Query("SELECT * FROM  PropietarioPlaza  WHERE codigoContribuyente LIKE :cc")
    public abstract List<PropietarioPlaza> recuperaPPxContribuyentes(String cc);

    @Query("SELECT * FROM  Contribuyente  WHERE codigoContribuyente LIKE :cc")
    public abstract List<Contribuyente> recuperaContribuyentexcodigo(String cc);

    @Query("SELECT * FROM  Contribucion  WHERE codigoContribuyente LIKE :cc")
    public abstract List<Contribucion> recuperaContribucionxContribuyente(String cc);








@Query("DELETE FROM Contribucion")
 abstract void borrarContribuciones();

@Query("DELETE FROM Contribucion WHERE folioDedicado=:folio ")
 public  abstract void borrarContribucionFolioDedicado(String folio);

    /**
     * Usar con cuidado
     */
    @Transaction
    public void borrarBase(){
        try{
            borrarContribuciones();

            //borrarPropietaroPlaza();
            //borrarContribuyente();

            //borrarPlaza();


        }
        catch (Exception e){

        }
    }


    @Query("DELETE FROM Contribuyente")
    abstract  void borrarContribuyente();


    @Query("SELECT * FROM Recaudador ")
    public abstract List<Recaudador> getRecaudadores();
    @Query("SELECT * FROM RecaudadorTipoPlaza ")
    public abstract List<RecaudadorTipoPlaza> getRecaudadoresTiposPlaza();
    @Transaction
    public  List<Recaudador> getAllRecaudadores(){
        List<Recaudador> recaudadores= getRecaudadores();
        agregarTiposPlazas(recaudadores);

        return  recaudadores;
    }

    private void agregarTiposPlazas(List<Recaudador>recaudadores){
        List<RecaudadorTipoPlaza> tipoPlazas= getRecaudadoresTiposPlaza();
        for(RecaudadorTipoPlaza rtp: tipoPlazas){
            recaudadores.get(recaudadores.indexOf(rtp.getRecaudador())).getTipoPlazas().add(rtp);
        }
    }

    @Query("SELECT * FROM PropietarioPlaza WHERE codigoPlaza = :codigoPlaza AND codigoTipoPlaza = :codigoTipoPlaza")
    abstract List<PropietarioPlaza> getPropietariosPlaza(String codigoPlaza, String codigoTipoPlaza);

    @Query("UPDATE PropietarioPlaza SET adeudo = :adeudo, adeudoAnterior = :adeudoAnterior, fechasVencidas = :fechasVencidas WHERE codigoPropietarioPlaza = :codigoPropietarioPlaza")
    abstract void actualizarAdeudo(String codigoPropietarioPlaza, Double adeudo, Double adeudoAnterior, String fechasVencidas);

    /**
     * Cancela en el adeudo local de la plaza lo que cubre una contribución pagada (o condonada),
     * sin esperar a que el servidor la refleje: el adeudo anterior completo, o la fecha vencida
     * que se cobró junto con su monto.
     */
    @Transaction
    public void descontarPagoLocal(Contribucion con){
        if(con==null || con.getEstadoPago()==null || con.getEstadoPago()!=1 || con.getPlaza()==null || con.getTipoPlaza()==null){
            return;
        }
        for(PropietarioPlaza pp: getPropietariosPlaza(con.getPlaza().getCodigoPlaza(), con.getTipoPlaza().getCodigoTipoPlaza())){
            double adeudo= pp.getAdeudo()!=null? pp.getAdeudo(): 0.0;
            double anterior= pp.getAdeudoAnterior()!=null? pp.getAdeudoAnterior(): 0.0;
            String vencidas= pp.getFechasVencidas()!=null? pp.getFechasVencidas(): "";
            if(Boolean.TRUE.equals(con.getPagoAdeudoAnterior())){
                adeudo-= anterior;
                anterior= 0.0;
            }
            else{
                String fecha= new SimpleDateFormat("yyyy-MM-dd").format(con.getFecha());
                //en el mensual un pago en cualquier fecha del mes cubre el mes, en el anual cualquiera del año
                ConfiguracionCobro config= getConfiguracionCobro(con.getTipoPlaza().getCodigoTipoPlaza());
                List<String> restantes= new ArrayList<>();
                boolean estaba= false;
                for(String vencida: vencidas.split(",")){
                    if(!estaba && cubre(config, fecha, vencida.trim())){
                        estaba= true;
                    }
                    else if(!vencida.trim().isEmpty()){
                        restantes.add(vencida.trim());
                    }
                }
                if(!estaba){
                    continue;
                }
                //lo vencido se adeuda a precio normal; sin configuración de cobro, al importe cobrado o condonado
                Double monto= con.getImporteNormal()!=null? con.getImporteNormal()
                        : (Boolean.TRUE.equals(con.getCondonada())? con.getMontoCondonado(): con.getImporte());
                adeudo-= monto!=null? monto: 0.0;
                vencidas= android.text.TextUtils.join(",", restantes);
            }
            actualizarAdeudo(pp.getCodigoPropietarioPlaza(), Math.max(0.0, adeudo), anterior, vencidas);
        }
    }

    private static boolean cubre(ConfiguracionCobro config, String fechaPagada, String vencida){
        if(vencida.isEmpty()){
            return false;
        }
        if(config==null){
            return vencida.equals(fechaPagada);
        }
        try {
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
            return config.mismaObligacion(sdf.parse(fechaPagada), sdf.parse(vencida));
        }
        catch (java.text.ParseException e){
            return false;
        }
    }

    @Query("SELECT * FROM ConfiguracionCobro WHERE codigoTipoPlazaCobro = :codigoTipoPlaza")
    public abstract ConfiguracionCobro getConfiguracionCobro(String codigoTipoPlaza);

    /** Tras bajar el catálogo: vuelve a cancelar lo ya cobrado en este equipo que el servidor aún no recibe. */
    @Transaction
    public void descontarPagosSinEnviar(){
        for(Contribucion con: recuperarContribucionesEstadoServidor()){
            descontarPagoLocal(con);
        }
    }

    @Query("SELECT * FROM PropietarioPlaza WHERE codigoPropietarioPlaza = :codigoPropietarioPlaza")
    public abstract PropietarioPlaza getPropietarioPlaza(String codigoPropietarioPlaza);

    @Transaction
    public void borrarInsertarConfiguracionesCobro(List<ConfiguracionCobro> configuraciones){
        borrarConfiguracionesCobro();
        insertarConfiguracionesCobro(configuraciones);
    }

    @Query("DELETE FROM ConfiguracionCobro")
    abstract void borrarConfiguracionesCobro();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertarConfiguracionesCobro(List<ConfiguracionCobro> configuraciones);

    @Query("SELECT * FROM ConfiguracionCobro")
    public abstract List<ConfiguracionCobro> getConfiguracionesCobro();

    @Transaction
    public void borrarInsertarRecaudadoresWithTipoPlaza(List<Recaudador> recaudadores){
        borrarTiposPlazaRecaudador();

        insertarRecaudadores(recaudadores);
        for(Recaudador rec: recaudadores){
            insertarRecaudadorTiposPlaza(rec.getTipoPlazas());
        }
    }

    @Query("DELETE FROM RecaudadorTipoPlaza")
    abstract  void borrarTiposPlazaRecaudador();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract  void insertarRecaudadores(List<Recaudador> recaudadores);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract  void insertarRecaudadorTiposPlaza(List<RecaudadorTipoPlaza> tipoPlazasRecaudador);

    @Transaction
    public void insertarContribuyentesPlazasEstablecidasPropietarios(AuxActualizacionRemota aux){
        insertarContribuyentes(aux.getContribuyentes());
        insertarPlazas(aux.getPlazas());
        insertarPropietarioPlazas(aux.getPropietarioPlazas());

    }
    @Transaction
    public void insertarContribuciones(List<Contribucion> contribucionesRemote){
        for(Contribucion con: contribucionesRemote){
              int updateV= actualizarContribucionR(con.getRecaudador().getCodigoRecaudador(), con.getFechaModificacion(), con.getEstadoPago(), con.getPlaza().getCodigoPlaza(), con.getContribuyente().getCodigoContribuyente(),
                      con.getFecha(), con.getTipoPlaza().getCodigoTipoPlaza());
              if(updateV==0){
                  insertarContribucionRemote(con);
              }
        }


    }
    @Query("UPDATE Contribucion SET codigoRecaudador=:recaudador, fechaModificacion=:fechaModificacion, estadoPago=:estadoPago" +
            " WHERE codigoPlaza=:codigoPlaza AND codigoContribuyente=:codigoContribuyente AND fecha=:fechaContribucion AND codigoTipoPlaza=:codigoTipoPlaza")
    abstract  int actualizarContribucionR(String recaudador, Date fechaModificacion, Integer estadoPago, String codigoPlaza, String codigoContribuyente, Date fechaContribucion, String codigoTipoPlaza);

    @Insert()
    abstract void insertarContribucionRemote(Contribucion contribucion);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract  void insertarPlazas(List<Plaza> plazas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract  void insertarPropietarioPlazas(List<PropietarioPlaza> propietarioPlazas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract  void insertarActualizarContribucion(Contribucion contribucion);

    @Query("SELECT * FROM Folio WHERE codigoFolio=:codigoFolio")
     abstract Folio getFolio(String codigoFolio);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract   void insertarActualizarFolio(Folio folio);

    @Transaction
    public Contribucion finalizarContribucion(Contribucion contribucion){
        try {
            if (contribucion.getFolioDedicado() == null || contribucion.getFolioDedicado().isEmpty()) {
                Folio folio = getFolio(contribucion.getTipoPlaza().getCodigoTipoPlaza());
                if (folio != null) {
                    folio.setFolio(folio.getFolio() + 1);

                } else {
                    folio = new Folio();
                    folio.setCodigoFolio(contribucion.getTipoPlaza().getCodigoTipoPlaza());
                    folio.setFolio(1);

                }
                insertarActualizarFolio(folio);
                contribucion.setFolioDedicado(folio.getCodigoFolio() + "_" + String.format("%08d", folio.getFolio()));
            }
            insertarActualizarContribucion(contribucion);
            Contribucion con = getContribucion(contribucion.getContribuyente().getCodigoContribuyente(), contribucion.getPlaza().getCodigoPlaza(), contribucion.getFecha());

            return con;
        }
        catch (Exception e){
            return  null;
        }

    }

    @Query("SELECT * FROM Contribucion WHERE estadoServidor=0")
    public abstract List<Contribucion> recuperarContribucionesEstadoServidor();


    @Query("UPDATE Contribucion SET estadoServidor=:estado WHERE folioDedicado = :folio")
    public abstract void  updateContribucion(boolean estado, String folio);

    @Query("UPDATE Contribucion SET estadoServidor=:estado, estadoPago=:estadoPago WHERE folioDedicado = :folio")
    public abstract void  updateContribucion(boolean estado,int estadoPago, String folio);


    @Query("DELETE FROM EquipoRecaudador" )
    public abstract void  borrarEquipoRecaudador();



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract  void insertarUpdateEquipoRecaudador(EquipoRecaudador equipoRecaudador);

    @Query("SELECT * FROM EquipoRecaudador" )
    public abstract EquipoRecaudador  recuperarEquipoRecaudador();

    @Query("SELECT * FROM Plaza WHERE inServerPlaza=0")
    public abstract  List<Plaza> getPlazasToUpload();

    @Query("SELECT * FROM Contribuyente WHERE inServerContribuyente=0")
    public abstract  List<Contribuyente> getContribuyenteToUpload();

    @Query("SELECT * FROM PropietarioPlaza WHERE inServerPropietarioPlaza=0")
    public abstract  List<PropietarioPlaza> getPropietarioPlazaToUpload();

    @Query("UPDATE Plaza SET inServerPlaza=:estado WHERE codigoPlaza = :codigoPlaza")
    public abstract void  updatePlaza(boolean estado, String codigoPlaza);

    @Query("UPDATE Contribuyente SET inServerContribuyente=:estado WHERE codigoContribuyente = :codigoContribuyente")
    public abstract void  updateContribuyente(boolean estado, String codigoContribuyente);

    @Query("UPDATE PropietarioPlaza SET inServerPropietarioPlaza=:estado WHERE codigoPropietarioPlaza = :codigoPP")
    public abstract void  updatePropietarioPlaza(boolean estado, String codigoPP);


    @Query("UPDATE Contribuyente SET inServerContribuyente=:estado, codigoContribuyente=:codigoConNew WHERE codigoContribuyente = :codigoContribuyenteOld")
    public abstract void  updateContribuyente(boolean estado, String codigoContribuyenteOld, String codigoConNew);









}
