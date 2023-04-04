package nz.org.wiles.klm.puzzle.service.impl;

import nz.org.wiles.klm.puzzle.model.Airport;
import nz.org.wiles.klm.puzzle.model.Grid;
import nz.org.wiles.klm.puzzle.model.Plane;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

import static nz.org.wiles.klm.puzzle.model.OccupationType.FUEL_TRUCK;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AirportLayoutServiceImpTest {

    @Autowired
    private AirportLayoutServiceImp candidate;

    @Test
    void When_ModelSolutionIsConfigured_Then_LayoutIsAsExpected() {
        // setup
        int[] colFuelTruckCounters = {2, 1, 1, 1, 1, 1, 2};
        int[] rowFuelTruckCounters = {3, 1, 1, 1, 0, 2, 1};

        Plane[] planes = {
            Plane.builder().gridPos(new Point(0,2)).build(),
            Plane.builder().gridPos(new Point(1,0)).build(),
            Plane.builder().gridPos(new Point(1,4)).build(),
            Plane.builder().gridPos(new Point(1,6)).build(),
            Plane.builder().gridPos(new Point(2,6)).build(),
            Plane.builder().gridPos(new Point(4,1)).build(),
            Plane.builder().gridPos(new Point(5,3)).build(),
            Plane.builder().gridPos(new Point(6,0)).build(),
            Plane.builder().gridPos(new Point(6,6)).build(),
        };

        // act
        Airport airport = candidate.initialiseLayout(
                rowFuelTruckCounters,
                colFuelTruckCounters,
                planes);

        // verify grid col and row counts against expected fuel truck counters
        Grid[][] layout = airport.getGridLayout().getLayout();
        for (int i = 0; i < rowFuelTruckCounters.length; i++) {
            int rowCount = Arrays.stream(layout[i]).mapToInt(e -> e.getOccupationType().equals(FUEL_TRUCK) ? 1 : 0).sum();
            assertEquals(rowFuelTruckCounters[i], rowCount);

            final int col = i;
            int colCount = Arrays.stream(layout).mapToInt(e -> e[col].getOccupationType().equals(FUEL_TRUCK) ? 1 : 0).sum();
            assertEquals(colFuelTruckCounters[i], colCount);
        }


        // verify locations
        assertEquals("F|E|P|E|F|E|F", Arrays.stream(airport.getGridLayout().getLayout()[0])
                                          .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                          .collect(Collectors.joining("|")));

        assertEquals("P|E|F|E|P|E|P", Arrays.stream(airport.getGridLayout().getLayout()[1])
                                          .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                          .collect(Collectors.joining("|")));

        assertEquals("E|E|E|E|E|F|P", Arrays.stream(airport.getGridLayout().getLayout()[2])
                                          .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                          .collect(Collectors.joining("|")));

        assertEquals("E|F|E|E|E|E|E", Arrays.stream(airport.getGridLayout().getLayout()[3])
                                          .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                          .collect(Collectors.joining("|")));

        assertEquals("E|P|E|E|E|E|E", Arrays.stream(airport.getGridLayout().getLayout()[4])
                                          .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                          .collect(Collectors.joining("|")));

        assertEquals("F|E|E|P|E|E|F", Arrays.stream(airport.getGridLayout().getLayout()[5])
                                          .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                          .collect(Collectors.joining("|")));

        assertEquals("P|E|E|F|E|E|P", Arrays.stream(airport.getGridLayout().getLayout()[6])
                                          .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                          .collect(Collectors.joining("|")));

     }

    @Test
    void When_4x4and3Planes_Then_LayoutIsAsExpected() {
        // setup
        int[] rowFuelTruckCounters = {1, 0, 1, 1};
        int[] colFuelTruckCounters = {1, 1, 1, 0};

        Plane[] planes = {
            Plane.builder().gridPos(new Point(0,0)).build(),
            Plane.builder().gridPos(new Point(1,2)).build(),
            Plane.builder().gridPos(new Point(3,1)).build()
        };

        // act
        Airport airport = candidate.initialiseLayout(
            rowFuelTruckCounters,
            colFuelTruckCounters,
            planes);

        // verify
        assertEquals(4, airport.getGridLayout().getLayout().length);

        assertEquals("P|F|E|E", Arrays.stream(airport.getGridLayout().getLayout()[0])
                                    .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                    .collect(Collectors.joining("|")));

        assertEquals("E|E|P|E", Arrays.stream(airport.getGridLayout().getLayout()[1])
                                    .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                    .collect(Collectors.joining("|")));

        assertEquals("E|E|F|E", Arrays.stream(airport.getGridLayout().getLayout()[2])
                                    .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                    .collect(Collectors.joining("|")));

        assertEquals("F|P|E|E", Arrays.stream(airport.getGridLayout().getLayout()[3])
                                    .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                    .collect(Collectors.joining("|")));


    }

    @Test
    void When_4x4and4Planes_Then_LayoutIsAsExpected() {
        // setup
        int[] rowFuelTruckCounters = {2, 0, 1, 1};
        int[] colFuelTruckCounters = {1, 1, 1, 1};

        Plane[] planes = {
            Plane.builder().gridPos(new Point(0,0)).build(),
            Plane.builder().gridPos(new Point(3,0)).build(),
            Plane.builder().gridPos(new Point(1,3)).build(),
            Plane.builder().gridPos(new Point(2,2)).build()
        };

        // act
        Airport airport = candidate.initialiseLayout(
            rowFuelTruckCounters,
            colFuelTruckCounters,
            planes);

        // verify
        assertEquals(4, airport.getGridLayout().getLayout().length);

        assertEquals("P|F|E|F", Arrays.stream(airport.getGridLayout().getLayout()[0])
                                    .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                    .collect(Collectors.joining("|")));

        assertEquals("E|E|E|P", Arrays.stream(airport.getGridLayout().getLayout()[1])
                                    .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                    .collect(Collectors.joining("|")));

        assertEquals("F|E|P|E", Arrays.stream(airport.getGridLayout().getLayout()[2])
                                    .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                    .collect(Collectors.joining("|")));

        assertEquals("P|E|F|E", Arrays.stream(airport.getGridLayout().getLayout()[3])
                                    .map(grid -> grid.getOccupationType().name().substring(0, 1))
                                    .collect(Collectors.joining("|")));

    }

}