export interface Airport {
  airportLayout: OccupationType[][];

}

export enum OccupationType {
  PLANE = "PLANE",
  FUEL_TRUCK = "FUEL_TRUCK",
  EMPTY = "EMPTY",
  EDIT = "EDIT"
}
