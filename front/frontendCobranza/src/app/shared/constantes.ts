import { environment } from 'src/environments/environment';

export class Constantes {
    // Viene del entorno: environment.ts en desarrollo, environment.prod.ts al compilar con
    // --configuration production (ver angular.json fileReplacements)
    static base_url: string = environment.apiUrl;
}
