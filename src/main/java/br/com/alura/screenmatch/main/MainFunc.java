package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.service.AnaliseSerieResponse;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MainFunc {

    private final Scanner leitura = new Scanner(System.in);
    private final SerieService serieService;
    private final List<DadosSerie> dadosSeries = new ArrayList<>();

    public MainFunc(SerieService serieService) {
        this.serieService = serieService;
    }

    public void menu() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("""
                    
                    1 - Buscar série
                    2 - Buscar episódios por série
                    3 - Exibir análises completas da série
                    4 - Listar Series Buscadas
                    
                    0 - Sair
                    """);

            try {
                opcao = Integer.parseInt(leitura.nextLine());

                switch (opcao) {
                    case 1 -> buscarSerie();
                    case 2 -> buscarEpisodiosPorSerie();
                    case 3 -> exibirAnalisesCompletas();
                    case 4 -> listarSeriesBuscadas();
                    case 0 -> System.out.println("Saindo...");
                    default -> System.out.println("Opção inválida.");
                }

            } catch (Exception e) {
                System.out.println("Erro ao processar opção: " + e.getMessage());
            }
        }
    }

    private void buscarSerie() throws Exception {
        DadosSerie dados = getDadosSerie();
        dadosSeries.add(dados);
        System.out.println(dados);
    }

    private void buscarEpisodiosPorSerie() throws Exception {
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = serieService.buscarEpisodiosPorSerie(dadosSerie.titulo());
        temporadas.forEach(System.out::println);
    }

    private void exibirAnalisesCompletas() throws Exception {
        DadosSerie dadosSerie = getDadosSerie();
        AnaliseSerieResponse analise = serieService.exibirAnalisesCompletas(dadosSerie.titulo());
        System.out.println(analise);
    }

    private void listarSeriesBuscadas() {
        List<Serie> series = dadosSeries.stream()
                .map(Serie::new)
                .sorted(Comparator.comparing(Serie::getGenero))
                .collect(Collectors.toList());
        series.forEach(System.out::println);
    }

    private DadosSerie getDadosSerie() throws Exception {
        System.out.println("Digite o nome da série para buscar:");
        String nomeSerie = leitura.nextLine().trim();
        return serieService.buscarSerie(nomeSerie);
    }
}