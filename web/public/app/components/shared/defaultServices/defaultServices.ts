import { Injectable } from '@angular/core';
import { Http, Headers, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

import {URL_BASE} from '../../rutas';
import {URL_EXPORT_XLS} from '../../rutas';

@Injectable()
export class DefaultServices{
  constructor(private http:Http) {}

  getData(URL): Observable<any> {
    return this.http.get(URL).map((response) => response.json());
  }

  postJsonData(URL:string, data:any): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    return this.http.post(URL, data, {headers: headers});
  }

  putJsonData(URL:string, data:any): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    return this.http.put(URL, data, {headers: headers});
  }

  postGenerateXLS(json: Object): Observable<Response> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    return this.http.post(URL_EXPORT_XLS, json, {headers: headers});
  }

  exportToXLS(json: Object, filename){
    this.postGenerateXLS(json).subscribe(
        (res) => {
            var blob=new Blob([res['_body']]);
            var link=document.createElement('a');
            link.href = 'data:attachment/text,' + encodeURIComponent(res["_body"]);
            link.target = '_blank';
            link.download=filename+".xls";
            link.click();
        },
        err => console.error("EL ERROR FUE: ", err)
    );
  }
}