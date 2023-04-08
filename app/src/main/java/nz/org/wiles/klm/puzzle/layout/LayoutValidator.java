package nz.org.wiles.klm.puzzle.layout;

import nz.org.wiles.klm.puzzle.model.Grid;
import nz.org.wiles.klm.puzzle.model.OccupationType;
import nz.org.wiles.klm.puzzle.model.Plane;
import nz.org.wiles.klm.puzzle.model.grid.GridDirectionType;
import nz.org.wiles.klm.puzzle.web.api.LayoutRequestApi;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Arrays;

import static nz.org.wiles.klm.puzzle.model.OccupationType.FUEL_TRUCK;

@Component
public class LayoutValidator {
  private final int[] rowFuelTruckCount;
  private final int[] colFuelTruckCount;

  public LayoutValidator(int[] rowFuelTruckCount, int[] colFuelTruckCount) {
    this.colFuelTruckCount = colFuelTruckCount;
    this.rowFuelTruckCount = rowFuelTruckCount;
  }

  public boolean validateLayout(Grid[][] layout) {
    return (!validateRowCount(layout)) ? false : validateColCount(layout);
  }

  public boolean isAvailable(Point point, Grid[][] layout) {
    return  !layout[point.x][point.y].isOccupied()
                && sumRow(point.x, layout) < rowFuelTruckCount[point.x]
                && sumCol(point.y, layout) < colFuelTruckCount[point.y];
  }

  public boolean validateFuellingGrid(Point point, GridDirectionType direction, Grid[][] layout) {
    return switch (direction) {
      case LEFT -> isGridLeftAvailable(point.x, point.y, layout);
      case RIGHT -> isGridRightAvailable(point.x, point.y, layout);
      case BELOW -> isGridBelowAvailable(point.x, point.y, layout);
      case ABOVE -> isGridAboveAvailable(point.x, point.y, layout);
    };
  }

  private int sumRow(final int row, final Grid[][] layout) {
    return Arrays.stream(layout[row]).mapToInt(e -> e.getOccupationType().equals(FUEL_TRUCK) ? 1 : 0).sum();
  }

  private int sumCol(final int col, final Grid[][] layout) {
    return Arrays.stream(layout).mapToInt(e -> e[col].getOccupationType().equals(FUEL_TRUCK) ? 1 : 0).sum();
  }

  private boolean validateRowCount(Grid[][] layout) {
    for (int i = 0; i < layout.length; i++) {
      if (rowFuelTruckCount[i] != sumRow(i, layout)) {
        return false;
      }
    }
    return true;
  }

  private boolean validateColCount(Grid[][] layout) {
    for (int i = 0; i < colFuelTruckCount.length; i++) {
      if (colFuelTruckCount[i] != sumCol(i, layout)) {
        return false;
      }
    }
    return true;
  }

  private boolean isGridRightAvailable(int row, int col, Grid[][] layout) {
    return (col+1 < layout[row].length && !layout[row][col+1].isOccupied()
                && sumRow(row, layout) <= rowFuelTruckCount[row]
                && sumCol(col+1, layout) <= colFuelTruckCount[col+1]);
  }

  private boolean isGridLeftAvailable(int row, int col, Grid[][] layout) {
    return (col > 0 && !layout[row][col-1].isOccupied()
                && sumRow(row, layout) <= rowFuelTruckCount[row]
                && sumCol(col-1, layout) <= colFuelTruckCount[col-1]);
  }

  private boolean isGridAboveAvailable(int row, int col, Grid[][] layout) {
    return (row > 0 && !layout[row][col].isOccupied()
                && sumRow(row, layout) <= rowFuelTruckCount[row]
                && sumCol(col, layout) <= colFuelTruckCount[col]);
  }

  private boolean isGridBelowAvailable(int row, int col, Grid[][] layout) {
    return (row + 1 < layout.length && !layout[row + 1][col].isOccupied()
                && sumRow(row+1, layout) <= rowFuelTruckCount[row+1]
                && sumCol(col, layout) <= colFuelTruckCount[col]);
  }

  public boolean isAdjacent(Point to, Grid[][] grid) {
    // above
    if (isAboveAdjacent(to, grid)) {
      return true;
    }
    // left
    if (to.y > 0 && FUEL_TRUCK == grid[to.x][to.y-1].getOccupationType()) {
      return true;
    }
    // right
    if (to.y + 1 < grid[to.x].length && FUEL_TRUCK == grid[to.x][to.y+1].getOccupationType()) {
      return true;
    }
    // below
    if (isBelowAdjacent(to, grid)) {
      return true;
    }
    return false;
  }

  private boolean isAboveAdjacent(Point to, Grid[][] grid) {
    if (to.x > 0) {
      if (isColumnAdjacent(to.x - 1, to.y - 1, grid)) {
        return true;
      }
    }
    return false;
  }

  private boolean isBelowAdjacent(Point to, Grid[][] grid) {
    if (to.x + 1 < grid.length) {
      if (isColumnAdjacent(to.x + 1, to.y - 1, grid)) {
        return true;
      }
    }
    return false;
  }

  private boolean isColumnAdjacent(int row, int col, Grid[][] grid) {
    for (int i = 0; i < 3; i++) {
      if ((col + i >=0 && col + i < grid[row].length) && FUEL_TRUCK == grid[row][col + i].getOccupationType()) {
        return true;
      }
    }
    return false;
  }
}
