package com.prudential.interview.controller.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class RentHistory {
    public long recordId;
    public long modelId;
    public Date startTime;
    public Date endTime;



}
