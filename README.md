# Wallet Management Service 

This is a fully functional Rest Api that handles Player's monetary transactions

## System Main features consist in the following:

### Player Management
- Create a new player
- Update an existing player
- Get a specific player based on player Id
- Get all systems players

### Transaction Management
- Add a credit transaction for a player
- Add a debit transaction for a player (Only if enough credit exist for selected player)
- Get player balance in system base currency (USD)
- Get all transaction history for a player

### Third party online services
- Integrated with openexchangerates.org rest api for live exchange rates, current free subscription limits service to one base currency (USD), however system can be easily updated to support multiple base currencies if using a paid subscription

## Technologies Used
- Java 11
- Maven  
- Spring Boot
- Swagger
- Orika
- JPA 
- H2 (Can be switch to any Relational DB)  
- Liquibase
- Feign  
- Lombok
- Docker
- Junit 5
- Mockito
- WireMock
- Caching - Ehcache (For clustered cache redis can be implemented instead)

## Prerequisites prior running the application
- Java 11 
- Maven  
- Docker is required to run the application

## Running The Application
- Go in the project parent folder and from the terminal execute ***mvn clean install*** (make sure your default jdk is java 11+ or else run from IDE)
- Access api module and execute ***docker-compose build && docker compose up***
- All APIs can be accessed using ***http://localhost:8080/*** and swagger documentation can be accessed from ***http://localhost:8080/swagger-ui/*** (Different port can be expose inside docker-compose.yml)


## Contact me
For any additional information please feel free to contact me by email on christmagro@gmail.com
