package gui.util;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import model.entities.Seller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Constraints {

    public static void setTextFieldInteger(TextField txt) {
        txt.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                txt.setText(oldValue);
            }
        });
    }

    public static void setTextFieldMaxLength(TextField txt, int max) {
        txt.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > max) {
                txt.setText(oldValue);
            }
        });
    }

    public static void setTextFieldDouble(TextField txt) {
        txt.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
                txt.setText(oldValue);
            }
        });
    }

    public static void setTextFieldSalaryDynamic(TextField txt) {
        // Define o formatador para o padrão brasileiro
        // O `Locale` é fundamental para garantir a vírgula como separador decimal.
        DecimalFormat df = new DecimalFormat("R$ #,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));

        // Define o valor inicial como "R$ 0,00"
        // Usa Platform.runLater para garantir que a UI esteja pronta antes de definir o texto inicial

        // Cria e armazena o ChangeListener em uma variável para poder removê-lo e adicioná-lo
        ChangeListener<String> textChangeListener = new ChangeListener<String>() {
            @Override

            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Remove temporariamente o listener para evitar loop infinito
                txt.textProperty().removeListener(this);

                try {
                    // Remove tudo que não for dígito do novo valor para processamento
                    String cleanValue = newValue.replaceAll("[^\\d]", "");

                    // Se a string limpa estiver vazia (todos os números foram apagados)x
                    if (cleanValue.isEmpty()) {
                        txt.setText(df.format(0.00)); // Volta para R$ 0,00
                        javafx.application.Platform.runLater(() -> txt.positionCaret(txt.getText().length()));
                        return; // Sai do listener
                    }

                    // Converte a string limpa para BigDecimal, considerando as duas casas decimais
                    // Ex: "123" -> 1.23, "1" -> 0.01, "12345" -> 123.45
                    BigDecimal value = new BigDecimal(cleanValue).divide(new BigDecimal(100));

                    // Formata o valor para exibição (R$ #,##0.00)
                    String formattedText = df.format(value);

                    // Atualiza o texto do TextField
                    txt.setText(formattedText);

                    // Posiciona o cursor no final do texto após a formatação
                    // Usamos Platform.runLater para garantir que isso ocorra após a atualização do texto na UI
                    javafx.application.Platform.runLater(() -> txt.positionCaret(formattedText.length()));

                } finally {
                    // Garante que o listener seja reativado, mesmo se ocorrer uma exceção
                    txt.textProperty().addListener(this);
                }
            }
        };

        // Adiciona o listener ao textProperty() do TextField
        txt.textProperty().addListener(textChangeListener);

        // Listener para quando o campo ganha/perde foco, para gerenciar a exibição
        txt.focusedProperty().addListener((obs, oldF, newF) -> {
            if (newF) { // Se o campo ganhou foco
                // Remove a formatação para permitir a digitação direta dos números
                // Ex: "R$ 123,45" -> "12345"
                String currentText = txt.getText().replaceAll("[^\\d]", "");
                txt.setText(currentText);
                // Posiciona o cursor no final do texto limpo
                javafx.application.Platform.runLater(() -> txt.positionCaret(currentText.length()));
            } else { // Se o campo perdeu o foco
                // Garante que o valor esteja formatado para exibição
                String cleanValue = txt.getText().replaceAll("[^\\d]", "");
                if (cleanValue.isEmpty()) {
                    txt.setText(df.format(0.00));
                } else {
                    BigDecimal value = new BigDecimal(cleanValue).divide(new BigDecimal(100));
                    txt.setText(df.format(value));
                }
                // Não precisa posicionar o cursor ao perder o foco
            }
        });
    }

    public static void setTextFieldDate(TextField txt) {
        final int MAX_LENGTH = 10;

        // 1. O Listener Principal: Reage a cada mudança no texto do TextField
        ChangeListener<String> textChangeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Remove o listener temporariamente para evitar loops infinitos
                txt.textProperty().removeListener(this);

                try {
                    // Passo A: Limpar a entrada
                    // Remove qualquer caractere que NÃO seja um dígito (0-9) do novo valor.
                    // Isso garante que o usuário só possa digitar números.
                    String cleanValue = newValue.replaceAll("[^\\d]", "");

                    // Passo B: Construir o valor formatado com as barras
                    StringBuilder formattedValue = new StringBuilder();

                    // Se há dígitos para processar
                    if (cleanValue.length() > 0) {
                        // Adiciona os primeiros 2 dígitos (dia)
                        // Math.min(2, cleanValue.length()) evita IndexOutOfBoundsException se houver menos de 2 dígitos
                        formattedValue.append(cleanValue.substring(0, Math.min(2, cleanValue.length())));

                        // Se já digitou 2 ou mais dígitos, adiciona a primeira barra e o mês
                        if (cleanValue.length() > 2) {
                            formattedValue.append("/");
                            // Adiciona os próximos 2 dígitos (mês)
                            formattedValue.append(cleanValue.substring(2, Math.min(4, cleanValue.length())));

                            // Se já digitou 4 ou mais dígitos, adiciona a segunda barra e o ano
                            if (cleanValue.length() > 4) {
                                formattedValue.append("/");
                                // Adiciona os próximos 4 dígitos (ano)
                                // Limita a 8 dígitos no total para o ano (cleanValue.substring(4, Math.min(8, cleanValue.length())))
                                formattedValue.append(cleanValue.substring(4, Math.min(8, cleanValue.length())));
                            }
                        }
                    }

                    // Passo C: Limitar o comprimento final do texto formatado
                    // Garante que o texto final não exceda "xx/xx/xxxx" (10 caracteres)
                    if (formattedValue.length() > MAX_LENGTH) {
                        // Se exceder, reverte para o valor antigo.
                        // Isso impede que o usuário digite mais do que o permitido após o ano.
                        txt.setText(oldValue);
                        // E reposiciona o cursor no final do valor antigo
                        Platform.runLater(() -> txt.positionCaret(oldValue.length()));
                        return; // Sai do listener para evitar mais processamento
                    }

                    // Passo D: Atualizar o TextField apenas se o texto mudou após a formatação
                    // Isso evita que o listener seja disparado desnecessariamente e previne loops.
                    if (!formattedValue.toString().equals(newValue)) {
                        txt.setText(formattedValue.toString());
                        // Passo E: Posicionar o cursor no final do texto após a atualização
                        // Isso é crucial para uma boa experiência do usuário,
                        // pois as barras são adicionadas automaticamente e o cursor pode "saltar".
                        // O Platform.runLater garante que isso aconteça na thread de UI depois que o setText for processado.
                        Platform.runLater(() -> txt.positionCaret(formattedValue.length()));
                    }

                } finally {
                    // Garante que o listener seja reativado, mesmo se ocorrer uma exceção
                    txt.textProperty().addListener(this);
                }
            }
        };

        // Adiciona o listener criado ao textProperty() do TextField
        txt.textProperty().addListener(textChangeListener);
    }

}