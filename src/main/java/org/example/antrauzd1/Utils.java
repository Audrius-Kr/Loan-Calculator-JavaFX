package org.example.antrauzd1;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class Utils {

    public static <T> Callback<TableColumn<T, Double>, TableCell<T, Double>> getRoundedCellFactory() {
        return column -> new TableCell<T, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        };
    }
}
