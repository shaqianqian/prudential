package com.prudential.interview.controller.req;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RentCarReq {
    long modelId;
    LocalDate startTime;
    LocalDate endTime;

}
