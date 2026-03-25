package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;

import java.util.List;
import java.util.Map;

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
}