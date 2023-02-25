package com.prudential.interview.service;

import com.prudential.interview.controller.req.RentCarReq;
import com.prudential.interview.entity.RentRecord;
import com.prudential.interview.repository.RentRecordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentService {
    @Autowired
    private RentRecordRepo rentRecordRepo;

    public List<RentRecord> getListOfUsedCarByModel(List<RentRecord> rentRecords, LocalDate fromTime, LocalDate toTime) {
        List<RentRecord> rentRecordsInRange = rentRecords.stream().filter(rentRecord -> {
            if (rentRecord.getStartTime().compareTo(fromTime) <= 0 &&
                    rentRecord.getEndTime().compareTo(fromTime) >= 0 &&
                    rentRecord.getEndTime().compareTo(toTime) >= 0) {
                return true;
            }
            if (rentRecord.getEndTime().compareTo(toTime) >= 0 &&
                    rentRecord.getStartTime().compareTo(fromTime) >= 0 &&
                    rentRecord.getStartTime().compareTo(toTime) <= 0) {
                return true;
            }
            if (rentRecord.getStartTime().compareTo(fromTime) <= 0 &&
                    rentRecord.getEndTime().compareTo(toTime) >= 0) {
                return true;
            }
            if (rentRecord.getStartTime().compareTo(fromTime) >= 0 &&
                    rentRecord.getEndTime().compareTo(toTime) <= 0) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return rentRecordsInRange;
    }

    public void saveRentRecord(RentCarReq req) {
        RentRecord rentRecord = new RentRecord();
        rentRecord.setCarModelId(req.getModelId());
        rentRecord.setStartTime(req.getStartTime());
        rentRecord.setEndTime(req.getEndTime());
        rentRecordRepo.save(rentRecord);
    }
}
