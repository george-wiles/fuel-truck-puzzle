package nz.org.wiles.klm.puzzle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grid {
    private Vehicle vehicle;

    private OccupationType occupationType;

    public boolean isOccupied() {
        return vehicle != null;
    }
}
