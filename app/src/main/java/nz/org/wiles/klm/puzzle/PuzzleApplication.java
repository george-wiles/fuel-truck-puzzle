package nz.org.wiles.klm.puzzle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Situation:
 * • 9 aircraft are parked at an airport and need to be refuelled before they can take off.
 * • Fuel trucks are used to refuel the aircraft.
 * Task:
 * • Place fuel trucks in the grid.
 * • Each aircraft should have one fuel truck next to it (horizontally or vertically).
 * • Fuel trucks do not touch each other, not even diagonally.
 * • The numbers outside the grid show the total number of fuel trucks in the corresponding row
 * or column.
 */
@SpringBootApplication
public class PuzzleApplication {

	public static void main(String[] args) {
		SpringApplication.run(PuzzleApplication.class, args);
	}

}
