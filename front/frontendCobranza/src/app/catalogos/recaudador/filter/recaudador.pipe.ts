import { Pipe, PipeTransform } from '@angular/core';
import { Recaudador } from '../model/recaudador';

@Pipe({
  name: 'filterRecaudadores'
})
export class RecaudadorPipe implements PipeTransform {
  transform(items: Recaudador[], searchText: string): Recaudador[] {
    if(!items){
      return [];
    }
    if(!searchText){
      return items;
    }
    searchText= searchText.toLocaleLowerCase();
    return items.filter(it=>{
      return (it.codigoRecaudador+" "+ it.nombreRecaudador).toLocaleLowerCase().includes(searchText);

    });
    
  }

}
