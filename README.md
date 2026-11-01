# 🩸 Blood and Organ Donor Management System — Backend API

---

## 👤 Author

| Field | Details |
|-------|---------|
| **Name** | Reteno Tounkara Mady Baba |
| **Student ID** | 26748 |
| **Project** | Blood and Organ Donor Management System |


---

## 📋 Project Description

The **Blood and Organ Donor Management System** is a backend REST API that provides full CRUD operations, advanced queries (JPQL, derived queries, aggregations), business-rule enforcement, and paginated results for:

- **Donors** — Register and manage donor profiles (blood type, donor code, etc.)
- **Donations** — Track donation records with status flow (REGISTERED → SCREENED → COLLECTED / DEFERRED)
- **Donation Types** — Classify donations (blood, organ, etc.)
- **Blood Units** — Manage collected blood unit inventory linked to donations
- **Collection Sites** — Manage donation collection locations
- **Coordinators** — Manage coordinators assigned to collection sites and donation types
- **Dashboard** — Aggregated statistics and overview counts

---

## 🛠️ Tech Stack

| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 4.1.1 |
| Spring Data JPA (Hibernate) | Included |
| PostgreSQL | Latest |
| Maven | 3.x+ |
| Jackson (JSON / JSR-310) | Included |

---

## 🚀 Steps to Run the API

### Prerequisites

