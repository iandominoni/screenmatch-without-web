package br.com.alura.screenmatch.model;


import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Episodio {
    private Integer temporada;
    private String titulo;
    private Integer episodio;
    private Double avaliacao;
    private LocalDate dataLancamento;

    private LocalDate converterData(String data){
        if(data == null || data.equalsIgnoreCase("N/A")) {
            return null;
        }
        try {
            return LocalDate.parse(data);
        }catch(DateTimeException e){
            return null;
        }
    }
    private double converterAvaliacao(String valor){
        try {
            return Double.parseDouble(valor);
        }catch(NumberFormatException e){
            return 0.0;
        }
    }
    public Episodio(Integer numeroTemporada, DadosEpisodio dadosEpisodio) {
        this.temporada = numeroTemporada;
        this.titulo = dadosEpisodio.titulo();
        this.episodio = dadosEpisodio.episodio();
        this.dataLancamento = converterData(dadosEpisodio.dataLancamento());
        this.avaliacao = converterAvaliacao(dadosEpisodio.avaliacao());
    }





    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public Integer getEpisodio() {
        return episodio;
    }

    public void setEpisodio(Integer episodio) {
        this.episodio = episodio;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return "Episodio{" +
                "Temporada = " + temporada +
                ", Titulo = " + titulo + '\'' +
                ", Episódio = " + episodio +
                ", Avaliação =" + avaliacao +
                ", Data Lancamento = " +
                (dataLancamento != null ? dataLancamento.format(formatter) : null);
    }
}
