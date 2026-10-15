export class DiaMes {
    /** null = todos los años; con valor reemplaza al calendario general en ese año */
    anio: number;
    mes: number;
    dia: number;
    ultimoDia: boolean;
}

export class PeriodoAnual {
    anio: number;
    fechaInicio: string;
    fechaFin: string;
}

/** Configuración de cobro de un tipo de plaza. Las fechas viajan como yyyy-MM-dd. */
export class ConfiguracionCobro {
    codigoTipoPlaza: string;
    tipoCobro: string;
    diasSemana: number[];
    fechaInicioCobro: string;
    diasMes: DiaMes[];
    periodoInicioDia: number;
    periodoInicioMes: number;
    periodoFinDia: number;
    periodoFinMes: number;
    periodosAnuales: PeriodoAnual[];

    constructor() {
        this.diasSemana = [];
        this.diasMes = [];
        this.periodosAnuales = [];
    }
}
