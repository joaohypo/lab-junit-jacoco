package br.ufrgs.inf.lab.nivel1;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * NÍVEL 1 (guiado): Conta.
 *
 * Padrão usado em todos os testes (AAA):
 *   Arrange (preparar)  ->  Act (agir)  ->  Assert (verificar)
 */
class ContaTest {

    // Tolerância para comparar números decimais (double) com assertEquals
    private static final double DELTA = 0.001;

    // ---------------------------------------------------------------
    // PRONTOS (exemplos para você imitar)
    // ---------------------------------------------------------------

    @Test
    void contaNovaDeveGuardarTitularESaldoInicial() {
        // Arrange + Act
        Conta conta = new Conta("Ana", 100.0);

        // Assert: assertEquals(esperado, obtido)
        assertEquals("Ana", conta.getTitular());
        assertEquals(100.0, conta.getSaldo(), DELTA);
    }

    @Test
    void depositoValidoDeveAumentarOSaldo() {
        Conta conta = new Conta("Ana", 100.0);   // Arrange

        boolean depositou = conta.depositar(50.0); // Act

        assertTrue(depositou);                          // Assert: o método devolveu true
        assertEquals(150.0, conta.getSaldo(), DELTA);   // Assert: o saldo mudou
    }

    // ---------------------------------------------------------------
    // COMPLETADOS (N1-A a N1-D)
    // ---------------------------------------------------------------

    @Test
    void depositoComValorNegativoDeveSerRecusado() {
        Conta conta = new Conta("Ana", 100.0);

        boolean depositou = conta.depositar(-10.0);

        assertFalse(depositou);
        assertEquals(100.0, conta.getSaldo(), DELTA);
    }

    @Test
    void saqueValidoDeveDiminuirOSaldo() {
        Conta conta = new Conta("Ana", 100.0);

        boolean sacou = conta.sacar(30.0);

        assertTrue(sacou);
        assertEquals(70.0, conta.getSaldo(), DELTA);
    }

    @Test
    void saqueMaiorQueOSaldoDeveSerRecusado() {
        Conta conta = new Conta("Ana", 100.0);

        boolean sacou = conta.sacar(100.01);

        assertFalse(sacou);
        assertEquals(100.0, conta.getSaldo(), DELTA);
    }

    @Test
    void saqueDoSaldoExatoDeveSerPermitidoEZerarAConta() {
        Conta conta = new Conta("Ana", 100.0);

        // valor-limite: com "valor >= saldo" este saque seria recusado, mas a regra permite
        boolean sacou = conta.sacar(100.0);

        assertTrue(sacou);
        assertEquals(0.0, conta.getSaldo(), DELTA);
    }

    // ---------------------------------------------------------------
    // EXTRAS (caça aos ifs: lados que nenhum teste cobria)
    // ---------------------------------------------------------------

    @Test
    void saldoInicialNegativoDeveVirarZero() {
        Conta conta = new Conta("Ana", -50.0);

        assertEquals(0.0, conta.getSaldo(), DELTA);
    }

    @Test
    void saqueComValorZeroOuNegativoDeveSerRecusado() {
        Conta conta = new Conta("Ana", 100.0);

        assertFalse(conta.sacar(0.0));
        assertFalse(conta.sacar(-10.0));
        assertEquals(100.0, conta.getSaldo(), DELTA);
    }
}
