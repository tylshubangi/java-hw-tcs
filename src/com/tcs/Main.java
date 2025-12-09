package com.tcs;
import java.sql.*;
import java.util.*;

class Employee{
    Scanner sc = new Scanner(System.in);
    String name;
    Byte age;
    String designation;
    int salary;

    // JDBC connection variables
    Connection conn;
    PreparedStatement pst;
    ResultSet rs;

    // Method to establish JDBC connection
    void connectToDatabase() {
        try {
            // Load the MySQL driver (Make sure the MySQL JDBC driver is in your classpath)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/companyDB", "root", "Root123$"); // Modify with your credentials
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to close JDBC connection
    void closeConnection() {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void continues() {
        System.out.println("Enter y to continue; n to go back");
        char con = sc.next().charAt(0);
        sc.nextLine();
        switch (con) {
            case 'y':
                create();
                break;
            case 'n':
                screen();
                break;
            default:
                System.out.println("Enter the correct character");
                continues();
                break;
        }
    }

    void screen() {
        System.out.println("Enter 1 to create.");
        System.out.println("Enter 2 to display.");
        System.out.println("Enter 3 to raise salary.");
        System.out.println("Enter 4 to exit.");
        Byte num = sc.nextByte();
        sc.nextLine();
        switch(num) {
            case 1:
                create();
                break;
            case 2:
                display();
                break;
            case 3:
                raiseSalary();
                break;
            case 4:
                exit();
                break;
            default:
                System.out.println("Select only from the given options");
                screen();
        }
    }

    void create() {
        connectToDatabase();  // Establish database connection
        System.out.println("Enter name: ");
        name = sc.nextLine();
        int space = 0;
        for(int i=0; i<name.length(); i++) {
            if(name.charAt(i) == ' ') space += 1;
        }
        if(space > 1) {
            System.out.println("Enter only 2 words...Reset..");
            create();
        }

        System.out.println("Enter age between 20 and 60: ");
        age = sc.nextByte();
        sc.nextLine();
        if(age < 20 || age > 60) {
            System.out.println("Enter age only between 20 and 60...Reset..");
            create();
        }

        System.out.println("Enter Designation: ");
        designation = sc.nextLine();
        switch(designation.toLowerCase()) {
            case "programmer":
                salary = 20000;
                break;
            case "manager":
                salary = 25000;
                break;
            case "tester":
                salary = 15000;
                break;
            default:
                System.out.println("Enter correct designation");
                create();
                break;
        }

        try {
            String query = "INSERT INTO employees (name, age, designation, salary) VALUES (?, ?, ?, ?)";
            pst = conn.prepareStatement(query);
            pst.setString(1, name);
            pst.setByte(2, age);
            pst.setString(3, designation);
            pst.setInt(4, salary);
            pst.executeUpdate();
            System.out.println("Employee created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();  // Close the database connection
        continues();
    }

    void display() {
        connectToDatabase();  // Establish database connection
        System.out.println("Enter employee name to display: ");
        name = sc.nextLine();

        try {
            String query = "SELECT * FROM employees WHERE name = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, name);
            rs = pst.executeQuery();

            if(rs.next()) {
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Age: " + rs.getByte("age"));
                System.out.println("Designation: " + rs.getString("designation"));
                System.out.println("Salary: " + rs.getInt("salary"));
            } else {
                System.out.println("No employee found with the name " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();  // Close the database connection
        screen();
    }

    void raiseSalary() {
        connectToDatabase();  // Establish database connection
        System.out.print("Enter employee name to raise salary: ");
        name = sc.nextLine();

        try {
            String query = "SELECT * FROM employees WHERE name = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, name);
            rs = pst.executeQuery();

            if(rs.next()) {
                salary = rs.getInt("salary");
                System.out.println("Current Salary: " + salary);

                System.out.print("Enter percentage increase (up to 10%): ");
                Byte percent = sc.nextByte();
                sc.nextLine();

                if (percent <= 10) {
                    salary += salary * percent / 100;
                    System.out.println("Salary incremented by " + percent + "%");
                    System.out.println("Updated Salary: " + salary);

                    // Update the salary in the database
                    String updateQuery = "UPDATE employees SET salary = ? WHERE name = ?";
                    pst = conn.prepareStatement(updateQuery);
                    pst.setInt(1, salary);
                    pst.setString(2, name);
                    pst.executeUpdate();
                } else {
                    System.out.println("Please enter a percentage below 10.");
                    raiseSalary();
                }
            } else {
                System.out.println("No employee found with the name " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();  // Close the database connection
        screen();
    }

    void exit() {
        System.out.println("Thank you for using this application");
    }
}

public class Main {
    public static void main(String[] args) {
        Employee emp = new Employee();
        emp.screen();
    }
}
