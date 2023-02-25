package com.prudential.interview.repository;

import com.prudential.interview.entity.RentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentRecordRepo extends JpaRepository<RentRecord, Long> {

    List<RentRecord> findByCarModelId(long carModelId);
}
