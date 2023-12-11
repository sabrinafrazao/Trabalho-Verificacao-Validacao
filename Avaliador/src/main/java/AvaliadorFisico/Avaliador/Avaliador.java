package AvaliadorFisico.Avaliador;

import java.util.Scanner;

public class Avaliador {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// Entrada de dados
		String sexo = obterSexo(scanner);
		double fatorAtividade = entradaFatorAtividade(scanner);

		int idade = obterInteiroInput(scanner, "Informe sua idade: "); // Idade
		int altura = obterInteiroInput(scanner, "Informe sua altura em cm: ");// Altura
		int peso = obterInteiroInput(scanner, "Informe seu peso em quilogramas: ");
		int cintura = obterInteiroInput(scanner, "Informe a circunferência da sua cintura em cm: "); // Cintura
		int quadril = obterInteiroInput(scanner, "Informe a circunferência do seu quadril em cm: "); // quadril
		int pescoco = obterInteiroInput(scanner, "Informe a circunferência do seu pescoço em cm: "); // pescoço

		if (!validarDados(altura, sexo, peso, idade, cintura, quadril, pescoco)) {
			System.out.println("\nDados inválidos. Encerrando o programa.");
			scanner.close();
			return;
		}

		// Cálculos
		double tmb = calcularTMB(sexo, peso, altura, idade, fatorAtividade);
		String imc = calcularIMC(peso, altura);
		double percentualGordura = calcularPercentualGordura(sexo, cintura, quadril, pescoco, altura);

		// Exibição dos resultados
		exibirResultados(tmb, imc, percentualGordura);

		scanner.close();
	}

	public static String obterSexo(Scanner scanner) {
		System.out.println("Opções de Sexo:");
		System.out.println("M - Masculino");
		System.out.println("F - Feminino");

		System.out.print("Escolha seu sexo (M ou F): ");
		return scanner.nextLine().toUpperCase();
	}

	public static double entradaFatorAtividade(Scanner scanner) {
		System.out.println("Taxa de Atividade:");
		System.out.println("1 - Sedentários (pouco ou nenhum exercício)");
		System.out.println("2 - Levemente ativo (exercício leve 1 a 3 dias por semana)");
		System.out.println("3 - Moderadamente ativo (exercício moderado, faz esportes 3 a 5 dias por semana)");
		System.out.println("4 - Altamente ativo (exercício pesado de 5 a 6 dias por semana)");
		System.out.println("5 - Extremamente ativo (exercício pesado diariamente e até 2 vezes por dia)");

		double fatorAtividade = 0;

		// Loop para lidar com entrada inválida
		while (true) {
			System.out.print("Escolha sua taxa de atividade (1 a 5): ");
			try {
				fatorAtividade = obterFatorAtividade(scanner.nextInt());
				break; // Saia do loop se a leitura for bem-sucedida
			} catch (java.util.InputMismatchException e) {
				System.out.println("Escolha de taxa de atividade inválida. Usando o fator de atividade padrão (1.55).");
				scanner.nextLine(); // Limpe o buffer de entrada
			}
		}

		return fatorAtividade;
	}

	public static int obterInteiroInput(Scanner scanner, String prompt) {
		System.out.print(prompt);

		while (!scanner.hasNextInt()) {
			System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
			System.out.print(prompt);
			scanner.next(); // Limpar o token inválido
		}

		return scanner.nextInt();
	}

	// Funções auxiliares para exibição de resultados
	public static void exibirResultados(double tmb, String imc, double percentualGordura) {
		System.out.println("\nResultados:");
		System.out.println("Taxa de Metabolismo Basal (TMB): " + tmb + " calorias/dia");
		System.out.println("Índice de Massa Corporal (IMC): " + imc);
		System.out.println("Percentual de Gordura: " + percentualGordura + "%");
	}

	// Validação de dados
	public static boolean validarDados(double altura, String sexo, double peso, int idade, int cintura, int quadril,
			int pescoco) {
		if (!sexo.equals("M") && !sexo.equals("F")) {
			return false;
		} else if (idade < 0 || idade > 151) {
			return false;
		} else if (altura < 0 || altura >= 300) {
			return false;
		} else if (peso < 0 || peso > 700) {
			return false;
		} else if (cintura < 0 || cintura >= 300) {
			return false;
		} else if (quadril < 0 || quadril >= 300) {
			return false;
		} else if (pescoco < 0 || pescoco > 99) {
			return false;
		}
		return true;
	}

	// Fórmula de TMB para homens e mulheres com o fator de atividade
	public static double calcularTMB(String sexo, double peso, double altura, int idade, double fatorAtividade) {
		if (sexo.equals("M")) {
			// Fórmula para homens
			return fatorAtividade * (66 + (13.7 * peso) + (5 * altura) - (6.8 * idade));
		} else {
			// Fórmula para mulheres (não é necessário verificar novamente, pois a validação
			// já foi feita)
			return fatorAtividade * (655 + (9.6 * peso) + (1.8 * altura) - (4.7 * idade));
		}
	}

	// Fórmula do Índice de Massa Corporal (IMC)
	public static String calcularIMC(double peso, double altura) {
		double imc = peso / ((altura / 100) * (altura / 100));

		if (imc < 18.5) {
			return "Baixo peso";
		} else if (imc >= 18.5 && imc <= 24.9) {
			return "Peso normal";
		} else if (imc >= 25 && imc <= 29.9) {
			return "Sobrepeso";
		} else if (imc >= 30 && imc <= 34.9) {
			return "Obesidade grau I";
		} else if (imc >= 35 && imc <= 39.9) {
			return "Obesidade grau II";
		} else {
			return "Obesidade grau III";
		}
	}

	// Fórmula para estimar o Percentual de Gordura com base no IMC e na idade
	public static double calcularPercentualGordura(String sexo, int cintura, int quadril, int pescoco, double altura) {
		if (sexo.equals("M")) {
			return 495 / (1.0324 - 0.19077 * (Math.log10(cintura - pescoco)) + 0.15456 * (Math.log10(altura))) - 450;
		} else {
			return 495
					/ (1.29579 - 0.35004 * (Math.log10(cintura + quadril - pescoco)) + 0.22100 * (Math.log10(altura)))
					- 450;
		}
	}

	// Obtém o fator de atividade com base na escolha do usuário
	public static double obterFatorAtividade(int escolhaAtividade) {
		switch (escolhaAtividade) {
			case 1:
				return 1.2;
			case 2:
				return 1.375;
			case 3:
				return 1.55;
			case 4:
				return 1.725;
			case 5:
				return 1.9;
			default:
				System.out.println("Escolha de taxa de atividade inválida. Usando o fator de atividade padrão (1.55).");
				return 1.55;
		}
	}
}
