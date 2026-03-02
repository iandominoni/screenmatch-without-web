package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.service.ApiCallOmdb;
import br.com.alura.screenmatch.service.ConverterDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.core.Converter;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var ApiCall = new ApiCallOmdb();
		Map<String,String> params = new HashMap();
		params.put("t", "Gilmore Girls");
		var response = ApiCall.get(params);
		ConverterDados conversor = new ConverterDados();
		DadosSerie dados = conversor.obterDados(response, DadosSerie.class);
		System.out.println(dados);

	}
}
