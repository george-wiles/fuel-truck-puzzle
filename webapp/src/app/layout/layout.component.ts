import {
  Airport,
  AirportLayoutRequest,
  ColCount,
  OccupationType,
  RowCount,
} from '../model/airport.model';
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormControl, FormGroup, FormArray, FormBuilder } from '@angular/forms';
import { AirportService } from '../airport/airport.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss'],
})
export class LayoutComponent implements OnInit, OnDestroy {
  @Input() airport: Airport;

  layoutForm: FormGroup;
  rowCountFormControl!: FormControl[];
  colCountFormControl!: FormControl[];
  airportSubscription!: Subscription;

  constructor(
    private airportService: AirportService,
    private http: HttpClient,
    private formBuilder: FormBuilder
  ) {
    this.airport = airportService.airport;
    this.layoutForm = this.formBuilder.group({
      rowCountEls: this.formBuilder.array([]),
      colCountEls: this.formBuilder.array([]),
    });
  }

  ngOnInit(): void {
    console.log(this.airport.rowTruckCounters);
    console.log(this.airport.colTruckCounters);

    for (let i = 0; i < this.airport.rowTruckCounters.length; i++) {
      this.addRowCountEl(this.airport.rowTruckCounters[i]);
    }
    for (let i = 0; i < this.airport.colTruckCounters.length; i++) {
      this.addColCountEl(this.airport.colTruckCounters[i]);
    }
  }

  ngOnDestroy(): void {
    if (this.airportSubscription) {
      this.airportSubscription.unsubscribe();
    }
  }

  get rowCountEls(): FormArray {
    return this.layoutForm.get('rowCountEls') as FormArray;
  }

  get colCountEls(): FormArray {
    return this.layoutForm.get('colCountEls') as FormArray;
  }

  newRowEl(row: number): FormGroup {
    return this.formBuilder.group({
      rowEl: row,
    });
  }

  newColEl(col: number): FormGroup {
    return this.formBuilder.group({
      colEl: col,
    });
  }

  addRowCountEl(row: number) {
    this.rowCountEls.push(this.newRowEl(row));
  }

  addColCountEl(col: number) {
    this.colCountEls.push(this.newColEl(col));
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
    if ((type as OccupationType) === OccupationType.PLANE) {
      return 'bi bi-airplane grid-icon';
    } else if ((type as OccupationType) === OccupationType.FUEL_TRUCK) {
      return 'bi bi-truck grid-icon';
    } else if ((type as OccupationType) === OccupationType.EDIT) {
      return 'bi bi-pencil-square grid-icon';
    }
    return 'bi bi-dot blank-icon';
  }

  onClick(row: number, col: number) {
    this.airport.airportLayout[row][col] =
      this.airport.airportLayout[row][col] === OccupationType.PLANE
        ? OccupationType.EMPTY
        : OccupationType.PLANE;
  }

  onSubmit() {
    this.airportSubscription = this.airportService
      .fetchLayout(this.setupRequest()).subscribe({
        next: (respData) => {
          this.airport.airportLayout = respData.airportLayout;
        },
        error: (err) => {
          alert('No Solution Found');
        },
        complete: () => {
          console.info(' complete');
        },
    });
  }

  onClear() {
    this.airportService.initialiseLayout();
  }

  setupRequest() : AirportLayoutRequest {
    let planes = [];
    for (let i = 0; i < this.airport.rowTruckCounters.length; i++) {
      for (let j = 0; j < this.airport.colTruckCounters.length; j++) {
        if (this.airport.airportLayout[i][j] === OccupationType.PLANE) {
          planes.push({ x: i, y: j });
        }
      }
    }

    for(let i=0; i < this.rowCountEls.length; i++) {
      let rowEl: RowCount = this.rowCountEls.controls[i].value;
      this.airport.rowTruckCounters[i] = rowEl.rowEl;
    }

    for(let i=0; i < this.colCountEls.length; i++) {
      let colEl: ColCount = this.colCountEls.controls[i].value;
      this.airport.colTruckCounters[i] = colEl.colEl;
    }

    return {
      numberRows: this.airport.rowTruckCounters.length,
      numberColumns: this.airport.colTruckCounters.length,
      rowTruckCounters: this.airport.rowTruckCounters,
      colTruckCounters: this.airport.colTruckCounters,
      planes: planes,
    };
  }
}


