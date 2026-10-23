RideFlow

RideFlow is a Spring Boot ride-booking backend. The current project
foundation includes user, driver-profile, vehicle, and ride entities,
PostgreSQL persistence, PostGIS spatial columns/indexes, and Flyway
database migrations. The next planned milestone focuses on nearby-driver
selection, driver assignment, and ride lifecycle handling.

Tech stack

Java 25

Spring Boot 4.1.1

Maven

Spring Data JPA and Hibernate

PostgreSQL 17+

PostGIS

Flyway database migrations

Spring Security and JWT

Prerequisites

Install and configure:

JDK 25.

PostgreSQL 17 or a compatible version.

PostGIS for your PostgreSQL installation.

Maven (or use the Maven wrapper if the repository includes mvnw /
mvnw.cmd).

Git.

Check the Java and Maven installations:

java -version
mvn -version

Database setup

The examples below use the database name rideflow_db and application
role rideflow_user. Use your own secure password; do not commit
passwords to Git.

1. Create the database and application role

Connect to PostgreSQL using an administrator account and run:

CREATE ROLE rideflow_user WITH LOGIN PASSWORD 'replace-with-a-strong-password' CONNECTION LIMIT 10;
CREATE DATABASE rideflow_db OWNER rideflow_user;

If the role already exists, do not create it again. Instead, update its
password or connection limit as needed:

ALTER ROLE rideflow_user WITH PASSWORD 'replace-with-a-strong-password';
ALTER ROLE rideflow_user CONNECTION LIMIT 10;

2. Enable PostGIS

Connect to the newly created rideflow_db database and run:

CREATE EXTENSION IF NOT EXISTS postgis;

Run this as a PostgreSQL administrator if the application role does not
have permission to create extensions.

If the database already exists, do not drop or recreate it just to set
up the project.

Environment variables

The application reads its database credentials and JWT secret from
environment variables:

DB_USERNAME

DB_PASSWORD

JWT_SECRET

Do not put real credentials or JWT secrets in application.properties,
README files, or source control.

Windows PowerShell --- current terminal session

Replace the example values with your own:

$env:DB_USERNAME = "rideflow_user"
$env:DB_PASSWORD = "your-database-password"
$env:JWT_SECRET = "replace-with-a-long-random-secret"

These values apply only to that PowerShell session and processes
launched from it.

Eclipse

If you run the application from Eclipse, open Run Configurations →
your RideFlow Spring Boot application → Environment and add the three
variables above. Restart the application after changing them.

Configure the database URL

The current src/main/resources/application.properties may point to a
local test database:

spring.datasource.url=jdbc:postgresql://localhost:5432/rideflow_test_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

For your regular local development database, change the URL to:

spring.datasource.url=jdbc:postgresql://localhost:5432/rideflow_db

Use a separate test database when verifying that migrations can build a
fresh schema. Do not point a fresh-database test at a database
containing data you need to preserve.

Flyway migrations

SQL migration files are stored in:

src/main/resources/db/migration/

Current migration files:

V1__create_users_and_driver_profiles.sql

V2__create_vehicles_and_rides.sql

V3__add_spatial_indexes.sql

Flyway runs pending migrations at application startup and records
applied migrations in flyway_schema_history.

Important for a fresh clone: verify that all three migrations
execute successfully against a newly created, empty test database before
relying on the setup on another developer's machine. A baseline in an
existing database can cause earlier migrations to be skipped; do not
manually insert rows into flyway_schema_history to make a migration
appear applied.

The project has used these Flyway settings during setup:

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=2
spring.flyway.validate-on-migrate=true
spring.flyway.fail-on-missing-locations=true
spring.flyway.out-of-order=false

The baseline settings were introduced while working with an existing
database. Review them before deploying to another environment, and
verify the behavior against a fresh database. For a clean,
migration-owned schema, Hibernate should validate the schema rather than
independently changing it; after migrations are confirmed, consider:

spring.jpa.hibernate.ddl-auto=validate

Do not change this setting without first confirming that the migrations
create every table and column expected by the entities.

Run the application

Option A: Maven

From the directory containing pom.xml, set the environment variables
in PowerShell and run:

mvn clean spring-boot:run

Option B: Eclipse

Import the project as an Existing Maven Project.

Wait for Maven dependencies to finish resolving.

Set DB_USERNAME, DB_PASSWORD, and JWT_SECRET in the Run
Configuration.

Confirm that spring.datasource.url points to the intended
database.

Run the main Spring Boot application class.

The configured application port is 8080.

Verify the database

Connect to the database configured in spring.datasource.url and run:

SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

Check the migration history:

SELECT installed_rank, version, description, success
FROM public.flyway_schema_history
ORDER BY installed_rank;

Check the spatial indexes:

SELECT indexname, indexdef
FROM pg_indexes
WHERE schemaname = 'public'
  AND indexname IN (
      'idx_driver_profiles_location',
      'idx_rides_pickup_location'
  )
ORDER BY indexname;

Expected application tables include users, driver_profiles,
vehicles, and rides. If startup fails, inspect the first
underlying exception in the console; later Spring bean errors may be
consequences of an earlier database or configuration failure.

Troubleshooting

FATAL: too many connections for role "rideflow_user"

Check the role's connection limit:

SELECT rolname, rolconnlimit
FROM pg_roles
WHERE rolname = 'rideflow_user';

Check its active sessions:

SELECT usename, application_name, state, COUNT(*) AS connection_count
FROM pg_stat_activity
WHERE usename = 'rideflow_user'
GROUP BY usename, application_name, state
ORDER BY connection_count DESC;

If appropriate, a PostgreSQL administrator can raise the limit:

ALTER ROLE rideflow_user CONNECTION LIMIT 10;

The application also configures a small local Hikari pool:

spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.minimum-idle=1

PostGIS extension or geography type errors

Make sure PostGIS is installed for your PostgreSQL installation and
enabled in the target database:

CREATE EXTENSION IF NOT EXISTS postgis;

Environment variable errors

Confirm that all three variables (DB_USERNAME, DB_PASSWORD,
JWT_SECRET) are available to the process launching Spring Boot.
Restart Eclipse or the terminal process if you changed environment
variables outside the running process.

Migrations do not run

Confirm spring.flyway.enabled=true.

Confirm migration files are under
src/main/resources/db/migration/.

Confirm filenames follow Flyway's versioned naming convention, such
as V1__description.sql.

Check the log for the database name and Flyway's
validation/migration messages.

Check flyway_schema_history in the same database that appears in
the JDBC URL.

Do not delete migration history or reset a database containing data
as a troubleshooting shortcut.

Security notes

Never commit database passwords, production credentials, or real JWT
secrets.

Keep local secrets in environment variables or an ignored local
configuration file.

Use a strong, randomly generated JWT secret appropriate for the
configured signing algorithm.

Use separate databases and credentials for development, testing, and
production.

Back up important databases before making schema changes.

Current development focus

The next planned RideFlow milestone is driver assignment and ride
lifecycle management, including nearby-driver search, Haversine distance
calculation, driver ranking, and concurrency-safe assignment. The
existing schema and PostGIS setup are the foundation; implement and test
the assignment logic before adding database changes unless a specific
requirement calls for a migration.