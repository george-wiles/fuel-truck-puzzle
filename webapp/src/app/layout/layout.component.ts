import { Airport, OccupationType } from './../model/airport-model';
import { Component, Input, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent implements OnInit {

  @Input() layout: OccupationType[][];
  @Input() rowCounters: number[];
  @Input() colCounters: number[];

  constructor(private http: HttpClient) {
    this.layout = [];
    this.rowCounters =[];
    this.colCounters = [];
  }

  ngOnInit(): void {
    console.log(this.rowCounters);
    console.log(this.colCounters);
    this.initialiseLayout(this.rowCounters.length, this.colCounters.length);


  }

  isPlaneOccupied(type: OccupationType): boolean {
    return type === OccupationType.PLANE;
  }

  isTruckOccupied(type: OccupationType): boolean {
    return type === OccupationType.FUEL_TRUCK;
  }

  isEditOccupied(type: OccupationType): boolean {
    return type === OccupationType.EDIT;
  }

  getSvgSrc(type: OccupationType): string {

   // bi bi-pencil-square
    if (type as OccupationType === OccupationType.PLANE) {
      return 'bi bi-airplane grid-icon';
    } else if (type as OccupationType === OccupationType.FUEL_TRUCK) {
      return 'bi bi-truck grid-icon';
    }  else if (type as OccupationType === OccupationType.EDIT) {
      return  'bi bi-pencil-square grid-icon';
    }
    return 'bi bi-dot blank-icon';
  }

  initialiseLayout(maxRow: number, maxCol: number) {
    for(let i=0; i < maxRow; i++) {
      this.layout[i] = [];
      for(let j=0; j < maxCol; j++) {
        this.layout[i][j] = OccupationType.EMPTY
      }
    }
  }

  onClick(row: number, col: number) {
    this.layout[row][col] =
      (this.layout[row][col] === OccupationType.PLANE) ? OccupationType.EMPTY : OccupationType.PLANE;
  }

  onSubmit() {
    let planes = [];
    for(let i=0; i < this.rowCounters.length; i++) {
      for(let j=0; j < this.colCounters.length; j++) {
        if (this.layout[i][j] === OccupationType.PLANE) {
          planes.push({ "x" : i, "y" : j})
        }
      }
    }

    let postData = {
      "numberRows" : this.rowCounters.length,
      "numberColumns" : this.colCounters.length,
      "rowTruckCounters" : this.rowCounters,
      "colTruckCounters" : this.colCounters,
      "planes" : planes
    };


    this.http.post<Airport>(
      'http://localhost:8080/api/v1/layout',
      postData
    ).subscribe({
      next: (respData) => {
        this.layout = respData.airportLayout;

      },
      error: (err) => {
        alert("No Solution Found");
      },
      complete: () => {
        console.info(' complete');
      }
    });

  }

  onClear() {
    this.initialiseLayout(this.rowCounters.length, this.colCounters.length)

  }

}


