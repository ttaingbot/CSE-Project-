# CSE-Project-
A repo for the CSE 360 project


## Project Overview

This project is a Java-based application designed for managing user accounts and articles. It includes functionalities for user login, account setup, and article management. The project leverages JavaFX for the user interface and H2 database for data storage.

## Features

- **User Management**: Create and manage user accounts with different roles (Admin, Student, Instructor).
- **Login System**: Secure login system with password validation and one-time password (OTP) support.
- **Article Management**: Create, update, delete, and display articles stored in the H2 database.
- **Database Integration**: Uses H2 database for storing user and article information.

## Technologies Used

- **Java**: Core programming language.
- **JavaFX**: For building the user interface.
- **H2 Database**: For data storage.
- **Maven**: For project management and build automation.

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- H2 Database

### Setup

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/CSE-Project-.git
    ```
2. Navigate to the project directory:
    ```sh
    cd CSE-Project-
    ```
3. Build the project using Maven:
    ```sh
    mvn clean install
    ```
4. Run the application:
    ```sh
    mvn javafx:run
    ```

## Project Structure

- **src/main/java/project**: Contains the main Java source files.
  - `User.java`: Defines the User class with attributes and methods for user management.
  - `Login.java`: Handles the login process and user interactions.
  - `DatabaseHelper.java`: Manages database connections and operations.
  - `Admin.java`: Extends the User class for admin-specific functionalities.
- **src/main/resources**: Contains FXML files and other resources.
- **pom.xml**: Maven configuration file.
- **.classpath**: Eclipse classpath configuration.
- **.project**: Eclipse project configuration.

## Contributing

Contributions are welcome! Please fork the repository and create a pull request with your changes.

## Authors

- [ttaingbot](https://github.com/ttaingbot)
- [Kalyanam Dewri](https://github.com/kalyanamdewri)
- [Kage](https://github.com/Kage-Bot)
- [Arnav Amin](https://github.com/aamin15)

## Acknowledgments

- Thanks to the CSE 360 course instructors and TAs for their support and guidance.
- Special thanks to the open-source community for providing valuable resources and tools.
