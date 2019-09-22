# web-generator

Example project to demonstrate the usage of philipp22/web-generator-core in a web application environment.
The project contains a concrete implementation of a generator which generates a number of frontend and backend artifacts.

## Capabilities

The generator generates parts of a web application (backend and frontend) with the following technology stack:

### Backend

* Spring Boot
* MySQL

The following artifacts are generated:

* Domain classes
* DTOs (Data transfer objects)
* Converters that convert between incoming/outgoing DTOs and persisted domain objects
* DAOs (Data access objects)
* Rest endpoints (`@RestController`s)

### Frontend

* Angular 8
* Ngrx for state management
* ngrx-forms for redux-compliant form handling

The following artifacts are generated

* Redux store components
* Redux actions and effects for domain object retrieval and manipulation
* API clients
* Redux form components

## Build

To build and run the generator, run `mvn clean install exec:java`.
