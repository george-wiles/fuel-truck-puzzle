export interface Airport {
  rowTruckCounters: number[];
  colTruckCounters: number[];
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

export interface PlaneLocation {
  x: number,
  y: number
}

export interface AirportLayoutRequest {
  numberRows: number,
  numberColumns: number,
  rowTruckCounters: number[],
  colTruckCounters: number[],
  planes: PlaneLocation[],
}
