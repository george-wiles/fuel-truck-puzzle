package nz.org.wiles.klm.puzzle.layout;

import lombok.extern.slf4j.Slf4j;
import nz.org.wiles.klm.puzzle.model.Airport;
import nz.org.wiles.klm.puzzle.model.Grid;
import nz.org.wiles.klm.puzzle.model.grid.GridLayout;
import nz.org.wiles.klm.puzzle.model.Plane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.Point;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AirportLayoutFactory {

    private final Logger log = LoggerFactory.getLogger(AirportLayoutFactory.class);

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

        logLayout(layout);
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

    private void logLayout(Grid[][] layout) {
        for (int i = 0; i < layout.length; i++) {
            log.info(Arrays.stream(layout[i])
                                   .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                   .collect(Collectors.joining("|")));
        }
    }
}
