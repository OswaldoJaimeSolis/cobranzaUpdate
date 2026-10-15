export class Recaudador {
    codigoRecaudador: string;
    passRecaudador: string;
    nombreRecaudador: string;
    activoRecaudador: boolean;
    puedeCondonar: boolean;

    constructor(){
        this.codigoRecaudador="";
        this.passRecaudador="";
        this.nombreRecaudador="";
        this.activoRecaudador=true;
        this.puedeCondonar=false;
    }
}
