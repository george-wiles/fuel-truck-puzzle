package nz.org.wiles.klm.puzzle.service.impl;

import nz.org.wiles.klm.puzzle.model.Airport;
import nz.org.wiles.klm.puzzle.model.Plane;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AirportLayoutServiceImpTest {

    @Autowired
    private AirportLayoutServiceImp candidate;

    @Test
    void When_ModelSolutionIsConfigured_Then_LayoutIsAsExpected() {
        // setup
        int[] xFuelTruckCounters = {2, 1, 1, 1, 1, 1, 2};
        int[] yFuelTruckCounters = {3, 1, 1, 1, 0, 2, 1};
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
                xFuelTruckCounters,
                yFuelTruckCounters,
                planes);

        // verify
        assertEquals(7, airport.getGridLayout().getLayout().length);
    }

    @Test
    void When_SimpleSolutionIsConfigured_Then_LayoutIsAsExpected() {
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


    }

}