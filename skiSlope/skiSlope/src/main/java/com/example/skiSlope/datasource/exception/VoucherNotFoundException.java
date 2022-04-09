package com.example.skiSlope.datasource.exception;

import org.springframework.http.HttpStatus;

public class VoucherNotFoundException extends ResourceNotFoundException{
    public VoucherNotFoundException() {
        super("Voucher doesn't exist!", HttpStatus.NOT_FOUND.value());
    }
}
