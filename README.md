# Banking app

Banking app is a simple example of a banking java application using spring boot.

## Use cases.

### Wire transfer.

Steps:
1. wireTransfer(user driven): create transfer entity and store it. <br>
   transfer status = CREATED <br>
   event sent = _TransferCreatedEvent_
2. withdrawMoney(captures _TransferCreatedEvent_): it reduces source wallet money with event.amount, creates a new withdraw operation. <br>
   transfer status = CREATED <br>
   stores new wallet state. <br>
   event sent = _WithdrawalTransferDoneEvent_
3. updateTransferStatus(captures _WithdrawalTransferDoneEvent_): <br>
   transfer status = WITHDRAWN <br>
   sendEvent _NotedCashWithdrawalEvent_
4. depositMoney(captures _NotedCashWithdrawalEvent_): it increases destination wallet money with event.amount, it also creates a new deposit operation. <br>
   store new wallet state. <br>
   event sent = _MoneyTransferDepositedEvent_
5. finishTransfer(captures _MoneyTransferDepositedEvent_): it updates transfer status to FINISHED. <br>
   event sent _TransferPerformedEvent_

### Register user.

event sent _UserRegisteredEvent_

### Create wallet.

event sent _WalletCreatedEvent_

### Make deposit.

event sent _DepositDoneEvent_

## Requirements.
1. java 17 or higher.
2. Docker.
3. Internet access.

## Installation.

1. Download app source code from github.
2. run ```./mvnw -U clean package``` at the project source directory.

## Running the app.

1. Compile source code following the [previous installation section](#installation).
2. Update [application configuration](src/main/resources/application-development.properties) and [docker-compose](compose.yaml) files as needed 
   (**updating database host, username, password, etc properties at compose file will require to update application configuration file**).
3. Start local dependencies (database) using docker compose at the project root directory:
    ```shell
    docker-compose up &
    ```
4. Once local dependencies are running start the banking application with the following command:
    ```shell
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=development
   ```
   or
    ```shell
   java -jar -Dspring.profiles.active=development banking-0.0.1-SNAPSHOT.jar
   ```
   **[WARNING] Note that is important to use spring boot development profile in order to run the application at the development environment 
   (there are only application configuration files for [development](src/main/resources/application-development.properties) and [it](src/test/resources/application-it.properties) environments)**
5. Once local dependencies are running you can access mariadb and query it. <br>
   **[HINT] For getting binary(16) columns in UUID format use the following query template:**
   ```mariadb
   select 
   LOWER(CONCAT(
   SUBSTR(HEX(id), 1, 8),'-',
   SUBSTR(HEX(id), 9, 4), '-',
   SUBSTR(HEX(id), 13, 4), '-',
   SUBSTR(HEX(id), 17, 4), '-',
   SUBSTR(HEX(id), 21)
   )) as id, name, surname from users;
   ```
   
## Running tests.

run ```./mvnw -U clean test``` at the project source directory.

## Running integration tests.

Integration tests uses [test containers](https://testcontainers.com/) for booting app dependencies, then in order to 
run integration tests docker is required. <br>
run ```./mvnw -U clean verify``` at the project source directory.

## Testing the application with swagger ui.

1. run the application following ["Running the app" section steps](#running-the-app).
2. access swagger ui at: ````http://localhost:8080/swagger-ui/index.html````

## Contributing.

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License.

[the unlicense](LICENSE)
