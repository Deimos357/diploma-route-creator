package ua.nure.tanasiuk.resource;

import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.nure.tanasiuk.algorithm.AntColonyAlgorithm;
import ua.nure.tanasiuk.dto.RouteRequest;
import ua.nure.tanasiuk.model.Ticket;
import ua.nure.tanasiuk.service.RouteService;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/route", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "route", description = "Operations pertaining to routes")
@Validated
public class RouteResource {
    private final RouteService routeService;
    private final AntColonyAlgorithm antColonyAlgorithm;

    public RouteResource(RouteService routeService, AntColonyAlgorithm antColonyAlgorithm) {
        this.routeService = routeService;
        this.antColonyAlgorithm = antColonyAlgorithm;
    }

    @ApiOperation("Get route")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "sss")
    })
    @PostMapping
    @Authorization("Bearer")
    public ResponseEntity<List<Ticket>> getRoute(@RequestBody RouteRequest routeRequest) {
        return ResponseEntity.ok(routeService.getRoute(routeRequest));
    }

    @ApiOperation("Edit route")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "sss")
    })
    @PutMapping
    @Authorization("Bearer")
    public ResponseEntity<List<Ticket>> editRoute(@RequestBody RouteRequest routeRequest) {
        return ResponseEntity.ok(routeService.editRoute(routeRequest));
    }

    @ApiOperation("")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "sss")
    })
    @PostMapping("/seed")
    @Authorization("Bearer")
    public ResponseEntity setSeed(@RequestBody Long l) {
        antColonyAlgorithm.seed = l;
        return ResponseEntity.ok().build();
    }
}
