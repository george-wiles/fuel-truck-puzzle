package nz.org.wiles.klm.puzzle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class Plane implements Vehicle {

    private Point gridPos;

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.PLANE;
    }
}
