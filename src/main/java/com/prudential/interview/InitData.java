package com.prudential.interview;

import com.prudential.interview.entity.CarModel;
import com.prudential.interview.entity.Stock;
import com.prudential.interview.repository.CarModelRepo;
import com.prudential.interview.repository.StockRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class InitData implements ApplicationListener<ApplicationStartedEvent> {
	@Autowired
	CarModelRepo carModelRepo;

	@Autowired
	StockRepo stockRepo;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		CarModel toyota = new CarModel();
		toyota.setId(1L);
		toyota.setModelName("Toyota Camry");
		carModelRepo.save(toyota);

		CarModel bmw = new CarModel();
		bmw.setId(2L);
		bmw.setModelName("BMW 650");
		carModelRepo.save(bmw);

		Stock stockToyota = new Stock();
		stockToyota.setId(1L);
		stockToyota.setQuantity(2);
		stockToyota.setCarModelId(1L);
		stockRepo.save(stockToyota);

		Stock stockBmw = new Stock();
		stockBmw.setId(2L);
		stockBmw.setQuantity(2);
		stockBmw.setCarModelId(2L);
		stockRepo.save(stockBmw);
	}
}
