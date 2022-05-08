package com.example.skiSlope.api;

import com.example.skiSlope.exception.NewStartDateBeforeStartDateException;
import com.example.skiSlope.model.FullPrice;
import com.example.skiSlope.model.TicketOption;
import com.example.skiSlope.model.enums.DiscountType;
import com.example.skiSlope.model.enums.EntriesEnum;
import com.example.skiSlope.model.request.TicketOptionFullPriceRequest;
import com.example.skiSlope.service.implementations.TicketOptionService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;

@AllArgsConstructor
@RequestMapping("api/prices/set")
@RestController
public class FullPriceController {

    TicketOptionService ticketOptionService;


    @PostMapping("/ticket")
    public void addNewTicketOption(@Valid @NonNull @RequestBody FullPrice fullPrice) throws ParseException {

        for(TicketOption t: ticketOptionService.getAllTicketOptions()){
            if(fullPrice.getStartDate().compareTo(t.getStartDate()) <= 0){
                throw new NewStartDateBeforeStartDateException();
            }
        }
        ticketOptionService.updateLatestTicketOptionData(fullPrice.getStartDate());
        for(EntriesEnum entriesEnum : EntriesEnum.values() ){
            for(DiscountType discountType : DiscountType.values()){
                TicketOption ticketOption = new TicketOptionFullPriceRequest().createFullPricePriceRequest(discountType, entriesEnum, fullPrice);
                ticketOptionService.addTicketOption(ticketOption);
            }
        }


    }
}
