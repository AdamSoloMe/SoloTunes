package com.csc229labfiles.finalaudioplayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.sql.Statement;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SecondaryController {

    
    /*class DataBaseThread implements Runnable {

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
    }

}*/
private Connection conn = null;
    @FXML
    private Button getFIleExtension;
    
    @FXML
    private TextField showAudioData;
    
    @FXML
    private TextField TitleField;
    @FXML
    private TextField ArtistField;
    
    @FXML
    private ListView myListView;
    
    private static String FileData;
    
    private static  ObservableList<String> Showitems;
  
    @FXML
    private void selectAudioFile(ActionEvent event) {
              System.out.println("Thread name"+Thread.currentThread().getName());
                String songtitle=TitleField.getText();
        String songartist=ArtistField.getText();
             if(songtitle==""||songartist ==""){
                  try {
                      throw new Exception("Please fillout all textFIeld before selecting the file");
                              
                              } catch (Exception ex) {
                      java.util.logging.Logger.getLogger(SecondaryController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                         Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Input parameters not filled");
            alert.setHeaderText("Please fill all text fields before adding a file ");
            alert.showAndWait();

        
                  }
            
        
        
             }
        else if(songtitle!=""&&songartist!=""){
            File file = null;
        Stage stage = (Stage) getFIleExtension.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtension = new FileChooser.ExtensionFilter("Audio files", "*.mp3", "*.wav");
        fileChooser.getExtensionFilters().add(fileExtension);
        file = fileChooser.showOpenDialog(stage);
        String FileName=file.toString();
      
        showAudioData.setText(FileName);
         FileData=convertAllDatatoJsonString(songtitle,songartist,FileName);
      
    }
    }
    
    private String convertAllDatatoJsonString(String songtitle,String Artist,String FileName){
        MusicFile songdata = new MusicFile(songtitle,Artist,FileName);
        GsonBuilder builder = new GsonBuilder(); 
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        String jsonString = gson.toJson(songdata);
         Showitems = myListView.getItems();
         Showitems.add(jsonString);
         return jsonString;
    }
    
    private MusicFile convertMusicDataIntoJSONObj(String jsonData) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        MusicFile addedSong = gson.fromJson(jsonData, MusicFile.class);
        return addedSong;

    }
    
    private void updateDatabase(){
         System.out.println("Thread name"+Thread.currentThread().getName());
        try{
           MusicFile DatabeingAdded = null;
        
            DatabeingAdded = convertMusicDataIntoJSONObj(FileData);

       
       
    
       
        String databaseURL = "";
        Connection conn = null;

        try {
            databaseURL = "jdbc:ucanaccess://.//MusicPlayerDatabase.accdb";
            conn = DriverManager.getConnection(databaseURL);

            String sql = "INSERT INTO musicLibrary(SongName,Artist,FileName) VALUES (?, ?, ?)";
            //clearDatabase();
            String  SongName = DatabeingAdded.getSongtitle();
            String Artist = DatabeingAdded.getArtist();
            String FileName = DatabeingAdded.getFileName();

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, SongName);
            preparedStatement.setString(2, Artist);
            preparedStatement.setString(3, FileName);
            int row = preparedStatement.executeUpdate();
            if (row > 0) {
                System.out.println("Row inserted");
            }
              Platform.runLater(() -> {
             
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Database Insertion Operation Notification");
            alert.setHeaderText("Your file has Sucessfully been inserted into the Database ");
            alert.showAndWait();

          
        });
        } catch (SQLException ex) {
            Platform.runLater(() -> {
             
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Database Insertion error");
            alert.setHeaderText("Please check if the file you are trying to add is not a duplicate and/or is on your Harddrive ");
            alert.showAndWait();

           
        });
       java.util.logging.Logger.getLogger(SecondaryController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
        }
     
        catch (Exception ex){
            Platform.runLater(() -> {
             
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Input parameters not filled");
             alert.setHeaderText("Please fill all text fields before adding a file ");
            alert.showAndWait();

           
        });
           
                    
        }
    }

    @FXML
    private void clearAllSongData(ActionEvent event){
        Showitems.clear();
        TitleField.clear();
         ArtistField.clear();
         showAudioData.clear();
    }
    
     
    
    @FXML
    private void addFiletoDB(ActionEvent event) {
        
        Thread updateDatabaseThread=new Thread(()->{
            updateDatabase();
        });
        updateDatabaseThread.start();
  
    }
    private void clearDB(){
        System.out.println("Thread name"+Thread.currentThread().getName());
          String databaseURL = "";
        Connection conn = null;
        System.out.println("The Database has been cleared so no duplicate entries will be stored within the database");
        try {
            databaseURL = "jdbc:ucanaccess://.//MusicPlayerDatabase.accdb";
            conn = DriverManager.getConnection(databaseURL);
            String sql = "DELETE FROM musicLibrary";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            int rowsDeleted = preparedStatement.executeUpdate();
            
               Platform.runLater(() -> {
             
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Database clear Operation Notification");
            alert.setHeaderText("The Database has been Successfully cleared ");
            alert.showAndWait();

           
        });
            System.out.println("Database Cleared"); 

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
              try {
                  conn.close();
              } catch (SQLException ex) {
                  java.util.logging.Logger.getLogger(SecondaryController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
              }
        }
    }
    
     @FXML
    private void clearDatabase(ActionEvent event) throws SQLException {
        
         Thread clearDatabaseThread=new Thread(()->{
            clearDB();
        });
        clearDatabaseThread.start();
        
    }
    
     @FXML
    private void gobacktoMainDisplay(ActionEvent event) throws IOException{
        App.setRoot("primary");
    }
}
