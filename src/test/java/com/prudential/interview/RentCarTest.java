package com.prudential.interview;


import com.prudential.interview.entity.CarModel;
import com.prudential.interview.repository.CarModelRepo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;


public class RentCarTest extends TestBoot{

    @Autowired
    private CarModelRepo carModelRepo;

    @Test
    public void testCreateCarModel(){
        CarModel carModel = new CarModel();
        carModel.setId(1L);
        carModel.setModelName("aabb");
        carModelRepo.save(carModel);
        CarModel car =  carModelRepo.findById(1L).orElse(null);
        assertNotNull(car);
    }

}
