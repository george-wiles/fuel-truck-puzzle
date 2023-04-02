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

  private Grid[][] solution;

  private LayoutValidator validator;

  LayoutProximityAllocator() {
  }

  public Grid[][] allocate(final Grid[][] layout, LayoutValidator validator, List<Plane> planes, Map<Point, Plane> available) {
    this.validator = validator;
    this.solution = GridLayout.copyLayout(layout);

    if (allocateSolution(solution, layout.length, 0)) {
      return solution;
    }
    // no solution found.
    throw new RuntimeException("No Solution found!");
  }

  private boolean allocateSolution(final Grid[][] grid, int maxCount, int row) {
    if (row == maxCount) {
      return true;
    }
    for (int col = 0; col < grid[row].length; col++) {
      Grid pos = grid[row][col];
      System.out.println(String.format("  (%d,%d) pos: [%s]", row, col, pos));
      if (pos.hasPlane() && !((Plane)pos.getVehicle()).isFuelling()) {
        Plane plane = (Plane)pos.getVehicle();
        for (GridDirectionType direction: plane.getAvailableFuelingPoints()) {
          Point to = directionToGrid(row, col, direction);
          if (validator.isAvailable(to, grid)) {
            plane.setFuelTruckLocation(Grid.builder().vehicle(FuelTruck.builder().gridPos(to).build()).occupationType(FUEL_TRUCK).build());
            grid[to.x][to.y] = plane.getFuelTruckLocation();
            if (allocateSolution(grid, maxCount, row+1)) {
              System.out.println(String.format("     (%d, %d) allocate() -> return true", row, col));
              return true;
            } else {
              grid[to.x][to.y] = Grid.builder().occupationType(EMPTY).build();
              plane.setFuelTruckLocation(null);
              System.out.println(String.format("     (%d, %d) allocate() -> return false", row, col));
            }
          }
        }
        System.out.println(String.format("     (%d, %d) plane %s -> return false", plane));
        return false;
      }
    }
    System.out.println(String.format("  {} -> return true"));

    return true;
  }

  /**
   * Convert direction to point. NOTE: At this point all relative position
   * for a plane are within the bounds of grid.
   */
  private Point directionToGrid(int row, int col, GridDirectionType direction) {
    if (LEFT.equals(direction) || RIGHT.equals(direction)) {
      return LEFT.equals(direction) ? new Point(row, col - 1) : new Point(row, col + 1);
    }
    return ABOVE.equals(direction) ? new Point(row - 1, col) : new Point(row + 1, col);
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
