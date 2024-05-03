package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);

    private AuthenticatedUser currentUser;





    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    //If we have time: we should pull more of this method out into the TransferService/Console Service
    // so this method can look more like a series of calls to other methods in other services.
    private void startSend() {
        boolean isSend = true;
        System.out.println();
        System.out.println("Select a user name to Initiate a Transfer: \n");
        transferService.printAllUsers(currentUser);
        boolean validUser = false;
        String targetUser = "";
        while(!validUser) {
            targetUser = consoleService.promptForString("Enter a valid username for the transfer: ");

            if(transferService.validUserSelected(targetUser, currentUser)){
                validUser = true;
            }
        }

        boolean validAmount = false;
        BigDecimal transferAmount = BigDecimal.ZERO;
        while(!validAmount) {
            transferAmount = consoleService.promptForBigDecimal("Please enter a transfer amount " +
                    "(cannot be negative or greater than your balance): ");

            if(transferService.validAmountEntered(transferAmount, currentUser, isSend)){
                validAmount = true;
            }
        }

        transferService.createTransaction(isSend, currentUser, targetUser, transferAmount);
        BigDecimal balance = transferService.getBalanceByUser(currentUser);
        System.out.println();
        System.out.println("Your new balance is: " + balance);
    }

	private void viewCurrentBalance() {
		BigDecimal balance = transferService.getBalanceByUser(currentUser);
        System.out.println("Your current balance is: " + balance);
		
	}

	private void viewTransferHistory() {
        //implement api call method to getTransferArrayByAccountId
        transferService.printTransferListByAccountId(currentUser);
		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		startSend();
		
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
