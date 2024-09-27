package org.example.demojavapdf.interfaces;

import org.example.demojavapdf.models.MyFile;

import java.sql.SQLException;
import java.util.List;

public interface FileInterface {

    void handleCreatePDF(String fileName, String fileDestinataion) throws SQLException;
      void handledeletepdf(int id) throws SQLException;

    void handlemodifypdf(String newFileName, int id, String newfileDestinataion) throws SQLException;

    public List<MyFile> getMyFiles() throws SQLException;

    MyFile handlegetfiledetails(int id) throws SQLException;

    MyFile handleGetFileDetails(int id) throws SQLException;
}
