package org.example.demojavapdf.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.demojavapdf.models.MyFile;
import java.awt.Desktop;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import com.itextpdf.layout.element.Image;

public class FileController {




    @FXML
    private TextField fileNameField;
    @FXML
    private TableView<MyFile> fileTableView;
    @FXML
    private ObservableList<MyFile> fileData;

    @FXML
    private TableColumn<MyFile, String> fileNameColumn;

    @FXML
    private TableColumn<MyFile, Integer> IdColumn;
    private MyFile myFiles;

    @FXML
    private TableColumn<?, ?> dateColumn;

    public void initialize() throws SQLException {
        myFiles = new MyFile();

        fileData = FXCollections.observableArrayList();
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("file_name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("upload_date"));
        loadFiles();

    fileTableView.setItems(this.fileData);

    }


    public void handleCreatePDF(ActionEvent event) {
        String fileName = fileNameField.getText();
        if (fileName.isEmpty()) {
            fileName = "default";
        }

        File imageFile = chooseImage((Stage) fileNameField.getScene().getWindow());
        if (imageFile != null) {
            try {
                String destinationPath = "src/main/resources/pdf/" + fileName + ".pdf";
                File pdfFile = new File(destinationPath);

                MyFile myFile = new MyFile();
                myFile.handleCreatePDF(fileName, destinationPath);

                // Create PDF using iText
                PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);

                ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
                Image image = new Image(imageData);

                document.add(image);
                document.close();

                myFiles = new MyFile();

                fileData = FXCollections.observableArrayList();
                IdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("file_name"));
                dateColumn.setCellValueFactory(new PropertyValueFactory<>("upload_date"));
                loadFiles();

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private File chooseImage(Stage stage) {
        FileChooser imageChooser = new FileChooser();
        imageChooser.setTitle("Sélectionner une image à insérer dans le PDF");
        imageChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers Image", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );
        return imageChooser.showOpenDialog(stage);
    }

@FXML
void handleopenfolder(ActionEvent event) {
    MyFile selectedFile = fileTableView.getSelectionModel().getSelectedItem();

    if (selectedFile != null) {

        String destinationPath = "src/main/resources/pdf/" + selectedFile.getFile_name() + ".pdf";


        File fileToOpen = new File(destinationPath);


        if (fileToOpen.exists()) {
            try {

                Desktop.getDesktop().open(fileToOpen);
            } catch (IOException e) {

                showAlert("Error", "Unable to open the file: " + fileToOpen.getAbsolutePath());
            }
        } else {

            showAlert("File Not Found", "The file does not exist: " + fileToOpen.getAbsolutePath());
        }
    } else {

        showAlert("No Selection", "No file selected. Please select a file to open.");


    } }

    public void handleModifyPDF(ActionEvent event) {
        MyFile selectedFile = fileTableView.getSelectionModel().getSelectedItem();

        if (selectedFile != null) {
            // Demander un nouveau nom de fichier à l'utilisateur
            TextInputDialog dialog = new TextInputDialog(selectedFile.getFile_name());
            dialog.setTitle("Modifier le fichier PDF");
            dialog.setHeaderText("Modifier le fichier PDF sélectionné");
            dialog.setContentText("Veuillez entrer un nouveau nom de fichier (sans extension) :");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                String newFileName = result.get().trim();

                // Vérifier que le nouveau nom n'est pas vide et différent de l'ancien nom
                if (!newFileName.isEmpty() && !newFileName.equals(selectedFile.getFile_name())) {
                    // Choisir une nouvelle image pour le PDF
                    File newImageFile = chooseImage((Stage) fileNameField.getScene().getWindow());

                    if (newImageFile != null) {
                        // Chemins de l'ancien et du nouveau fichier
                        String oldFilePath = "src/main/resources/pdf/" + selectedFile.getFile_name() + ".pdf";
                        String newDestinationPath = "src/main/resources/pdf/" + newFileName + ".pdf";

                        File oldFile = new File(oldFilePath);
                        File newFile = new File(newDestinationPath);

                        // Vérifier que le nouveau fichier n'existe pas déjà
                        if (newFile.exists()) {
                            showAlert("Erreur", "Un fichier avec ce nom existe déjà. Veuillez choisir un autre nom.");
                            return;
                        }

                        // Renommer le fichier dans le système de fichiers
                        boolean renamed = oldFile.renameTo(newFile);
                        if (renamed) {
                            try {
                                // Créer le nouveau PDF avec la nouvelle image
                                PdfWriter writer = new PdfWriter(new FileOutputStream(newFile));
                                PdfDocument pdfDoc = new PdfDocument(writer);
                                Document document = new Document(pdfDoc);

                                ImageData imageData = ImageDataFactory.create(newImageFile.getAbsolutePath());
                                Image image = new Image(imageData);

                                document.add(image);
                                document.close();

                                // Mettre à jour le fichier dans la base de données
                                selectedFile.handlemodifypdf(newFileName, selectedFile.getId(), newDestinationPath);

                                // Mettre à jour l'ObservableList et le TableView
                                selectedFile.setFile_name(newFileName);
                                selectedFile.setDestinationPath(newDestinationPath);
                                fileTableView.refresh();

                                // Afficher un message de succès
                                showAlert("Succès", "Le fichier a été renommé et l'image a été modifiée avec succès.");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                showAlert("Erreur", "Une erreur s'est produite lors de la mise à jour du fichier dans la base de données.");
                            } catch (FileNotFoundException | MalformedURLException e) {
                                e.printStackTrace();
                                showAlert("Erreur", "Une erreur s'est produite lors de la création du nouveau fichier PDF.");
                            }
                        } else {
                            showAlert("Erreur", "Impossible de renommer le fichier dans le système de fichiers.");
                        }
                    } else {
                        showAlert("Erreur", "Aucune nouvelle image sélectionnée.");
                    }
                } else {
                    showAlert("Erreur", "Le nouveau nom de fichier est invalide ou identique à l'ancien.");
                }
            }
        } else {
            // Alerte si aucun fichier n'est sélectionné
            showAlert("Aucun fichier sélectionné", "Veuillez sélectionner un fichier à modifier.");
        }
    }

    private void loadFiles() throws SQLException {

        fileData.clear();

        try {
            fileData.addAll(myFiles.getMyFiles());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        fileTableView.setItems(fileData);

    }


    public void handleDeletePDF(ActionEvent event) {

        MyFile selectedFile = fileTableView.getSelectionModel().getSelectedItem();

        if (selectedFile != null) {
            // Confirmer la suppression
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmer la suppression");
            confirmationAlert.setHeaderText("Voulez-vous vraiment supprimer ce fichier ?");
            confirmationAlert.setContentText("Fichier: " + selectedFile.getFile_name());
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Chemin du fichier à supprimer
                String destinationPath = "src/main/resources/pdf/" + selectedFile.getFile_name() + ".pdf";
                File fileToDelete = new File(destinationPath);

                // Affichage du chemin pour le débogage
                System.out.println("Chemin du fichier à supprimer : " + fileToDelete.getAbsolutePath());

                if (fileToDelete.exists()) {
                    if (fileToDelete.delete()) {
                        try {
                            // Supprimer l'entrée de la base de données
                            myFiles.handledeletepdf(selectedFile.getId());

                            // Mettre à jour l'ObservableList et le TableView
                            fileData.remove(selectedFile);
                            fileTableView.refresh();

                            // Afficher un message de succès
                            showAlert("Succès", "Le fichier a été supprimé avec succès.");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            showAlert("Erreur", "Une erreur s'est produite lors de la suppression du fichier de la base de données.");
                        }
                    } else {
                        showAlert("Erreur", "Impossible de supprimer le fichier du système de fichiers.");
                    }
                } else {
                    // Si le fichier n'existe pas, afficher un message d'erreur
                    showAlert("Erreur", "Le fichier n'existe pas dans le système de fichiers.");
                }
            }
        } else {
            // Alerte si aucun fichier n'est sélectionné
            showAlert("Aucun fichier sélectionné", "Veuillez sélectionner un fichier à supprimer.");
         }






}

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}




