package com.prudential.interview.controller.req;

import lombok.Data;

import java.util.Date;

@Data
public class QueryAvailableCarListReq {
    Date fromTime;
    Date toTime;

}
