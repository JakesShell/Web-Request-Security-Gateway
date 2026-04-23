# Web Request Security Gateway

## Overview

Web Request Security Gateway is a Spring Boot application that demonstrates basic defensive request filtering and response hardening for web applications.

This project is positioned as a recruiter-ready backend security demo showing how a Java web service can inspect incoming HTTP requests, block suspicious patterns, and attach protective response headers.

The gateway includes:

- Request Inspection For Obvious Suspicious Query Patterns
- Security Header Injection On Outgoing Responses
- Small Demo Endpoints For Allowed And Blocked Traffic Behavior

## Real-World Business Use Case

This project maps to realistic backend security workflows used by:

- Backend Engineers
- Application Security Teams
- Platform Engineers
- Internal Security Review Teams
- Developers Building Secure APIs

A company may need to answer questions such as:

- How can suspicious request patterns be stopped before they reach core business logic?
- How can security headers be added consistently across responses?
- How can a lightweight request filtering layer be demonstrated in a Java application?
- How can teams prototype secure traffic handling before implementing more advanced controls?

This kind of project is useful as a proof-of-concept for defensive traffic handling in internal tools, APIs, gateways, and security-minded Java services.

## Key Features

- Request Filtering For Suspicious Query Patterns
- Response Header Hardening
- Spring Boot Web Application Structure
- Demo Health Endpoint
- Demo Search Endpoint
- Allowed Request Example
- Blocked Request Example

## Tech Stack

- Java
- Spring Boot
- Maven

## Project Structure

```text
Web-Request-Security-Gateway/
|-- pom.xml
|-- .gitignore
|-- README.md
|-- src/
|   |-- main/
|   |   |-- java/
|   |   |   |-- com/
|   |   |   |   |-- waf/
|   |   |   |   |   |-- WafApplication.java
|   |   |   |   |   |-- config/
|   |   |   |   |   |   |-- FilterConfig.java
|   |   |   |   |   |-- controller/
|   |   |   |   |   |   |-- DemoController.java
|   |   |   |   |   |-- filter/
|   |   |   |   |   |   |-- RequestFilter.java
|   |   |   |   |   |   |-- ResponseFilter.java
|   |   |-- resources/
|   |   |   |-- application.properties
|-- docs/
|   |-- images/
|       |-- allowed-request.png
|       |-- blocked-request.png

