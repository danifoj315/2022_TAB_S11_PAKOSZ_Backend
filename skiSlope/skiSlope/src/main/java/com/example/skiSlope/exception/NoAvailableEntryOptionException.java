package com.example.skiSlope.exception;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.example.skiSlope.model.enums.TimePeriod.allTimePeriodsOptions;

public class NoAvailableEntryOptionException extends  BusinessException{
    public NoAvailableEntryOptionException() {
        super(HttpStatus.NOT_ACCEPTABLE.value(), "This entry option is not available! The following entry amounts that are available: "
                + Arrays.stream(allTimePeriodsOptions()).collect(Collectors.joining(", ")));
    }
}