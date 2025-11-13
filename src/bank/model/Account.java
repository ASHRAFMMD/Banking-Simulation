package bank.model;

public class Account {
    private int accountNo;
    private String name;
    private double balance;
    private String email;
    private String phone;
    private String accstatus;

    // Updated constructor
    public Account(int accountNo, String name, double balance, String email, String phone, String accstatus) {
        this.accountNo = accountNo;
        this.name = name;
        this.balance = balance;
        this.email = email;
        this.phone = phone;
        this.accstatus=accstatus;
    }

    // Getters
    public int getAccountNo() {
        return accountNo;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
    public String getaccstatus() {
        return accstatus;
    }
    // Setters (optional but good practice)
    public void setAccountNo(int accountNo) {
        this.accountNo = accountNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setStatus(String status) {
        this.accstatus = accstatus;
    }

    @Override
    public String toString() {
        return "Account No: " + accountNo +
                ", Name: " + name +
                ", Balance: ₹" + balance +
                ", Email: " + email +
                ", Phone: " + phone +
                ", Account Status: " + accstatus;
    }
}
