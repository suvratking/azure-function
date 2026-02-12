# Azure CRUD Function

This project is a serverless CRUD application built using Java, Spring Boot, and Azure Functions. It provides basic create, read, update, and delete operations for employees.

## Features

- **Create, Read, Update, Delete (CRUD):** Perform basic database operations on employee data.
- **Serverless:** Deployed as an Azure Function, which scales automatically and is cost-effective.
- **RESTful API:** Exposes a set of RESTful endpoints for interacting with the employee data.

## Technologies Used

- **Java 21:** The application is built using Java 21.
- **Spring Boot:** Provides a robust framework for building the application logic.
- **Spring Cloud Function:** Enables the use of Spring Boot features in a serverless environment.
- **Spring Data JPA:** Simplifies database access and operations.
- **Azure Functions:** The serverless compute service used to host the application.
- **PostgreSQL:** The relational database used for data persistence.
- **Lombok:** Reduces boilerplate code for model and entity classes.
- **Maven:** Used for project build and dependency management.

## Project Structure

```
.
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── example
│       │           └── azurefunction
│       │               ├── dto           # Data Transfer Objects
│       │               ├── enitity       # JPA Entity classes
│       │               ├── handler       # Business logic handlers
│       │               ├── service       # Service layer
│       │               ├── functions     # Azure Function triggers
│       │               ├── repository    # Spring Data JPA repositories
│       │               └── AzureFunctionApplication.java
│       └── resources
│           ├── application.properties
│           └── host.json
├── pom.xml
└── local.settings.json
```

## Configuration

### Local Development

For local development, create a `local.settings.json` file in the root of the project with the following content:

```json
{
  "IsEncrypted": false,
  "Values": {
    "AzureWebJobsStorage": "",
    "FUNCTIONS_WORKER_RUNTIME": "java",
    "MAIN_CLASS":"com.example.azurefunction.AzureFunctionApplication",
    "spring.datasource.url": "jdbc:postgresql://localhost:5435/postgres",
    "spring.datasource.username": "your-username",
    "spring.datasource.password": "your-password",
    "spring.jpa.hibernate.ddl-auto": "update"
  }
}
```

### Azure Deployment

The Azure deployment is configured in the `pom.xml` file within the `azure-functions-maven-plugin` section. Key configuration properties include:

- `appName`: The name of the Azure Function App.
- `resourceGroup`: The Azure resource group.
- `region`: The Azure region where the resources will be deployed.
- `appServicePlanName`: The name of the App Service Plan.
- `pricingTier`: The pricing tier for the App Service Plan.

## How to Run Locally

1.  **Prerequisites:**
    - Java 21
    - Maven
    - Azure Functions Core Tools
    - PostgreSQL database running locally or on a remote server.

2.  **Build the project:**
    ```bash
    mvnw clean install
    ```

3.  **Run the function:**
    ```bash
    mvnw azure-functions:run
    ```

## How to Deploy to Azure

1.  **Login to Azure:**
    ```bash
    az login
    ```

2.  **Deploy the function:**
    ```bash
    mvnw azure-functions:deploy
    ```

## API Endpoints

The following endpoints are available:

| Method | Path                  | Description              |
|--------|-----------------------|--------------------------|
| POST   | `/api/employees`      | Create a new employee    |
| GET    | `/api/employees`      | Get all employees        |
| GET    | `/api/employees/{id}` | Get an employee by ID    |
| PUT    | `/api/employees/{id}` | Update an employee by ID |
| DELETE | `/api/employees/{id}` | Delete an employee by ID |
