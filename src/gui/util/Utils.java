package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.entities.Seller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {

    public static Stage currentStage(ActionEvent event){
        return (Stage) ((Node)event.getSource()).getScene().getWindow();
    }

    public static Integer tryParseToInt(String str){
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double tryParseToDouble(String str){
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double tryParseSalaryfForDouble(TextField txt) {
        String text = txt.getText();

        if (text == null || text.trim().isEmpty() || text.equals("R$ 0,00")) {
            return 0.0;
        }

        DecimalFormat df = new DecimalFormat("R$ #,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
        df.setParseBigDecimal(true);

        try {
            Number number = df.parse(text);

            BigDecimal value = (BigDecimal) number;
            return value.doubleValue();

        } catch (ParseException e) {
            System.err.println("Erro ao converter salário: " + text + " - " + e.getMessage());
            return null;
        }
    }

    public static LocalDate defineLocalDateFormat(LocalDate localDate, String fmt) {
        return LocalDate.parse(localDate.format(DateTimeFormatter.ofPattern(fmt)));
    }

    public static String defineDecimalFormat(Double value) {
        DecimalFormat df = new DecimalFormat("R$ #,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
        if (value == null) {
            return "R$ 0,00";
        }
            System.out.println(value);
            System.out.println(df.format(value));
            return df.format(value);
    }
}
