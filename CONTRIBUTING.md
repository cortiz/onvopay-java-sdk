# Contributing to OnvoPay Java SDK

Thanks for your interest in contributing! This document outlines how to propose changes and set up your environment.

## Getting started
- Java 21+
- Maven 3.9+
- Fork this repository and clone your fork.

## Build and test
- Build: `mvn -B -ntp -U -e verify`
- Run tests: `mvn test`

## Branching model
- Create feature branches from `develop`.
- Open Pull Requests targeting `main` (if the project uses GitFlow-like model, PRs may target `develop`). Follow the repo’s current workflow indicated by open PRs.

## Coding guidelines
- Follow standard Java conventions.
- Add/maintain Javadoc on public APIs.
- Keep dependencies minimal and up-to-date.
- Include unit tests for new behavior and bug fixes.

## Commit messages
- Write clear, descriptive commit messages.
- Reference related issues when applicable (e.g., "Fixes #123").

## Pull Requests
- Use the PR template.
- Ensure CI is green.
- Provide tests and documentation updates as appropriate.

## Release process
- The project uses Maven and GitHub Actions.
- Artifacts are published to GitHub Packages until further notice.

## Code of Conduct
By participating, you agree to abide by the Code of Conduct (see CODE_OF_CONDUCT.md).
