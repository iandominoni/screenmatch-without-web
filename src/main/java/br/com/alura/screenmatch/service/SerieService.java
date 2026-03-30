package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.EpisodioRepository;
import br.com.alura.screenmatch.repository.SerieRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SerieService {
    private final ApiCallOmdb api;
    private final ConverterDados conversor;
    private final SerieRepository serieRepository;
    private final EpisodioRepository episodioRepository;
    private final ConsultaIATraducao tradutor;

    public SerieService(ApiCallOmdb api, ConverterDados conversor, ConsultaIATraducao tradutor,
                        SerieRepository serieRepository, EpisodioRepository episodioRepository) {
        this.api = api;
        this.conversor = conversor;
        this.tradutor = tradutor;
        this.serieRepository = serieRepository;
        this.episodioRepository = episodioRepository;
    }

    @Transactional
    public Serie obterSerieOuBuscar(String nomeSerie) throws Exception {
        Optional<Serie> serieExistente = serieRepository.findByTituloContainingIgnoreCaseWithEpisodios(nomeSerie);

        if (serieExistente.isPresent()) {
            return serieExistente.get();
        } else {
            System.out.println("Série não encontrada no banco. Buscando na API...");
            return buscarESalvarSerie(nomeSerie);
        }
    }

    @Transactional
    public List<DadosTemporada> obterEpisodiosOuBuscar(Serie serie) throws Exception {
        if (!serie.getEpisodios().isEmpty()) {
            System.out.println("Episódios encontrados no banco!");
            return converterEpisodiosParaDados(serie.getEpisodios());
        } else {
            System.out.println("Buscando episódios na API...");
            List<DadosTemporada> temporadas = buscarEpisodiosPorSerie(serie.getTitulo());
            salvarEpisodios(temporadas, serie);
            return temporadas;
        }
    }

    @Transactional
    public Serie buscarESalvarSerie(String nomeSerie) throws Exception {
        DadosSerie dadosSerie = buscarSerie(nomeSerie);
        Serie serie = new Serie(dadosSerie);

        Serie serieSalva = serieRepository.save(serie);

        List<DadosTemporada> temporadas = buscarEpisodiosPorSerie(nomeSerie);
        salvarEpisodios(temporadas, serieSalva);

        return serieRepository.findByTituloContainingIgnoreCaseWithEpisodios(nomeSerie).orElse(serieSalva);
    }


    @Transactional
    private void salvarEpisodios(List<DadosTemporada> temporadas, Serie serie) {
        temporadas.forEach(temporada ->
                temporada.episodios().forEach(dadosEpisodio -> {
                    Episodio episodio = new Episodio(temporada.temporada(), dadosEpisodio);
                    episodio.setSerie(serie);
                    episodioRepository.save(episodio);
                })
        );
    }

    private List<DadosTemporada> converterEpisodiosParaDados(List<Episodio> episodios) {
        Map<Integer, List<Episodio>> porTemporada = episodios.stream()
                .collect(Collectors.groupingBy(Episodio::getTemporada));

        return porTemporada.entrySet().stream()
                .map(entry -> {
                    List<DadosEpisodio> dados = entry.getValue().stream()
                            .map(e -> new DadosEpisodio(
                                    e.getTitulo(),
                                    e.getNumero(),
                                    String.valueOf(e.getAvaliacao()),
                                    e.getDataLancamento() != null ? e.getDataLancamento().toString() : "N/A"
                            ))
                            .toList();
                    return new DadosTemporada(entry.getKey(), dados);
                })
                .toList();
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
                        + " Episodio: " + e.getNumero()
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
        return root.path("data").path("translations").get(0).path("translatedText").asText();
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