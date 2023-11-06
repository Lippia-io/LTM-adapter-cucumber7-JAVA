# [JVM] Lippia Test Manager Cucumber7 Adapter
[![Crowdar Official Page](https://img.shields.io/badge/crowdar-official%20page-brightgreen)](https://crowdar.com.ar/)
[![Lippia Official Page](https://img.shields.io/badge/lippia-official%20page-brightgreen)](https://www.lippia.io/)

 The Lippia Test Manager adapter allows to ingest cucumber test results into a Lippia Test Manager instance. 
 To have access to a Lippia Test Manager go to [Lippia.io](https://lippia.io/) website.
 To use it you need to to have a test automation code project and use Cucumber 7. 

## Getting Started

### Import dependency
```xml
<dependencies>
    ...
    <dependency>
        <groupId>io.lippia.report</groupId>
        <artifactId>ltm-cucumber7-adapter</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    ...
</dependencies>
```

### Report Class
You need to create the class that implements the **TestManagerAPIAdapter** interface to capture screenshot using a variable *driver*  depending on the type of application you are testing with the automation project

### Web Applications 
```java
 import org.openqa.selenium.remote.RemoteWebDriver
```

### Mobile Applications 
```java
import io.appium.java_client.AppiumDriver 
```

### Class implementation

```java
public class TestManagerReporter extends TestManagerAPIAdapter {
    public TestManagerReporter(String arg0) {
        super();
    }

    @Override
    public String getBase64Image() {
        return ((TakesScreenshot) driver.getScreenshotAs(OutputType.BASE64);
    }
}
```

### Configure the class like as Cucumber Plugin
```xml
<properties>
    ...
    <test-manager.plugin>--plugin classpath:TestManagerReporter:</test-manager.plugin>
    <cucumber.options>... ${test-manager.plugin}</cucumber.options>
    ...
</properties>
```

### Configure the following properties 
```xml
<plugins>
    ...
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>${maven-surefire-plugin.version}</version>
        <configuration>
            ...
            <systemPropertyVariables>
                <TEST_MANAGER_USERNAME></TEST_MANAGER_USERNAME>
                <TEST_MANAGER_PASSWORD></TEST_MANAGER_PASSWORD>
                <TEST_MANAGER_API_HOST></TEST_MANAGER_API_HOST>
                <TEST_MANAGER_API_PORT></TEST_MANAGER_API_PORT>
                <TEST_MANAGER_RUN_NAME></TEST_MANAGER_RUN_NAME>
                <TEST_MANAGER_PROJECT_CODE></TEST_MANAGER_PROJECT_CODE>
            </systemPropertyVariables>
            ...
        </configuration>
    </plugin>
    ...
</plugins>
```
If you need to know in detail the purpose of each of the properties mentioned above, please continue to the next point, otherwise, ignore it.

| Key                        | Concept                                                                 | Is         |
|----------------------------|-------------------------------------------------------------------------|------------|
| TEST_MANAGER_USERNAME      | User with which the Test Manager instance will be authenticated         | Mandatory  |
| TEST_MANAGER_PASSWORD      | Password with which the Test Manager instance will be authenticated     | Mandatory  |
| TEST_MANAGER_API_HOST      | Host to which the adapter will attempt to authenticate                  | Mandatory  |
| TEST_MANAGER_API_PORT      | Port on which the Test Manager instance will be listening               | Optional   |
| TEST_MANAGER_RUN_NAME      | Run name, serves as identifier of the suite execution                   | Mandatory  |
| TEST_MANAGER_PROJECT_CODE  | Project Code into which the adapter will attempt to inject test results | Mandatory  |

### Configure screenshot strategies
By default, it won't take screenshots; If you need to configure your injection, please refer to the following reference & table

**config.properties**
```properties
...
test-manager.screenshots = <strategy>
...
```

| Key              | Concept                                                   | Is        |
|------------------|-----------------------------------------------------------|-----------|
| ON_EACH_STEP     | Will take screenshots in each step that is completed      | Optional  |
| ON_EACH_SCENARIO | Will take screenshots in each scenario that is completed  | Optional  |
| ON_FAILURE       | Will take screenshots in each failure step                | Optional  |
| DISABLED         | it won't take screenshots                                 | Optional  |
