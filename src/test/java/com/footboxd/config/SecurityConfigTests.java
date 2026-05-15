package com.footboxd.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityConfigTests {

    private static final String PRIVATE_NETWORK_HEADER = "Access-Control-Request-Private-Network";
    private static final String ALLOW_PRIVATE_NETWORK_HEADER = "Access-Control-Allow-Private-Network";

    @Autowired
    private MockMvc mockMvc;

    @LocalServerPort
    private int port;

    @Test
    void authLoginPreflightFromLocalFileIsAllowed() throws Exception {
        mockMvc.perform(options("/auth/login")
                .header(HttpHeaders.ORIGIN, "null")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type")
                .header(PRIVATE_NETWORK_HEADER, "true"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "null"))
                .andExpect(header().string(ALLOW_PRIVATE_NETWORK_HEADER, "true"));
    }

    @Test
    void authLoginPostIsPublicAndReachesController() throws Exception {
        mockMvc.perform(post("/auth/login")
                .header(HttpHeaders.ORIGIN, "http://localhost:5500")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "login": "usuario-inexistente@footboxd.test",
                          "senha": "senha-incorreta"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authLoginHttpErrorResponseIsNotConvertedToForbidden() throws Exception {
        String body = """
                {
                  "login": "usuario-inexistente@footboxd.test",
                  "senha": "senha-incorreta"
                }
                """;

        HttpResponse<String> response = postJson("/auth/login", body);

        org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    void authRegisterValidationErrorResponseIsNotConvertedToForbidden() throws Exception {
        String body = """
                {
                  "login": "novo@footboxd.test",
                  "senha": "1234",
                  "role": "USER"
                }
                """;

        HttpResponse<String> response = postJson("/auth/register", body);

        org.assertj.core.api.Assertions.assertThat(response.statusCode()).isEqualTo(400);
    }

    private HttpResponse<String> postJson(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
