export interface Airport {
  airportLayout: OccupationType[][];
}

export interface ColCount {
  colEl: number
}

export interface RowCount {
  rowEl: number
}

export enum OccupationType {
  PLANE = "PLANE",
  FUEL_TRUCK = "FUEL_TRUCK",
  EMPTY = "EMPTY",
  EDIT = "EDIT"
}
