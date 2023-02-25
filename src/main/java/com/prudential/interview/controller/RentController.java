package com.prudential.interview.controller;

import com.prudential.interview.controller.req.RentCarReq;
import com.prudential.interview.controller.rsp.*;
import com.prudential.interview.entity.CarModel;
import com.prudential.interview.entity.RentRecord;
import com.prudential.interview.entity.Stock;
import com.prudential.interview.exception.CarRentalException;
import com.prudential.interview.repository.CarModelRepo;
import com.prudential.interview.repository.RentRecordRepo;
import com.prudential.interview.repository.StockRepo;
import com.prudential.interview.service.RentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@ResponseBody
public class RentController {
    @Autowired
    RentService rentService;
    @Autowired
    CarModelRepo carModelRepo;
    @Autowired
    RentRecordRepo rentRecordRepo;
    @Autowired
    StockRepo stockRepo;

    @Operation(summary = "Rent a car by its id, start time and end time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully rent the car",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RentCarRsp.class))}),
            @ApiResponse(responseCode = "500", description = "Impossible to rent the car",
                    content = @Content)})
    @RequestMapping(value = "/rentCar", method = {RequestMethod.POST})
    public RentCarRsp handle(@RequestBody RentCarReq req) throws CarRentalException {
        RentCarRsp rsp = new RentCarRsp();
        if (!checkPermission(req, rsp)) {
            throw new CarRentalException("Status : " + rsp.getStatus() + " Reason : " + rsp.getMsg());
        }
        CarModel carModel = carModelRepo.findById(req.getModelId()).orElse(null);
        Stock stock = stockRepo.findByCarModelId(req.getModelId());
        if (carModel == null || stock == null) {
            rsp.setMsg("We don't have this type of car, please recheck");
            rsp.setStatus("fail");
            throw new CarRentalException("Status : " + rsp.getStatus() + " Reason : " + rsp.getMsg());
        }
        List<RentRecord> rentRecords = rentRecordRepo.findByCarModelId(req.getModelId());
        List<RentRecord> listOfUsedCarByModel = rentService.getListOfUsedCarByModel(rentRecords, req.getStartTime(), req.getEndTime());
        if (listOfUsedCarByModel.size() < stock.getQuantity()) {
            rentService.saveRentRecord(req);
            rsp.setMsg("Rent car " + carModel.getModelName() + " successfully");
            rsp.setStatus("succ");
        } else {
            rsp.setMsg("Out of stock, fail to rent car" + carModel.getModelName());
            rsp.setStatus("fail");
            throw new CarRentalException("Status : " + rsp.getStatus() + " Reason : " + rsp.getMsg());
        }
        return rsp;
    }

    @RequestMapping(value = "/queryAvailableCarList/fromTime={fromDate}&toTime={toDate}", method = {RequestMethod.GET})
    public QueryAvailableCarListRsp handle(@Parameter(description = "start date with format yyyy-MM-dd")
                                           @PathVariable String fromDate,
                                           @Parameter(description = "end date with format yyyy-MM-dd")
                                           @PathVariable String toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        LocalDate fromTime = LocalDate.parse(fromDate, formatter);
        LocalDate toTime = LocalDate.parse(toDate, formatter);
        QueryAvailableCarListRsp queryAvailableCarListRsp = new QueryAvailableCarListRsp();
        List<CarModelDto> availableCarModels = new ArrayList<>();
        if (fromTime == null || toTime == null) {
            queryAvailableCarListRsp.setAvailableCarModels(availableCarModels);
            return queryAvailableCarListRsp;
        }
        List<CarModel> carModelList = carModelRepo.findAll();
        for (CarModel carModel : carModelList) {
            Stock totalStock = stockRepo.findByCarModelId(carModel.getId());
            if (totalStock == null || totalStock.getQuantity() == 0) {
                continue;
            }
            List<RentRecord> rentRecords = rentRecordRepo.findByCarModelId(carModel.getId());
            List<RentRecord> listOfUsedCarByModel = rentService.getListOfUsedCarByModel(rentRecords, fromTime, toTime);
            if (totalStock.getQuantity() > listOfUsedCarByModel.size()) {
                CarModelDto carModelDto = new CarModelDto();
                carModelDto.setModelName(carModel.getModelName());
                carModelDto.setStock(totalStock.getQuantity() - listOfUsedCarByModel.size());
                carModelDto.setModelId(carModel.getId());
                availableCarModels.add(carModelDto);
            }
        }
        queryAvailableCarListRsp.setAvailableCarModels(availableCarModels);
        return queryAvailableCarListRsp;
    }

    @RequestMapping(value = "/showRentHistory/{id}", method = {RequestMethod.GET})
    public ShowRentHistoryRsp handle(@Parameter(description = "id of car to be searched")
                                     @PathVariable long id) {
        ShowRentHistoryRsp rsp = new ShowRentHistoryRsp();
        CarModel carModel = carModelRepo.findById(id).orElse(null);
        if (carModel == null) {
            rsp.setRentHistoryList(new ArrayList<>());
            return rsp;
        }
        List<RentHistory> rentHistoryList = rentRecordRepo.findByCarModelId(id).stream().
                map(rentRecord -> {
                    RentHistory history = new RentHistory();
                    history.setEndTime(rentRecord.getEndTime());
                    history.setStartTime(rentRecord.getStartTime());
                    history.setModelId(rentRecord.getCarModelId());
                    history.setRecordId(rentRecord.getId());
                    return history;
                }).collect(Collectors.toList());
        rsp.setRentHistoryList(rentHistoryList);
        return rsp;
    }

    private static boolean checkPermission(RentCarReq req, RentCarRsp rsp) {
        if (req.getStartTime() == null || req.getEndTime() == null) {
            rsp.setMsg("Please input start time and end time for renting the car");
            rsp.setStatus("fail");
            return false;
        }
        if (req.getStartTime().isAfter(req.getEndTime())) {
            rsp.setMsg("start time should be earlier than end time");
            rsp.setStatus("fail");
            return false;
        }
        if (req.getStartTime().isBefore(LocalDate.now())) {
            rsp.setMsg("please choose a future time as start time");
            rsp.setStatus("fail");
            return false;
        }
        return true;
    }

}
