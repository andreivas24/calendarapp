package app.calendar;

import app.calendar.user.presentation.request.AuthenticationRequest;
import app.calendar.user.presentation.request.RegisterRequest;
import app.calendar.user.presentation.response.AuthenticationResponse;
import app.calendar.user.presentation.response.RegisterResponse;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserApiTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    TestRestTemplate restTemplate;


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:" + port));
    }

    @AfterEach
    void clear() {
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop"); // Ensures schema is created for tests
    }


    @Test
    void allwaysGood() {
        Assertions.assertTrue(true);
    }

    @Test
    void checkDefault() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/user/default", String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertFalse(response.getBody().isEmpty(), "Response body should not be empty");
        System.out.println("Response from /api/v1/user/default: " + response.getBody());
    }

    @Test
    void testRegisterAndAuth() {
        // Create a RegisterRequest object
        RegisterRequest registerRequest = new RegisterRequest("newuser@example.com", "password123");

        // Set headers (optional, as TestRestTemplate can handle JSON automatically)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest, headers);

        // Perform the POST request to /api/v1/user/register
        ResponseEntity<RegisterResponse> response = restTemplate.postForEntity("/api/v1/user/register", request, RegisterResponse.class);

        // Assert that the status is OK (200)
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        // Check that the response contains a non-null token
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getToken(), "Response token should not be null");
        // Create a RegisterRequest object for authentication

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        AuthenticationRequest authRequest = new AuthenticationRequest("newuser@example.com", "password123");

        // Set headers (optional)
        HttpHeaders headers_authenticate = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthenticationRequest> request_authenticate = new HttpEntity<>(authRequest, headers);

        // Perform the POST request to /api/v1/user/authenticate
        ResponseEntity<AuthenticationResponse> response_authenticate = restTemplate.postForEntity("/api/v1/user/authenticate", request, AuthenticationResponse.class);

        // Assert that the status is OK (200)
        Assertions.assertEquals(HttpStatus.OK, response_authenticate.getStatusCode());

        // Check that the response contains a non-null token
        Assertions.assertNotNull(response_authenticate.getBody());
        Assertions.assertNotNull(response_authenticate.getBody().getToken(), "Response token should not be null");

        Assertions.assertEquals(response_authenticate.getBody().getToken(), response.getBody().getToken());
    }
}