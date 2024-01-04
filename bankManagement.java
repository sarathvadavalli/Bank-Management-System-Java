package bankproject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class bankManagement {
    private static final int NULL = 0;

    static Connection con = connection.getConnection();
    static String sql = "";
    static int prev;
    public static boolean createAccount(String name, int passCode) // create account function
    {
        try {
            // validation
            if (name == "" || passCode == NULL) {
                System.out.println("All Field Required!");
                return false;
            }
            // query
            Statement st = con.createStatement();
            sql = "INSERT INTO hello(cname,balance,pass_code) values('"
                    + name + "',1000," + passCode + ")";

            // Execution
            if (st.executeUpdate(sql) == 1) {
                System.out.println(name
                        + ", Now You Login!");
                return true;
            }
            // return
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Username already exists!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
   
    public static boolean loginAccount(String name, int passCode) // login method
    {
        try {
            // validation
            if (name == "" || passCode == NULL) {
                System.out.println("All Field Required!");
                return false;
            }

            System.out.println("Searching..");
            // query
            sql = "select * from hello where cname='"+ name + "' and pass_code= "+ passCode;


            PreparedStatement st = con.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            int senderAc = 0;

            // Execution
            BufferedReader sc = new BufferedReader(
                    new InputStreamReader(System.in));

            if (rs.next()) {
                int ch = 5;
                int amt = 0;
                senderAc = rs.getInt(3);

                int receiveAc=0;
                while (true) {
                    try {
                        String userName = rs.getString(1);
                        int bal = rs.getInt(2);
                        System.out.println(
                                "Hello, "
                                        +userName);
                        System.out.println("\n1) Deposit Money");
                        System.out.println("2) Withdraw Money");
                        System.out.println("3) View Balance");
                        System.out.println("4) Previous Transaction");
                        System.out.println("5) LogOut");

                        System.out.print("Enter Choice:");
                        ch = Integer.parseInt(sc.readLine());

                        if (ch == 1) {
                            System.out.print(
                                    "Enter Amount:");
                            amt = Integer.parseInt(
                                    sc.readLine());

                            if (bankManagement.deposit(senderAc, amt)) {
                                System.out.println(
                                        "MSG : Money deposited Successfully!\n");
                            }
                            else {
                                System.out.println(
                                        "ERR : Failed!\n");
                            }
                        }
                        else if (ch == 2) {
                            System.out.print(
                                    "Enter Amount:");
                            amt = Integer.parseInt(
                                    sc.readLine());

                            if (bankManagement.withDrawMoney(senderAc, amt)) {
                                System.out.println(
                                        "MSG : Money Sent Successfully!\n");
                            }
                            else {
                                System.out.println(
                                        "ERR : Failed!\n");
                            }
                        }
                        else if (ch == 3) {

                            bankManagement.getBalance(
                                    senderAc);
                        }
                        else if(ch == 4)
                            bankManagement.previous(userName,senderAc,bal);
                        else if (ch == 5) {
                            break;
                        }
                        else {
                            System.out.println(
                                    "Err : Enter Valid input!\n");
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                return false;
            }
            return true;
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Username Not Available!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void getBalance(int acNo)
    {
        try {
            // query
            sql = "select * from hello where pass_code="
                    + acNo;
            PreparedStatement st
                     = con.prepareStatement(sql);

            ResultSet rs = st.executeQuery(sql);
            System.out.println(
                    "-----------------------------------------------------------");
            System.out.printf("%12s %10s %10s\n",
                    "Account No", "Name",
                    "Balance");

            // Execution

            while (rs.next()) {
                System.out.printf("%12d %10s %10d.00\n",
                        rs.getInt("pass_code"),
                        rs.getString("cname"),
                        rs.getInt("balance"));
            }
            System.out.println(
                    "-----------------------------------------------------------\n");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deposit(int sender_ac, int amount) throws SQLException
    {
        try {
            con.setAutoCommit(false);
            Statement st = con.createStatement();
            con.setSavepoint();

            sql = "update hello set balance=balance+"
                    + amount + " where pass_code=" + sender_ac;
            if (st.executeUpdate(sql) == 1) {
                System.out.println("Amount Credited!");
                prev = amount;
            }

            con.commit();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            con.rollback();
        }
        // return
        return false;
    }

    public static boolean withDrawMoney(int sender_ac, int amount) throws SQLException
    {
        try {
            con.setAutoCommit(false);
            sql = "select * from hello where pass_code="
                    + sender_ac;
            PreparedStatement ps
                    = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getInt("balance") < amount) {
                    System.out.println(
                            "Insufficient Balance!");
                    return false;
                }
            }

            Statement st = con.createStatement();

            // debit
            con.setSavepoint();

            sql = "update hello set balance=balance-"
                    + amount + " where pass_code=" + sender_ac;
            if (st.executeUpdate(sql) == 1) {
                System.out.println("Amount Debited!");
                prev = -amount;
            }
            con.commit();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            con.rollback();
        }
        // return
        return false;
    }
    public static void previous(String name,int acNo,int bal)
    {
        System.out.println("-------------------------------------------------------");
        System.out.println("User Name: "+name);
        System.out.println("Account No: "+acNo);
        if(prev > 0) {
            System.out.println("Deposited: " + prev);
        }
        else if(prev < 0) {
            System.out.println("Withdrawn: " +Math.abs(prev));
        }
        else {
            System.out.println("No Transaction Occured");
        }
        System.out.println("Balance: "+(bal+prev));
        System.out.println("-------------------------------------------------------");
    }
}