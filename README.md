# Overview

This was a problem assigned to me during a Java interview, I went a bit further and provided a
very simple front-end Angular app and deployment via IaC AWS.
The truck fueling puzzle has two parts: 
(i) the Java API that solves the layout as per the General Approach and provides
 a simple rest API to retrieve the layout.
(ii) an Angular frontend that calls the app API and renders on a webpage.

![](/Users/georgewiles/dev/git/george-wiles/doof/klm-puzzle/images/AircraftRefuellingPuzzle.png)

# General Approach
1. Create a grid of m by n cells to represent the parking lot, plane and the fuel trucks.
2. Initialize the grid with all planes, and cells set to EMPTY, meaning no fuel truck is placed yet.
3. For each aircraft, find all the adjacent cells (horizontally and vertically) and mark them as AVAILABLE for fuel trucks.
4. For each row and column, count the number of aircraft that need to be refueled and set the corresponding number outside the grid.
5. For each row and column, find all the possible combinations of fuel truck locations that satisfy the total number of fuel trucks required.
6. Use a recursive backtracking algorithm to try all possible combinations of fuel truck locations starting from position [0][0].
7. If a solution is found, return the grid with the plane and fuel truck locations. If no solution is found, return an error message.

# 1. App Java based algorithm with simple REST API
Location: app
## Pre-requisites
Oracle OpenJDK version 18
Maven 3.8.5

* `cd app`
* `mvn clean install`
* `mvn spring-boot:run`


# 2. WebApp Angular consumes app REST API
Location: webapp
## Pre-requisites
Angular cli installed

* `cd webapp`
* `npm install`
* `npm run start`
* `open a browser on http://localhost:4200`





