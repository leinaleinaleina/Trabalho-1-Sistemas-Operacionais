package app.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HistoricalReader {

    // Retorna um Map contendo o tipo de processo (em minúsculas) e o seu peso (taxa de seleção de 0 a 100).
    
    public static Map<String, Integer> extractWeights(String csvPath) {
        Map<String, Integer> typeCounts = new HashMap<>();
        Map<String, Integer> typeSelections = new HashMap<>();
        Map<String, Integer> weights = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String header = br.readLine();
            if (header == null) return weights;

            // Localiza as colunas de forma dinâmica
            String[] columns = header.split(",");
            int typeIdx = -1, selectedIdx = -1;
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].trim().equals("tipo_processo")) typeIdx = i;
                if (columns[i].trim().equals("selecionado")) selectedIdx = i;
            }

            if (typeIdx == -1 || selectedIdx == -1) {
                System.err.println("Colunas 'tipo_processo' ou 'selecionado' não encontradas no histórico.");
                return weights;
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length <= Math.max(typeIdx, selectedIdx)) continue;

                String type = data[typeIdx].trim().toLowerCase();
                int selected = Integer.parseInt(data[selectedIdx].trim());

                typeCounts.put(type, typeCounts.getOrDefault(type, 0) + 1);
                if (selected == 1) {
                    typeSelections.put(type, typeSelections.getOrDefault(type, 0) + 1);
                }
            }

            // Calcula a percentagem de vitórias (Peso)
            for (String type : typeCounts.keySet()) {
                int total = typeCounts.get(type);
                int selected = typeSelections.getOrDefault(type, 0);
                int percentage = (int) Math.round(((double) selected / total) * 100);
                weights.put(type, percentage);
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler ficheiro de histórico: " + e.getMessage());
        }
        
        return weights;
    }
}