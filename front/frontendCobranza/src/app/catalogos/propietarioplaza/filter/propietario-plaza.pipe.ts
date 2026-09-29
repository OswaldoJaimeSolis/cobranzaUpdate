import { Pipe, PipeTransform } from '@angular/core';
import { PropietarioPlaza } from '../model/propietario-plaza';

@Pipe({
  name: 'filterPropietarioPlaza'
})
export class PropietarioPlazaPipe implements PipeTransform {

  transform(items: PropietarioPlaza[], searchText: string): PropietarioPlaza[] {
    if(!items){
      return [];
    }
    if(!searchText){
      return items;
    }
    searchText= searchText.toLocaleLowerCase();
    return items.filter(it=>{
      /*let cadena:string= it.plaza.codigoPlaza+" "+ it.contribuyente.codigoContribuyente+" "+ it.contribuyente.nombre+" "+it.contribuyente.apePaterno+" "+ it.contribuyente.apeMaterno+
      " "+ it.tipoPlaza.codigoTipoPlaza;*/
      let cadena:string= it.plaza+" "+ it.contribuyente+ " "+ it.tipoPlaza


      return (cadena).toLocaleLowerCase().includes(searchText);

    });
    
  }


}
