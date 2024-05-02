package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {

    public final String baseURL;
    private final RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser authenticatedUser;
    private Account account;


    public TransferService(String baseURL){
        this.baseURL = baseURL;
    }

    public Account getAccountByUserId(AuthenticatedUser authenticatedUser){
        Account account = null;
        try{
            account = restTemplate.getForObject(baseURL + authenticatedUser.getUser().getId(), Account.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public BigDecimal getBalanceById(AuthenticatedUser authenticatedUser){

        BigDecimal balance = new BigDecimal("-1");
        try{
            Account account = getAccountByUserId(authenticatedUser);
            balance = restTemplate.getForObject(baseURL + account.getAccountId(), BigDecimal.class);
            if(balance.compareTo(BigDecimal.ZERO)< 0){
                throw new RuntimeException("Unable to retrieve balance");
            }

        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }



    private HttpEntity<Account> makeAccountEntity (Account account, AuthenticatedUser authUser){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authUser.getToken());
        return new HttpEntity<>(account, headers);
    }
    private HttpEntity<Transfer> makeTransferEntity (Transfer transfer, AuthenticatedUser authUser){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authUser.getToken());
        return new HttpEntity<>(transfer, headers);
    }

}
