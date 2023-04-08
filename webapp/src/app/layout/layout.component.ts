import { Airport, ColCount, OccupationType, RowCount } from './../model/airport-model';
import { Component, Input, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormControl, FormGroup, FormArray, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent implements OnInit {

  @Input() layout: OccupationType[][];
  @Input() rowCounters: number[];
  @Input() colCounters: number[];

  layoutForm: FormGroup;
  rowCountFormControl!: FormControl[];
  colCountFormControl!: FormControl[];

  constructor(private http: HttpClient,
    private formBuilder: FormBuilder) {
    this.layout = [];
    this.rowCounters =[];
    this.colCounters = [];

    this.layoutForm = this.formBuilder.group({
      rowCountEls: this.formBuilder.array([]),
      colCountEls: this.formBuilder.array([])
    });
  }

  ngOnInit(): void {
    console.log(this.rowCounters);
    console.log(this.colCounters);
    this.initialiseLayout(this.rowCounters.length, this.colCounters.length);
    for(let i=0; i < this.rowCounters.length; i++) {
      this.addRowCountEl(this.rowCounters[i]);
    }
    for(let i=0; i < this.colCounters.length; i++) {
      this.addColCountEl(this.colCounters[i]);
    }
  }

  get rowCountEls() : FormArray {
    return this.layoutForm.get("rowCountEls") as FormArray
  }

  get colCountEls() : FormArray {
    return this.layoutForm.get("colCountEls") as FormArray
  }

  newRowEl(row: number): FormGroup {
    return this.formBuilder.group({
      rowEl: row
    })
 }

  newColEl(col: number): FormGroup {
    return this.formBuilder.group({
      colEl: col
    })
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
    console.log(this.layoutForm.value);

    let planes = [];
    for(let i=0; i < this.rowCounters.length; i++) {
      for(let j=0; j < this.colCounters.length; j++) {
        if (this.layout[i][j] === OccupationType.PLANE) {
          planes.push({ "x" : i, "y" : j})
        }
      }
    }

    for(let i=0; i < this.rowCountEls.length; i++) {
      let rowEl: RowCount = this.rowCountEls.controls[i].value;
      this.rowCounters[i] = rowEl.rowEl;
    }

    for(let i=0; i < this.colCountEls.length; i++) {
      let colEl: ColCount = this.colCountEls.controls[i].value;
      this.colCounters[i] = colEl.colEl;
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


