package nz.org.wiles.klm.puzzle.service.impl;

import nz.org.wiles.klm.puzzle.factory.AirportLayoutFactory;
import nz.org.wiles.klm.puzzle.model.Airport;
import nz.org.wiles.klm.puzzle.model.Plane;
import nz.org.wiles.klm.puzzle.service.AirportLayoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AirportLayoutServiceImp implements AirportLayoutService {

    private final AirportLayoutFactory airportLayoutFactory;

    @Autowired
    public AirportLayoutServiceImp(AirportLayoutFactory airportLayoutFactory) {
        this.airportLayoutFactory = airportLayoutFactory;
    }

    @Override
    public Airport initialiseLayout(int[] rowFuelTruckCounters,
                                    int[] colFuelTruckCounters,
                                    Plane[] planes) {

        return airportLayoutFactory.create(rowFuelTruckCounters,
                colFuelTruckCounters,
                planes);
    }


}
