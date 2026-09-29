import { TipoPlaza } from './tipo-plaza';

export class TipoPlazaVigencia {
    idTipoPlazaVigencia: number;
    tipoPlaza: TipoPlaza;
    vigenciaInicial: Date;
    vigenciaFinal: Date;
    importe: number;

    constructor(){
        this.idTipoPlazaVigencia= 0;
        this.tipoPlaza= null;
        this.vigenciaInicial= new Date();
        this.vigenciaFinal= new Date('2099-12-31');
        this.importe=0;

    }
}
