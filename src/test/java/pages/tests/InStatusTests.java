package pages.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;

import static utilities.StringTrimUtility.trimString;

public class InStatusTests {

    private final String BEARER_TOKEN = "8d16333936e72e705980878e18c95976";
    private String pageID;
    private String componentID;

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://api.instatus.com/v1";
    }

    @Test(priority = 1)
    public void testGetPages() {
        Response response = RestAssured.given().header("Authorization", "Bearer " + BEARER_TOKEN)
                .when().get("/pages").then().statusCode(200).extract().response();
        pageID = response.getBody().jsonPath().getString("id");
    }


    @Test(priority = 2)
    public void testEmptySecondPage() {
        String page2 = "[]";
        Response response = RestAssured.given().header("Authorization", "Bearer " + BEARER_TOKEN)
                .when().get("/pages?page=2&per_page=10").then().statusCode(200).extract().response();
        Assert.assertEquals(response.getBody().asString(), page2);
    }

    @Test(priority = 3)
    public void getAllComponents() {
        for (String id:pageID.split(",")) {
            id = trimString(id);
            Response response = RestAssured.given().header("Authorization", "Bearer " + BEARER_TOKEN)
                    .when().get("/" + id + "/components ").then().statusCode(200).extract().response();
            System.out.println(response.getBody().asString());

            componentID = response.getBody().jsonPath().getString("id");
        }
        System.out.println();
    }

    @Test(priority = 4)
    public void getComponents() {
        for (String id:pageID.split(",")) {
            for (String component:componentID.split(",")) {
                id = trimString(id);
                component = trimString(component);

                Response response = RestAssured.given().header("Authorization", "Bearer " + BEARER_TOKEN)
                        .when().get("/" + id + "/components/" + component).then().statusCode(200).extract().response();
                System.out.println(response.getBody().asString());
            }
            System.out.println();
        }
    }

    @Test(priority = 5)
    public void getIncidents() {
        for (String id:pageID.split(",")) {
            id = trimString(id);
            Response response = RestAssured.given().header("Authorization", "Bearer " + BEARER_TOKEN)
                    .when().get("/" + id + "/incidents ").then().statusCode(200).extract().response();
            System.out.println(response.getBody().asString());
        }
    }

    @Test(priority = 6)
    public void testGetPagesWithInvalidToken() {
        String invalidTokenMessage = "API key does not belong to a user.";
        Response response = RestAssured.given().header("Authorization", "Bearer X" + BEARER_TOKEN)
                .when().get("/pages").then().statusCode(401).extract().response();
        String error = response.getBody().jsonPath().getString("error");
        Assert.assertTrue(error.contains(invalidTokenMessage), "API key is valid");
    }

    @Test(priority = 7)
    public void testGetComponentsWithInvalidPageID() {
        String errorMessage = "There is no status page with this ID.";
        Response response = RestAssured.given().header("Authorization", "Bearer " + BEARER_TOKEN)
                .when().get("/invalid_id/components").then().statusCode(404).extract().response();
        String error = response.getBody().jsonPath().getString("error");
        Assert.assertTrue(error.contains(errorMessage), "This id has a status page");
    }

    @Test(priority = 8)
    public void testGetComponentWithInvalidComponentID() {
        String[] arrayPageID = pageID.split(",");
        String validPageID = trimString(Arrays.toString(arrayPageID));
        String errorMessage = "No status page for that component";
        Response response = RestAssured.given().header("Authorization", "Bearer " + BEARER_TOKEN)
                .when().get("/" + validPageID + "/components/invalid_component_id").then().statusCode(500).extract().response();

        String error = response.getBody().jsonPath().getString("error");
        Assert.assertTrue(error.contains(errorMessage), "This component has a status page");
    }

    @Test(priority = 9)
    public void testGetIncidentsWithInvalidPageID() {
        String errorMessage = "There is no status page with this ID.";
        Response response = RestAssured.given().header("Authorization", "Bearer " + BEARER_TOKEN)
                .when().get("/invalid_id/incidents").then().statusCode(404).extract().response();
        String error = response.getBody().jsonPath().getString("error");
        Assert.assertTrue(error.contains(errorMessage), "This id has a status page");
    }
}
