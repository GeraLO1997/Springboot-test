package org.gerardo.test.springboot.app.services;

import lombok.AllArgsConstructor;
import org.gerardo.test.springboot.app.models.Banco;
import org.gerardo.test.springboot.app.models.Cuenta;
import org.gerardo.test.springboot.app.repositories.BancoRepository;
import org.gerardo.test.springboot.app.repositories.CuentaRepository;

import java.math.BigDecimal;

@AllArgsConstructor
public class CuentaServiceImpl implements CuentaService{

    private CuentaRepository cuentaRepository;
    private BancoRepository bancoRepository;

    @Override
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id);
    }

    @Override
    public int revisarTotalTransferencias(Long bancoId) {
        Banco banco = bancoRepository.findById(bancoId);
        return banco.getTotalTransferencias();
    }

    @Override
    public BigDecimal revisarSaldo(Long cuentaId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId);
        return cuenta.getSaldo();
    }

    @Override
    public void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long bancoId) {

        Banco banco = bancoRepository.findById(bancoId);
        int totalTransferencia = banco.getTotalTransferencias();

        banco.setTotalTransferencias(++totalTransferencia);
        bancoRepository.update(banco);

        Cuenta cuentaOrigen = cuentaRepository.findById(numCuentaOrigen);
        cuentaOrigen.debito(monto);
        cuentaRepository.update(cuentaOrigen);

        Cuenta cuentaDestino = cuentaRepository.findById(numCuentaDestino);
        cuentaDestino.credito(monto);
        cuentaRepository.update(cuentaDestino);
    }
}
