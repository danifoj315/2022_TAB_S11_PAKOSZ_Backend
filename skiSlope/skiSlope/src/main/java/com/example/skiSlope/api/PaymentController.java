package com.example.skiSlope.api;

import com.example.skiSlope.model.*;
import com.example.skiSlope.model.request.PaymentCreateRequest;
import com.example.skiSlope.model.request.TicketCreatePaymentRequest;
import com.example.skiSlope.model.request.VoucherCreatePaymentRequest;
import com.example.skiSlope.model.response.CardItemsResponse;
import com.example.skiSlope.model.response.PaymentResponse;
import com.example.skiSlope.model.response.TicketPaymentResponse;
import com.example.skiSlope.model.response.VoucherPaymentResponse;
import com.example.skiSlope.service.implementations.*;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RequestMapping("api/payment")
@RestController
public class PaymentController {

    private PaymentService paymentService;
    private TicketService ticketService;
    private VoucherService voucherService;
    private SkiLiftService skiLiftService;
    private TicketOptionService ticketOptionService;
    private VoucherOptionService voucherOptionService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_CUSTOMER')")
    public void addPayment(@Valid @NonNull @RequestBody PaymentCreateRequest paymentCreateRequest) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Payment payment = paymentCreateRequest.paymentRequest();
        payment.setUser(loggedUser);
        paymentService.addPayment(payment);
        List<Ticket> tickets = new ArrayList<>();
        List<Voucher> vouchers = new ArrayList<>();
        for (TicketCreatePaymentRequest t : paymentCreateRequest.getTickets()) {
            Ticket ticket = t.ticketRequest();
            ticket.setPayment(payment);
            ticket.setSkiLift(skiLiftService.getSkiLiftByName(t.getSkiLiftName()));
            ticket.setUser(loggedUser);
            ticket.setPrice(ticketOptionService.getTicketOptionByCurrentDateAndDiscountTypeAndEntries(t.getDiscountType(), t.getNumberOfEntries()));
            ticketService.addTicket(ticket);
            tickets.add(ticket);
        }
        for (VoucherCreatePaymentRequest v : paymentCreateRequest.getVouchers()) {
            Voucher voucher = v.voucherRequest();
            voucher.setUser(loggedUser);
            voucher.setPayment(payment);
            voucher.setPrice(voucherOptionService.getCurrentVoucherOptionByDiscountTypeAndTimePeriod(v.getDiscountType(), v.getTimePeriod()));
            voucherService.addVoucher(voucher);
            vouchers.add(voucher);
        }
        List<Card> cards = new ArrayList<>();
        cards.addAll(vouchers);
        cards.addAll(tickets);
        payment.setCardSet(cards);
        paymentService.updatePaymentData(payment, payment.getId());
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    public PaymentResponse getPaymentById(@PathVariable("id") Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return getPaymentResponse(payment);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    public List<PaymentResponse> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return payments.stream().map(
                payment -> getPaymentResponse(payment)).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_CUSTOMER')")
    public void setPaymentToPaidOff(@PathVariable("id") Long id) {
        paymentService.setPaymentToPaidOff(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    public void deletePaymentById(@PathVariable("id") Long id) {
        ticketService.deleteAllTicketsByPaymentId(id);
        voucherService.deleteAllVouchersByPaymentId(id);
        paymentService.deletePayment(id);
    }

    @DeleteMapping()
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    public void deleteAllPayments() {
        for (Payment p : paymentService.getAllPayments()) {
            ticketService.deleteAllTicketsByPaymentId(p.getId());
            voucherService.deleteAllVouchersByPaymentId(p.getId());
            paymentService.deletePayment(p.getId());
        }
    }
    private PaymentResponse getPaymentResponse(Payment payment){
        return PaymentResponse.builder()
                .id(payment.getId())
                .firstName(payment.getUser().getFirstName())
                .lastName(payment.getUser().getLastName())
                .email(payment.getUser().getEmail())
                .totalPrice(BigDecimal.valueOf(payment.getTotalPrice()).setScale(2, RoundingMode.UP))
                .paidOff(payment.getPaidOff())
                .items(CardItemsResponse.builder()
                        .tickets(payment.getTickets().stream().map(
                                ticketRes -> TicketPaymentResponse.builder()
                                        .ownerName(ticketRes.getOwnerName())
                                        .ticketPrice(BigDecimal.valueOf(ticketRes.getPrice().getPrice()).setScale(2, RoundingMode.UP))
                                        .discountType(ticketRes.getPrice().getDiscountType().toString())
                                        .entryAmount(ticketRes.getNumberOfEntries())
                                        .skiLiftName(ticketRes.getSkiLift().getName())
                                        .build()
                        ).collect(Collectors.toList()))
                        .vouchers(payment.getVouchers().stream().map(
                                voucher -> VoucherPaymentResponse.builder()
                                        .ownerName(voucher.getOwnerName())
                                        .voucherPrice(BigDecimal.valueOf(voucher.getPrice().getPrice()).setScale(2, RoundingMode.UP))
                                        .discountType(voucher.getPrice().getDiscountType().toString())
                                        .periodTime(voucherOptionService.getVoucherOptionById(voucher.getPayment().getId()).getTimePeriod().getName())
                                        .build()
                        ).collect(Collectors.toList()))
                        .build())
                .build();
    }
}
