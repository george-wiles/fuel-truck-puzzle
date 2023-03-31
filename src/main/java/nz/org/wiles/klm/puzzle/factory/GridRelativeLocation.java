package nz.org.wiles.klm.puzzle.factory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nz.org.wiles.klm.puzzle.factory.GridDirectionType;

import java.awt.Point;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GridRelativeLocation {

  private GridDirectionType direction;
  private Point location;

}
