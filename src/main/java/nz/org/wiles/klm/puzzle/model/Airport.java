package nz.org.wiles.klm.puzzle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
