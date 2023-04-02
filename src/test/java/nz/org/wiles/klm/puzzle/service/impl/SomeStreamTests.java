package nz.org.wiles.klm.puzzle.service.impl;

import nz.org.wiles.klm.puzzle.model.Grid;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nz.org.wiles.klm.puzzle.model.OccupationType.EMPTY;
import static nz.org.wiles.klm.puzzle.model.OccupationType.FUEL_TRUCK;
import static nz.org.wiles.klm.puzzle.model.OccupationType.PLANE;

public class SomeStreamTests {

  @Test
  public void testStreamSum() {
    int[] colFuelTruckCounters = {1, 4, 1};
    int[] rowFuelTruckCounters = {3, 0, 2};

    Grid truck = Grid.builder().occupationType(FUEL_TRUCK).build();
    Grid plane = Grid.builder().occupationType(PLANE).build();
    Grid empty = Grid.builder().occupationType(EMPTY).build();

    Grid[][] layout = {
        {truck, plane, empty},
        {empty, plane, empty},
        {truck, plane, truck}
    };


    System.out.println("col[0] sum => " + Arrays.stream(layout).mapToInt(e -> e[0].getOccupationType().equals(FUEL_TRUCK) ? 1 : 0).sum());
    System.out.println("col[1] sum => " + Arrays.stream(layout).mapToInt(e -> e[1].getOccupationType().equals(FUEL_TRUCK) ? 1 : 0).sum());
    System.out.println("col[2] sum => " + Arrays.stream(layout).mapToInt(e -> e[2].getOccupationType().equals(FUEL_TRUCK) ? 1 : 0).sum());

    System.out.println("row[0] sum => " + Arrays.stream(layout[0]).mapToInt(e -> e.getOccupationType().equals(FUEL_TRUCK) ? 1 : 0).sum());
    System.out.println("row[1] sum => " + Arrays.stream(layout[1]).mapToInt(e -> e.getOccupationType().equals(FUEL_TRUCK) ? 1 : 0).sum());
    System.out.println("row[2] sum => " + Arrays.stream(layout[2]).mapToInt(e -> e.getOccupationType().equals(FUEL_TRUCK) ? 1 : 0).sum());
  }

}
