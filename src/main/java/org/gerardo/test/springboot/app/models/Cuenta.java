package org.gerardo.test.springboot.app.models;

import lombok.*;
import org.gerardo.test.springboot.app.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Cuenta {

    private Long id;
    private String persona;
    private BigDecimal saldo;

    public void debito(BigDecimal monto){
        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0){
            throw new DineroInsuficienteException("Dinero insuficiente en la cuenta.");
        }
        this.saldo = nuevoSaldo;
    }

    public void credito(BigDecimal monto){
        this.saldo = saldo.add(monto);
    }
}
