
# General Approach
1. Create a grid of m by n cells to represent the parking lot, plane and the fuel trucks.
2. Initialize the grid with all planes, and cells set to EMPTY, meaning no fuel truck is placed yet.
3. For each aircraft, find all the adjacent cells (horizontally and vertically) and mark them as AVAILABLE for fuel trucks.
4. For each row and column, count the number of aircraft that need to be refueled and set the corresponding number outside the grid.
5. For each row and column, find all the possible combinations of fuel truck locations that satisfy the total number of fuel trucks required.
6. Use a backtracking algorithm to try all possible combinations of fuel truck locations for each row and column, while making sure that no two fuel trucks touch each other (horizontally, vertically, or diagonally).
7. If a combination of fuel truck locations satisfies all the constraints, mark the corresponding cells in the grid as 1, meaning a fuel truck is placed there.
8. If a solution is found, return the grid with the fuel truck locations. If no solution is found, return an error message.

# Test driven approach
TEST: AirportLayoutServiceImpTest.java
1. Start with a simple 4 by 4 grid with 3 planes
2. Increase complexity with 4 by 4 with 4 planes
3. Test against master 7 by 7 grid allocation

# How to Run
The puzzle can be solved by running the unit tests that verifies the grid layout has allocated the planes
and trucks to their expected positions in the grid. As above there are three tests including the 7 by 7 model solution.

## Pre-requistes 
1. Built and testetd with:
1.1. Oracle OpenJDK version 18 \
1.2. Maven 3.8.5

Running test

1. Run `mvn clean install`
2. This will run the 3 tests described above
```
F|E|P|E|F|E|F
P|E|F|E|P|E|P
E|E|E|E|E|F|P
E|F|E|E|E|E|E
E|P|E|E|E|E|E
F|E|E|P|E|E|F
P|E|E|F|E|E|P
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 4.421 s - in nz.org.wiles.klm.puzzle.service.impl.AirportLayoutServiceImpTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

# Java springboot
This initial revision is written in Java with Springboot, initially
I was going to build a quick REST api GET /api/v1/layout and hook it
up with an Angular frontend to build a simple UI, however with the
time constraints just implemented the service layer.

# References
No references to quote. I identified the steps and knew I required
some form of backtracking algorithm with recursion. 

# Design
Rather than taking a pure functional approach I took an
Object Orientation approach, which in hindsight was a bit
overkill, with time constraints probably should have just
written a simple standalone application with limited
classes/design.




