# OpenAPI Mocker <Still Under Development>

This project demonstrates how to create a dynamic API mocker using Spring Boot and OpenAPI specifications.

## Table of Contents

- [Overview](#overview)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running the Application](#running-the-application)
- [Usage](#usage)
  - [Dynamic API Handling](#dynamic-api-handling)
  - [Generating Fake Data](#generating-fake-data)
- [Contributing](#contributing)
- [License](#license)

## Overview

The OpenAPI Mocker project showcases how to build a Spring Boot application that dynamically handles API requests based on OpenAPI specifications. It also demonstrates how to generate fake data for responses using the Faker library.

## Getting Started

### Prerequisites

- Java 11 or later
- Maven

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/openapi-mocker.git
   cd openapi-mocker

### Running the Application
#### Build the project:
- `mvn clean install`
#### Run the application:
- `mvn spring-boot:run`

The application will start and listen on http://localhost:8080.

## Usage
### Dynamic API Handling
The application dynamically handles API requests based on the provided OpenAPI specifications. You can send requests to paths defined in the OpenAPI document, and the application will respond accordingly.

### Generating Fake Data
The application uses the Faker library to generate fake data for responses. It reads the OpenAPI schema and generates appropriate fake data based on the schema's properties.

### Contributing
Contributions are welcome! Feel free to open issues and submit pull requests.




