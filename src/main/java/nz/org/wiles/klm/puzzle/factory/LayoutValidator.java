package nz.org.wiles.klm.puzzle.factory;

import nz.org.wiles.klm.puzzle.model.Grid;
import org.springframework.stereotype.Component;

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

  public boolean validate(Grid[][] layout) {
    if (!validateRowCount(layout)) {
      return false;
    }
    return validateColCount(layout);
  }

  private boolean validateRowCount(Grid[][] layout) {
    for (int i = 0; i < layout.length; i++) {
      int sum = Arrays.stream(layout[i]).mapToInt(e -> e.getOccupationType().equals(FUEL_TRUCK) ? 1 : 0).sum();
      if (rowFuelTruckCount[i] != sum) {
        return false;
      }
    }
    return true;
  }

  private boolean validateColCount(Grid[][] layout) {
    for (int i = 0; i < colFuelTruckCount.length; i++) {
      final int col = i;
      int sum = Arrays.stream(layout).mapToInt(e -> e[col].getOccupationType().equals(FUEL_TRUCK) ? 1 : 0).sum();
      if (colFuelTruckCount[i] != sum) {
        return false;
      }
    }
    return true;
  }
}
