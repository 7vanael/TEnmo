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
    private String[] transferType = new String[]{"Request", "Send"};
    private String[] transferStatus = new String[]{"Pending", "Approved", "Rejected"};




    public TransferService(String baseURL){
        this.baseURL = baseURL;
    }

    //This should probably be in an AccountService
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
    //This should probably be in a AccountService
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

    //This should probably be in an UserService
    public List<User> getAllUsers (AuthenticatedUser authenticatedUser){

        HttpEntity<Void> entity = makeVoidEntity(authenticatedUser);

        ResponseEntity<User[]> users = restTemplate.exchange(baseURL + "users",
                HttpMethod.GET, entity, User[].class);

        List<User> allUsers = Arrays.asList(users.getBody());
        return allUsers;
    }

    //This should probably be in an UserService
    public void printAllUsers(AuthenticatedUser authenticatedUser){
        List<User> users = getAllUsers(authenticatedUser);
        for(User user : users ){
            if(!user.getUsername().equalsIgnoreCase(authenticatedUser.getUser().getUsername())){
                System.out.println(user.getUsername());
            }
        }

    }

    //This should probably be in an UserService
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

    //This should probably be in an UserService
    public User getUserByUserName(AuthenticatedUser authenticatedUser, String username){
        List<User> users = getAllUsers(authenticatedUser);
        for(User user : users){
            if(username.equals(user.getUsername())){
                return user;
            }
        }
        return null;
    }

    //This should probably be in an UserService
    public String getUsernameByUserId(AuthenticatedUser authenticatedUser, int userId){
        List<User> users = getAllUsers(authenticatedUser);
        for(User user : users){
            if(user.getId() == userId){
                return user.getUsername();
            }
        }
        return null;
    }

    //This should probably be in an UserService OR AccountService
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


    //This should probably be broken up with some chunk to ConsoleService
    //We initially had written in fewer lines, but extended out to one method per
    //line to aid in debugging

    //Methods like this probably don't -need- a return, but by including them, they
    //are more easily testable (if we had had the time to do testing!)
    public List<Transfer> printTransferListByAccountId(AuthenticatedUser authenticatedUser){
        HttpEntity<Void> entity = makeVoidEntity(authenticatedUser);
        ResponseEntity<Transfer[]> transferList = restTemplate.exchange(baseURL +
                "transfer", HttpMethod.GET, entity, Transfer[].class);
        Transfer[] body = transferList.getBody();
        List<Transfer> results = Arrays.asList(body);
        System.out.println("-------------------------------");
        System.out.println("Transfers");
        System.out.println("ID" + "   " + "From/To " + "     " + "Amount");
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
            System.out.println(transfer.getTransferId() + toFrom + username + " $" + transfer.getTransferAmount());

        }
        System.out.println("---------");
        return results;
    }

    //This should probably have some parts assigned out to ConsoleService
    public Transfer printTransferByTransferId(int transferId, AuthenticatedUser authenticatedUser){
        HttpEntity<Void> entity = makeVoidEntity(authenticatedUser);
        Transfer transfer = new Transfer();
        try{
            ResponseEntity<Transfer> responseEntity = restTemplate.exchange(baseURL + "transfer/" + transferId,
                    HttpMethod.GET, entity, Transfer.class);
            transfer = responseEntity.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }


        String nameFrom = getUsernameByAccountId(transfer.getAccountFrom(), authenticatedUser);
        String nameTo = getUsernameByAccountId(transfer.getAccountTo(), authenticatedUser);
        String type = transferType[transfer.getTransferTypeId() - 1];
        String status = transferStatus[transfer.getTransferStatusId() - 1];



        System.out.println("-------------------------------");
        System.out.println("Transfer Details");
        System.out.println("-------------------------------");
        String printLine =
                "TransferId: " + transferId + "\n" +
                "From: " + nameFrom + "\n" +
                "To: " + nameTo + "\n" +
                "TransferType: " + type + "\n" +
                "TransferStatus: " + status + "\n" +
                "Amount: $" + transfer.getTransferAmount()
                ;

        System.out.println(printLine);

        return transfer;
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
