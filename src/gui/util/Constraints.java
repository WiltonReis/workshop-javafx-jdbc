package gui.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

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

    public static void setTextFieldSalaryDynamic(TextField txt) {
        DecimalFormat df = new DecimalFormat("R$ #,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));

        ChangeListener<String> textChangeListener = new ChangeListener<String>() {
            @Override

            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                txt.textProperty().removeListener(this);

                try {
                    String cleanValue = newValue.replaceAll("[^\\d]", "");

                    if (cleanValue.isEmpty()) {
                        txt.setText(df.format(0.00));
                        javafx.application.Platform.runLater(() -> txt.positionCaret(txt.getText().length()));
                        return; // Sai do listener
                    }
                    BigDecimal value = new BigDecimal(cleanValue).divide(new BigDecimal(100));

                    String formattedText = df.format(value);

                    txt.setText(formattedText);
                    javafx.application.Platform.runLater(() -> txt.positionCaret(formattedText.length()));

                } finally {
                    txt.textProperty().addListener(this);
                }
            }
        };
        txt.textProperty().addListener(textChangeListener);

        txt.focusedProperty().addListener((obs, oldF, newF) -> {
            if (newF) {
                String currentText = txt.getText().replaceAll("[^\\d]", "");
                txt.setText(currentText);
                javafx.application.Platform.runLater(() -> txt.positionCaret(currentText.length()));
            } else {
                String cleanValue = txt.getText().replaceAll("[^\\d]", "");
                if (cleanValue.isEmpty()) {
                    txt.setText(df.format(0.00));
                } else {
                    BigDecimal value = new BigDecimal(cleanValue).divide(new BigDecimal(100));
                    txt.setText(df.format(value));
                }
            }
        });
    }
}