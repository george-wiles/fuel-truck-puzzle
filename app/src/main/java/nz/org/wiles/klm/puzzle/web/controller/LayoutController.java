package nz.org.wiles.klm.puzzle.web.controller;

import lombok.extern.slf4j.Slf4j;
import nz.org.wiles.klm.puzzle.model.Airport;
import nz.org.wiles.klm.puzzle.model.Grid;
import nz.org.wiles.klm.puzzle.model.OccupationType;
import nz.org.wiles.klm.puzzle.model.Plane;
import nz.org.wiles.klm.puzzle.service.AirportLayoutService;
import nz.org.wiles.klm.puzzle.web.api.LayoutRequestApi;
import nz.org.wiles.klm.puzzle.web.api.LayoutResponseApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.Point;

import static nz.org.wiles.klm.puzzle.model.OccupationType.EMPTY;

@RequestMapping("/api/v1")
@RestController
@Slf4j
public class LayoutController {

  Logger logger = LoggerFactory.getLogger(LayoutController.class);
  private final AirportLayoutService layoutService;

  public LayoutController(AirportLayoutService layoutService) {
    this.layoutService = layoutService;
  }

  @PostMapping({"/layout"})
  @CrossOrigin(origins = "http://localhost:4200")
  public ResponseEntity<LayoutResponseApi> getLayout(@RequestBody LayoutRequestApi request) {
    logger.info("Received request " + request);
    Plane[] planes = request.getPlanes()
                         .stream()
                         .map(e -> Plane.builder().gridPos(new Point(e.getX(), e.getY())).build())
                         .toArray(Plane[]::new);

    final Airport airport = layoutService.initialiseLayout(
        request.getRowTruckCounters().stream().mapToInt(i->i).toArray(),
        request.getColTruckCounters().stream().mapToInt(i->i).toArray(),
        planes
    );

    Grid[][] result = airport.getGridLayout().getLayout();

    final LayoutResponseApi responseApi =
        LayoutResponseApi.builder()
            .airportLayout(mapGrid(result)).build();

    return new ResponseEntity<>(responseApi, HttpStatus.OK);
  }

  private OccupationType[][] mapGrid(Grid[][] src) {
    OccupationType[][] dest = new OccupationType[src.length][];
    for (int i = 0; i < src.length; i++) {
      dest[i] = new OccupationType[src[i].length];
      for (int j = 0; j < src[i].length; j++) {
        dest[i][j] = (!src[i][j].isOccupied()) ? EMPTY : src[i][j].getOccupationType();
      }
    }
    return dest;
  }
}
