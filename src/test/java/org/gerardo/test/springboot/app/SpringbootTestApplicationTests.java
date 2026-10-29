package org.gerardo.test.springboot.app;

import org.gerardo.test.springboot.app.exceptions.DineroInsuficienteException;
import org.gerardo.test.springboot.app.models.Banco;
import org.gerardo.test.springboot.app.models.Cuenta;
import org.gerardo.test.springboot.app.repositories.BancoRepository;
import org.gerardo.test.springboot.app.repositories.CuentaRepository;
import org.gerardo.test.springboot.app.services.CuentaService;

import org.gerardo.test.springboot.app.services.CuentaServiceImpl;
import static  org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class SpringbootTestApplicationTests {

	CuentaRepository cuentaRepository;
	BancoRepository bancoRepository;

	CuentaService service;

	@BeforeEach
	void setUp() {

		cuentaRepository = mock(CuentaRepository.class);
		bancoRepository = mock(BancoRepository.class);

		service = new CuentaServiceImpl(cuentaRepository, bancoRepository);

		// Reiniciar Datos - o creamos los metodos
//		Datos.CUENTA_001.setSaldo(new BigDecimal("1000"));
//		Datos.CUENTA_002.setSaldo(new BigDecimal("2000"));
//		Datos.BANCO.setTotalTransferencias(0);
	}

	@Test
	@DisplayName("Test atributos de la cuenta, y transferencia")
	void contextLoads() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000",saldoDestino.toPlainString());

		service.transferir(1L,2L,new BigDecimal("100"),1L);
		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("900",saldoOrigen.toPlainString());
		assertEquals("2100",saldoDestino.toPlainString());

		int total = service.revisarTotalTransferencias(1L);

		assertEquals(1, total);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository,times(3)).findById(2L);
		verify(cuentaRepository,times(2)).update(any(Cuenta.class));

		verify(bancoRepository, times(2)).findById(1L);
		verify(bancoRepository).update(any(Banco.class));
	}

	@Test
	@DisplayName("Test afirmar excepcion que sea correcta")
	void contextLoads2() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000",saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteException.class, ()-> {
			service.transferir(1L,2L,new BigDecimal("1200"),1L);
		});


		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000",saldoOrigen.toPlainString());
		assertEquals("2000",saldoDestino.toPlainString());

		int total = service.revisarTotalTransferencias(1L);

		assertEquals(0, total);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository,times(2)).findById(2L);
		verify(cuentaRepository,never()).update(any(Cuenta.class));

		verify(bancoRepository, times(1)).findById(1L);
		verify(bancoRepository, never()).update(any(Banco.class));
	}

}
