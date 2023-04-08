package nz.org.wiles.klm.puzzle.model.grid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nz.org.wiles.klm.puzzle.model.Grid;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GridLayout {

    private Grid[][] layout;

    public static Grid[][] copyLayout(final Grid[][] src) {
        Grid[][] dest = new Grid[src.length][src[0].length];
        for (int i = 0; i < src.length; i++) {
            for (int j = 0; j < src[0].length; j++) {
                Grid grid = src[i][j];
                dest[i][j] = Grid.builder().vehicle(grid.getVehicle()).occupationType(grid.getOccupationType()).build();
            }
        }
        return dest;
    }

}
