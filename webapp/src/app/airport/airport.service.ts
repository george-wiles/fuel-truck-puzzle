import { Injectable, OnInit } from '@angular/core';
import { Airport, AirportLayoutRequest, OccupationType } from '../model/airport.model';
import { Observable, Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AirportService {
  airport: Airport;

  constructor(private http: HttpClient) {
    // setup an intial layout structure
    let initialAirport: Airport = {
      rowTruckCounters: [3, 1, 1, 1, 0, 2, 1],
      colTruckCounters: [2, 1, 1, 1, 1, 1, 2],
      airportLayout: [],
    };
    for (let i = 0; i < initialAirport.rowTruckCounters.length; i++) {
      initialAirport.airportLayout[i] = [];
      for (let j = 0; j < initialAirport.colTruckCounters.length; j++) {
        initialAirport.airportLayout[i][j] = OccupationType.EMPTY;
      }
    }
    initialAirport.airportLayout[0][2] = OccupationType.PLANE;
    initialAirport.airportLayout[1][0] = OccupationType.PLANE;
    initialAirport.airportLayout[1][4] = OccupationType.PLANE;
    initialAirport.airportLayout[1][6] = OccupationType.PLANE;
    initialAirport.airportLayout[2][6] = OccupationType.PLANE;
    initialAirport.airportLayout[4][1] = OccupationType.PLANE;
    initialAirport.airportLayout[5][3] = OccupationType.PLANE;
    initialAirport.airportLayout[6][0] = OccupationType.PLANE;
    initialAirport.airportLayout[6][6] = OccupationType.PLANE;

    this.airport = initialAirport;
  }

  initialiseLayout() {
    for (let i = 0; i < this.airport.rowTruckCounters.length; i++) {
      this.airport.rowTruckCounters[i] = 0;
      for (let j = 0; j < this.airport.colTruckCounters.length; j++) {
        this.airport.airportLayout[i][j] = OccupationType.EMPTY;
        this.airport.colTruckCounters[j] = 0;
      }
    }

  }


  fetchLayout(postData: AirportLayoutRequest ) : Observable<Airport> {
    return this.http.post<Airport>('http://localhost:8080/api/v1/layout', postData);
  }
}
