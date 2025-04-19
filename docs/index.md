# Installation
## For server owners:
- Download the latest release.
- Place the .jar into your server's `plugins` folder.

## For developers looking to use the API:
Maven:
```xml
<dependencies>
    <!-- ... -->
    <dependency>
        <groupId>co.dotarch.paper</groupId>
        <artifactId>x</artifactId>
        <version><!-- Latest version number --></version>
        <scope>provided</scope>
    </dependency>
    <!-- ... -->
</dependencies>
```

Gradle:
```groovy
compileOnly 'co.dotarch.paper:x:<!-- Latest version number -->' // The full Spigot server with no shadowing. Requires mavenLocal.
```
> Note: I haven't tried using this with Gradle, if this is wrong please PR the correct usage. Sorry - I don't really use gradle XD

## [API usage](usage.md)