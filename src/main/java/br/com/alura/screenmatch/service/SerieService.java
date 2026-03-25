package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SerieService {

    private final ApiCallOmdb api;
    private final ConverterDados conversor;
    private final ConsultaIATraducao tradutor;

    public SerieService(ApiCallOmdb api, ConverterDados conversor, ConsultaIATraducao tradutor) {
        this.api = api;
        this.conversor = conversor;
        this.tradutor = tradutor;
    }

    public DadosSerie buscarSerie(String nomeSerie) throws Exception {
        DadosSerie dadosSerie = getDadosSeriePorNome(nomeSerie);

        String traducaoJson = tradutor.traduzir(dadosSerie.sinopse());
        String sinopseTraduzida = extrairTraducao(traducaoJson);

        return new DadosSerie(
                dadosSerie.titulo(),
                dadosSerie.totalTemporadas(),
                dadosSerie.avaliacao(),
                dadosSerie.genero(),
                dadosSerie.atores(),
                dadosSerie.poster(),
                sinopseTraduzida
        );
    }

    public List<DadosTemporada> buscarEpisodiosPorSerie(String nomeSerie) throws Exception {
        DadosSerie dadosSerie = getDadosSeriePorNome(nomeSerie);
        return getTemporadas(nomeSerie, dadosSerie.totalTemporadas());
    }

    public AnaliseSerieResponse exibirAnalisesCompletas(String nomeSerie) throws Exception {
        DadosSerie serie = buscarSerie(nomeSerie);
        List<DadosTemporada> temporadas = buscarEpisodiosPorSerie(nomeSerie);

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .toList();

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream().map(d -> new Episodio(t.temporada(), d)))
                .toList();

        List<String> melhores5 = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .sorted(Comparator.comparingDouble(Episodio::getAvaliacao).reversed())
                .limit(5)
                .map(e -> "Temporada " + e.getTemporada()
                        + " Episodio: " + e.getEpisodio()
                        + " Nome: " + e.getTitulo())
                .toList();

        Map<Integer, Double> mediaPorTemporada = temporadas.stream()
                .collect(Collectors.toMap(
                        DadosTemporada::temporada,
                        t -> t.episodios().stream()
                                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                                .mapToDouble(e -> Double.parseDouble(e.avaliacao()))
                                .average()
                                .orElse(0.0)
                ));

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        DecimalFormat df = new DecimalFormat("#0.00");

        return new AnaliseSerieResponse(
                serie,
                dadosEpisodios,
                melhores5,
                mediaPorTemporada,
                df.format(est.getAverage()),
                df.format(est.getMax()),
                df.format(est.getMin())
        );
    }

    private String extrairTraducao(String json) throws Exception {
        JsonNode root = conversor.getMapper().readTree(json);

        return root
                .path("data")
                .path("translations")
                .get(0)
                .path("translatedText")
                .asText();
    }

    private DadosSerie getDadosSeriePorNome(String nomeSerie) throws Exception {
        Map<String, String> paramsSerie = new HashMap<>();
        paramsSerie.put("t", nomeSerie);

        String responseSerie = api.get(paramsSerie);
        return conversor.obterDados(responseSerie, DadosSerie.class);
    }

    private List<DadosTemporada> getTemporadas(String nomeSerie, int totalTemporadas) throws Exception {
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= totalTemporadas; i++) {
            Map<String, String> paramsTemporada = new HashMap<>();
            paramsTemporada.put("t", nomeSerie);
            paramsTemporada.put("Season", String.valueOf(i));

            String responseTemporada = api.get(paramsTemporada);
            temporadas.add(conversor.obterDados(responseTemporada, DadosTemporada.class));
        }

        return temporadas;
    }
}