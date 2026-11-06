package org.gerardo.test.springboot.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gerardo.test.springboot.app.Datos;
import org.gerardo.test.springboot.app.models.Cuenta;
import org.gerardo.test.springboot.app.models.TransaccionDto;
import org.gerardo.test.springboot.app.services.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType; // es
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; // agregamos todos, status, content


@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CuentaService cuentaService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void detalle() throws Exception {
        // given
        when(cuentaService.findById(1L)).thenReturn(Datos.crearCuenta001().orElseThrow());

        // when
        mvc.perform(get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
        // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persona").value("Andres"))
                .andExpect(jsonPath("$.saldo").value("1000"));

        verify(cuentaService).findById(1L);
    }

    @Test
    void testTransferir() throws Exception {

        TransaccionDto dto = new TransaccionDto();
        dto.setCuentaOrigenId(1L);
        dto.setCuentaDestinoId(2L);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1L);

        System.out.println(objectMapper.writeValueAsString(dto));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status","OK");
        response.put("mensaje", "Transferencia realizada con exito");
        response.put("transaccion", dto);

        System.out.println(objectMapper.writeValueAsString(response));

        // when
        mvc.perform(post("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con exito"))
                .andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(dto.getCuentaOrigenId()))

                .andExpect(content().json(objectMapper.writeValueAsString(response)));


    }

    @Test
    void testListar() throws Exception {
        //given
        List<Cuenta> cuentas = Arrays.asList(Datos.crearCuenta001().orElseThrow(),
                Datos.crearCuenta002().orElseThrow());
        when(cuentaService.findAll()).thenReturn(cuentas);

        //when
        mvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // probamos los datos
                .andExpect(jsonPath("$[0].persona").value("Andres"))
                .andExpect(jsonPath("$[1].persona").value("Jhon"))
                .andExpect(jsonPath("$[0].saldo").value("1000"))
                .andExpect(jsonPath("$[1].saldo").value("2000"))
                .andExpect(jsonPath("$",hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(cuentas)));
    }
}