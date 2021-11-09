package RestAssured;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBodyExtractionOptions;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ImageTest extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/waterfall.jpg";
    static String encodedFile;
    String uploadedImageDeleteHashId;

    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
    }


    @Test
    void uploadFileBase64Test() {
        uploadedImageDeleteHashId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void negativeUploadFileBase64Test() {
        given()
                .headers("Authorization", token)
                .multiPart("image", "123456")
                .expect()
                .body("success", is(false))
                .statusCode(400)
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek();
        uploadedImageDeleteHashId=null;
    }

    @Test
    void withoutTokenUploadFileBase64Test() {
        uploadedImageDeleteHashId = null;
                given()
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(false))
                .statusCode(401)
                .when()
                .post("https://api.imgur.com/3/image");
    }

    @Test
    void uploadFileImageTest() {
        uploadedImageDeleteHashId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void imageDetailsTest(){
        JsonPath rspJson = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/test/resources/waterfall.jpg"))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath();

        uploadedImageDeleteHashId = rspJson
                .getString("data.deletehash");
        String imageHash = rspJson
                .getString("data.id");

        given()
                .headers("Authorization", token)
                .expect()
                .body("success", is(true))
                .body("data.id", is(imageHash))
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}", imageHash);

    }

    @Test
    void imageCountTest(){
        int cntBefore = given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .when()
                .get("https://api.imgur.com/3/account/{username}/images/count",username)
                .prettyPeek()
                .jsonPath()
                .getInt("data");

        JsonPath rspJson = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/test/resources/waterfall.jpg"))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath();
        uploadedImageDeleteHashId = rspJson
                .getString("data.deletehash");
        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .body("data",is(cntBefore+1))
                .when()
                .get("https://api.imgur.com/3/account/{username}/images/count",username);
    }

    @Test
    void updateImageInformationTest(){
        JsonPath rspJson = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/test/resources/waterfall.jpg"))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath();
        uploadedImageDeleteHashId = rspJson
                .getString("data.deletehash");
        String imageHash = rspJson
                .getString("data.id");
        given()
                .headers("Authorization", token)
                .formParam("title","title")
                .formParam("description","description")
                .expect()
                .statusCode(200)
                .body("success", is (true))
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}", imageHash)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath();
    }

    @Test
    void favoriteAnImageTest(){
        JsonPath rspJson = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/test/resources/waterfall.jpg"))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath();
        uploadedImageDeleteHashId = rspJson
                .getString("data.deletehash");
        String imageHash = rspJson
                .getString("data.id");
        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .body("success", is (true))
                .body("data", is("favorited"))
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", imageHash)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath();
    }

    @Test
    void imageCheckFav(){
        JsonPath rspJson = given()
                .headers("Authorization", token)
                .multiPart("image", new File("src/test/resources/waterfall.jpg"))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath();

        uploadedImageDeleteHashId = rspJson
                .getString("data.deletehash");
        String imageHash = rspJson
                .getString("data.id");
        Boolean favBefore=rspJson
                .getBoolean("data.favorite");
        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(200)
                .body("success", is (true))
                .body("data", is("favorited"))
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", imageHash)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath();

        given()
                .headers("Authorization", token)
                .expect()
                .body("success", is(true))
                .body("data.favorite", is  (!favBefore))
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}", imageHash);

    }

    //Imgur позволяет удалять изображения, которых не существует
    @Test
    void deleteNotExistImage(){
        given()
                    .headers("Authorization", token)
                    .expect()
                    .statusCode(200)
                    .when()
                    .delete("https://api.imgur.com/3/image/{imageDeleteHash}",  "1234567")
                    .prettyPeek()
                    .then();
    }

    @AfterEach
    void tearDown() {
        if ( uploadedImageDeleteHashId!=null) {
            given()
                    .headers("Authorization", token)
                    .when()
                    .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", username, uploadedImageDeleteHashId)
                    .prettyPeek()
                    .then()
                    .statusCode(200);
        }
    }

    private byte[] getFileContent() {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }


}

