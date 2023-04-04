import { Airport } from '../model/airport-model';
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'app-airport',
  templateUrl: './airport.component.html',
  styleUrls: ['./airport.component.sass']
})
export class AirportComponent  implements OnInit {
  airport: Airport;
  rowTruckCounters = [ 3, 1, 1, 1, 0, 2, 1 ];
  colTruckCounters = [ 2, 1, 1, 1, 1, 1, 2 ];

  constructor(private http: HttpClient) {
    this.airport = {
      airportLayout: []
    };
  }

  ngOnInit(): void {

    let postData = {
      "numberRows" : 7,
      "numberColumns" : 7,
      "rowTruckCounters" : this.rowTruckCounters,
      "colTruckCounters" : this.colTruckCounters,
      "planes" : [{
        "x" : 0, "y" : 2 }, {
        "x" : 1, "y" : 0 }, {
        "x" : 1, "y" : 4 }, {
        "x" : 1, "y" : 6 }, {
        "x" : 2, "y" : 6 }, {
        "x" : 4, "y" : 1 }, {
        "x" : 5, "y" : 3 }, {
        "x" : 6, "y" : 0 }, {
        "x" : 6, "y" : 6 } ]
    };

    this.http.post<Airport>(
      'http://localhost:8080/api/v1/layout',
      postData
    ).subscribe(responseData => {
      console.log(responseData)
      this.airport = responseData;
    });
  }


}
