package nz.org.wiles.klm.puzzle.factory;

import nz.org.wiles.klm.puzzle.model.FuelTruck;
import nz.org.wiles.klm.puzzle.model.Grid;
import nz.org.wiles.klm.puzzle.model.Plane;
import nz.org.wiles.klm.puzzle.model.VehicleType;
import org.springframework.stereotype.Component;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import static nz.org.wiles.klm.puzzle.factory.GridDirectionType.ABOVE;
import static nz.org.wiles.klm.puzzle.factory.GridDirectionType.BELOW;
import static nz.org.wiles.klm.puzzle.factory.GridDirectionType.LEFT;
import static nz.org.wiles.klm.puzzle.factory.GridDirectionType.RIGHT;
import static nz.org.wiles.klm.puzzle.model.OccupationType.AVAILABLE;
import static nz.org.wiles.klm.puzzle.model.OccupationType.EMPTY;
import static nz.org.wiles.klm.puzzle.model.OccupationType.FUEL_TRUCK;
import static nz.org.wiles.klm.puzzle.model.OccupationType.PLANE;

/**
 * Represents a manager for creating and validating a layout grid for planes and their fueling
 * vehicles.
 */
@Component
public class LayoutProximityManager {

  public Grid[][] createLayout(int[] rowFuelTruckCount, int[] colFuelTruckCount, Map<Point, Plane> planesByPoint) {

    final Grid[][] layout = createAvailabilityLayout(rowFuelTruckCount, colFuelTruckCount, planesByPoint);

    allocateFuellingTrucks(layout);
    System.out.println("## Airport Allocation Layout is ##");
    logLayout(layout);

    return layout;
  }

  private Grid[][] createAvailabilityLayout(int[] rowFuelTruckCount, int[] colFuelTruckCount, Map<Point, Plane> planesByPoint) {
    int rowDim = rowFuelTruckCount.length;
    int colDim = colFuelTruckCount.length;
    Grid[][] layout = new Grid[rowDim][colDim];

    // pass through grid layout allocating planes and available spaces for fuel trucks in grids.
    for (int row = 0; row < rowDim; row++) {
      for (int col = 0; col < colDim; col++) {
        layout[row][col] = Grid.builder().occupationType(AVAILABLE).build();
        final Plane plane = planesByPoint.get(new Point(row, col));
        if (plane != null) {
          layout[row][col] = Grid.builder().vehicle(plane).occupationType(PLANE).build();
          if (rowFuelTruckCount[row] > 0) {
            setLeftWithGrid(row, col, layout, Grid.builder().occupationType(AVAILABLE).build());
            setRightWithGrid(row, col, layout, Grid.builder().occupationType(AVAILABLE).build());
          }
          if (colFuelTruckCount[col] > 0) {
            setAboveWithGrid(row, col, layout, Grid.builder().occupationType(AVAILABLE).build());
            setBelowWithGrid(row, col, layout, Grid.builder().occupationType(AVAILABLE).build());
          }
        } else {
          boolean noFuelTruckAtPointIsPossible = (rowFuelTruckCount[row] == 0 || colFuelTruckCount[col] == 0);
          if (noFuelTruckAtPointIsPossible) {
            layout[row][col] = Grid.builder().occupationType(EMPTY).build();
          }
        }
      }
    }
    System.out.println("## Airport Availability Layout is ##");
    logLayout(layout);
    return layout;
  }


  private void setLeftWithGrid(int row, int col, Grid[][] layout, Grid setGrid) {
    int left = col - 1;
    if (col > 0) {
      if (layout[row][left] == null || !layout[row][left].isOccupied()) {
        layout[row][left] = setGrid;
      }
    }
  }

  private void setRightWithGrid(int row, int col, Grid[][] layout, Grid setGrid) {
    int right = col + 1;
    if (right < layout[row].length ) {
      if (layout[row][right] == null || !layout[row][right].isOccupied()) {
        layout[row][right] = setGrid;
      }
    }
  }

  private void setAboveWithGrid(int row, int col, Grid[][] layout, Grid setGrid) {
    int above = row - 1;
    if (row > 0) {
      if (layout[above][col] == null || !layout[above][col].isOccupied()) {
        layout[above][col] = setGrid;
      }
    }
  }

