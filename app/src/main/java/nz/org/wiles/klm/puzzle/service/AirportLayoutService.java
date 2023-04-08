package nz.org.wiles.klm.puzzle.service;

import nz.org.wiles.klm.puzzle.model.Airport;
import nz.org.wiles.klm.puzzle.model.Plane;

public interface AirportLayoutService {

    Airport initialiseLayout(int[] xFuelTruckCounters,
                             int[] yFuelTruckCounters,
                             Plane[] planes);
}
