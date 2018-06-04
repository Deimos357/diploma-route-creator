package ua.nure.tanasiuk.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.nure.tanasiuk.model.Ticket;
import ua.nure.tanasiuk.resource.deserializer.UnixDateDeserializer;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteRequest {
    @JsonDeserialize(using = UnixDateDeserializer.class)
    private Date startDate;
    private Integer startStation;
    private List<StationInRoute> stationsToVisit;
    private List<Integer> transportTypes;
    private Double factor; // 1 - greed, 0 - fast

    private List<Ticket> tickets;
}
