package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ApiCallOmdb;
import br.com.alura.screenmatch.service.ConverterDados;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MainFunc {
    private Scanner leitura = new Scanner(System.in);
    private Map<String,String> paramsSerie = new HashMap();
    ApiCallOmdb api = new ApiCallOmdb();

    public void menu() throws IOException, Exception {
        try {
            System.out.println("Digite o nome da série para buscar: ");
            String resposta = leitura.nextLine();
            paramsSerie.put("t", resposta);
            var responseSerie = api.get(paramsSerie);

            ConverterDados conversor = new ConverterDados();
            DadosSerie dadosSerie = conversor.obterDados(responseSerie, DadosSerie.class);

            List<DadosTemporada> temporadas = new ArrayList<>();
            for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
                Map<String,String> paramsTemporadas = new HashMap();
                paramsTemporadas.put("t", resposta);
                paramsTemporadas.put("Season", String.valueOf(i));
                var responseTemporada = api.get(paramsTemporadas);
                DadosTemporada dadosTemporada = conversor.obterDados(responseTemporada, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(t -> t.episodios().forEach((e -> System.out.println(e.titulo()))));

            List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                    .flatMap( t -> t.episodios().stream())
                    .collect(Collectors.toList());
            System.out.println("\n Melhores 5 Ep");
            dadosEpisodios.stream()
                    .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                    .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                    .limit(5)
                    .forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap( t -> t.episodios().stream()
                            .map(d -> new Episodio(t.temporada(), d))
                    ).collect(Collectors.toList());
            episodios.forEach(System.out::println);

            System.out.println("A partir de qual data vc quer ver");
            var ano = leitura.nextInt();
            leitura.nextLine();
            LocalDate dataBusca = LocalDate.of(ano, 1 ,1);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            episodios.stream()
                    .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                    .forEach(e -> System.out.println("Temporada:  " + e.getTemporada()+
                            " Episodio: " + e.getEpisodio() +
                            " Titulo: " + e.getTitulo() +
                            " Data Lançamento: " + e.getDataLancamento().format(formatter)));
        }
        catch(Exception e){
                System.out.println("Erro " + e);
                menu();
            }

    }
}
