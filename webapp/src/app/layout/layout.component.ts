
import { OccupationType } from './../model/airport-model';
import { Component, Input, OnInit } from '@angular/core';
import { EMPTY } from 'rxjs';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent implements OnInit {
  @Input() layout: OccupationType[][];
  @Input() rowCounters: number[];
  @Input() colCounters: number[];

  constructor() {
    this.layout = [];
    this.rowCounters =[];
    this.colCounters = [];
  }

  ngOnInit(): void {
  }

  isPlaneOccupied(type: OccupationType): boolean {
    return type === OccupationType.PLANE;
  }

  isTruckOccupied(type: OccupationType): boolean {
    return type === OccupationType.FUEL_TRUCK;
  }

  getSvg(type: OccupationType): string {
    let svgPath = './images/airplane.svg';
    if (type as OccupationType === OccupationType.PLANE) {
      svgPath = 'images/airplane.svg';
      console.log(`assigning : ${type}, path: ${svgPath} `);

    } else if (type as OccupationType === OccupationType.FUEL_TRUCK) {
      svgPath =  'images/truck.svg';
    }
    console.log(`getSvg type: ${type}, path: ${svgPath} `);
    return svgPath;
  }

}
