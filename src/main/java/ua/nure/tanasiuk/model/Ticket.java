package ua.nure.tanasiuk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {
    private long id;
    private String name;
    private long originalId;
    private double cost;
    private int duration;
    private Date departureTime;
    private int transportTypeId;
    private int fromId;
    private int toId;
    private int companyId;
    private boolean isAvailable;

    @JsonIgnore
    private double rate;
}
