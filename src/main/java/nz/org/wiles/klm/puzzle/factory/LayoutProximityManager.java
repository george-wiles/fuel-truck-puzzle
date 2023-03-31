package nz.org.wiles.klm.puzzle.factory;

import nz.org.wiles.klm.puzzle.model.FuelTruck;
import nz.org.wiles.klm.puzzle.model.Grid;
import nz.org.wiles.klm.puzzle.model.Plane;
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
    logLayout("## Airport Availability Layout is ##", layout);

    calculateAvailableFuelingPositions(layout, rowFuelTruckCount, colFuelTruckCount, planesByPoint);
    allocateFuellingTrucks(layout, rowFuelTruckCount, colFuelTruckCount, planesByPoint);
    logLayout("## Airport Complete Layout is ##", layout);

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

  private void allocateFuellingTrucks(final Grid[][] layout, int[] rowFuelTruckCount, int[] colFuelTruckCount, Map<Point, Plane> planesByPoint) {
    final List<Point> points = planesByPoint.keySet().stream().collect(Collectors.toList());
    for (Point point : points) {
      final Plane plane = planesByPoint.get(point);
      if (plane.getFuelTruckLocation() != null) {
        // unwind recursion loop
        return;
      }
      // allocate a fuel truck to plane from relative position
      final List<GridRelativeLocation> availableLocations = plane.getAvailableFuelingPoints();
      if (availableLocations.size() == 1) {
        final GridRelativeLocation location = availableLocations.get(0);
        final Point current = location.getLocation();
        allocateFuellingTruck(current.x, current.y, layout, location.getDirection());
      }
    }
  }

  private void calculateAvailableFuelingPositions(final Grid[][] layout, int[] rowFuelTruckCount, int[] colFuelTruckCount, Map<Point, Plane> planesByPoint) {
    final List<Point> points = planesByPoint.keySet().stream().collect(Collectors.toList());
    for (Point point: points) {
      final Plane plane = planesByPoint.get(point);
      final List<GridRelativeLocation> available = getAvailability(point.x, point.y, layout);
      plane.setAvailableFuelingPoints(available);
      System.out.println("Plane: " + plane);
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

  private List<GridRelativeLocation> getAvailability(int row, int col, final Grid[][] layout) {
    List<GridRelativeLocation> available = new ArrayList<>();

    if (isGridRightAvailable(row, col, layout)) {
      available.add(GridRelativeLocation.builder().direction(RIGHT).location(new Point(row, col)).build());
    }
    if (isGridLeftAvailable(row, col, layout)) {
      available.add(GridRelativeLocation.builder().direction(LEFT).location(new Point(row, col)).build());
    }
    if (isGridAboveAvailable(row, col, layout)) {
      available.add(GridRelativeLocation.builder().direction(ABOVE).location(new Point(row, col)).build());
    }
    if (isGridBelowAvailable(row, col, layout)) {
      available.add(GridRelativeLocation.builder().direction(BELOW).location(new Point(row, col)).build());
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

  private void logLayout(String heading, Grid[][] layout) {
    System.out.println(heading);
    for (int i = 0; i < layout.length; i++) {
      System.out.println(Arrays.stream(layout[i])
                             .map(grid -> grid.getOccupationType().name().substring(0, 1))
                             .collect(Collectors.joining("|")));
    }
  }
}
