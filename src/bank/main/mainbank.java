package bank.main;

import java.util.Scanner;
import db.bank.AccountDAO;
import bank.model.Account;
import java.util.List;

public class mainbank {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AccountDAO dao = new AccountDAO();
        int choice;

        do {
            System.out.println("\n==== Simple Banking Simulator ====");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. View Account Balance");
            System.out.println("5. Transfer Money");
            System.out.println("6. Account Status");
            System.out.println("7. Display All Accounts");
            System.out.println("8. View Transaction History(Specific Account)");
            System.out.println("0. Exit");
            System.out.println("-----------------------------------------------------------------");
            System.out.print("Choose an option: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter Account Number: ");
                    int accNo = sc.nextInt();
                    sc.nextLine(); // consume newline
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();
                    System.out.print("Enter Phone Number: ");
                    String phone = sc.nextLine();
                    System.out.print("Enter Initial Balance: ");
                    double bal = sc.nextDouble();

                    dao.addAccount(accNo, name, bal, email, phone);
                    break;

                case 2:
                    System.out.print("Enter Account Number: ");
                    accNo = sc.nextInt();
                    System.out.print("Enter Deposit Amount: ");
                    double dep = sc.nextDouble();
                    dao.deposit(accNo, dep);
                    break;

                case 3:
                    System.out.print("Enter Account Number: ");
                    accNo = sc.nextInt();
                    System.out.print("Enter Withdraw Amount: ");
                    double wd = sc.nextDouble();
                    dao.withdraw(accNo, wd);
                    break;

                case 4:
                    System.out.print("Enter Account Number: ");
                    accNo = sc.nextInt();
                    dao.showBalance(accNo);
                    break;

                case 5:
                    System.out.print("Enter Source Account Number: ");
                    int fromAcc = sc.nextInt();
                    System.out.print("Enter Destination Account Number: ");
                    int toAcc = sc.nextInt();
                    System.out.print("Enter Amount to Transfer: ");
                    double amount = sc.nextDouble();
                    dao.transfer(fromAcc, toAcc, amount);
                    break;

                case 6:
                    System.out.print("Enter Account Number: ");
                    accNo = sc.nextInt();
                    dao.showAccountStatus(accNo);
                    break;

                case 7:
                    List<Account> accounts = dao.getAllAccounts();
                    System.out.println("\n==== All Accounts ====");
                    for (Account acc : accounts) {
                        System.out.println("Account No: " + acc.getAccountNo() +
                                ", Name: " + acc.getName() +
                                ", Email: " + acc.getEmail() +
                                ", Phone: " + acc.getPhone() +
                                ", Balance: ₹" + acc.getBalance() +
                                ", Status: " + acc.getaccstatus());
                    }
                    break;

                case 8:
                    System.out.print("Enter Account Number: ");
                    int transAccNo = sc.nextInt();
                    dao.showTransactionHistory(transAccNo);
                    break;



                case 0:
                    System.out.println("Exiting... Thank you!");
                    break;

                default:
                    System.out.println("Invalid option! Try again.");
            }

        } while (choice != 6);

        sc.close();
    }
}


