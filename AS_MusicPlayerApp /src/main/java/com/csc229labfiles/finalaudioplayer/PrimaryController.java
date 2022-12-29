package com.csc229labfiles.finalaudioplayer;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.Track;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author LENOVO
 */
public class PrimaryController {

    boolean firstTime = true;
    @FXML
    private ToggleButton playPause;
    @FXML
    private MediaView player;
    @FXML
    private Button prevButton, nextButton;
    @FXML
    private Slider volume;
    @FXML
    private ImageView albumArt;
    @FXML
    private Button browse;
    @FXML
    private Label MusicTitle;
    @FXML
    private Label ArtistLabel;
    @FXML
    private Slider MusicProgressBar;
    
    //private static MediaPlayer ainMediaPlayer

    
    private ArrayList<Media> mediaFiles = new ArrayList();
    private int counter = -1;

    private static String FileData;
    @FXML
    private ListView PlayList;

    @FXML
    private ListView SongInfo;
    
    /**
     * This Event handler method allows the User to Close the Application from the Top menu 
     * @param event 
     */
    @FXML
    private void closeApp(ActionEvent event){
        Platform.exit();
    }
    
    /**
     * The Purpose of this Event Handler Method is to inform the User When they start Appliaction of how to properly use it and describes all of it's functionality
     * When they select the help/About Menu Item
     * @param event 
     */
    @FXML
    private void showHelpInfo(ActionEvent event){
             Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("User Notifcation");
            alert.setHeaderText("MusicPlayer Info");
            alert.setContentText("This messgae is to inform you how music player functions so in order to play song you must first click Add a song  then from the add song window you first fill all the text fields before selecting your Audio FIle and then you must click insert to database Afterwards please click the back button to return to this window and then press list current songs, select you desired song(From the FIle List on the RIGHT) , Press load song here and then hit play if at any point you need this information once again please select the help button from the help menu" );
            alert.showAndWait();

           
    }
    /**
     * This Event Handler allows the User to play or pause of the Playback of there desired song
     * @param event 
     */
    @FXML
    private void playPauseClicked(ActionEvent event) {
      MediaPlayer currentSongPlaying=player.getMediaPlayer();//this Line of code is where we are getting the current Song object being played
          Media currentSong= player.getMediaPlayer().getMedia();//this Line of code is where we are getting the current Song object's data(like which Speciifc Song
            if (playPause.isSelected()) {//this if statement Is checking if the user has selected the play button in the GUI
                    currentSongPlaying.play();
                     currentSongPlaying.currentTimeProperty().addListener(new ChangeListener<javafx.util.Duration>() {//this event handler/change listenr is responsible for handling any changes made to the music progress bar 
                @Override
                public void changed(ObservableValue<? extends javafx.util.Duration> observable, javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                   MusicProgressBar.setValue(newValue.toSeconds());
                }
            }
            );
                     
       
            
            MusicProgressBar.setOnMousePressed(new EventHandler<MouseEvent>() {//this event handler is resonsible for allowing the USer to phyically click the progress bar with there mouse
                @Override
                public void handle(MouseEvent event) {
                   currentSongPlaying.seek(Duration.seconds(MusicProgressBar.getValue()));
                }
            });
            
            MusicProgressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {//this event handler is resonsible for allowing the USer to phyically drag the progress bar with there mouse to whatever point in the song they wish
                @Override
                public void handle(MouseEvent event) {
                  currentSongPlaying.seek(Duration.seconds(MusicProgressBar.getValue()));
                }
            });
           currentSongPlaying.setOnReady(new Runnable() {//this iniailizes the Progress bar
                @Override
                public void run() {
                    javafx.util.Duration total =currentSong.getDuration();
                    MusicProgressBar.setMax(total.toSeconds());
                }
            });
                
                  

            } else {//else if the play button is not selected the song is paused
                //cancelTimer();
              currentSongPlaying.pause();
            }
        }
    
    
    /**
     * This event Handelr Method allows the User to select which song file they want to play from the song file ListVuew
     * @param event 
     */

    @FXML
    public void handleMouseClick(MouseEvent event) {
        FileData = (String) PlayList.getSelectionModel().getSelectedItem();
    }
    
    /**
     * this event handler Creates a new Thread which opens a database connection and Updates both lsit Viewa with Song Info in the Left LsitView and The phyical song FIle in the Left
     * @param event 
     */
    @FXML
    public void UpdateListview(ActionEvent event) {
        ObservableList<String> Showitems = PlayList.getItems();
        ObservableList<String> ShowSongInfo=SongInfo.getItems();
           Thread updateListView=new Thread(()->{//this is where we create the new thread to open the database connection to update the LsitView
           System.out.println("Thread name"+Thread.currentThread().getName());
        String databaseURL = "";
        
        Connection conn = null;
        try {//here we open the JDBC connection 

            databaseURL = "jdbc:ucanaccess://.//MusicPlayerDatabase.accdb";
            conn = DriverManager.getConnection(databaseURL);

            String tableName = "musicLibrary";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("select FileName from " + tableName);
            while (result.next()) {

               
                String SongData = result.getString("FileName");
              System.out.println("Thread name "+Thread.currentThread().getName());
             Showitems.add(SongData);//here we get all of row and column data for the audio files

               
            }
            ResultSet result2 = stmt.executeQuery("select SongName,Artist from " + tableName);
            while (result2.next()) {

                String songName = result2.getString("SongName");
               String songArtist = result2.getString("Artist");
         
             System.out.println("Thread name"+Thread.currentThread().getName());
                ShowSongInfo.add(songName+" "+songArtist);//here we get all of row and column data for the Song Info
   
            }

        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(PrimaryController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    });
        updateListView.start();//here we start the thread operation 
    
    }
   
    /**
     * this browseClicked Event Handler Method Allows the User to load there song into the Media player to be played
     * @param event 
     */

    @FXML
    private void browseClicked(ActionEvent event) {

        Stage stage = (Stage) playPause.getScene().getWindow();
      
        File file = new File(FileData);//this is where we are storing the file selected by the User
      
       
        if (file.isFile() != true) {//if the select file is nor a file or it deos not exist we genenrrate an execption otifying the User
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("FIle Not Found");
            alert.setHeaderText("This File Does not Exist or is in a different directory Please go to the add song Menu and re add it ");
            alert.showAndWait();
        }
      //   System.out.println("This is added soing count before it's added"+addSongCounter);
        Media audioFile;
        audioFile = new Media(file.toURI().toString());//this is where we phyically convert the auido file inro an object that the media player can play
        //String Musictitle = file.getName();
        mediaFiles.add(audioFile);//this adds the file to our Araaylist of files to be played
        MediaPlayer audioPlayer = new MediaPlayer(audioFile);//this is where we load our song file into the media player
        try {//this try catch is to make sure we properly removed the previously loaded audio file 
            player.getMediaPlayer().dispose();
        } catch (Exception e) {
            System.out.println("media not disposed"+e);
        }
        prevButton.setDisable(false);
        nextButton.setDisable(false);
        ++counter;//this increments for each song loaded
        player = new MediaView(audioPlayer);
       
        
        //this change listen is respobile for geting/updating all of the metadata from our song file and displaying it in the GUI
        player.getMediaPlayer().getMedia().getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> change) -> {
            if (change.wasAdded()) {
                String key=change.getKey();
                System.out.println("this the current key data "+key);
                Object value=change.getValueAdded();
                System.out.println("this the current value data "+value);
                if(change.getKey().equals("title")){
                   MusicTitle.setText(value.toString());
                    
                }
                
                 if(change.getKey().equals("artist")){
                   ArtistLabel.setText(value.toString());
                    
                }
                if (change.getKey().equals("image")) {
                    Image art = (Image) change.getValueAdded();
                    System.out.println("image changed from when we loaded a song");
                     
                    double artWidth = art.getWidth(), viewWidth = albumArt.getFitWidth();
                    albumArt.setX(50);
                    albumArt.setImage(art);
                    albumArt.setX(50);
                    FadeTransition fadeTransition =
                new FadeTransition(Duration.seconds(2), albumArt);
                 fadeTransition.setFromValue(1.0); fadeTransition.setToValue(0.0);
                    fadeTransition.setCycleCount(2); fadeTransition.setAutoReverse(true); fadeTransition.play();
                }
            }
        });
    
       //this ChangeListenr is repsonsbile for handling volum control
        volume.valueProperty().addListener((Observable observable) -> {
            if (volume.isValueChanging()) {
                System.out.println(volume.getValue());
                player.getMediaPlayer().setVolume(volume.getValue() / 200);
            }
        });
        
        //this will make sure that when a song is finshed playing it the play button will be false 
        player.getMediaPlayer().setOnEndOfMedia(() -> {
            playPause.setSelected(false);
        });
    }
    
  
   /**
    * this event Handler is responsible for going to the previous song Once the User selects previous Song from the GUI
    * @param event 
    */

    @FXML
    private void prevClicked(ActionEvent event) {
        if (counter == 0) {//this statement is check if there is no longer a previosu song to be played
            player.getMediaPlayer().seek(Duration.ZERO);
          player.getMediaPlayer().stop();
           playPause.setSelected(false);
        } else {//if there is then it will dispose of the current song 

            player.getMediaPlayer().dispose();
             
                albumArt.setImage(null);
                 
            player = new MediaView(new MediaPlayer(mediaFiles.get(--counter)));
            
         
           // player.getMediaPlayer().play(); //and will then play the previosu song
            
            playPause.setSelected(true);
           volume.valueProperty().addListener((Observable observable) -> {
            if (volume.isValueChanging()) {
                System.out.println(volume.getValue());
                player.getMediaPlayer().setVolume(volume.getValue() / 200);
            }
        });
           
           //lines 335-344 I am updating the image, songLabel and Artist Label in the GUI to the data assovcated with the last song
            System.out.println("this is the image data"+player.getMediaPlayer().getMedia().getMetadata());
            String newTitle=(String) player.getMediaPlayer().getMedia().getMetadata().get("title");
             MusicTitle.setText(newTitle);
             Image art= (Image) player.getMediaPlayer().getMedia().getMetadata().get("image");
             String newArtistLabel=(String) player.getMediaPlayer().getMedia().getMetadata().get("artist");
              ArtistLabel.setText(newArtistLabel);
                double artWidth = art.getWidth(), viewWidth = albumArt.getFitWidth();
                        albumArt.setX(50);
                        albumArt.setImage(art);
                        albumArt.setX(50);
    
              player.getMediaPlayer().setOnEndOfMedia(() -> {
                playPause.setSelected(false);
            });    
        }
                      
    }

    @FXML
    private void nextClicked(ActionEvent event) {
        playPause.setSelected(false);
        if (counter + 1 == mediaFiles.size()) {
            player.getMediaPlayer().stop();
            playPause.setSelected(false);
        } else {
            player.getMediaPlayer().dispose();
           
   
            albumArt.setImage(null);
            player = new MediaView(new MediaPlayer(mediaFiles.get(++counter)));
            
            playPause.setSelected(true);
           // player.getMediaPlayer().play();
            
             String newTitle=(String) player.getMediaPlayer().getMedia().getMetadata().get("title");
             MusicTitle.setText(newTitle);
             String newArtistLabel=(String) player.getMediaPlayer().getMedia().getMetadata().get("artist");
              ArtistLabel.setText(newArtistLabel);
             Image art= (Image) player.getMediaPlayer().getMedia().getMetadata().get("image");
                double artWidth = art.getWidth(), viewWidth = albumArt.getFitWidth();
                        albumArt.setX(50);
                        albumArt.setImage(art);
                        albumArt.setX(50);
                             FadeTransition fadeTransition =
                new FadeTransition(Duration.seconds(2), albumArt);
                 fadeTransition.setFromValue(1.0); fadeTransition.setToValue(0.0);
                    fadeTransition.setCycleCount(2); fadeTransition.setAutoReverse(true); fadeTransition.play();
                  player.getMediaPlayer().setOnEndOfMedia(() -> {
                playPause.setSelected(false);
            });
        }
    }



    @FXML
    private void goToAddSongWindow(ActionEvent event) throws IOException {
        if(playPause.isSelected()==true){
          player.getMediaPlayer().stop();
        }
        App.setRoot("secondary");

    }
}
