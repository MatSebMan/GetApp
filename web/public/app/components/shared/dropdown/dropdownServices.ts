import { Injectable } from '@angular/core';
import { Http, Headers, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

import {URL_BASE} from '../../rutas';

@Injectable()
export class DropdownServices {
  constructor(private http:Http) {}

  getData(URL): Observable<any> {
    return this.http.get(URL_BASE+URL).map((response) => response.json());
  }
}
