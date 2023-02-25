package com.prudential.interview.controller.rsp;

import lombok.Data;

import java.util.List;

@Data
public class QueryAvailableCarListRsp {
    List<CarModelDto> availableCarModels;
}
