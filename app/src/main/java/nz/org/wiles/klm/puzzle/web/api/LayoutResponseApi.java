package nz.org.wiles.klm.puzzle.web.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nz.org.wiles.klm.puzzle.model.OccupationType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LayoutResponseApi {
  OccupationType[][] airportLayout;

}
