package com.jg.GPS.repository;

import com.jg.GPS.model.POIModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface POIModelRepository extends JpaRepository<POIModel, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM tb_poi WHERE x = :x AND y= :y")
    Boolean findIfExists(@Param("x") double x, @Param("y") double y);
}