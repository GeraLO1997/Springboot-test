package org.gerardo.test.springboot.app;

import org.gerardo.test.springboot.app.models.Cuenta;
import org.gerardo.test.springboot.app.repositories.CuentaRepository;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@DataJpaTest
public class IntegracionJpaTest {

    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById(){
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);

        assertTrue(cuenta.isPresent());
        assertEquals("Andres",cuenta.orElseThrow().getPersona());
    }

    @Test
    void testFindByPersona(){
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andres");

        assertTrue(cuenta.isPresent());
        assertEquals("Andres",cuenta.orElseThrow().getPersona());
        assertEquals("1000.00",cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByPersonaThrowException(){
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Rod");

        assertThrows(NoSuchElementException.class, () -> {
            cuenta.orElseThrow();
        });

        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll(){
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());

        assertEquals(2,cuentas.size());
    }

    @Test
    void testSave(){
        // Given
        Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));

        // When
//        Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
//        Cuenta cuenta = cuentaRepository.findById(3L).orElseThrow();
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);


        // Then
        assertEquals("Pepe",cuenta.getPersona());
        assertEquals("3000",cuenta.getSaldo().toPlainString());
//        assertEquals(3L,cuenta.getId());
    }
}
