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
import java.net.http.HttpResponse;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.List;

public class TransferService {

    public final int TRANSFER_STATUS_PENDING = 1;
    public final int TRANSFER_STATUS_APPROVED = 2;
    public final int TRANSFER_STATUS_REJECTED = 3;
    public final int TRANSFER_TYPE_REQUEST = 1;
    public final int TRANSFER_TYPE_SEND = 2;
    public final String baseURL;
    private final RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser authenticatedUser;
    private Account account;
    private User user;


    public TransferService(String baseURL){
        this.baseURL = baseURL;
    }

    public Account getAccountByUserId(AuthenticatedUser authenticatedUser){

        HttpEntity<Void> entity = makeVoidEntity(authenticatedUser);
        Account account = null;
        try{
           ResponseEntity<Account> responseEntity = restTemplate.exchange(baseURL + "user/"+
                   authenticatedUser.getUser().getId() + "/account", HttpMethod.GET, entity, Account.class);
           account = responseEntity.getBody();
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

        ResponseEntity<User[]> users = restTemplate.exchange(baseURL + "users",
                HttpMethod.GET, entity, User[].class);

        List<User> allUsers = Arrays.asList(users.getBody());
        return allUsers;
    }

    public void printAllUsers(AuthenticatedUser authenticatedUser){
        List<User> users = getAllUsers(authenticatedUser);
        for(User user : users ){
            if(!user.getUsername().equalsIgnoreCase(authenticatedUser.getUser().getUsername())){
                System.out.println(user.getUsername());
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
    public boolean validAmountEntered(BigDecimal transferAmount, AuthenticatedUser authenticatedUser, Boolean send) {

        boolean validNumber = transferAmount.compareTo(BigDecimal.ZERO)>0;
        boolean sufficientfunds = false;
        if(send) {
            BigDecimal balance = getBalanceByUser(authenticatedUser);
            sufficientfunds = (balance.compareTo(transferAmount) >= 0);
        }else{
            sufficientfunds = true;
        }
        return (validNumber && sufficientfunds);
    }






    public void createTransaction(boolean isSend, AuthenticatedUser authenticatedUser,
                                  String targetUser, BigDecimal transferAmount){
        //Create a transfer object from parameters
        Transfer transfer = new Transfer();
        if(isSend){
            transfer.setTransferTypeId(TRANSFER_TYPE_SEND);
        }else{
            transfer.setTransferTypeId(TRANSFER_TYPE_REQUEST);
        }
        transfer.setTransferStatusId(TRANSFER_STATUS_APPROVED);
        transfer.setTransferAmount(transferAmount);
        transfer.setAccountFrom(authenticatedUser.getUser().getId());
        try {
            transfer.setAccountTo(getUserByUserName(authenticatedUser, targetUser).getId());
        } catch (NullPointerException ex){
            throw new RuntimeException("Could not find user.");
        }
        //Send the transfer object to the server!
        HttpEntity<Transfer> entity = makeTransferEntity(transfer, authenticatedUser);
        ResponseEntity<Transfer> response = restTemplate.exchange(baseURL + "transfer",
                    HttpMethod.POST, entity, Transfer.class);
        if(response.getBody().getTransferId()>0){
            System.out.println("Transfer was successfully completed!");
        }

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

    public String getUsernameByUserId(AuthenticatedUser authenticatedUser, int userId){
        List<User> users = getAllUsers(authenticatedUser);
        for(User user : users){
            if(user.getId() == userId){
                return user.getUsername();
            }
        }
        return null;
    }

    public String getUsernameByAccountId(int accountId, AuthenticatedUser authenticatedUser){
        HttpEntity<Void> entity = makeVoidEntity(authenticatedUser);
        String username = "";

        try{
            ResponseEntity<String> responseEntity = restTemplate.exchange(baseURL + "user/" + accountId,
                    HttpMethod.GET, entity, String.class);
            username = responseEntity.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return username;
    }
    //make call to api to getTransferArrayByAccountId
    public List<Transfer> printTransferListByAccountId(AuthenticatedUser authenticatedUser){
        HttpEntity<Void> entity = makeVoidEntity(authenticatedUser);
        ResponseEntity<Transfer[]> transferList = restTemplate.exchange(baseURL +
                "transfer", HttpMethod.GET, entity, Transfer[].class);
        Transfer[] body = transferList.getBody();
        List<Transfer> results = Arrays.asList(body);
        System.out.println("-------------------------------");
        System.out.println("Transfers");
        System.out.println("ID" + "   " + "From/To " + "   " + "Amount");
        System.out.println("-------------------------------");
        for(Transfer transfer : results){

            String toFrom = "";
            String username = "";
            int fromAccountId = transfer.getAccountFrom();
            int toAccountId = transfer.getAccountTo();
            Account currentUserAccount = getAccountByUserId(authenticatedUser);
            int currentUserAccountId = currentUserAccount.getAccountId();

            if(fromAccountId == currentUserAccountId){
                toFrom = " To: ";
                username = getUsernameByAccountId(toAccountId, authenticatedUser);
            } else {
                toFrom = " From: ";
                username = getUsernameByAccountId(fromAccountId, authenticatedUser);
            }

            //System.out.println("Id: " + transfer.getTransferId());
            System.out.println(transfer.getTransferId() + toFrom + username + " $" + transfer.getTransferAmount());

        }
        System.out.println("---------");
        return results;
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
