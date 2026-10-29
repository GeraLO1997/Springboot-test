package org.gerardo.test.springboot.app.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Banco {
    private Long id;
    private String nombre;
    private int totalTransferencias;
}
