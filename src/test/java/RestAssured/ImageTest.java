package RestAssured;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.MultiPartSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ImageTest extends BaseTest {
    String uploadedImageDeleteHashId;
    MultiPartSpecification multiPartSpecification;

    @BeforeEach
    void beforeTest() {
        String base64Content = FileUtils.getContentAsBase64String(filename);
        multiPartSpecification = new MultiPartSpecBuilder(base64Content)
                .controlName("image")
                .build();
    }

    @Test
    void uploadFileBase64Test() {
        uploadedImageDeleteHashId = given()
                .spec(requestSpecs)
                .multiPart(multiPartSpecification)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .spec(positiveResponseBaseSpecs)
                .body("data.id", is(notNullValue()))
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void negativeUploadFileBase64Test() {
        given()
                .spec(requestSpecs)
                .multiPart("image", "123456")
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .spec(negativeResponseBaseSpecs)
                .statusCode(400);
        uploadedImageDeleteHashId = null;
    }

    @Test
    void withoutTokenUploadFileBase64Test() {
        uploadedImageDeleteHashId = null;
        given()
                .multiPart(multiPartSpecification)
                .when()
                .post("https://api.imgur.com/3/image")
                .then()
                .spec(negativeResponseBaseSpecs)
                .statusCode(401);
    }

    @Test
    void uploadFileImageTest() {
        uploadedImageDeleteHashId = given()
                .spec(requestSpecs)
                .multiPart("image", FileUtils.getFileContent(filename))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .spec(positiveResponseBaseSpecs)
                .body("data.id", is(notNullValue()))
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void imageDetailsTest() {
        JsonPath rspJson = given()
                .spec(requestSpecs)
                .multiPart("image", new File("src/test/resources/waterfall.jpg"))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .spec(positiveResponseBaseSpecs)
                .extract()
                .response()
                .jsonPath();

        uploadedImageDeleteHashId = rspJson
                .getString("data.deletehash");
        String imageHash = rspJson
                .getString("data.id");

        given()
                .spec(requestSpecs)
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}", imageHash)
                .then()
                .spec(positiveResponseBaseSpecs)
                .body("data.id", is(imageHash));
    }

    @Test
    void imageCountTest() {
        int cntBefore = given()
                .spec(requestSpecs)
                .when()
                .get("https://api.imgur.com/3/account/{username}/images/count", username)
                .prettyPeek()
                .then()
                .spec(positiveResponseBaseSpecs)
                .extract()
                .jsonPath()
                .getInt("data");

        JsonPath rspJson = given()
                .spec(requestSpecs)
                .multiPart(multiPartSpecification)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .spec(positiveResponseBaseSpecs)
                .extract()
                .response()
                .jsonPath();
        uploadedImageDeleteHashId = rspJson
                .getString("data.deletehash");
        given()
                .spec(requestSpecs)
                .when()
                .get("https://api.imgur.com/3/account/{username}/images/count", username)
                .then()
                .spec(positiveResponseBaseSpecs)
                .body("data", is(cntBefore + 1));
    }

    @Test
    void updateImageInformationTest() {
        JsonPath rspJson = given()
                .spec(requestSpecs)
                .multiPart(multiPartSpecification)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .spec(positiveResponseBaseSpecs)
                .extract()
                .response()
                .jsonPath();

        uploadedImageDeleteHashId = rspJson
                .getString("data.deletehash");
        String imageHash = rspJson
                .getString("data.id");

        given()
                .spec(requestSpecs)
                .formParam("title", "title")
                .formParam("description", "description")
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}", imageHash)
                .prettyPeek()
                .then()
                .spec(positiveResponseBaseSpecs)
                .body("success", is(true))
                .extract()
                .response()
                .jsonPath();
    }

    @Test
    void favoriteAnImageTest() {
        JsonPath rspJson = given()
                .spec(requestSpecs)
                .multiPart(multiPartSpecification)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .spec(positiveResponseBaseSpecs)
                .extract()
                .response()
                .jsonPath();

        uploadedImageDeleteHashId = rspJson
                .getString("data.deletehash");
        String imageHash = rspJson
                .getString("data.id");

        given()
                .spec(requestSpecs)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", imageHash)
                .prettyPeek()
                .then()
                .spec(positiveResponseBaseSpecs)
                .body("data", is("favorited"))
                .extract()
                .response()
                .jsonPath();
    }

    @Test
    void imageCheckFav() {
        JsonPath rspJson = given()
                .spec(requestSpecs)
                .multiPart(multiPartSpecification)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .spec(positiveResponseBaseSpecs)
                .extract()
                .response()
                .jsonPath();

        uploadedImageDeleteHashId = rspJson
                .getString("data.deletehash");
        String imageHash = rspJson
                .getString("data.id");
        Boolean favBefore = rspJson
                .getBoolean("data.favorite");

        given()
                .spec(requestSpecs)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", imageHash)
                .prettyPeek()
                .then()
                .spec(positiveResponseBaseSpecs)
                .body("data", is("favorited"))
                .extract()
                .response()
                .jsonPath();

        given()
                .spec(requestSpecs)
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}", imageHash)
                .then()
                .spec(positiveResponseBaseSpecs)
                .body("data.favorite", is(!favBefore));
    }

    //Imgur позволяет удалять изображения, которых не существует
    @Test
    void deleteNotExistImage() {
        given()
                .spec(requestSpecs)
                .when()
                .delete("https://api.imgur.com/3/image/{imageDeleteHash}", "1234567")
                .prettyPeek()
                .then()
                .spec(positiveResponseBaseSpecs);
    }

    @AfterEach
    void tearDown() {
        if (uploadedImageDeleteHashId != null) {
            given()
                    .spec(requestSpecs)
                    .when()
                    .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", username, uploadedImageDeleteHashId)
                    .prettyPeek()
                    .then()
                    .spec(positiveResponseBaseSpecs);
        }
    }
}

