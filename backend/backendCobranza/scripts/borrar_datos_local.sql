-- Borra los datos de la base local de desarrollo y deja el esquema intacto.
-- TRUNCATE también reinicia los AUTO_INCREMENT. NO correr en producción.
--   MYSQL_PWD=devI mysql -uoswaldo_dev -h127.0.0.1 jalpagob_cobranza < borrar_datos_local.sql
--
-- Para conservar los catálogos y borrar sólo los movimientos, comenta la sección 2.

USE jalpagob_cobranza;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Movimientos
TRUNCATE TABLE condonacion;
TRUNCATE TABLE contribucion;
TRUNCATE TABLE folio;

-- 2. Catálogos y configuración
TRUNCATE TABLE propietario_plaza;
TRUNCATE TABLE plaza;
TRUNCATE TABLE contribuyente;
TRUNCATE TABLE recaudador_tipo_plaza;
TRUNCATE TABLE recaudador;
TRUNCATE TABLE equipo_recaudador;
TRUNCATE TABLE cobro_mensual_dia;
TRUNCATE TABLE cobro_periodo_anual;
TRUNCATE TABLE configuracion_cobro;
TRUNCATE TABLE tipo_plaza_vigencia;
TRUNCATE TABLE tipo_plaza;

SET FOREIGN_KEY_CHECKS = 1;