  private void setBelowWithGrid(int row, int col, Grid[][] layout, Grid setGrid) {
    int below = row + 1;
    if (below < layout.length) {
      if (layout[below][col] == null || !layout[below][col].isOccupied()) {
        layout[below][col] = setGrid;
      }
    }
  }

  private void allocateFuellingTrucks(final Grid[][] layout) {
    for (int row = 0; row < layout.length; row++) {
      for (int col = 0; col < layout[row].length; col++) {
        final Grid current = layout[row][col];
        if (PLANE == current.getOccupationType()) {
          final List<GridDirectionType> available = getAvailability(row, col, layout);
          if (available.size() == 1) {
            GridDirectionType direction = available.get(0);
            allocateFuellingTruck(row, col, layout, direction);
          }
        }
      }
    }
  }

  private void allocateFuellingTruck(int row, int col, Grid[][] layout, GridDirectionType direction) {
    if (PLANE != layout[row][col].getOccupationType()) {
      return;
    }
    if (LEFT.equals(direction)) {
      if (col > 0) {
        layout[row][col-1] =
            Grid.builder()
                .vehicle(new FuelTruck(new Point(row,col-1)))
                .occupationType(FUEL_TRUCK).build();
        setAvailabilityAfterAllocation(row, col-1, layout);
      }
    }
    if (RIGHT.equals(direction)) {
      if (col+1 < layout[row].length) {
        layout[row][col+1] =
            Grid.builder()
                .vehicle(new FuelTruck(new Point(row,col+1)))
                .occupationType(FUEL_TRUCK).build();
        setAvailabilityAfterAllocation(row, col+1, layout);
      }
    }
    if (ABOVE.equals(direction)) {
      if (row > 0) {
        layout[row-1][col] =
            Grid.builder()
                .vehicle(new FuelTruck(new Point(row-1,col)))
                .occupationType(FUEL_TRUCK).build();
        setAvailabilityAfterAllocation(row-1, col, layout);
      }
    }
    if (BELOW.equals(direction)) {
      if (row+1 < layout.length) {
        layout[row+1][col] =
            Grid.builder()
                .vehicle(new FuelTruck(new Point(row+1,col)))
                .occupationType(FUEL_TRUCK).build();
        setAvailabilityAfterAllocation(row+1, col, layout);
      }
    }
  }

  private void setAvailabilityAfterAllocation(int row, int col, Grid[][] layout) {
    setLeftWithGrid(row, col, layout, Grid.builder().occupationType(EMPTY).build());
    setRightWithGrid(row, col, layout, Grid.builder().occupationType(EMPTY).build());
    setAboveWithGrid(row, col, layout, Grid.builder().occupationType(EMPTY).build());
    setBelowWithGrid(row, col, layout, Grid.builder().occupationType(EMPTY).build());
  }

  private List<GridDirectionType> getAvailability(int row, int col, final Grid[][] layout) {
    List<GridDirectionType> available = new ArrayList<>(4);
    if (isGridRightAvailable(row, col, layout)) {
      available.add(RIGHT);
    }
    if (isGridLeftAvailable(row, col, layout)) {
      available.add(LEFT);
    }
    if (isGridAboveAvailable(row, col, layout)) {
      available.add(ABOVE);
    }
    if (isGridBelowAvailable(row, col, layout)) {
      available.add(BELOW);
    }
    return available;
  }
  private boolean isGridRightAvailable(int row, int col, Grid[][] layout) {
    return (col+1 < layout[row].length && AVAILABLE == layout[row][col+1].getOccupationType());
  }

  private boolean isGridLeftAvailable(int row, int col, Grid[][] layout) {
    return (col > 0 && AVAILABLE == layout[row][col-1].getOccupationType());
  }

  private boolean isGridAboveAvailable(int row, int col, Grid[][] layout) {
    return (row > 0 && AVAILABLE == layout[row-1][col].getOccupationType());
  }

  private boolean isGridBelowAvailable(int row, int col, Grid[][] layout) {
    return (row+1 < layout.length  && AVAILABLE == layout[row+1][col].getOccupationType());
  }

  private void logLayout(Grid[][] layout) {
    for (int i = 0; i < layout.length; i++) {
      System.out.println(Arrays.stream(layout[i])
                             .map(grid -> grid.getOccupationType().name().substring(0, 1))
                             .collect(Collectors.joining("|")));
    }
  }
}
