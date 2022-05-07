package com.vitalytyrenko.pigeonmail;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreatePigeonController {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField xTextField;
    @FXML
    private TextField yTextField;
    @FXML
    private TextField degreesTextField;

    @FXML
    private CheckBox randomCoordinatesCheckBox;
    @FXML
    private CheckBox randomDegreesCheckBox;
    @FXML
    private CheckBox hasMailCheckBox;

    @FXML
    private ChoiceBox<String> moveTypeChoiceBox;

    @FXML
    private RadioButton radioButton1;
    @FXML
    private RadioButton radioButton2;
    @FXML
    private RadioButton radioButton3;

    @FXML
    private Button applyButton;
    @FXML
    private Button cancelButton;

    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final ObservableList<String> choiceBoxItems = FXCollections
            .observableArrayList("Звичайний голуб", "Голуб-винищувач");

    public void resetFields() {
        nameTextField.requestFocus();

        nameTextField.setText("");
        xTextField.setText("0");
        yTextField.setText("0");
        degreesTextField.setText("0");

        randomCoordinatesCheckBox.setSelected(false);
        randomDegreesCheckBox.setSelected(false);
        hasMailCheckBox.setSelected(false);
        hasMailCheckBox.setDisable(true);

        radioButton1.setSelected(true);
        moveTypeChoiceBox.setValue(choiceBoxItems.get(0));
    }

    public void initialize() {
        moveTypeChoiceBox.setItems(choiceBoxItems);
        radioButton1.setToggleGroup(toggleGroup);
        radioButton2.setToggleGroup(toggleGroup);
        radioButton3.setToggleGroup(toggleGroup);

        resetFields();

        // field listeners
        nameTextField.setPromptText(Pigeon.UNNAMED);
        nameTextField.textProperty().addListener((obv, oldValue, newValue) -> {
            if (newValue.length() > 16)
                nameTextField.setText(oldValue);
        });
        xTextField.textProperty()
                .addListener(new NumberFieldController(xTextField, Universal.WIDTH - Pigeon.WIDTH));
        yTextField.textProperty()
                .addListener(new NumberFieldController(yTextField, Universal.WIDTH - Pigeon.WIDTH));
        degreesTextField.textProperty().addListener(new NumberFieldController(degreesTextField, 360));

        randomCoordinatesCheckBox.selectedProperty().addListener((obv, v, isChecked) -> {
            xTextField.setDisable(isChecked);
            yTextField.setDisable(isChecked);
        });
        randomDegreesCheckBox.selectedProperty()
                .addListener((obv, v, isChecked) -> degreesTextField.setDisable(isChecked));

        toggleGroup.selectedToggleProperty()
                .addListener((obv, t, toggle) -> hasMailCheckBox.setDisable(toggle != radioButton3));

        // button listeners
        cancelButton.setOnAction(e -> onCancelButtonClicked());
        applyButton.setOnAction(e -> onApplyButtonClicked());
        cancelButton.setCancelButton(true);
        applyButton.setDefaultButton(true);
    }

    private void onCancelButtonClicked() {
        ((Stage) cancelButton.getScene().getWindow()).close();
        resetFields();
    }

    private void onApplyButtonClicked() {
        Pigeon pigeon;
        Toggle pigeonLevel = toggleGroup.getSelectedToggle();
        if (pigeonLevel == radioButton1) {
            pigeon = new Pigeon();
        } else if (pigeonLevel == radioButton2) {
            pigeon = new WhitePigeon();
        } else {
            pigeon = new PostPigeon();
            ((PostPigeon) pigeon).setHasMail(hasMailCheckBox.isSelected());
        }

        if (!nameTextField.getText().isBlank()) {
            pigeon.setName(nameTextField.getText().strip());
        }

        if (!randomCoordinatesCheckBox.isSelected()) {
            pigeon.setX(Integer.parseInt(xTextField.getText().strip()));
            pigeon.setY(Integer.parseInt(yTextField.getText().strip()));
        }
        if (!randomDegreesCheckBox.isSelected()) {
            pigeon.setDegrees(Integer.parseInt(degreesTextField.getText().strip()));
        }

        pigeon.setMoveType(choiceBoxItems.indexOf(moveTypeChoiceBox.getValue()));
        Universal.getInstance().bindPigeonToScene(pigeon);

        ((Stage) applyButton.getScene().getWindow()).close();
        resetFields();
    }

    private record NumberFieldController(TextField textField, int maxValue) implements ChangeListener<String> {

        private static final String numRegExp = "([1-9][0-9]*|0)";
        private static final String digitRegExp = "[0-9]+";

        @Override
        public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
            if (newValue.strip().equals("")) {
                textField.setText("0");
            } else if (newValue.strip().matches(numRegExp)) {
                int xValue = Integer.parseInt(newValue.strip());
                if (xValue > maxValue) {
                    textField.setText(String.valueOf(maxValue));
                }
            } else if (newValue.strip().matches(digitRegExp)) {
                textField.setText(newValue.substring(1));
            } else {
                textField.setText(oldValue);
            }
        }
    }
}
