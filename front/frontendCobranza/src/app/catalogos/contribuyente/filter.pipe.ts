import { Pipe, PipeTransform } from '@angular/core';
import { Contribuyente } from './model/contribuyente';

@Pipe({
    name: 'filterContribuyentes',
    standalone: false
})
export class FilterPipe implements PipeTransform {

  transform(items: Contribuyente[], searchText: string): Contribuyente[] {
    if(!items){
      return [];
    }
    if(!searchText){
      return items;
    }
    searchText= searchText.toLocaleLowerCase();
    return items.filter(it=>{
      return (it.codigoContribuyente+" "+ it.nombre+" "+it.apePaterno+" "+it.apeMaterno).toLocaleLowerCase().includes(searchText);

    });
    
  }

}
