package org.gerardo.test.springboot.app.services;

import lombok.AllArgsConstructor;
import org.gerardo.test.springboot.app.models.Banco;
import org.gerardo.test.springboot.app.models.Cuenta;
import org.gerardo.test.springboot.app.repositories.BancoRepository;
import org.gerardo.test.springboot.app.repositories.CuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
public class CuentaServiceImpl implements CuentaService{

    private CuentaRepository cuentaRepository;
    private BancoRepository bancoRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id).orElseThrow();
    }

    @Transactional
    @Override
    public Cuenta save(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    @Transactional(readOnly = true)
    @Override
    public int revisarTotalTransferencias(Long bancoId) {
        Banco banco = bancoRepository.findById(bancoId).orElseThrow();
        return banco.getTotalTransferencias();
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal revisarSaldo(Long cuentaId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId).orElseThrow();
        return cuenta.getSaldo();
    }

    @Transactional
    @Override
    public void transferir(Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto, Long bancoId) {

        Cuenta cuentaOrigen = cuentaRepository.findById(numCuentaOrigen).orElseThrow();
        cuentaOrigen.debito(monto);
        cuentaRepository.save(cuentaOrigen);

        Cuenta cuentaDestino = cuentaRepository.findById(numCuentaDestino).orElseThrow();
        cuentaDestino.credito(monto);
        cuentaRepository.save(cuentaDestino);

        Banco banco = bancoRepository.findById(bancoId).orElseThrow();
        int totalTransferencia = banco.getTotalTransferencias();

        banco.setTotalTransferencias(++totalTransferencia);
        bancoRepository.save(banco);
    }
}
