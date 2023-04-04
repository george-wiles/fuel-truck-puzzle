package nz.org.wiles.klm.puzzle.factory;

import nz.org.wiles.klm.puzzle.model.Grid;
import nz.org.wiles.klm.puzzle.model.Plane;
import nz.org.wiles.klm.puzzle.model.grid.GridRelativeLocation;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nz.org.wiles.klm.puzzle.model.OccupationType.AVAILABLE;
import static nz.org.wiles.klm.puzzle.model.OccupationType.EMPTY;
import static nz.org.wiles.klm.puzzle.model.OccupationType.PLANE;
import static nz.org.wiles.klm.puzzle.model.grid.GridDirectionType.ABOVE;
import static nz.org.wiles.klm.puzzle.model.grid.GridDirectionType.BELOW;
import static nz.org.wiles.klm.puzzle.model.grid.GridDirectionType.LEFT;
import static nz.org.wiles.klm.puzzle.model.grid.GridDirectionType.RIGHT;

/**
 * Represents a manager for creating and validating a layout grid for planes and their fueling
 * vehicles.
 */
@Component
public class LayoutProximityManager {

  private final LayoutProximityAllocator layoutAllocator;

  LayoutProximityManager(LayoutProximityAllocator layoutAllocator) {
    this.layoutAllocator = layoutAllocator;
  }

  public Grid[][] createLayout(int[] rowFuelTruckCount, int[] colFuelTruckCount, Map<Point, Plane> pointByPlanes) {
    final LayoutValidator validator = new LayoutValidator(rowFuelTruckCount, colFuelTruckCount);
    final Grid[][] layout = createAvailabilityLayout(rowFuelTruckCount, colFuelTruckCount, pointByPlanes);

    final Map<Point, Plane> available = calculateAvailableFuelingPositions(
        layout, rowFuelTruckCount, colFuelTruckCount, pointByPlanes);

    final List<Plane> planes = pointByPlanes.values().stream().distinct()
                             .sorted(Comparator.comparingInt(Plane::getAvailableCount))
                             .collect(Collectors.toList());

    for(Plane plane: planes) {
      System.out.println("plane -> " + plane);
    }

    Grid[][] result = layoutAllocator.allocate(layout, validator);
    for (int i = 0; i < result.length; i++) {
      for (int j = 0; j < result[i].length; j++) {
        if (!result[i][j].isOccupied()) {
          result[i][j] = Grid.builder().occupationType(EMPTY).build();
        }
      }
    }
    return result;
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
    if (right < layout[row].length) {
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

  private Map<Point, Plane> calculateAvailableFuelingPositions(final Grid[][] layout, int[] rowCount, int[] colCount, Map<Point, Plane> planesByPoint) {
    final Map<Point, Plane> availableFuelingPositions = new HashMap<>();
    final List<Point> points = planesByPoint.keySet().stream().collect(Collectors.toList());

    for (Point point : points) {
      final Plane plane = planesByPoint.get(point);
      final List<GridRelativeLocation> available = getAvailability(point, layout, rowCount, colCount);
      plane.setAvailableFuelingPoints(available.stream().map(e -> e.getDirection()).collect(Collectors.toList()));
      for (GridRelativeLocation relativeLocation : available) {
        availableFuelingPositions.put(relativeLocation.getLocation(), plane);
      }
    }
    return availableFuelingPositions;
  }


  private List<GridRelativeLocation> getAvailability(Point point, final Grid[][] layout, int[] rowCount, int[] colCount) {
    List<GridRelativeLocation> available = new ArrayList<>();

    if (isGridRightAvailable(point.x, point.y, layout, rowCount, colCount)) {
      available.add(GridRelativeLocation.builder().direction(RIGHT).location(new Point(point.x, point.y + 1)).build());
    }
    if (isGridLeftAvailable(point.x, point.y, layout, rowCount, colCount)) {
      available.add(GridRelativeLocation.builder().direction(LEFT).location(new Point(point.x, point.y - 1)).build());
    }
    if (isGridAboveAvailable(point.x, point.y, layout, rowCount, colCount)) {
      available.add(GridRelativeLocation.builder().direction(ABOVE).location(new Point(point.x - 1, point.y)).build());
    }
    if (isGridBelowAvailable(point.x, point.y, layout, rowCount, colCount)) {
      available.add(GridRelativeLocation.builder().direction(BELOW).location(new Point(point.x + 1, point.y)).build());
    }
    return available;
  }

  private boolean isGridRightAvailable(int row, int col, Grid[][] layout, int[] rowCount, int[] colCount) {
    return (col + 1 < layout[row].length && !layout[row][col + 1].isOccupied() && rowCount[row] > 0 && colCount[col+1] > 0);
  }

  private boolean isGridLeftAvailable(int row, int col, Grid[][] layout, int[] rowCount, int[] colCount) {
    return (col > 0 && !layout[row][col - 1].isOccupied() && rowCount[row] > 0 && colCount[col-1] > 0);
  }

  private boolean isGridAboveAvailable(int row, int col, Grid[][] layout, int[] rowCount, int[] colCount) {
    return (row > 0 && !layout[row - 1][col].isOccupied() && rowCount[row-1] > 0 && colCount[col] > 0);
  }

  private boolean isGridBelowAvailable(int row, int col, Grid[][] layout, int[] rowCount, int[] colCount) {
    return (row + 1 < layout.length && !layout[row + 1][col].isOccupied() && rowCount[row+1] > 0 && colCount[col] > 0);
  }

}
