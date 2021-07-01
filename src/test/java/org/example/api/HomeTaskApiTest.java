package org.example.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.example.model.Order;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class HomeTaskApiTest {
    @BeforeClass
    public void prepare() throws IOException {

        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/")
                .addHeader("api_key", System.getProperty("api.key"))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
        RestAssured.filters(new ResponseLoggingFilter());
    }

    @Test
    public void createOrder() {
        Order order = new Order();
        order.setId(Integer.parseInt(System.getProperty("id")));
        order.setShipDate(System.getProperty("shipDate"));
        order.setStatus(System.getProperty("status"));

        given()
                .body(order)
                .when()
                .post("store/order")

                .then()
                .statusCode(200);
        Order newOrder =
                given()
                        .pathParam("id", Integer.parseInt(System.getProperty("id")))
                        .when()
                        .get("store/order/{id}")
                        .then()
                        .extract().body()
                        .as(Order.class);
        if (order.toString().equals(newOrder.toString())) System.out.println("Right!");
    }

    @Test
    public void deleteOrder() {
        given().
                pathParam("id", Integer.parseInt(System.getProperty("id")))
                .when()
                .delete("store/order/{id}")
                .then()
                .statusCode(200);
        given()
                .pathParam("id", Integer.parseInt(System.getProperty("id")))
                .when()
                .get("store/order/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    public void saveHashMap() {
        Map inventory =
                given()
                        .when()
                        .get("store/inventory")
                        .then()
                        .extract().body()
                        .as(Map.class);
        Assert.assertTrue(inventory.containsKey("string"));
    }
}
