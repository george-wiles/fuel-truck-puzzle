package nz.org.wiles.klm.puzzle.factory;

import nz.org.wiles.klm.puzzle.model.FuelTruck;
import nz.org.wiles.klm.puzzle.model.Grid;
import nz.org.wiles.klm.puzzle.model.Plane;
import nz.org.wiles.klm.puzzle.model.grid.GridDirectionType;
import nz.org.wiles.klm.puzzle.model.grid.GridLayout;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nz.org.wiles.klm.puzzle.model.OccupationType.EMPTY;
import static nz.org.wiles.klm.puzzle.model.OccupationType.FUEL_TRUCK;
import static nz.org.wiles.klm.puzzle.model.OccupationType.PLANE;
import static nz.org.wiles.klm.puzzle.model.grid.GridDirectionType.ABOVE;
import static nz.org.wiles.klm.puzzle.model.grid.GridDirectionType.BELOW;
import static nz.org.wiles.klm.puzzle.model.grid.GridDirectionType.LEFT;
import static nz.org.wiles.klm.puzzle.model.grid.GridDirectionType.RIGHT;

/**
 * Layout proximity allocator is a backtracking algorithm to trial a number
 * of layout allocation combinations starting with planes occupied grids
 * that have the least number of adjacent places, until a solution is reached.
 * <p>
 * Is a brute force method due to time constraints.
 */
@Component
public class LayoutProximityAllocator {

  public Grid[][] allocate(final Grid[][] layout, LayoutValidator validator, List<Plane> planes, Map<Point, Plane> available) {

    for (Plane plane : planes) {
      System.out.println(String.format("Running first trial starting with plane at [%s]", plane.getGridPos()));
      final Grid[][] trialLayout = GridLayout.copyLayout(layout);
      final Map<Point, Plane> trialAvailable =
          available.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

      allocateFuellingTrucks(trialLayout, validator, plane, trialAvailable);

      if (validator.validate(trialLayout)) {
        System.out.println(String.format("Solution found starting with plane at [%s]", plane.getGridPos()));
        return trialLayout;
      }
      System.out.println(String.format("Solution not found starting with plane at [%s]", plane.getGridPos()));
      System.out.println("");
    }
    // no solution found.
    throw new RuntimeException("No Solution found!");
  }

  private boolean allocateFuellingTrucks(final Grid[][] layout, LayoutValidator validator, Plane plane, Map<Point, Plane> available) {
    if (available.isEmpty()) {
      return false;
    }
    while (!available.isEmpty()) {
      final List<Point> availablePointsForPlane = available.entrySet().stream()
                                    .filter(e -> e.getValue().getGridPos().equals(plane.getGridPos()))
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
      for (Point allocateTo: availablePointsForPlane) {
        //Point allocateTo = evaluateLocation(layout, plane, available);
        System.out.println(String.format("  Plane [%s] -> Truck [%s] -> available [%d]", plane.getGridPos(), allocateTo, available.size()));
        allocateTruckToGrid(plane, allocateTo, layout, available);
        if (!available.isEmpty()) {
          final List<Plane> planes = available.values().stream().distinct()
                                         .sorted(Comparator.comparingInt(Plane::getAvailableCount))
                                         .collect(Collectors.toList());
          if (!allocateFuellingTrucks(layout, validator, planes.get(0), available)) {
            System.out.println(String.format("  !allocateFuellingTrucks.isEmpty()  Plane [%s] -> Truck [%s] ", plane.getGridPos(), allocateTo));
          }
        } else {
          System.out.println(String.format("  !available.isEmpty()  Plane [%s] -> Truck [%s] ", plane.getGridPos(), allocateTo));
        }
      }
    }
    return false;
  }

