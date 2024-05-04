package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Service.TransferService;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;

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

    @GetMapping(value = "users")
    public User[] findAllUsers(){
        return transferService.findAllUsers();
    }

    @RequestMapping(value = "transfer", method = RequestMethod.POST)
    public Transfer createTransfer(@Valid @RequestBody Transfer transfer, Principal principal){
        return transferService.createTransfer(transfer, principal);
    }
    @RequestMapping(value = "transfer", method = RequestMethod.GET)
    public Transfer[] getTransferArrayByAccountId(Principal principal){
        return transferService.getTransferArrayByAccountId(principal);
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.GET)
    public String getUsernameByAccountId(@PathVariable int id){
        return transferService.getUsernameByAccountId(id);
    }
    //we know this is terrible, we're running out of time
    @RequestMapping(value = "user/{id}/account", method = RequestMethod.GET)
    public Account getAccountByUserId(@PathVariable int id){
        return transferService.getAccountByUserId(id);
    }

}
