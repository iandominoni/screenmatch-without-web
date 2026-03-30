package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnaliseSerieResponse {
    private DadosSerie serie;
    private List<DadosEpisodio> episodiosEncontrados;
    private List<String> melhores5Episodios;
    private Map<Integer, Double> mediaPorTemporada;
    private String mediaGeral;
    private String maiorNota;
    private String menorNota;

    public AnaliseSerieResponse(DadosSerie serie,
                                List<DadosEpisodio> episodiosEncontrados,
                                List<String> melhores5Episodios,
                                Map<Integer, Double> mediaPorTemporada,
                                String mediaGeral,
                                String maiorNota,
                                String menorNota) {
        this.serie = serie;
        this.episodiosEncontrados = episodiosEncontrados;
        this.melhores5Episodios = melhores5Episodios;
        this.mediaPorTemporada = mediaPorTemporada;
        this.mediaGeral = mediaGeral;
        this.maiorNota = maiorNota;
        this.menorNota = menorNota;
    }

    public DadosSerie getSerie() { return serie; }
    public List<DadosEpisodio> getEpisodiosEncontrados() { return episodiosEncontrados; }
    public List<String> getMelhores5Episodios() { return melhores5Episodios; }
    public Map<Integer, Double> getMediaPorTemporada() { return mediaPorTemporada; }
    public String getMediaGeral() { return mediaGeral; }
    public String getMaiorNota() { return maiorNota; }
    public String getMenorNota() { return menorNota; }

    @Override
    public String toString() {
        return """                
                SÉRIE: %s
                Título: %s
                Total de Temporadas: %d
                Avaliação: %s
                Gênero: %s
                Atores: %s
                Sinopse: %s
                MELHORES 5 EPISÓDIOS:
                %s
                MÉDIA POR TEMPORADA:%s
                ESTATÍSTICAS GERAIS:
                Média Geral: %s
                Maior Nota: %s
                Menor Nota: %s
                """.formatted(
                serie.titulo(),
                serie.titulo(),
                serie.totalTemporadas(),
                serie.avaliacao(),
                serie.genero(),
                serie.atores(),
                serie.sinopse(),
                melhores5Episodios.stream()
                        .map(e -> "  • " + e)
                        .collect(Collectors.joining("\n")),
                mediaPorTemporada.entrySet().stream()
                        .map(e -> "  Temporada " + e.getKey() + ": " + String.format("%.2f", e.getValue()))
                        .collect(Collectors.joining("\n")),
                mediaGeral,
                maiorNota,
                menorNota
        );
    }
}