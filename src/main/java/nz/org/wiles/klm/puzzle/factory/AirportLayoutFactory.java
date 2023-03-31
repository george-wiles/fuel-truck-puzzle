package nz.org.wiles.klm.puzzle.factory;

import nz.org.wiles.klm.puzzle.model.Airport;
import nz.org.wiles.klm.puzzle.model.Grid;
import nz.org.wiles.klm.puzzle.model.GridLayout;
import nz.org.wiles.klm.puzzle.model.Plane;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AirportLayoutFactory {

    private final LayoutProximityManager layoutProximityManager;

    public AirportLayoutFactory(LayoutProximityManager layoutProximityManager) {
        this.layoutProximityManager = layoutProximityManager;
    }

    public Airport create(int[] rowFuelTruckCounters,
                                int[] colFuelTruckCounters,
                                Plane[] planes) {

        final Map<Point, Plane> planesByPoint = Arrays.stream(planes).collect(
                Collectors.toMap(
                        plane -> plane.getGridPos(),
                        plane -> plane));

        final Grid[][] layout = layoutProximityManager.createLayout(
            rowFuelTruckCounters, colFuelTruckCounters, planesByPoint);

        final GridLayout gridLayout = GridLayout.builder()
                .layout(layout)
                .build();

        return Airport.builder()
                .dimensionX(rowFuelTruckCounters.length)
                .dimensionY(rowFuelTruckCounters.length)
                .numPlanes(planes.length)
                .numFuelTrucks(planes.length)
                .gridLayout(gridLayout)
                .build();
    }
}
