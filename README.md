# Personal Finance Tracker — API Documentation

> Spring Boot REST API for personal finance management. JWT-authenticated.

## Tech Stack
- Java 17, Spring Boot 4.0.3, PostgreSQL
- Spring Security + JWT (jjwt 0.11.5)
- SpringDoc OpenAPI / Swagger UI 2.8.6
- SLF4J + Logback (`@Slf4j`), Maven, Render deployment

## Run Locally

```bash
DB_URL=jdbc:postgresql://localhost:5432/ExpenseTracker \
DB_USER=postgres DB_PASS=postgres \
JWT_SECRET=YourSuperSecretKeyThatIsAtLeast32CharactersLong123456 \
PORT=8004 mvn spring-boot:run
```

## Authentication
All endpoints except `/public/**` require `Authorization: Bearer <token>`

1. `POST /public/signup` — register
2. `POST /public/login` — returns `{ "token": "..." }`

## API Quick Reference

### Public
| Method | Endpoint | Description |
|:---|:---|:---|
| POST | /public/signup | Register user |
| POST | /public/login | Get JWT token |

### User
| Method | Endpoint | Description |
|:---|:---|:---|
| PUT | /user/updateBalance | Set balances |
| GET | /user/getBalance | Get balances |

### Expenses
| Method | Endpoint | Description |
|:---|:---|:---|
| POST | /expense/newExpense | Create (deducts balance) |
| GET | /expense/allExpenses | All expenses |
| GET | /expense/allExpenses/{category} | By category |
| GET | /expense/filter | Filter by category+date |
| GET | /expense/total | Total spent |
| PUT | /expense/updateExpense/{id} | Update |
| DELETE | /expense/delete?expenseId=1 | Delete |

paymentMode: CASH or ONLINE
category: FOOD, TRAVEL, RENT, ENTERTAINMENT, HEALTH, SHOPPING, UTILITIES, OTHERS

### Income
| Method | Endpoint | Description |
|:---|:---|:---|
| POST | /income/newIncome | Record income |
| GET | /income/allIncomes | All incomes |
| GET | /income/total | Total income |
| DELETE | /income/delete?incomeId=1 | Delete |

### Debts (money you borrowed)
| Method | Endpoint | Description |
|:---|:---|:---|
| POST | /debt/newDebt | Record debt |
| GET | /debt/allDebts | All debts + ledger |
| POST | /debt/pay | Pay off debt |
| GET | /debt/{id}/history | Payment history |
| DELETE | /debt/delete?debtId=1 | Delete |

isHistorical=false -> adds to balance (received cash)
isHistorical=true  -> old debt, no balance change

### Receivables (money owed to you)
| Method | Endpoint | Description |
|:---|:---|:---|
| POST | /receivable/newReceivable | Record receivable |
| GET | /receivable/allReceivables | All + ledger |
| POST | /receivable/collect | Collect payment |
| GET | /receivable/{id}/history | Collection history |
| DELETE | /receivable/delete?receivableId=1 | Delete |

## Swagger UI
http://localhost:8004/swagger-ui/index.html

To authenticate: click Authorize, enter "Bearer <your_token>"

## Logging
Services use @Slf4j. Usage:
  log.info("Message: {}", value);
  log.warn("Warning: {}", value);
  log.error("Error: {}", e.getMessage());

## Error Responses
- 401: Invalid/expired JWT
- 400: Wrong credentials
- 500: Insufficient balance or resource not found
