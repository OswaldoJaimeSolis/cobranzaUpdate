-- Configuración avanzada de cobros por tipo de plaza, morosidad y condonaciones.
--
-- En desarrollo no hace falta ejecutarlo: spring.jpa.hibernate.ddl-auto=update crea
-- estas mismas tablas y columnas al arrancar. Este script es para aplicar el cambio
-- a mano en una base donde ddl-auto no esté en "update" (producción).
--
-- Es idempotente: se puede volver a correr si falló a medias o si el backend ya
-- creó parte del esquema. Requiere MySQL 8.
--   mysql -u <usuario> -p jalpagob_cobranza < 2026_10_configuracion_cobros.sql
--
-- Las tablas nuevas se crean con el mismo charset que las existentes (utf8mb3): si
-- tomaran el default de MySQL 8 (utf8mb4) las llaves foráneas sobre varchar fallan.

DROP PROCEDURE IF EXISTS tmp_agregar_columna;

DELIMITER //
CREATE PROCEDURE tmp_agregar_columna(IN tabla VARCHAR(64), IN columna VARCHAR(64), IN definicion VARCHAR(255))
BEGIN
  IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                 WHERE table_schema = DATABASE() AND table_name = tabla AND column_name = columna) THEN
    SET @ddl = CONCAT('ALTER TABLE `', tabla, '` ADD COLUMN `', columna, '` ', definicion);
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END //
DELIMITER ;

CALL tmp_agregar_columna('recaudador', 'puede_condonar', 'bit(1) DEFAULT NULL');

-- Precio con descuento de cada vigencia; importe es el precio normal. Los precios de la
-- configuración de cobro salen de aquí (sólo tipos de plaza con importe global).
CALL tmp_agregar_columna('tipo_plaza_vigencia', 'precio_descuento', 'double DEFAULT NULL');

CALL tmp_agregar_columna('propietario_plaza', 'licencia_comercio', 'bit(1) DEFAULT NULL');
CALL tmp_agregar_columna('propietario_plaza', 'adeudo_anterior', 'double DEFAULT NULL');

CALL tmp_agregar_columna('contribucion', 'importe_normal', 'double DEFAULT NULL');
CALL tmp_agregar_columna('contribucion', 'descuento_aplicado', 'double DEFAULT NULL');
-- pagada con importe 0; detalle en condonacion
CALL tmp_agregar_columna('contribucion', 'condonada', 'bit(1) DEFAULT NULL');
-- abono al adeudo anterior, fuera del calendario
CALL tmp_agregar_columna('contribucion', 'pago_adeudo_anterior', 'bit(1) DEFAULT NULL');
CALL tmp_agregar_columna('contribucion', 'comentario', 'varchar(500) DEFAULT NULL');

CREATE TABLE IF NOT EXISTS configuracion_cobro (
  id_configuracion_cobro int NOT NULL AUTO_INCREMENT,
  codigo_tipo_plaza varchar(255) DEFAULT NULL,
  tipo_cobro varchar(255) DEFAULT NULL,           -- DIARIO, MENSUAL o ANUAL
  dias_semana varchar(255) DEFAULT NULL,          -- DIARIO: días ISO separados por coma (1 = lunes)
  fecha_inicio_cobro date DEFAULT NULL,           -- las obligaciones se generan desde esta fecha
  periodo_inicio_dia int DEFAULT NULL,            -- ANUAL: período de pago por omisión
  periodo_inicio_mes int DEFAULT NULL,
  periodo_fin_dia int DEFAULT NULL,
  periodo_fin_mes int DEFAULT NULL,
  PRIMARY KEY (id_configuracion_cobro),
  UNIQUE KEY uk_configuracion_cobro_tipo_plaza (codigo_tipo_plaza),
  CONSTRAINT fk_configuracion_cobro_tipo_plaza FOREIGN KEY (codigo_tipo_plaza) REFERENCES tipo_plaza (codigo_tipo_plaza)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

CREATE TABLE IF NOT EXISTS cobro_mensual_dia (
  id_cobro_mensual_dia int NOT NULL AUTO_INCREMENT,
  codigo_tipo_plaza varchar(255) DEFAULT NULL,
  anio int DEFAULT NULL,                          -- NULL: todos los años; con valor reemplaza al calendario general ese año
  mes int DEFAULT NULL,
  dia int DEFAULT NULL,                           -- NULL cuando ultimo_dia = 1
  ultimo_dia bit(1) DEFAULT NULL,
  PRIMARY KEY (id_cobro_mensual_dia),
  CONSTRAINT fk_cobro_mensual_dia_tipo_plaza FOREIGN KEY (codigo_tipo_plaza) REFERENCES tipo_plaza (codigo_tipo_plaza)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

CREATE TABLE IF NOT EXISTS cobro_periodo_anual (
  id_cobro_periodo_anual int NOT NULL AUTO_INCREMENT,
  codigo_tipo_plaza varchar(255) DEFAULT NULL,
  anio int DEFAULT NULL,
  fecha_inicio date DEFAULT NULL,
  fecha_fin date DEFAULT NULL,
  PRIMARY KEY (id_cobro_periodo_anual),
  CONSTRAINT fk_cobro_periodo_anual_tipo_plaza FOREIGN KEY (codigo_tipo_plaza) REFERENCES tipo_plaza (codigo_tipo_plaza)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

-- Si cobro_mensual_dia ya existía (creada por ddl-auto o por una versión previa de este script)
CALL tmp_agregar_columna('cobro_mensual_dia', 'anio', 'int DEFAULT NULL');

DROP PROCEDURE tmp_agregar_columna;

-- Bitácora de auditoría: una fila por contribución condonada desde la app móvil
CREATE TABLE IF NOT EXISTS condonacion (
  id_condonacion int NOT NULL AUTO_INCREMENT,
  id_condonacion_local varchar(255) DEFAULT NULL, -- folio de la contribución condonada
  codigo_recaudador varchar(255) DEFAULT NULL,
  codigo_equipo_rec varchar(255) DEFAULT NULL,
  codigo_plaza varchar(255) DEFAULT NULL,
  codigo_tipo_plaza varchar(255) DEFAULT NULL,
  codigo_contribuyente varchar(255) DEFAULT NULL,
  fecha_condonacion datetime(6) DEFAULT NULL,
  fecha_obligacion date DEFAULT NULL,             -- fecha de la contribución condonada
  monto_condonado double DEFAULT NULL,
  motivo varchar(500) NOT NULL,
  origen varchar(255) DEFAULT NULL,               -- dispositivo origen (MOBILE)
  PRIMARY KEY (id_condonacion),
  UNIQUE KEY uk_condonacion_id_local (id_condonacion_local),
  CONSTRAINT fk_condonacion_recaudador FOREIGN KEY (codigo_recaudador) REFERENCES recaudador (codigo_recaudador),
  CONSTRAINT fk_condonacion_equipo FOREIGN KEY (codigo_equipo_rec) REFERENCES equipo_recaudador (codigo_equipo_rec),
  CONSTRAINT fk_condonacion_plaza FOREIGN KEY (codigo_plaza) REFERENCES plaza (codigo_plaza),
  CONSTRAINT fk_condonacion_tipo_plaza FOREIGN KEY (codigo_tipo_plaza) REFERENCES tipo_plaza (codigo_tipo_plaza),
  CONSTRAINT fk_condonacion_contribuyente FOREIGN KEY (codigo_contribuyente) REFERENCES contribuyente (codigo_contribuyente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
