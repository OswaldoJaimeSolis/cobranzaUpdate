export class Usuario {
    usuario: string;
    /** sólo se envía; vacío al editar conserva la contraseña actual */
    password: string;
    nombre: string;
    rol: string;
    activo: boolean;

    constructor() {
        this.usuario = "";
        this.password = "";
        this.nombre = "";
        this.rol = "CONSULTA";
        this.activo = true;
    }
}
