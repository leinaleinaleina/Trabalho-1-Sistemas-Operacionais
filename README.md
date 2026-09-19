<img src="/lib/assets/header/image.png" style="width:5.36111in; height:auto;">

# Simulador de Escalonador de Processos da disciplina de Sistemas Operacionais

**Integrantes:** Leina Yoshida e Thaynara Nascimento

## 1. Como executar
//inserir passo a passo de como compilar e executar o simulador

## 2. Algoritmos Implementados
### 2.1 Round-Robin
*   **Mecânica:** Fila única com *quantum* fixo de 4 unidades de tempo.
*   **Tratamento de E/S:** Interrupções retiram o processo da cpu e movem-no para a lista de bloqueados.

### 2.2 Múltiplas Filas
*   **Fila 1 (Tempo Real/Interativo):** Quantum 2.
*   **Fila 2 (I/O Bound/Misto):** Quantum 4.
*   **Fila 3 (CPU Bound/Batch):** Quantum 8.
*   **Preempção e inanição:** explicar estratégia adotada.//aguardando le

### 2.3 Definir nome para o nosso scheduler proposto
*   **Critério de escolha:** Como o próximo processo é selecionado.
*   **Uso de prioridade:** Usa prioridade base?
*   **Uso de quantum:** Fixo, variável ou sem quantum?
*   **Tratamento de processos interativos:** Há preferência?
*   **Estratégia contra inanição:** Como evita a inanição?
*   **Justificativa:** Justificar porque o método faz sentido.
*   **Limitações:** Apontar falhas conhecidas.

## 3. Resultados e Métricas (Tabelas e Gantt)
//inserir os diagramas de gantt gerados no console e as tabelas com tempo de inicio, conclusao, retorno, espera, resposta e trocas de contexto de cada algoritmo

## 4. Comparação Analítica
*   **Qual algoritmo teve o menor tempo médio de espera?** 
    * *Análise com base nos resultados práticos.*
*   **Qual teve o menor tempo médio de resposta?** 
    * *Análise com base nos resultados práticos.*
*   **Qual favoreceu processos interativos?** 
    * *Justificativa baseada nas características dos algoritmos e nas fórmulas.*
*   **Qual favoreceu processos longos?** 
    * *Justificativa.*
*   **Houve risco de inanição?** 
    * *Explicação.*
*   **O método proposto foi melhor em algum cenário?** 
    * *Explicação com os dados obtidos.*
*   **Quais foram as limitações do método proposto?** 
    * *Discussão crítica.*

## Link do repositório
https://github.com/leinaleinaleina/Trabalho-1-Sistemas-Operacionais.git