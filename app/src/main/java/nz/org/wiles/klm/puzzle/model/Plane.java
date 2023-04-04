package nz.org.wiles.klm.puzzle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nz.org.wiles.klm.puzzle.model.grid.GridDirectionType;

import java.awt.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Plane implements Vehicle {

    private Point gridPos;

    private List<GridDirectionType> availableFuelingPoints;

    private Grid fuelTruckLocation;

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.PLANE;
    }

    public int getAvailableCount() {
        return (availableFuelingPoints == null) ? 0 : availableFuelingPoints.size();
    }

    public boolean isFuelling() {
        return fuelTruckLocation != null ? true : false;
    }

}
