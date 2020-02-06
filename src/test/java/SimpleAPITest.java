import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleAPITest {

    public String getSessionId() throws IOException {
        // URL
        String url = "http://localhost:8080/rest/auth/1/session";
        // Store body/payloads json file in java object
        String loginPayload = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir") +"/payloads/login.json")));

        RequestSpecification requestSpecification = RestAssured.given().body(loginPayload);
        requestSpecification.contentType("application/json");

        Response response =  requestSpecification.post(url);

        //System.out.println(response.statusCode());
        String stringResponse = response.asString();

        JsonPath jsonPath = new JsonPath(stringResponse);
        String sessionId = jsonPath.get("session.value");
       // System.out.println(sessionId);
       return sessionId;

    }

    @Test
    public void createIssue() throws IOException {
        SimpleAPITest simpleAPITest = new SimpleAPITest();
        String sessionId = simpleAPITest.getSessionId();
        String url = "http://localhost:8080/rest/api/2/issue/";
        String createIssuePayload = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir") +"/payloads/createIssue.json")));

        RequestSpecification requestSpecification = RestAssured.given().body(createIssuePayload);
        requestSpecification.contentType("application/json");
        requestSpecification.header("Cookie", "JSESSIONID=" + sessionId);

        Response response = requestSpecification.post(url);

        String stringResponse = response.asString();
        JsonPath jsonPath = new JsonPath(stringResponse);
        String issueKey = jsonPath.get("key");

        Assert.assertEquals(response.statusCode(), 201);
        Assert.assertEquals(issueKey,"QAA-32" );

    }
}
