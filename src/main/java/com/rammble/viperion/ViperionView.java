package com.rammble.viperion;

import com.rammble.viperion.ie.ImageEditor;
import com.rammble.viperion.ie.ImageSaveSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ViperionView implements Initializable {
    private File imageFile = null, directory = null;

    @FXML
    private ChoiceBox<ImageSaveSettings> imageOptionSelector;
    @FXML
    private Button fileSelectButton, directoryFileButton;
    @FXML
    private Label imageFileLabel, directoryFileLabel, generationLabel;
    @FXML
    public Stage stage;
    @FXML
    private ImageView imageView;
    @FXML
    private AnchorPane thePane;
    @FXML
    private TextField newImageName, optionSetting;

    ViperionController controller = new ViperionController();

    /**
     * This is called after the root elements have been added, instantiating the fields we need to add data to
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        optionSetting.setVisible(false);
        Class<ImageSaveSettings> e = ImageSaveSettings.class;
        imageOptionSelector.getItems().addAll(Arrays.stream(e.getEnumConstants()).toList());
        imageOptionSelector.setOnAction(this::updateOptionTextFieldDisplay);
    }

    @FXML
    void onFileSelectPressed(ActionEvent event) throws IOException {
        try {
            FileChooser chooser = new FileChooser();

            // display the file selection
            imageFile = chooser.showOpenDialog(stage);

            // inform the user
            displayMessage(imageFileLabel, "Image Selected: " + imageFile.getName());

            // display the selected file
            displayImage();
        } catch (Exception e) {
            displayError(imageFileLabel, "There was an issue with the file you selected, make sure the file is a png or jpg");
        }
    }

    @FXML
    void onDirectoryFilePressed(ActionEvent event) {
        try {
            DirectoryChooser chooser = new DirectoryChooser();

            // display the directory selection
            directory = chooser.showDialog(stage);

            // inform the user
            displayMessage(directoryFileLabel, "Save Location: " + directory.getAbsolutePath());
        } catch (Exception e) {
            displayError(directoryFileLabel, "There was an issue with selecting the directory");
        }

    }

    @FXML
    void generateImage(ActionEvent event) {
        if (imageFile == null || directory == null || newImageName.getText() == "") {
            displayError(generationLabel, "You need a valid image, directory, and file name to output an image");
            return;
        }

        displayImage();
    }

    /**
     * Changes the canvas to display a certain image
     */
    private void displayImage() {
        try {

            // we have a valid image, but not a valid directory, so display the chosen image
            if (imageFile != null && directory == null) {
                Image image = new Image(imageFile.toURI().toURL().toExternalForm());
                imageView.setImage(image);
            }

            // other than that, try applying the effects to the image
            else {
                // get the reference image
                String ogImagePath = imageFile.getAbsolutePath();
                String outImagePath = directory.getAbsolutePath() + "\\" + newImageName.getText();

                File outFile = new File(outImagePath);

                // set up the image editor and get the image option selection
                ImageEditor imageEditor = new ImageEditor(ogImagePath, outImagePath);
                ImageSaveSettings option = imageOptionSelector.getValue();

                // determine if the image needs an option
                int value;
                if (imageEditor.needsNumericalOption(option))
                    value = Integer.parseInt(optionSetting.getText());
                else
                    value = 0;

                // run the ImageEditor
                imageEditor.saveNewImage(option, value);

                // get the image for displaying
                Image image = new Image(outFile.toURI().toURL().toExternalForm());

                // display it
                imageView.setImage(image);
                displayMessage(generationLabel, "Image was generated");
            }


        } catch (Exception e) {
            displayError(generationLabel, "The image could not be displayed");
        }
    }

    /**
     * Changes the label to a red error message
     * @param label the label to change
     * @param message the message to display
     */
    private void displayError(Label label, String message) {
        label.setTextFill(Color.RED);
        label.setText(message);
    }

    /**
     * Changes the label to a message
     * @param label the label to change
     * @param message the message to display
     */
    private void displayMessage(Label label, String message) {
        label.setTextFill(Color.BLACK);
        label.setText(message);
    }

    /**
     * Updates whether the option setting textfield should be seen or not. This should only
     * be visible if the user wants to compress or pixelate the image
     */
    private void updateOptionTextFieldDisplay(ActionEvent event) {
        ImageSaveSettings currentOption = imageOptionSelector.getValue();
        if (currentOption == ImageSaveSettings.COMPRESS || currentOption == ImageSaveSettings.PIXELATE)
            optionSetting.setVisible(true);
        else
            optionSetting.setVisible(false);
    }
}
