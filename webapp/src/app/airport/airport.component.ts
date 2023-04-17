
import { Component } from '@angular/core';
import { AirportService } from './airport.service';

@Component({
  selector: 'app-airport',
  templateUrl: './airport.component.html',
  styleUrls: ['./airport.component.scss'],
})
export class AirportComponent {
  constructor(private airportService: AirportService) {
  }

}
