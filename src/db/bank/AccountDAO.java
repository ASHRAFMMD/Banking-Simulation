package db.bank;

import bank.model.Account;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import db.bank.TransactionLogger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class AccountDAO {

    // ✅ Add new account with email & phone
    public void addAccount(int accountNo, String name, double balance, String email, String phone) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO accounts (account_no, name, balance, email, phone,accstatus) VALUES (?, ?, ?, ?, ?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountNo);
            ps.setString(2, name);
            ps.setDouble(3, balance);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setString(6,"Active");
            ps.executeUpdate();

            System.out.println("✅ Account created successfully!");
            TransactionLogger.log("🟢 New account created: " + name + " (" + accountNo + ") | Email: " + email + " | Phone: " + phone);

        } catch (SQLException e) {
            System.out.println("❌ Error creating account: " + e.getMessage());
            TransactionLogger.log("❌ Error creating account (" + accountNo + "): " + e.getMessage());
        }
    }

    // ✅ Deposit money
    public void deposit(int accountNo, double amount) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, amount);
            ps.setInt(2, accountNo);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Amount deposited successfully!");
                TransactionLogger.log("💰 Deposited ₹" + amount + " into Account No: " + accountNo);
            } else {
                System.out.println("⚠️ Account not found!");
                TransactionLogger.log("⚠️ Deposit failed — account not found (" + accountNo + ")");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error in deposit: " + e.getMessage());
            TransactionLogger.log("❌ Deposit error (" + accountNo + "): " + e.getMessage());
        }
    }

    // ✅ Withdraw money (with low balance alert)
    public void withdraw(int accountNo, double amount) {
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT balance FROM accounts WHERE account_no = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, accountNo);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                double currentBal = rs.getDouble("balance");

                if (currentBal >= amount) {
                    double newBalance = currentBal - amount;

                    String sql = "UPDATE accounts SET balance = ? WHERE account_no = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setDouble(1, newBalance);
                    ps.setInt(2, accountNo);
                    ps.executeUpdate();

                    System.out.println("✅ Amount withdrawn successfully!");
                    TransactionLogger.log("💸 Withdrawn ₹" + amount + " from Account No: " + accountNo + ". New balance: ₹" + newBalance);

                    if (newBalance < 500) {
                        System.out.println("⚠️ Low balance alert! Your balance is below ₹500.");
                        TransactionLogger.log("⚠️ Low balance alert for Account " + accountNo + ". Balance: ₹" + newBalance);
                    }

                } else {
                    System.out.println("⚠️ Insufficient balance!");
                    TransactionLogger.log("⚠️ Withdrawal failed — insufficient balance in Account " + accountNo);
                }

            } else {
                System.out.println("⚠️ Account not found!");
                TransactionLogger.log("⚠️ Withdrawal failed — account not found (" + accountNo + ")");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error in withdrawal: " + e.getMessage());
            TransactionLogger.log("❌ Withdrawal error (" + accountNo + "): " + e.getMessage());
        }
    }

    // ✅ View account balance
    public void showBalance(int accountNo) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT name, balance, email, phone FROM accounts WHERE account_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double balance = rs.getDouble("balance");
                String email = rs.getString("email");
                String phone = rs.getString("phone");

                System.out.println("👤 Name: " + name);
                System.out.println("📧 Email: " + email);
                System.out.println("📱 Phone: " + phone);
                System.out.println("💰 Current Balance: ₹" + balance);

                TransactionLogger.log("📊 Balance viewed for Account " + accountNo + ": ₹" + balance);
            } else {
                System.out.println("⚠️ Account not found!");
                TransactionLogger.log("⚠️ Balance check failed — account not found (" + accountNo + ")");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching balance: " + e.getMessage());
            TransactionLogger.log("❌ Balance fetch error (" + accountNo + "): " + e.getMessage());
        }
    }

    // ✅ Transfer money between accounts
    public void transfer(int fromAcc, int toAcc, double amount) {
        try (Connection conn = DBConnection.getConnection()) {

            // Check sender balance
            String checkSql = "SELECT balance FROM accounts WHERE account_no = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, fromAcc);
            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {
                System.out.println("⚠️ Source account not found!");
                TransactionLogger.log("⚠️ Transfer failed — source account not found (" + fromAcc + ")");
                return;
            }

            double fromBalance = rs.getDouble("balance");
            if (fromBalance < amount) {
                System.out.println("⚠️ Insufficient balance in source account!");
                TransactionLogger.log("⚠️ Transfer failed — insufficient balance in Account " + fromAcc);
                return;
            }

            // Withdraw from source
            String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE account_no = ?";
            PreparedStatement withdrawPs = conn.prepareStatement(withdrawSql);
            withdrawPs.setDouble(1, amount);
            withdrawPs.setInt(2, fromAcc);
            withdrawPs.executeUpdate();

            // Deposit into destination
            String depositSql = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";
            PreparedStatement depositPs = conn.prepareStatement(depositSql);
            depositPs.setDouble(1, amount);
            depositPs.setInt(2, toAcc);
            int rows = depositPs.executeUpdate();

            if (rows == 0) {
                // Rollback
                String rollbackSql = "UPDATE accounts SET balance = balance + ? WHERE account_no = ?";
                PreparedStatement rollbackPs = conn.prepareStatement(rollbackSql);
                rollbackPs.setDouble(1, amount);
                rollbackPs.setInt(2, fromAcc);
                rollbackPs.executeUpdate();

                System.out.println("⚠️ Destination account not found! Transfer canceled.");
                TransactionLogger.log("⚠️ Transfer failed — destination account not found (" + toAcc + ")");
                return;
            }

            System.out.println("✅ ₹" + amount + " transferred from Account " + fromAcc + " to Account " + toAcc);
            TransactionLogger.log("💸 ₹" + amount + " transferred from Account " + fromAcc + " to Account " + toAcc);

            double newFromBalance = fromBalance - amount;
            if (newFromBalance < 500) {
                System.out.println("⚠️ Low balance alert! Source account balance is below ₹500.");
                TransactionLogger.log("⚠️ Low balance alert for Account " + fromAcc + ". Balance: ₹" + newFromBalance);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error in transfer: " + e.getMessage());
            TransactionLogger.log("❌ Transfer error (" + fromAcc + " -> " + toAcc + "): " + e.getMessage());
        }

    }
    public void showAccountStatus(int accountNo) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT name, accstatus FROM accounts WHERE account_no = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("👤 Name: " + rs.getString("name"));
                System.out.println("📋 Account Status: " + rs.getString("accstatus"));
            } else {
                System.out.println("⚠️ Account not found!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching account status: " + e.getMessage());
        }
    }



    // ✅ View all accounts
    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM accounts");
            while (rs.next()) {
                Account acc = new Account(
                        rs.getInt("account_no"),
                        rs.getString("name"),
                        rs.getDouble("balance"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("accstatus")
                );
                list.add(acc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void showTransactionHistory(int accountNo) {
        try (BufferedReader br = new BufferedReader(new FileReader("transactions.txt"))) {
            String line;
            boolean found = false;
            String accStr = String.valueOf(accountNo);

            System.out.println("📜 Transaction History for Account No: " + accountNo);
            while ((line = br.readLine()) != null) {
                if (line.contains("Account " + accStr) || line.contains("(" + accStr + ")")) {
                    System.out.println(line);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("⚠️ No transactions found for this account.");
            }

        } catch (IOException e) {
            System.out.println("❌ Error reading transaction log: " + e.getMessage());
        }
    }
}




