import java.sql.*;
import java.util.Scanner;
import java.util.Random;

public class Task1 {
    private static final int min = 1000;
    private static final int max = 9999;

    public static class User {
        private String username;
        private String password;

        Scanner sc = new Scanner(System.in);

        public String getUsername() {
            System.out.print("Enter Username: ");
            username = sc.nextLine();
            return username;
        }

        public String getPassword() {
            System.out.print("Enter Password: ");
            password = sc.nextLine();
            return password;
        }
    }

    public static class PnrRecord {
        private int pnrNumber;
        private String passengerName;
        private String trainNumber;
        private String classType;
        private String journeyDate;
        private String from;
        private String to;

        Scanner sc = new Scanner(System.in);

        public int getPnrNumber() {
            Random random = new Random();
            pnrNumber = random.nextInt(max - min + 1) + min; // Ensuring the range is correct
            return pnrNumber;
        }

        public String getPassengerName() {
            System.out.print("Enter the passenger name: ");
            passengerName = sc.nextLine();
            return passengerName;
        }

        public String getTrainNumber() {
            System.out.print("Enter the train number: ");
            trainNumber = sc.nextLine();
            return trainNumber;
        }

        public String getClassType() {
            System.out.print("Enter the class type: ");
            classType = sc.nextLine();
            return classType;
        }

        public String getJourneyDate() {
            System.out.print("Enter the Journey date (YYYY-MM-DD): ");
            journeyDate = sc.nextLine();
            return journeyDate;
        }

        public String getFrom() {
            System.out.print("Enter the starting place: ");
            from = sc.nextLine();
            return from;
        }

        public String getTo() {
            System.out.print("Enter the destination place: ");
            to = sc.nextLine();
            return to;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        User user = new User();
        String username = user.getUsername();
        String password = user.getPassword();

        String url = "jdbc:mysql://localhost:3306/vasu";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("User Connection Granted.\n");
                while (true) {
                    System.out.println("Choose an option:");
                    System.out.println("1. Insert Record");
                    System.out.println("2. Delete Record");
                    System.out.println("3. Show All Records");
                    System.out.println("4. Exit");

                    int choice = sc.nextInt();
                    sc.nextLine(); // Consume the newline character

                    switch (choice) {
                        case 1: // Insert Record
                            PnrRecord record = new PnrRecord();
                            int pnrNumber = record.getPnrNumber();
                            String passengerName = record.getPassengerName();
                            String trainNumber = record.getTrainNumber();
                            String classType = record.getClassType();
                            String journeyDate = record.getJourneyDate();
                            String from = record.getFrom();
                            String to = record.getTo();

                            String insertQuery = "INSERT INTO reservations VALUES (?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                                preparedStatement.setInt(1, pnrNumber);
                                preparedStatement.setString(2, passengerName);
                                preparedStatement.setString(3, trainNumber);
                                preparedStatement.setString(4, classType);
                                preparedStatement.setString(5, journeyDate);
                                preparedStatement.setString(6, from);
                                preparedStatement.setString(7, to);

                                int rowsAffected = preparedStatement.executeUpdate();
                                System.out.println(rowsAffected > 0 ? "Record added successfully." : "No records were added.");
                            } catch (SQLException e) {
                                System.err.println("SQL Error: " + e.getMessage());
                            }
                            break;

                        case 2: // Delete Record
                            System.out.print("Enter the PNR number to delete the record: ");
                            int pnrToDelete = sc.nextInt();

                            String deleteQuery = "DELETE FROM reservations WHERE pnr_number = ?";
                            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                                preparedStatement.setInt(1, pnrToDelete);
                                int rowsAffected = preparedStatement.executeUpdate();
                                System.out.println(rowsAffected > 0 ? "Record deleted successfully." : "No records were deleted.");
                            } catch (SQLException e) {
                                System.err.println("SQL Error: " + e.getMessage());
                            }
                            break;

                        case 3: // Show All Records
                            String showQuery = "SELECT * FROM reservations";
                            try (PreparedStatement preparedStatement = connection.prepareStatement(showQuery);
                                 ResultSet resultSet = preparedStatement.executeQuery()) {
                                System.out.println("\nAll records:\n");
                                while (resultSet.next()) {
                                    System.out.println("PNR Number: " + resultSet.getString("pnr_number"));
                                    System.out.println("Passenger Name: " + resultSet.getString("passenger_name"));
                                    System.out.println("Train Number: " + resultSet.getString("train_number"));
                                    System.out.println("Class Type: " + resultSet.getString("class_type"));
                                    System.out.println("Journey Date: " + resultSet.getString("journey_date"));
                                    System.out.println("From Location: " + resultSet.getString("from_location"));
                                    System.out.println("To Location: " + resultSet.getString("to_location"));
                                    System.out.println("--------------");
                                }
                            } catch (SQLException e) {
                                System.err.println("SQL Error: " + e.getMessage());
                            }
                            break;

                        case 4: // Exit
                            System.out.println("Exiting the program.");
                            sc.close();
                            return;

                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            } catch (SQLException e) {
                System.err.println("SQL Error: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading JDBC driver: " + e.getMessage());
        }
    }
}
