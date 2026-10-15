import { TipoPlaza } from './tipo-plaza';

export class TipoPlazaVigencia {
    idTipoPlazaVigencia: number;
    tipoPlaza: TipoPlaza;
    vigenciaInicial: Date;
    vigenciaFinal: Date;
    /** Precio normal */
    importe: number;
    /** Precio a más tardar en la fecha de cobro; lo usa la configuración de cobro */
    precioDescuento: number;

    constructor(){
        // null: el id lo genera la base; Hibernate 6 toma un id 0 como registro existente
        this.idTipoPlazaVigencia= null;
        this.tipoPlaza= null;
        this.vigenciaInicial= new Date();
        this.vigenciaFinal= new Date('2099-12-31');
        this.importe=0;
        this.precioDescuento=null;

    }
}
