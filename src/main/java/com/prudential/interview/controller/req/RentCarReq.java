package com.prudential.interview.controller.req;

import lombok.Data;

import java.util.Date;

@Data
public class RentCarReq {
    long modelId;
    Date startTime;
    Date endTime;

}
