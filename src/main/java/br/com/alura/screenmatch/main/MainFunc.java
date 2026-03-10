package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ApiCallOmdb;
import br.com.alura.screenmatch.service.ConverterDados;

import java.io.IOException;
import java.util.*;

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
            for (int i = 0;i <dadosSerie.totalTemporadas() ; i++) {
                List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
                System.out.printf("Temporada: %d%n", i+1);
                for(int j =0; j< episodiosTemporada.size(); j++){
                    System.out.println(episodiosTemporada.get(j).titulo());
                }
        }}
        catch(Exception e){
                System.out.println("Erro " + e);
                menu();
            }

    }
}
