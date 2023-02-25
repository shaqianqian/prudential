package com.prudential.interview.controller;

import com.prudential.interview.controller.req.QueryAvailableCarListReq;
import com.prudential.interview.controller.req.RentCarReq;
import com.prudential.interview.controller.req.ShowRentHistoryReq;
import com.prudential.interview.controller.rsp.*;
import com.prudential.interview.entity.CarModel;
import com.prudential.interview.entity.RentRecord;
import com.prudential.interview.entity.Stock;
import com.prudential.interview.repository.CarModelRepo;
import com.prudential.interview.repository.RentRecordRepo;
import com.prudential.interview.repository.StockRepo;
import com.prudential.interview.service.RentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    @RequestMapping(value = "/rentCar", method = {RequestMethod.POST})
    public RentCarRsp handle(@RequestBody RentCarReq req) {
        RentCarRsp rsp = new RentCarRsp();
        if(!checkPermission(req, rsp)){
            return rsp;
        }
        CarModel carModel = carModelRepo.findById(req.getModelId()).orElse(null);
        Stock stock = stockRepo.findByCarModelId(req.getModelId());
        if (carModel == null || stock == null) {
            rsp.setMsg("We don't have this type of car, please recheck");
            rsp.setStatus("fail");
            return rsp;
        }
        List<RentRecord> rentRecords = rentRecordRepo.findByCarModelId(req.getModelId());
        List<RentRecord> listOfUsedCarByModel = rentService.getListOfUsedCarByModel(rentRecords, req.getStartTime(), req.getEndTime());
        if (listOfUsedCarByModel.size() < stock.getQuantity()) {
            rentService.saveRentRecord(req);
            rsp.setMsg("Rent car" + carModel.getModelName() + "successfully");
            rsp.setStatus("succ");
        } else {
            rsp.setMsg("Out of stock, fail to rent car" + carModel.getModelName());
            rsp.setStatus("fail");
        }
        return rsp;
    }
    @RequestMapping(value = "/queryAvailableCarList", method = {RequestMethod.POST})
    public QueryAvailableCarListRsp handle(@RequestBody QueryAvailableCarListReq req) {
        QueryAvailableCarListRsp queryAvailableCarListRsp = new QueryAvailableCarListRsp();
        List<CarModelDto> availableCarModels = new ArrayList<>();
        if(req.getFromTime() == null || req.getToTime() == null) {
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
            List<RentRecord> listOfUsedCarByModel = rentService.getListOfUsedCarByModel(rentRecords, req.getFromTime(), req.getToTime());
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
    @RequestMapping(value = "/showRentHistory", method = {RequestMethod.POST})
    public ShowRentHistoryRsp handle(@RequestBody ShowRentHistoryReq req) {
        ShowRentHistoryRsp rsp = new ShowRentHistoryRsp();
        CarModel carModel = carModelRepo.findById(req.getModelId()).orElse(null);
        if (carModel == null) {
            rsp.setRentHistoryList(new ArrayList<>());
            return rsp;
        }
        List<RentHistory> rentHistoryList = rentRecordRepo.findByCarModelId(req.getModelId()).stream().
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
        if(req.getStartTime() == null || req.getEndTime() == null) {
            rsp.setMsg("Please input start time and end time for renting the car");
            rsp.setStatus("fail");
            return false;
        }
        if(req.getStartTime().after(req.getEndTime())){
            rsp.setMsg("start time should be earlier than end time");
            rsp.setStatus("fail");
            return false;
        }
        if(req.getStartTime().before(new Date())){
            rsp.setMsg("please dont choose a future time as start time");
            rsp.setStatus("fail");
            return false;
        }
        return true;
    }

}
