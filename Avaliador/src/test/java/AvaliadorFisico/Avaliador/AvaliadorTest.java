package AvaliadorFisico.Avaliador;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;

public class AvaliadorTest {

    private Scanner scanner;

    private final InputStream inputStreamOriginal = System.in;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private Scanner criarScannerComInput(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes()));
    }

    @Before
    public void configurarEntrada() {
        System.setIn(inputStreamOriginal);

        String input = "M\n3\n";
        String entrada1 = "M\n25\n";
        scanner = criarScannerComInput(input);
        scanner = criarScannerComInput(entrada1);

        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restaurarEntradaOriginal() {
        System.setIn(inputStreamOriginal);

        System.setOut(originalOut);
    }

    @Test
    public void testValidacaoDadosMain() {

        String entradaInvalida = "X\n" + // Sexo inválido
                "3\n" + // Taxa de atividade válida
                "-5\n" + // Idade inválida
                "170\n" + // Altura válida
                "80\n" + // Peso válido
                "90\n" + // Cintura válida
                "100\n" + // Quadril válido
                "45\n"; // Pescoço válido

        InputStream inputStreamSimulado = new ByteArrayInputStream(entradaInvalida.getBytes());

        System.setIn(inputStreamSimulado);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Chama o método main do seu programa
        Avaliador.main(null);

        // Captura a saída do sistema
        String output = outContent.toString();

        assertTrue(output.contains("Dados inválidos. Encerrando o programa."));
    }

    @Test
    public void testObterSexo() {
        assertEquals("M", Avaliador.obterSexo(scanner));
    }

    @Test
    public void testEntradaFatorAtividade() {
        assertEquals(1.55, Avaliador.entradaFatorAtividade(scanner), 0.01);
    }

    @Test
    public void testObterInteiroInputIdade() {
        String prompt = "Informe sua idade: ";
        assertEquals(25, Avaliador.obterInteiroInput(scanner, prompt));
    }

    @Test
    public void testExibirResultados() {
        // Redirecionar a saída padrão para verificar a saída impressa
        String expectedOutput = "Taxa de Metabolismo Basal (TMB):";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        Avaliador.exibirResultados(2000.0, "Peso normal", 20.0);

        System.setOut(System.out); // Restaurar a saída padrão

        String output = outputStream.toString();
        assertTrue(output.contains(expectedOutput));
    }

    @Test
    public void testValidarDadosFuncao() {
        // Casos válidos
        assertTrue(Avaliador.validarDados(180, "M", 70, 25, 80, 90, 35));

        // Casos inválidos
        assertFalse(Avaliador.validarDados(300, "F", 70, 25, 30, 90, 35)); // Altura inválida
        assertFalse(Avaliador.validarDados(180, "X", 70, 25, 30, 90, 35)); // Sexo inválido
        assertFalse(Avaliador.validarDados(180, "M", -5, 25, 30, 90, 35)); // Peso negativo
        assertFalse(Avaliador.validarDados(180, "F", 70, 160, 30, 90, 35)); // Idade fora do intervalo
        assertFalse(Avaliador.validarDados(180, "F", 70, 30, -30, 90, 35)); // Cintura invalida
        assertFalse(Avaliador.validarDados(180, "F", 70, 30, 30, 301, 35)); // quadril invalido
        assertFalse(Avaliador.validarDados(180, "F", 70, 30, 30, 90, 100)); // pescoço com valor invalido
    }

    @Test
    public void testCalcularTMB() {
        assertEquals(2628.8, Avaliador.calcularTMB("M", 70, 175, 30, 1.55), 0.01); // Exemplo para homem
        assertEquals(1927.06, Avaliador.calcularTMB("F", 60, 160, 25, 1.375), 0.01); // Exemplo para mulher
    }

    @Test
    public void testCalcularIMC() {
        assertEquals("Peso normal", Avaliador.calcularIMC(70, 175)); // Exemplo de IMC normal
        assertEquals("Baixo peso", Avaliador.calcularIMC(45, 160)); // Exemplo de baixo peso
        assertEquals("Sobrepeso", Avaliador.calcularIMC(83, 175)); // Exemplo de Sobrepeso
        assertEquals("Obesidade grau I", Avaliador.calcularIMC(100, 170)); // Exemplo de obesidade grau I
        assertEquals("Obesidade grau II", Avaliador.calcularIMC(88, 154)); // Exemplo de Obesidade grau II
        assertEquals("Obesidade grau III", Avaliador.calcularIMC(156, 187)); // Exemplo de Obesidade grau III
    }

    @Test
    public void testCalcularPercentualGordura() {
        assertEquals(15.35, Avaliador.calcularPercentualGordura("M", 80, 100, 35, 175), 0.01); // Exemplo para homem
        assertEquals(29.8, Avaliador.calcularPercentualGordura("F", 75, 95, 30, 160), 0.01); // Exemplo para mulher
    }

    @Test
    public void testObterFatorAtividade() {
        assertEquals(1.2, Avaliador.obterFatorAtividade(1), 0.01); // Exemplo para escolha 1
        assertEquals(1.375, Avaliador.obterFatorAtividade(2), 0.01); // Exemplo para escolha 1
        assertEquals(1.55, Avaliador.obterFatorAtividade(3), 0.01); // Exemplo para escolha 1
        assertEquals(1.725, Avaliador.obterFatorAtividade(4), 0.01); // Exemplo para escolha 1
        assertEquals(1.9, Avaliador.obterFatorAtividade(5), 0.01); // Exemplo para escolha 1
        assertEquals(1.55, Avaliador.obterFatorAtividade(6), 0.01); // ou qualquer outro valor padrão

        String mensagem = "Escolha de taxa de atividade inválida. Usando o fator de atividade padrão (1.55).";
        String output = outContent.toString();
        assertTrue(output.contains(mensagem));

    }
}
