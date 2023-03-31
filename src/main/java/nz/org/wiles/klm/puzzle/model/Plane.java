package nz.org.wiles.klm.puzzle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nz.org.wiles.klm.puzzle.factory.GridDirectionType;
import nz.org.wiles.klm.puzzle.factory.GridRelativeLocation;

import java.awt.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Plane implements Vehicle {

    private Point gridPos;

    private List<GridRelativeLocation> availableFuelingPoints;

    private Grid fuelTruckLocation;

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.PLANE;
    }

}
