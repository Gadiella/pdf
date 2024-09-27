package org.example.demojavapdf.models;

import org.example.demojavapdf.DBConfig.IDBConfig;
import org.example.demojavapdf.interfaces.FileInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MyFile implements FileInterface {
    private int id;
    private String file_name;
    private String upload_date;

    private String DestinationPath;
    private String Desktop;

    public String getDesktop() {
        return Desktop;
    }

    public void setDesktop(String desktop) {
        Desktop = desktop;
    }

    public String getDestinationPath() {
        return DestinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        DestinationPath = destinationPath;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public MyFile(int id, String file_name , String upload_date) {
        this.id = id;
        this.file_name = file_name;
        this.upload_date = upload_date;
    }

    public MyFile() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private Connection connection;

    public String getFile_name() {
        return file_name;
    }


    @Override
    public void handleCreatePDF(String fileName, String fileDestinataion) throws SQLException {
        connection = IDBConfig.getConnection();

        if (this.connection != null) {
            String query = "INSERT INTO files (file_name, file_path ) VALUES (?, ?)";
            try (PreparedStatement statement = this.connection.prepareStatement(query)) {
                statement.setString(1, fileName);
                statement.setString(2, fileDestinataion);

                statement.executeUpdate();
                System.out.println("Chemin du fichier inséré avec succès !");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Non connecté");
        }
    }

    @Override
    public void handledeletepdf(int id) throws SQLException {
        connection = IDBConfig.getConnection();
        if (this.connection != null) {
            String query = "DELETE FROM files WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        }
    }


    @Override
    public void handlemodifypdf(String newFileName, int id, String newfileDestinataion) throws SQLException {

        connection = IDBConfig.getConnection();

        if (this.connection != null) {
            String query = "UPDATE files SET file_name = ?, file_path = ? WHERE id = ?";
            try (PreparedStatement statement = this.connection.prepareStatement(query)) {
                statement.setString(1,  newFileName);
                statement.setString(2, newfileDestinataion);
                statement.setInt(3, id);
                statement.executeUpdate();
                System.out.println("Fichier mis à jour avec succès !");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Non connecté");
        }
    }

    @Override
    public List<MyFile> getMyFiles() throws SQLException {

        List<MyFile> myFiles = new ArrayList<>(); // Liste pour stocker les fichiers récupérés
        connection = IDBConfig.getConnection(); // Connexion à la base de données
        if (this.connection != null) {
            String query = "SELECT * FROM files";

            try {
                PreparedStatement statement = this.connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    myFiles.add(new MyFile(resultSet.getInt("id"), resultSet.getString("file_name"),resultSet.getString("upload_date")));
                }
            } catch (RuntimeException th) {
                System.out.println(th);
            }

        }

        return myFiles;

    }

    @Override
    public MyFile handlegetfiledetails(int id) throws SQLException {
        return null;
    }

    @Override
    public MyFile handleGetFileDetails(int id) throws SQLException {
        connection = IDBConfig.getConnection();
        MyFile myFile = null;

        if (this.connection != null) {
            String query = "SELECT id, file_name, file_path FROM files WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        myFile = new MyFile(resultSet.getInt("id"),
                                resultSet.getString("file_name"),
                                resultSet.getString("upload_date"));
                        myFile.setDestinationPath(resultSet.getString("file_path"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return myFile;
    }
}




