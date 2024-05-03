package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.List;

public class TransferService {

    public final String baseURL;
    private final RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser authenticatedUser;
    private Account account;
    private User user;

    private final int TRANSFER_TYPE_SEND = 2;


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

    public BigDecimal getBalanceByUser(AuthenticatedUser authenticatedUser){

        HttpEntity<Void> entity = makeVoidEntity(authenticatedUser);
        BigDecimal balance = new BigDecimal("-1");
        try{
            ResponseEntity<BigDecimal> response = restTemplate.exchange(baseURL+ "account",
                    HttpMethod.GET, entity, BigDecimal.class);
            balance = response.getBody();
            if(balance.compareTo(BigDecimal.ZERO)< 0){
                throw new RuntimeException("Unable to retrieve balance");
            }

        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }


    public List<User> getAllUsers (AuthenticatedUser authenticatedUser){

        HttpEntity<Void> entity = makeVoidEntity(authenticatedUser);

        ResponseEntity<User[]> users = restTemplate.exchange(baseURL + "account",
                HttpMethod.GET, entity, User[].class);

        List<User> allUsers = Arrays.asList(users.getBody());
        return allUsers;
    }

    public void printAllUsers(AuthenticatedUser authenticatedUser){
        List<User> users = getAllUsers(authenticatedUser);
        for(User user : users ){
            if(user.getUsername() != authenticatedUser.getUser().getUsername()){
                System.out.println(user.getId());
            }
        }

    }

    public boolean validUserSelected(String selectedUserName, AuthenticatedUser authenticatedUser){
        List<User> users = getAllUsers(authenticatedUser);
        for(User user : users){
            if(user.getUsername().equals(selectedUserName)){
                if(!user.getUsername().equals(authenticatedUser.getUser().getUsername())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Transfer createTransaction(int transferType, AuthenticatedUser authenticatedUser,
                                      String targetUser, BigDecimal transferAmount){
        //for challenge determine transfer_type_id will determine which account is too or from
        Transfer transfer = new Transfer();
        transfer.setTransferTypeId(TRANSFER_TYPE_SEND);
        transfer.setTransferAmount(transferAmount);
        transfer.setAccountFrom(authenticatedUser.getUser().getId());
        try {
            transfer.setAccountTo(getUserByUserName(authenticatedUser, targetUser).getId());
        } catch (NullPointerException ex){
            throw new RuntimeException("Could not find user.");
        }
        return transfer;
    }

    public User getUserByUserName(AuthenticatedUser authenticatedUser, String username){
        List<User> users = getAllUsers(authenticatedUser);
        for(User user : users){
            if(username.equals(user.getUsername())){
                return user;
            }
        }
        return null;
    }

    private HttpEntity<Void> makeVoidEntity (AuthenticatedUser authUser){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authUser.getToken());
        return new HttpEntity<>(headers);
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
