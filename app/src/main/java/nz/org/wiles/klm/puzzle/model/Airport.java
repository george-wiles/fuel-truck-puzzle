package nz.org.wiles.klm.puzzle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nz.org.wiles.klm.puzzle.model.grid.GridLayout;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airport {
    private Integer dimensionX;
    private Integer dimensionY;
    private Integer numPlanes;
    private Integer numFuelTrucks;
    private GridLayout gridLayout;
}
