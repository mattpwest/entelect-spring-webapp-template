# Entelect Spring WebApp Project

This template is based on the the project configuration from the last project I worked on. I wanted to retain a layered
structure for the code base as with our previous template, while incorporating some of the cool new stuff from Spring
and Spring Boot.

The result is this project template which includes:
 * Authentication & Authorization (with custom authorization checks, remember me and sudo)
 * Environment configuration (switch between in-memory dev profile and MSSQL local or prod profiles)
 * Email notifications (with in-memory mail server for testing in dev and local profiles and on-line template editing)
 * Flyway (for managing database upgrade scripts)
 * History events (simple mechanism for logging user viewable history events)
 * Layered structure (shared test & config modules, then domain, persistence, communication, service & web)
 * Read-only REST API for listing system users
 * Thymeleaf templates
 * User management and work flows (registration, verification, password reset, email change, etc)

## Getting Started

To try out the template project:
 1. Clone the repository
 2. Build with Maven `clean install`
 3. Set the environment variable `ENTELECT_ENV=dev`
 4. Deploy to a Tomcat instance
 5. Sign in with username `admin@entelect.co.za` and password `test123`
