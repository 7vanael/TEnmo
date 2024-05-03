package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Service.TransferService;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService){
        this.transferService = transferService;
    }

    @RequestMapping(value = "account", method = RequestMethod.GET)
    public BigDecimal getBalanceByUser(Principal principal){
        String userName = principal.getName();
        return transferService.getBalanceByUser(userName);
    }

    @GetMapping(value = "account")
    public User[] findAllUsers(){
        return transferService.findAllUsers();
    }

    @RequestMapping(value = "transfer", method = RequestMethod.POST)
    public Transfer createTransfer(@Valid @RequestBody Transfer transfer, Principal principal){
        return transferService.createTransfer(transfer, principal);
    }

}
