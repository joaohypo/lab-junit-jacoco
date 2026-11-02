package br.ufrgs.inf.lab.nivel2;

import static org.junit.jupiter.api.Assertions.*;

import br.ufrgs.inf.lab.nivel1.Conta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * NÍVEL 2 (semi-guiado): ServicoTransferencia.
 *
 * Aqui NÃO há os asserts: cada teste abaixo diz o CENÁRIO e o que deve
 * ser verificado. Você decide como escrever (assertEquals, assertTrue...).
 * Leia as regras R1..R5 em ServicoTransferencia.java.
 *
 * Sinta-se livre para ACRESCENTAR testes (e usar @ParameterizedTest).
 */
class ServicoTransferenciaTest {

    private static final double DELTA = 0.001;

    private ServicoTransferencia servico;
    private Conta origem;
    private Conta destino;

    // Roda ANTES de cada teste: garante contas "limpas" (testes independentes)
    @BeforeEach
    void preparar() {
        servico = new ServicoTransferencia();
        origem = new Conta("Origem", 10_000.0);
        destino = new Conta("Destino", 0.0);
    }

    // ---------------------------------------------------------------
    // PRONTO (exemplo)
    // ---------------------------------------------------------------

    @Test
    void transferenciaPequenaDeveSerAceitaSemTarifa() {
        StatusTransferencia status = servico.transferir(origem, destino, 200.0);

        assertEquals(StatusTransferencia.OK, status);
        assertEquals(9_800.0, origem.getSaldo(), DELTA);
        assertEquals(200.0, destino.getSaldo(), DELTA);
    }

    // ---------------------------------------------------------------
    // COMPLETADOS (N2-A a N2-E)
    // ---------------------------------------------------------------

    @Test
    void transferenciaAcimaDe1000PagaTarifaDe1Porcento() {
        // tarifa = 1% de 2.000 = 20 -> origem paga 2.020
        StatusTransferencia status = servico.transferir(origem, destino, 2_000.0);

        assertEquals(StatusTransferencia.OK, status);
        assertEquals(20.0, servico.calcularTarifa(2_000.0), DELTA);
        assertEquals(7_980.0, origem.getSaldo(), DELTA);
        assertEquals(2_000.0, destino.getSaldo(), DELTA);
    }

    @Test
    void transferenciaDeExatamente1000NaoPagaTarifa() {
        StatusTransferencia status = servico.transferir(origem, destino, 1_000.0);

        assertEquals(StatusTransferencia.OK, status);
        assertEquals(0.0, servico.calcularTarifa(1_000.0), DELTA);
        assertEquals(10.0001, servico.calcularTarifa(1_000.01), DELTA); // 1 centavo acima já paga
        assertEquals(9_000.0, origem.getSaldo(), DELTA);
        assertEquals(1_000.0, destino.getSaldo(), DELTA);
    }

    @Test
    void transferenciaAcimaDoLimiteDeveSerRecusada() {
        StatusTransferencia status = servico.transferir(origem, destino, 5_000.01);

        assertEquals(StatusTransferencia.ACIMA_DO_LIMITE, status);
        assertEquals(10_000.0, origem.getSaldo(), DELTA);
        assertEquals(0.0, destino.getSaldo(), DELTA);
    }

    @Test
    void transferenciaDeExatamente5000DeveSerAceita() {
        // 5.000 é permitido e paga tarifa de 50
        StatusTransferencia status = servico.transferir(origem, destino, 5_000.0);

        assertEquals(StatusTransferencia.OK, status);
        assertEquals(4_950.0, origem.getSaldo(), DELTA);
        assertEquals(5_000.0, destino.getSaldo(), DELTA);
    }

    @Test
    void saldoInsuficienteParaValorMaisTarifaNaoAlteraNenhumaConta() {
        // tem 2.000 para o valor, mas precisaria de 2.020 com a tarifa
        origem = new Conta("Origem", 2_000.0);

        StatusTransferencia status = servico.transferir(origem, destino, 2_000.0);

        assertEquals(StatusTransferencia.SALDO_INSUFICIENTE, status);
        assertEquals(2_000.0, origem.getSaldo(), DELTA);
        assertEquals(0.0, destino.getSaldo(), DELTA);
    }

    @Test
    void mesmaContaEValorInvalidoDevemSerRecusados() {
        // o lado falso de R1 e R2 já é coberto pelas transferências OK dos outros testes
        assertEquals(StatusTransferencia.MESMA_CONTA, servico.transferir(origem, origem, 100.0));
        assertEquals(StatusTransferencia.VALOR_INVALIDO, servico.transferir(origem, destino, 0.0));
        assertEquals(StatusTransferencia.VALOR_INVALIDO, servico.transferir(origem, destino, -50.0));

        assertEquals(10_000.0, origem.getSaldo(), DELTA);
        assertEquals(0.0, destino.getSaldo(), DELTA);
    }
}