1. **Java 21** (JDK) — [Download here](https://adoptium.net/)
2. **Maven 3.x+** — [Download here](https://maven.apache.org/download.cgi)
3. **PostgreSQL** installed and running — [Download here](https://www.postgresql.org/download/)

### Step 1 — Clone the Repository

```bash
git clone https://github.com/<your-username>/bloodand-organ-donor-management-system-backend.git
cd bloodand-organ-donor-management-system-backend
```

### Step 2 — Create the PostgreSQL Database

Open a PostgreSQL shell (`psql`) or pgAdmin and run:

```sql
CREATE DATABASE blood_donor_db;
```

### Step 3 — Configure Database Credentials

Edit `src/main/resources/application.properties` and set your PostgreSQL username and password:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blood_donor_db
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8080
```

### Step 4 — Build the Project

```bash
mvn clean install
```

### Step 5 — Run the Application

```bash
mvn spring-boot:run
```

The API will start on **http://localhost:8080**. Tables are auto-created/updated by Hibernate, and seed data is loaded via `DataInitializer`.

---

 API Endpoints

### 1. Donor Endpoints — `/api/donors`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/donors/save` | Create a new donor |
| GET | `/api/donors/all` | Get all donors |
| GET | `/api/donors/{id}` | Get a donor by UUID |
| PUT | `/api/donors/update/{id}` | Update a donor |
| DELETE | `/api/donors/delete/{id}` | Delete a donor |
| GET | `/api/donors/by-last-name?lastName=X` | Find donors by last name (case-insensitive, ordered) — *Derived Query (B1)* |
| GET | `/api/donors/of-coordinator/{coordinatorId}` | Find distinct donors of a coordinator — *JPQL (C4)* |

### 2. Donation Endpoints — `/api/donations`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/donations/save` | Create a donation (validates date not in past, checks coordinator not double-booked, auto-sets status to REGISTERED) |
| GET | `/api/donations/all` | Get all donations |
| GET | `/api/donations/{id}` | Get a donation by UUID |
| PUT | `/api/donations/update/{id}` | Update a donation |
| DELETE | `/api/donations/delete/{id}` | Delete a donation (protected: returns 409 CONFLICT if it has blood units) |
| GET | `/api/donations/by-status?status=X` | Find donations by status — *Derived Query (B2)* |
| GET | `/api/donations/between?start=X&end=Y` | Find donations in a date range (ISO dates) — *Derived Query (B3)* |
| PATCH | `/api/donations/{id}/status?status=X` | Update donation status with flow validation — *Business Rule (D2)* |
| GET | `/api/donations/stats/by-status` | Get donation count per status — *JPQL Aggregate (F1)* |
| PATCH | `/api/donations/cancel-day?coordinatorId=X&date=Y` | Cancel all donations for a coordinator on a given date — *JPQL Bulk Update (F2)* |
| GET | `/api/donations/page?page=0&size=5&sort=donationDate,desc` | Paginated donations — *Bonus* |

### 3. Donation Type Endpoints — `/api/donation-types`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/donation-types/save` | Create a donation type |
| GET | `/api/donation-types/all` | Get all donation types |
| GET | `/api/donation-types/{id}` | Get a donation type by UUID |
| PUT | `/api/donation-types/update/{id}` | Update a donation type |
| DELETE | `/api/donation-types/delete/{id}` | Delete a donation type |
| GET | `/api/donation-types/unused` | Get donation types held by no coordinator — *JPQL IS EMPTY (C3)* |

### 4. Blood Unit Endpoints — `/api/blood-units`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/blood-units/save?donationId=X` | Save a blood unit linked to a donation (validates donation exists and status is valid) |
| GET | `/api/blood-units/all` | Get all blood units |
| GET | `/api/blood-units/{id}` | Get a blood unit by UUID |
| DELETE | `/api/blood-units/delete/{id}` | Delete a blood unit |
| GET | `/api/blood-units/donor/{donorCode}` | Get blood unit history for a donor by donor code — *JPQL with path navigation (E)* |

### 5. Collection Site Endpoints — `/api/collection-sites`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/collection-sites/save` | Create a collection site |
| GET | `/api/collection-sites/all` | Get all collection sites |
| GET | `/api/collection-sites/{id}` | Get a collection site by UUID |
| PUT | `/api/collection-sites/update/{id}` | Update a collection site |
| DELETE | `/api/collection-sites/delete/{id}` | Delete a collection site |
| GET | `/api/collection-sites/free` | Get free/unassigned collection sites — *JPQL (C2)* |

### 6. Coordinator Endpoints — `/api/coordinators`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/coordinators/save` | Create a coordinator |
| GET | `/api/coordinators/all` | Get all coordinators |
| GET | `/api/coordinators/{id}` | Get a coordinator by UUID |
| PUT | `/api/coordinators/update/{id}` | Update a coordinator |
| DELETE | `/api/coordinators/delete/{id}` | Delete a coordinator |
| GET | `/api/coordinators/by-donation-type?name=X` | Find coordinators by donation type name — *JPQL with joins (C1)* |
| PUT | `/api/coordinators/{coordinatorId}/donation-types/{donationTypeId}` | Add a donation type to a coordinator — *Business Rule (D3)* |
| PATCH | `/api/coordinators/{coordinatorId}/collection-site?siteNumber=X` | Move coordinator to another collection site — *Business Rule (D4)* |

### 7. Dashboard Endpoint — `/api/dashboard`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/dashboard/summary` | Get dashboard summary (counts of donors, donations, coordinators, etc.) |

> **Total: 7 controllers, ~40 endpoints**

---

## 🗂️ Domain Entities

| Entity | Key Fields |
|--------|------------|
| **Donor** | `id` (UUID), `firstName`, `lastName`, `donorCode` (unique), `bloodType`, `phone` |
| **Donation** | `id` (UUID), `donationDate`, `status` (enum), `urgency`, → `Donor`, → `Coordinator` |
| **DonationStatus** | Enum: `REGISTERED`, `SCREENED`, `COLLECTED`, `DEFERRED` |
| **DonationType** | `id` (UUID), `name`, ↔ `Coordinator` (ManyToMany) |
| **BloodUnit** | `id` (UUID), `component`, `remarks`, `volumeMl`, `collectedDate`, → `Donation` |
| **CollectionSite** | `id` (UUID), `siteNumber`, `name` |
| **Coordinator** | `id` (UUID), `firstName`, `lastName`, `email`, → `CollectionSite`, ↔ `DonationType` (ManyToMany) |

---

## 📁 Project Structure

```
src/main/java/com/mady/bloodorgandonorsystem/
├── BloodAndOrganDonorManagementSystemApplication.java   # Main entry point
├── DataInitializer.java                                  # Seed data on startup
├── controller/
│   ├── BloodUnitController.java
│   ├── CollectionSiteController.java
│   ├── CoordinatorController.java
│   ├── DashboardController.java
│   ├── DonationController.java
│   ├── DonationTypeController.java
│   └── DonorController.java
├── domain/
│   ├── BloodUnit.java
│   ├── CollectionSite.java
│   ├── Coordinator.java
│   ├── Donation.java
│   ├── DonationStatus.java          # Enum
│   ├── DonationType.java
│   └── Donor.java
├── repository/
│   ├── BloodUnitRepository.java
│   ├── CollectionSiteRepository.java
│   ├── CoordinatorRepository.java
│   ├── DonationRepository.java
│   ├── DonationTypeRepository.java
│   └── DonorRepository.java
└── service/
    ├── BloodUnitService.java
    ├── CollectionSiteService.java
    ├── CoordinatorService.java
    ├── DashboardService.java
    ├── DonationService.java
    ├── DonationTypeService.java
    └── DonorService.java
```

---

## 🧪 Testing

### Postman Collection

A full Postman collection is included in `src/main/postman/` organized by assignment parts:

| Folder | Content |
|--------|---------|
| **Step 0** | Seed Data Verification & Setup |
| **Part A** | CRUD operations (Save, Get, Update, Delete) |
| **Part B** | Derived Queries (by last name, by status, between dates) |
| **Part C** | JPQL with Joins (coordinators by type, free sites, unused types, donors of coordinator) |
| **Part D** | Business Rules (status flow, assign donation type, move coordinator, double-booking check) |
| **Part E** | BloodUnit Entity (write, history, delete, protected deletion) |
| **Part F** | Reports & Bulk Updates (stats by status, cancel day) |
| **Bonus** | Dashboard summary, paginated donations |

