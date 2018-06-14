package ua.nure.tanasiuk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DPResult {
    private Date rdyTime;
    private double value;
    private Ticket ticket;
    private DPResult parent;
}
