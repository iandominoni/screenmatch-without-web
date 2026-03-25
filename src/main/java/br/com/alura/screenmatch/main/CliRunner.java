package br.com.alura.screenmatch.main;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CliRunner implements CommandLineRunner {

    private final MainFunc mainFunc;

    public CliRunner(MainFunc mainFunc) {
        this.mainFunc = mainFunc;
    }

    @Override
    public void run(String... args) throws Exception {
        mainFunc.menu();
    }
}