  private Map<Point, Plane> allocateTruckToGrid(Plane plane, Point allocateTo, Grid[][] layout, Map<Point, Plane> available) {
    int row = plane.getGridPos().x;
    int col = plane.getGridPos().y;
    GridDirectionType direction = calculateRelativeLocation(plane.getGridPos(), allocateTo);
    if (PLANE == layout[row][col].getOccupationType()) {
      if (LEFT.equals(direction)) {
        return allocateLeft(plane, allocateTo, layout, available);
      }
      if (RIGHT.equals(direction)) {
        return allocateRight(plane, allocateTo, layout, available);
      }
      if (ABOVE.equals(direction)) {
        return allocateAbove(plane, allocateTo, layout, available);
      }
      if (BELOW.equals(direction)) {
        return allocateBelow(plane, allocateTo, layout, available);
      }
    }
    return available;
  }

  private Map<Point, Plane> allocateBelow(Plane plane, Point to, Grid[][] layout, Map<Point, Plane> available) {
    Point from = plane.getGridPos();
    if (from.x + 1 < layout.length) {
      layout[from.x + 1][from.y] =
          Grid.builder()
              .vehicle(new FuelTruck(new Point(from.x + 1, from.y)))
              .occupationType(FUEL_TRUCK).build();
      available = setAvailabilityAfterAllocation(from.x + 1, from.y, layout, available);
      available.entrySet().removeIf(entry -> entry.getValue().equals(plane));
    }
    return available;
  }


  private  Map<Point, Plane> allocateAbove(Plane plane, Point to, Grid[][] layout, Map<Point, Plane> available) {
    Point from = plane.getGridPos();
    if (from.x > 0) {
      layout[from.x - 1][from.y] =
          Grid.builder()
              .vehicle(new FuelTruck(new Point(from.x - 1, from.y)))
              .occupationType(FUEL_TRUCK).build();
      available = setAvailabilityAfterAllocation(from.x - 1, from.y, layout, available);
      available.entrySet().removeIf(entry -> entry.getValue().equals(plane));
    }
    return available;
  }

  private  Map<Point, Plane> allocateRight(Plane plane, Point to, Grid[][] layout, Map<Point, Plane> available) {
    Point from = plane.getGridPos();
    if (from.y + 1 < layout[from.x].length) {
      layout[from.x][from.y + 1] =
          Grid.builder()
              .vehicle(new FuelTruck(new Point(from.x, from.y + 1)))
              .occupationType(FUEL_TRUCK).build();
      available = setAvailabilityAfterAllocation(from.x, from.y + 1, layout, available);
      available.entrySet().removeIf(entry -> entry.getValue().equals(plane));
    }
    return available;
  }

  private  Map<Point, Plane> allocateLeft(Plane plane, Point to, Grid[][] layout, Map<Point, Plane> available) {
    Point from = plane.getGridPos();
    if (from.y > 0) {
      layout[from.x][from.y - 1] =
          Grid.builder()
              .vehicle(new FuelTruck(new Point(from.x, from.y - 1)))
              .occupationType(FUEL_TRUCK).build();
      available = setAvailabilityAfterAllocation(from.x, from.y - 1, layout, available);
      available.entrySet().removeIf(entry -> entry.getValue().equals(plane));

    }
    return available;
  }

  private Map<Point, Plane> setAvailabilityAfterAllocation(int row, int col, Grid[][] layout, Map<Point, Plane> available) {
    final Map<Point, Plane> copy = available.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    for (int x = row - 1; x <= row + 1; x++) {
      for (int y = col - 1; y <= col + 1; y++) {
        // if index in range and not current position
        if ((x >= 0 && x < layout.length) && (y >= 0 && y < layout[row].length) && !(x == row && y == col)) {
          if (!layout[x][y].isOccupied()) {
            copy.remove(new Point(x, y));
            layout[x][y].setOccupationType(EMPTY);
          }
        }
      }
    }
    return copy;
  }

  private GridDirectionType calculateRelativeLocation(Point from, Point to) {
    if (from.y == to.y) {
      return (to.x > from.x) ? BELOW : ABOVE;
    }
    return (to.y > from.y) ? RIGHT : LEFT;
  }

}
