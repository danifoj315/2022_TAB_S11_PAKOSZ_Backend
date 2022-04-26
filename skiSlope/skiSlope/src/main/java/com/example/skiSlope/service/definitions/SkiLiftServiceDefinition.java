package com.example.skiSlope.service.definitions;

import com.example.skiSlope.model.SkiLift;
import com.example.skiSlope.model.Voucher;
import com.example.skiSlope.model.request.SkiLiftRequest;
import com.example.skiSlope.model.request.VoucherRequest;

import java.util.List;
import java.util.Optional;

public interface SkiLiftServiceDefinition {

    SkiLift addSkiLift(SkiLift skiLift);

    Optional<SkiLift> getSkyLiftById(Long id);

    List<SkiLift> getAllSkiLifts();

    void updateSkiLiftsData(SkiLiftRequest skiLiftRequest, Long id);

    void setSkiLiftsActive(SkiLiftRequest skiLiftRequest, Long id);

    void deleteSkiLift(Long id);
}
