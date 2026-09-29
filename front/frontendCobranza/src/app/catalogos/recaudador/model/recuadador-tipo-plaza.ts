import { Recaudador } from './recaudador';
import { TipoPlaza } from '../../tiposplaza/model/tipo-plaza';

export class RecuadadorTipoPlaza {
    idRecaudadorTipoPlaza: number;
    recaudador: Recaudador;
    tipoPlaza: TipoPlaza;

    constructor(){
        this.recaudador= new Recaudador();
        this.tipoPlaza= new TipoPlaza();
    }


}
