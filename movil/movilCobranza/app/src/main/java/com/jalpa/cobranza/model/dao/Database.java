package com.jalpa.cobranza.model.dao;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Database {

    private Context mCtx;
    private static Database mInstance;

    //our app database object
    private AppDataBase appDatabase;

    // Configuración de cobro, permiso y datos de condonación, descuento y adeudo. Se migra
    // en lugar de recrear la base para no perder contribuciones aún no enviadas al servidor.
    // Recaudador va embebido en Contribucion y RecaudadorTipoPlaza, por eso la columna se repite.
    static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE Recaudador ADD COLUMN puedeCondonar INTEGER");
            db.execSQL("ALTER TABLE RecaudadorTipoPlaza ADD COLUMN puedeCondonar INTEGER");
            db.execSQL("ALTER TABLE Contribucion ADD COLUMN puedeCondonar INTEGER");
            db.execSQL("ALTER TABLE Contribucion ADD COLUMN importeNormal REAL");
            db.execSQL("ALTER TABLE Contribucion ADD COLUMN descuento REAL");
            db.execSQL("ALTER TABLE Contribucion ADD COLUMN condonada INTEGER");
            db.execSQL("ALTER TABLE Contribucion ADD COLUMN motivoCondonacion TEXT");
            db.execSQL("ALTER TABLE Contribucion ADD COLUMN montoCondonado REAL");
            db.execSQL("ALTER TABLE PropietarioPlaza ADD COLUMN adeudo REAL");
            db.execSQL("ALTER TABLE PropietarioPlaza ADD COLUMN fechasVencidas TEXT");
            db.execSQL("ALTER TABLE PropietarioPlaza ADD COLUMN adeudoAnterior REAL");
            db.execSQL("ALTER TABLE Contribucion ADD COLUMN pagoAdeudoAnterior INTEGER");
            db.execSQL("ALTER TABLE Contribucion ADD COLUMN comentario TEXT");
            db.execSQL("CREATE TABLE IF NOT EXISTS `ConfiguracionCobro` (`codigoTipoPlazaCobro` TEXT NOT NULL, `tipoCobro` TEXT, `diasSemana` TEXT, `fechaInicioCobro` INTEGER, `precioDescuento` REAL, `precioNormal` REAL, `diasMes` TEXT, `periodoInicioDia` INTEGER, `periodoInicioMes` INTEGER, `periodoFinDia` INTEGER, `periodoFinMes` INTEGER, `periodosAnuales` TEXT, PRIMARY KEY(`codigoTipoPlazaCobro`))");
        }
    };

    // Los precios de la configuración de cobro pasan a la vigencia del tipo de plaza. SQLite en
    // minSdk 26 no tiene DROP COLUMN, así que ConfiguracionCobro se reconstruye sin los precios.
    static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE TipoPlazaHistorial ADD COLUMN precioDescuento REAL");
            db.execSQL("CREATE TABLE IF NOT EXISTS `ConfiguracionCobro_nueva` (`codigoTipoPlazaCobro` TEXT NOT NULL, `tipoCobro` TEXT, `diasSemana` TEXT, `fechaInicioCobro` INTEGER, `diasMes` TEXT, `periodoInicioDia` INTEGER, `periodoInicioMes` INTEGER, `periodoFinDia` INTEGER, `periodoFinMes` INTEGER, `periodosAnuales` TEXT, PRIMARY KEY(`codigoTipoPlazaCobro`))");
            db.execSQL("INSERT INTO ConfiguracionCobro_nueva (codigoTipoPlazaCobro, tipoCobro, diasSemana, fechaInicioCobro, diasMes, periodoInicioDia, periodoInicioMes, periodoFinDia, periodoFinMes, periodosAnuales) SELECT codigoTipoPlazaCobro, tipoCobro, diasSemana, fechaInicioCobro, diasMes, periodoInicioDia, periodoInicioMes, periodoFinDia, periodoFinMes, periodosAnuales FROM ConfiguracionCobro");
            db.execSQL("DROP TABLE ConfiguracionCobro");
            db.execSQL("ALTER TABLE ConfiguracionCobro_nueva RENAME TO ConfiguracionCobro");
        }
    };

    private Database(Context mCtx) {
        this.mCtx = mCtx;

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(mCtx, AppDataBase.class, "cobranza").addMigrations(MIGRATION_11_12, MIGRATION_12_13).fallbackToDestructiveMigration().build();
    }

    public static synchronized Database getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new Database(mCtx);
        }
        return mInstance;
    }

    public AppDataBase getAppDatabase() {
        return appDatabase;
    }
}
