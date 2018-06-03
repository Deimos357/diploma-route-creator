package ua.nure.tanasiuk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.nure.tanasiuk.model.Ticket;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteRequest {
    private Date startDate;
    private Integer startStation;
    private List<StationInRoute> stationsToVisit;
    private List<Integer> transportTypes;
    private Double factor; // 1 - greed, 0 - fast

    private List<Ticket> tickets;
}
