package RestAssured;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;


/**
 * Unit test for simple App.
 */
public abstract class BaseTest {
    static Properties properties = new Properties();
    static String token;
    static String username;
    static String filename;

    static ResponseSpecification positiveResponseBaseSpecs;
    static ResponseSpecification negativeResponseBaseSpecs;
    static RequestSpecification requestSpecs;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
        getProperties();
        token = properties.getProperty("token");
        username = properties.getProperty("username");
        filename = properties.getProperty("path_to_file");

        requestSpecs = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .build();

        positiveResponseBaseSpecs = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType("application/json")
                .expectBody("success", is(true))
                .expectBody("status", is(200))
                .build();

        negativeResponseBaseSpecs = new ResponseSpecBuilder()
                .expectContentType("application/json")
                .expectBody("success", is(false))
                .build();

    }

    public static void getProperties() {
        try (InputStream output = new FileInputStream("src/test/resources/application.properties")) {
            properties.load(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

