package com.prudential.interview.controller.rsp;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RentHistory {
    public long recordId;
    public long modelId;
    public LocalDate startTime;
    public LocalDate endTime;


}
