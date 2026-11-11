package org.gerardo.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gerardo.test.springboot.app.models.Cuenta;
import org.gerardo.test.springboot.app.models.TransaccionDto;
import org.junit.jupiter.api.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerWebTestClientTest {

    private WebTestClient client;

    @LocalServerPort
    private int port;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransferir() throws JsonProcessingException {

        // given
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);
        dto.setMonto(new BigDecimal("100"));


        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status","OK");
        response.put("mensaje", "Transferencia realizada con exito");
        response.put("transaccion", dto);


        // when
        client.post()
                .uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                //then
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.mensaje")
                .isEqualTo("Transferencia realizada con exito")
                .jsonPath("$.transaccion.cuentaOrigenId")
                .isEqualTo(dto.getCuentaOrigenId())
                .jsonPath("$.date")
                .isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));
    }

    @Test
    @Order(4)
    void testTransferirConConsume() throws JsonProcessingException {

        // given
        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setBancoId(1L);
        dto.setMonto(new BigDecimal("100"));


        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status","OK");
        response.put("mensaje", "Transferencia realizada con exito");
        response.put("transaccion", dto);


        // when
        client.post()
                .uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                //then
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(respuesta -> {
                    try {
                        JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
                        Assertions.assertEquals("Transferencia realizada con exito", json.path("mensaje").asText());
                        Assertions.assertEquals(1L, json.path("transaccion").path("cuentaOrigenId").asLong());
                        Assertions.assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        Assertions.assertEquals("100", json.path("transaccion").path("monto").asText());


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                });

    }

    @Test
    @Order(2)
    void testDetalle() throws JsonProcessingException { // este es probable que falle por que depende del orden que se ejecute

        Cuenta cuenta = new Cuenta(1L, "Andres", new BigDecimal("900")); // esperamos recibir exactamente este objeto

        client.get().uri("/api/cuentas/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Andres")
                .jsonPath("$.saldo").isEqualTo(900)
                .json(objectMapper.writeValueAsString(cuenta));
    }

    @Test
    @Order(3)
    void testDetalle2(){
        client.get().uri("/api/cuentas/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response -> {
                    Cuenta cuenta = response.getResponseBody();
                    Assertions.assertEquals("Jhon",cuenta.getPersona());
                    Assertions.assertEquals("2100.00",cuenta.getSaldo().toPlainString());
                });
    }
}