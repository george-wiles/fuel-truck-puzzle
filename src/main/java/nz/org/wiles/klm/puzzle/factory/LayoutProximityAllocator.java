package nz.org.wiles.klm.puzzle.factory;

import nz.org.wiles.klm.puzzle.model.FuelTruck;
import nz.org.wiles.klm.puzzle.model.Grid;
import nz.org.wiles.klm.puzzle.model.Plane;
import nz.org.wiles.klm.puzzle.model.grid.GridDirectionType;
import nz.org.wiles.klm.puzzle.model.grid.GridLayout;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static nz.org.wiles.klm.puzzle.model.OccupationType.EMPTY;
import static nz.org.wiles.klm.puzzle.model.OccupationType.FUEL_TRUCK;
import static nz.org.wiles.klm.puzzle.model.grid.GridDirectionType.ABOVE;
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

  public Grid[][] allocate(final Grid[][] layout, LayoutValidator validator) {
    this.validator = validator;
    this.solution = GridLayout.copyLayout(layout);

    allocateAll(solution);
    boolean isGridValid = validator.validateLayout(solution);
    if (isGridValid) {
      return solution;
    }

    // no solution found.
    throw new RuntimeException("No Solution found!");
  }

  private boolean allocateAll(final Grid[][] grid) {
    for (int row = 0; row < grid.length; row++) {
      for (int col = 0; col < grid[row].length; col++) {
        Grid pos = grid[row][col];
        // System.out.println(String.format("  (%d,%d) pos: [%s]", row, col, pos));
        if (pos.hasPlane() && !((Plane)pos.getVehicle()).isFuelling()) {
          Plane plane = (Plane)pos.getVehicle();
          for (GridDirectionType direction: plane.getAvailableFuelingPoints()) {
            Point to = directionToGrid(row, col, direction);
            if (validator.isAvailable(to, grid)) {
              plane.setFuelTruckLocation(Grid.builder().vehicle(FuelTruck.builder().gridPos(to).build()).occupationType(FUEL_TRUCK).build());
              grid[to.x][to.y] = plane.getFuelTruckLocation();
              if (allocateAll(grid)) {
//                System.out.println(String.format("     (%d, %d) allocate() -> return true", row, col));
                return true;
              } else {
                grid[to.x][to.y] = Grid.builder().occupationType(EMPTY).build();
                plane.setFuelTruckLocation(null);
//                System.out.println(String.format("     (%d, %d) allocate() -> return false", row, col));
              }
            }
          }
//          System.out.println(String.format("     (%d, %d) plane %s -> return false", row, col, plane));
          return false;
        }
      }
    }
//    System.out.println(String.format("  finished -> return true"));
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


}
