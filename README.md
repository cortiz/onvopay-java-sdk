# OnvoPay Java SDK

A lightweight, modern Java SDK to integrate with OnvoPay (https://onvopay.com). This library aims to provide simple, type-safe access to OnvoPay APIs in JVM applications and backend services.

Note: This project is in early development and the public API is subject to change until a stable v1.0.0 is released.

## Status
- Java version: 21+
- Build tool: Maven
- CI: GitHub Actions

[![CI - Build and Test](https://github.com/cortiz/onvopay-java-sdk/actions/workflows/ci.yml/badge.svg)](https://github.com/cortiz/onvopay-java-sdk/actions/workflows/ci.yml)

## Features (planned)

- Authenticated API httpClient for OnvoPay
- Idiomatic Java models and error handling
- JSON serialization via Jackson
- First-class test support

## Installation
This library is currently published to GitHub Packages (GitHub Maven Registry). You’ll need to authenticate Maven or Gradle to read from the GitHub Packages registry for this repository.

Group and artifact coordinates from the POM:
- groupId: `com.github.cortiz`
- artifactId: `onvopay-java-sdk`
- version: `1.0.0-SNAPSHOT` (example)

### Using Maven
1) Configure your `~/.m2/settings.xml` with a GitHub Packages server entry:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_PERSONAL_ACCESS_TOKEN</password>
    </server>
  </servers>
</settings>
```

2) Add dependency to your project’s `pom.xml`:

```xml
<dependency>
  <groupId>com.github.cortiz</groupId>
  <artifactId>onvopay-java-sdk</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

No repository stanza is necessary if the artifact is being resolved via this project’s `distributionManagement` section and your settings.xml includes the `github` server id shown above.

### Using Gradle (Kotlin DSL)
Add repository and dependency:

```kotlin
repositories {
  maven {
    url = uri("https://maven.pkg.github.com/cortiz/onvopay-java-sdk")
    credentials {
      username = System.getenv("GITHUB_USERNAME") ?: "YOUR_GITHUB_USERNAME"
      password = System.getenv("GITHUB_TOKEN") ?: "YOUR_GITHUB_PERSONAL_ACCESS_TOKEN"
    }
  }
}

dependencies {
  implementation("com.github.cortiz:onvopay-java-sdk:1.0.0-SNAPSHOT")
}
```

## Quick Start

```java
import com.github.cortiz.onvopay.OnvoPayAPIClient;

public class App {
    static void main(String[] args) {
    // Placeholder usage — the API surface will expand as endpoints are added.
        OnvoPayAPIClient httpClient = new OnvoPayAPIClient();
        // TODO: Configure httpClient with API key/base URL and call endpoints as they are implemented.
  }
}
```

## Requirements
- Java 21+

## Development
- Build: `mvn -B -ntp -U -e verify`
- Run tests: `mvn -q test`

## Versioning
This project follows Semantic Versioning once stable releases begin (MAJOR.MINOR.PATCH). Until v1.0.0, breaking changes may occur in minor versions.

## Contributing
Contributions are welcome!
- Please read CONTRIBUTING.md for guidelines.
- Open a GitHub Issue with details and reproduction steps when reporting bugs.
- Use the Pull Request template for contributions.

## Security
If you discover a security issue, please avoid filing a public issue. Instead, follow the instructions in SECURITY.md or contact the maintainer directly.

## License
This project is licensed under the BSD 3-Clause License. See the LICENSE file for details.


---

## Public Maven Repository (JitPack)
This project is available publicly via JitPack, so you can consume it without authentication.

- Project page: https://jitpack.io/#cortiz/onvopay-java-sdk
- Badge: [![](https://jitpack.io/v/cortiz/onvopay-java-sdk.svg)](https://jitpack.io/#cortiz/onvopay-java-sdk)

### Gradle (Kotlin DSL)
```kotlin
repositories {
  maven(url = "https://jitpack.io")
}

dependencies {
  implementation("com.github.cortiz:onvopay-java-sdk:1.0.0-SNAPSHOT") // or use a tagged release version
}
```

### Maven
Add the JitPack repository and dependency:
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.cortiz</groupId>
    <artifactId>onvopay-java-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

Note: Replace 1.0.0-SNAPSHOT with a specific Git tag once releases are tagged (recommended for reproducible builds).

