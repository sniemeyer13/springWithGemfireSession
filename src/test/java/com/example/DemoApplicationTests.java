package com.example;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ExtractableResponse;
import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
@WebIntegrationTest(randomPort = true)
public class DemoApplicationTests {
	@Value("${local.server.port}")
	private int port;

	@Before
	public void setup() {
		RestAssured.port = port;
		RestAssured.baseURI = "http://127.0.0.1";
	}

	@Test
	public void testWithManualSessionCreation() {
		ExtractableResponse<Response> responseFromFirstCall = given().get("/").then().extract();
		String sessionFromFirstCall = responseFromFirstCall.sessionId();

		ExtractableResponse<Response> responseFromSecondCall = given().get("/").then().extract();
		String sessionFromSecondCall = responseFromSecondCall.sessionId();

		assertEquals(sessionFromFirstCall, sessionFromSecondCall);

		assertEquals(responseFromFirstCall.body().jsonPath().getString("foo"), "1");
		assertEquals(responseFromSecondCall.body().jsonPath().getString("foo"), "2");
	}

	@Test
	public void testWithSDGSession() {
		ExtractableResponse<Response> responseFromFirstCall = given().get("/sdg").then().extract();
		String sessionFromFirstCall = responseFromFirstCall.sessionId();

		ExtractableResponse<Response> responseFromSecondCall = given().get("/sdg").then().extract();
		String sessionFromSecondCall = responseFromSecondCall.sessionId();

		assertEquals(sessionFromFirstCall, sessionFromSecondCall);

		assertEquals(responseFromFirstCall.body().jsonPath().getString("foo"), "1");
		assertEquals(responseFromSecondCall.body().jsonPath().getString("foo"), "2");
	}

}
