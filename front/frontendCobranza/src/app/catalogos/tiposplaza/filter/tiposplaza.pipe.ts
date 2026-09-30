import { Pipe, PipeTransform } from '@angular/core';
import { TipoPlaza } from '../model/tipo-plaza';

@Pipe({
    name: 'filterTiposPlaza',
    standalone: false
})
export class TiposplazaPipe implements PipeTransform {

  transform(items: TipoPlaza[], searchText: string): TipoPlaza[] {
    if(!items){
      return [];
    }
    if(!searchText){
      return items;
    }
    searchText= searchText.toLocaleLowerCase();
    return items.filter(it=>{
      return (it.codigoTipoPlaza+" "+ it.descripcionTipoPlaza).toLocaleLowerCase().includes(searchText);

    });
    
  }

}
