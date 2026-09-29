export class TipoPlaza {
    codigoTipoPlaza: string;
    descripcionTipoPlaza: string;
    porImporteGlobalTipoPlaza: boolean;
    leyendaTipoPlaza: string;
    addLocal: boolean;

    constructor(){
        this.codigoTipoPlaza="";
        this.descripcionTipoPlaza="";
        this.porImporteGlobalTipoPlaza= false;
        this.leyendaTipoPlaza="";
        this.addLocal= false;
    }
}
