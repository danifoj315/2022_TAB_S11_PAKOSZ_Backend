package com.example.skiSlope.api;

import com.example.skiSlope.exception.PriceNotFoundException;
import com.example.skiSlope.model.Ticket;
import com.example.skiSlope.model.User;
import com.example.skiSlope.model.request.TicketRequest;
import com.example.skiSlope.model.request.TicketUpdateRequest;
import com.example.skiSlope.model.response.TicketResponse;
import com.example.skiSlope.service.definitions.UserService;
import com.example.skiSlope.service.implementations.PriceService;
import com.example.skiSlope.service.implementations.SkiLiftService;
import com.example.skiSlope.service.implementations.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RequestMapping("api/v1/card")
@RestController
public class TicketController {

    private TicketService ticketService;
    private UserService userService;
    private PriceService priceService;
    private SkiLiftService skiLiftService;


    @PostMapping("/ticket")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_CUSTOMER')")
    public void addTicket(@Valid @NonNull @RequestBody TicketRequest ticketRequest) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user2 = userService.getUser(loggedUser.getUsername());
        Ticket ticket = ticketRequest.ticketRequestToUser();
        ticket.setPrice(priceService.getPriceById(ticketRequest.getPriceId()).orElseThrow(PriceNotFoundException::new));
        ticket.setSkiLift(skiLiftService.getSkyLiftById(ticketRequest.getLiftId()).orElseThrow(PriceNotFoundException::new));
        ticket.setUser(user2);
        ticketService.addTicket(ticket);
    }

    @GetMapping("/ticket")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    public List<TicketResponse> getAllTickets() {
        List<Ticket> ticketList = ticketService.getAllTickets();
        return ticketList.stream().map(
                ticketRes->TicketResponse
                        .builder()
                        .id(ticketRes.getId())
                        .code(ticketRes.getCode())
                        .userId(ticketRes.getUser().getId())
                        .active(ticketRes.getActive())
                        .entryAmount(ticketRes.getNumberOfEntries())
                        .ownerName(ticketRes.getOwnerName())
                        .build()
        ).collect(Collectors.toList());
    }

    @GetMapping("/ticket/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    public Ticket getTicketById(@PathVariable("id") Long id) {
        return ticketService.getTicketById(id)
                .orElse(null);
    }
    @GetMapping("/myTickets")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_CUSTOMER')")
    public List<TicketResponse> getAllTicketsByUserId() {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user2 = userService.getUser(loggedUser.getUsername());
        return user2.getTicketSet().stream().map(
                ticketRes->TicketResponse
                        .builder()
                        .id(ticketRes.getId())
                        .code(ticketRes.getCode())
                        .userId(ticketRes.getUser().getId())
                        .active(ticketRes.getActive())
                        .entryAmount(ticketRes.getNumberOfEntries())
                        .ownerName(ticketRes.getOwnerName())
                        .build()
        ).collect(Collectors.toList());
    }

    @DeleteMapping("/ticket/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    public void deleteTicketByCode(@PathVariable("id") Long id) {
        ticketService.deleteTicket(id);
    }

    @PutMapping("/ticket/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_CUSTOMER')")
    public void updateTicketByCode(@PathVariable("id") Long id, @Valid @NonNull @RequestBody TicketUpdateRequest ticketUpdateRequest) {
        ticketService.updateTicketsData(ticketUpdateRequest, id);
    }

}
