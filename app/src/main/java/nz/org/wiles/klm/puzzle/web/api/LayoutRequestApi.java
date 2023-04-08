package nz.org.wiles.klm.puzzle.web.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LayoutRequestApi {
  @Min(value = 2, message = "Number of grid rows must be greater than 0")
  @Max(value = 200, message = "Number of grid rows must not be greater than 200")
  Integer numberRows;

  @Min(value = 2, message = "Number of grid columns must be greater than 0")
  @Max(value = 100, message = "Number of grid columns must not be greater than 200")
  Integer numberColumns;

  List<Integer> rowTruckCounters;
  List<Integer> colTruckCounters;

  List<PlaneApi> planes;

}
