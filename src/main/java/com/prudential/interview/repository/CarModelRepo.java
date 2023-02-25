package com.prudential.interview.repository;

import com.prudential.interview.entity.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarModelRepo extends JpaRepository<CarModel, Long> {
}
