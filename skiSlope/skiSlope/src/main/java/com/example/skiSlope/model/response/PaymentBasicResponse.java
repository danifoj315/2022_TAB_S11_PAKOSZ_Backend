package com.example.skiSlope.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentBasicResponse {
    Long id;
    String firstName;
    String lastName;
    String email;
    BigDecimal totalPrice;
    boolean paidOff;
//    CardItemsResponse items;


}
