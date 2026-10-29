package org.gerardo.test.springboot.app.services;

import org.gerardo.test.springboot.app.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {
    Cuenta findById(Long id);

    int revisarTotalTransferencias(Long bancoId);  // cuantas transferencias se han realizado en el banco

    BigDecimal revisarSaldo(Long cuentaId);

    void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long bancoId);
}
