package nz.org.wiles.klm.puzzle.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import nz.org.wiles.klm.puzzle.model.OccupationType;
import nz.org.wiles.klm.puzzle.web.api.LayoutRequestApi;
import nz.org.wiles.klm.puzzle.web.api.LayoutResponseApi;
import nz.org.wiles.klm.puzzle.web.api.PlaneApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nz.org.wiles.klm.puzzle.model.OccupationType.EMPTY;
import static nz.org.wiles.klm.puzzle.model.OccupationType.FUEL_TRUCK;
import static nz.org.wiles.klm.puzzle.model.OccupationType.PLANE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class LayoutControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private LayoutController candidate;

  @Test
  void When_ModelSolutionIsConfigured_Then_LayoutIsAsExpected() throws Exception {
    String url = "/api/v1/layout";
    int[] colFuelTruckCounters = {2, 1, 1, 1, 1, 1, 2};
    int[] rowFuelTruckCounters = {3, 1, 1, 1, 0, 2, 1};
    List<Integer> cols = Arrays.stream(colFuelTruckCounters).boxed().collect(Collectors.toList());
    List<Integer> rows = Arrays.stream(rowFuelTruckCounters).boxed().collect(Collectors.toList());
    PlaneApi[] planes = {
        PlaneApi.builder().x(0).y(2).build(),
        PlaneApi.builder().x(1).y(0).build(),
        PlaneApi.builder().x(1).y(4).build(),
        PlaneApi.builder().x(1).y(6).build(),
        PlaneApi.builder().x(2).y(6).build(),
        PlaneApi.builder().x(4).y(1).build(),
        PlaneApi.builder().x(5).y(3).build(),
        PlaneApi.builder().x(6).y(0).build(),
        PlaneApi.builder().x(6).y(6).build()
    };
    LayoutRequestApi request =
        LayoutRequestApi.builder()
            .colTruckCounters(cols)
            .rowTruckCounters(rows)
            .numberColumns(cols.size())
            .numberRows(rows.size())
            .planes(Arrays.asList(planes))
            .build();

    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
    String json = writer.writeValueAsString(request);
    System.out.println(json);

    MvcResult mvcResult =
        this.mockMvc.perform(
            post("/api/v1/layout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    assertEquals("application/json",
        mvcResult.getResponse().getContentType());
    LayoutResponseApi responseApi =
        mapper.readValue(mvcResult.getResponse().getContentAsString(),
            LayoutResponseApi.class);
    assertEquals(7, responseApi.getAirportLayout().length);

    OccupationType[][] expected = {
        {FUEL_TRUCK, EMPTY, PLANE, EMPTY, FUEL_TRUCK, EMPTY, FUEL_TRUCK},
        {PLANE, EMPTY, FUEL_TRUCK, EMPTY, PLANE, EMPTY, PLANE},
        {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, FUEL_TRUCK, PLANE},
        {EMPTY, FUEL_TRUCK, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
        {EMPTY, PLANE, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
        {FUEL_TRUCK, EMPTY, EMPTY, PLANE, EMPTY, EMPTY, FUEL_TRUCK},
        {PLANE, EMPTY, EMPTY, FUEL_TRUCK, EMPTY, EMPTY, PLANE},

    };

    assertArrayEquals(expected[0], responseApi.getAirportLayout()[0]);
    assertArrayEquals(expected[1], responseApi.getAirportLayout()[1]);
    assertArrayEquals(expected[2], responseApi.getAirportLayout()[2]);
    assertArrayEquals(expected[3], responseApi.getAirportLayout()[3]);
    assertArrayEquals(expected[4], responseApi.getAirportLayout()[4]);
    assertArrayEquals(expected[5], responseApi.getAirportLayout()[5]);
    assertArrayEquals(expected[6], responseApi.getAirportLayout()[6]);

    System.out.println(mvcResult.getResponse().getContentAsString());

  }


}