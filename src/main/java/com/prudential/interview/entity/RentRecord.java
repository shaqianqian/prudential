package com.prudential.interview.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 這邊的 @Getter 與 @Setter 是 lombok 的功能, 不採用 @Data 在底下說明
 */
@Getter
@Setter
@Entity
public class RentRecord implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 32)
    private Long carModelId;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDate startTime;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDate endTime;

    @Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDate createTime;

    @Column(insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDate upLocalDateTime;
}