import { Plaza } from '../../plaza/model/plaza';
import { TipoPlaza } from '../../tiposplaza/model/tipo-plaza';
import { Contribuyente } from '../../contribuyente/model/contribuyente';

export class PropietarioPlaza {
    idPropietarioPlaza: string;
    plaza: Plaza;
    tipoPlaza: TipoPlaza;
    contribuyente: Contribuyente;
    vigenciaInicial: Date;
    vigenciaFinal: Date;
    giroDescripcion: string;
    importe: number;

    constructor() {
        this.plaza = new Plaza();
        this.tipoPlaza = new TipoPlaza();
        this.contribuyente = new Contribuyente();
        this.vigenciaInicial = new Date();
        this.vigenciaFinal = new Date();
        this.giroDescripcion = "";


    }
}
