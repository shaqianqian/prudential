package com.prudential.interview;


import com.prudential.interview.entity.CarModel;
import com.prudential.interview.entity.RentRecord;
import com.prudential.interview.repository.CarModelRepo;
import com.prudential.interview.service.RentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class RentCarTest extends TestBoot{

    @Autowired
    private CarModelRepo carModelRepo;

    @Autowired
    private RentService rentService;

    @Test
    public void testCreateCarModel(){
        CarModel carModel = new CarModel();
        carModel.setId(1L);
        carModel.setModelName("aabb");
        carModelRepo.save(carModel);
        CarModel car =  carModelRepo.findById(1L).orElse(null);
        assertNotNull(car);
    }

    @Test
    public void testRentService() throws ParseException {
        List<RentRecord> rentRecords = new ArrayList<>();
        String startStr = "2023.02.25 00:00:00";
        String endStr = "2023.03.25 00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date start = sdf.parse(startStr);
        Date end = sdf.parse(endStr);
        RentRecord rentRecord1 = new RentRecord();
        rentRecord1.setId(100L);
        rentRecord1.setCarModelId(1L);
        rentRecord1.setStartTime(start);
        rentRecord1.setEndTime(end);
        rentRecords.add(rentRecord1);

        startStr = "2023.03.10 00:00:00";
        endStr = "2023.04.01 00:00:00";
        start = sdf.parse(startStr);
        end = sdf.parse(endStr);
        RentRecord rentRecord2 = new RentRecord();
        rentRecord2.setId(200L);
        rentRecord2.setCarModelId(1L);
        rentRecord2.setStartTime(start);
        rentRecord2.setEndTime(end);
        rentRecords.add(rentRecord2);

        startStr = "2023.01.10 00:00:00";
        endStr = "2023.02.01 00:00:00";
        start = sdf.parse(startStr);
        end = sdf.parse(endStr);
        RentRecord rentRecord3 = new RentRecord();
        rentRecord3.setId(200L);
        rentRecord3.setCarModelId(2L);
        rentRecord3.setStartTime(start);
        rentRecord3.setEndTime(end);
        rentRecords.add(rentRecord3);

        startStr = "2023.02.27 00:00:00";
        endStr = "2023.03.01 00:00:00";
        start = sdf.parse(startStr);
        end = sdf.parse(endStr);
        List<RentRecord> records = rentService.getListOfUsedCarByModel(rentRecords, start, end);
        assertEquals(records.size(), 1);

        startStr = "2023.02.25 00:00:00";
        endStr = "2023.04.01 00:00:00";
        start = sdf.parse(startStr);
        end = sdf.parse(endStr);
        records = rentService.getListOfUsedCarByModel(rentRecords, start, end);
        assertEquals(records.size(), 2);
    }

}
