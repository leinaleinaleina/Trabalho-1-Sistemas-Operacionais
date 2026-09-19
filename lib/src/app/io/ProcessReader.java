package app.io;

import app.domain.Process;
import app.domain.ProcessType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//garante a leitura do arquivo CSV de entrada e o mapeamento dos campos exigidos para instanciar a lista de processos
public class ProcessReader {
    
    public static List<Process> lerArquivoCSV(String caminhoArquivo) {
        List<Process> processos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha = br.readLine();

            while ((linha = br.readLine()) != null) {
                String[] colunas = linha.split(",");

                if (colunas.length < 9) continue;
                
                String pid = colunas[0].trim();
                int tempoChegada = Integer.parseInt(colunas[1].trim());
                int tempoTotalCpu = Integer.parseInt(colunas[2].trim());
                int prioridade = Integer.parseInt(colunas[3].trim());
                ProcessType tipoProcesso = ProcessType.valueOf(colunas[4].trim().toUpperCase());
                boolean temOperacaoES = Boolean.parseBoolean(colunas[5].trim());
                double probabilidadeES = Double.parseDouble(colunas[6].trim());
                int mediaES = Integer.parseInt(colunas[7].trim());
                int duracaoES = Integer.parseInt(colunas[8].trim());
                
                Process processo = new Process(pid, tempoChegada, tempoTotalCpu, prioridade, tipoProcesso, temOperacaoES, probabilidadeES, mediaES, duracaoES);
                processos.add(processo);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o ficheiro de processos: " + e.getMessage());
        }

        return processos;
    }
}
