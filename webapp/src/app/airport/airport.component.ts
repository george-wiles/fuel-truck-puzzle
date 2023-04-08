import { Airport } from '../model/airport-model';
import { Component, OnInit } from '@angular/core';


@Component({
  selector: 'app-airport',
  templateUrl: './airport.component.html',
  styleUrls: ['./airport.component.sass']
})
export class AirportComponent  implements OnInit {
  airport: Airport;
  rowTruckCounters = [ 3, 1, 1, 1, 0, 2, 1 ];
  colTruckCounters = [ 2, 1, 1, 1, 1, 1, 2 ];

  constructor() {
    this.airport = {
      airportLayout: []
    };
  }

  ngOnInit(): void {
  }


}
