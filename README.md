money-transfer is a simple application meant to transfer money from one Account to another. 

## Libraries and Frameworks used:
1. Apache CXF for REST 
2. JUnit for Testing

## API descriptions

### Admin API:
Purpose: Add and get account. Only basic things taken care, for simplicity, since not part of main requirement.
#### POST /v1/admin/account 
Creates account <br/>
Sample Request:
```
{
	"accountNo":2,
	"balance":"1000"
}
```
Curl script:
```
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X POST --data  "{\"accountNo\":5,\"balance\":\"1000\"}" localhost:9000/v1/admin/account
```

#### GET /v1/admin/account/{accountNo}
Returns details of account <br/>
Sample Request:
```
localhost:9000/v1/admin/account/1
```
Sample Response:
```
{
	"accountNo":1,
	"balance":"12009.7"
}
```
Curl script:
```
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET localhost:9000/v1/admin/account/6
```

### MoneyTransfer API:
Purpose: API for Transfer between accounts

#### POST /v1/transfer
Transfers money from one account to another <br/>
Sample request:
```
{
	"fromAccount":1,
	"toAccount":2,
	"amount":"10.5"
}
```
Curl script:
```
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X POST --data  "{\"fromAccount\":5,\"toAccount\":2,\"amount\":\"3461\"}" localhost:9000/v1/transfer
```
Note: Depending on the system you are running the script on, escaping of quotes(") may vary.
 

## Build and deploy
To Build:
```
gradelw clean build
```

To Deploy:
```
gradelw run
```

##Unit tests
Includes both unit and load tests <br/>
To build and run tests:
```
gradelw test
```





