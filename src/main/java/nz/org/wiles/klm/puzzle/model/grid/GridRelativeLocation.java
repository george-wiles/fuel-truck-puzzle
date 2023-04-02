package nz.org.wiles.klm.puzzle.model.grid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.Point;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GridRelativeLocation {

  private GridDirectionType direction;
  private Point location;

}
