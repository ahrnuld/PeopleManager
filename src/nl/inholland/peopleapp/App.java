package nl.inholland.peopleapp;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Database db = new Database();
        ObservableList<Person> people = FXCollections.observableArrayList(db.getPeople());

        stage.setTitle("People manager");
        stage.setMinWidth(250);

        VBox layout = new VBox();
        layout.setPadding(new Insets(10));

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem loadItem = new MenuItem("Load...");
        MenuItem saveItem = new MenuItem("Save...");
        fileMenu.getItems().addAll(loadItem, saveItem);
        menuBar.getMenus().addAll(fileMenu);

        layout.getChildren().add(menuBar);

        saveItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try(FileOutputStream fos = new FileOutputStream("people.dat");
                    ObjectOutputStream oos = new ObjectOutputStream(fos)) {

                    for(Person person : people)
                    {
                        oos.writeObject(person);
                    }

                } catch(FileNotFoundException fnfe) {
                    System.out.println(fnfe.getMessage());
                } catch(IOException ioe)
                {
                    System.out.println(ioe.getMessage());
                }

            }
        });

        loadItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("people.dat"))){
                    people.clear();
                    while(true) {
                        try {
                            Person person = (Person)ois.readObject();
                            people.add(person);
                        } catch(EOFException eofe)
                        {
                            break;
                        }
                    }
                } catch (IOException | ClassNotFoundException ioe) {
                    System.out.println(ioe.getMessage());
                }
            }
        });


        TableView<Person> tableView = new TableView<>();

        TableColumn<Person, String> firstNameColumn = new TableColumn<>("First name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Person, String> lastNameColumn = new TableColumn<>("Last name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Person, String> birthDateColumn = new TableColumn<>("Birth date");
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

        tableView.getColumns().addAll(firstNameColumn, lastNameColumn, birthDateColumn);
        tableView.setItems(people);

        TextField firstNameInput = new TextField();
        firstNameInput.setPromptText("First name");
        TextField lastNameInput = new TextField();
        lastNameInput.setPromptText("Last name");
        DatePicker birthDateInput = new DatePicker();
        birthDateInput.setPromptText("Birth date");

        birthDateInput.setStyle("-fx-background-color: antiquewhite");

        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("dangerousButton");

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (!firstNameInput.equals("") && !lastNameInput.equals("") && birthDateInput.getValue() != null) {
                    Person person = new Person(firstNameInput.getText(), lastNameInput.getText(), birthDateInput.getValue());
                    people.add(person);
                    firstNameInput.clear();
                    lastNameInput.clear();
                    birthDateInput.setValue(null);
                }
            }
        });

        HBox form = new HBox();
        form.setPadding(new Insets(10));
        form.setSpacing(10);

        form.getChildren().addAll(firstNameInput, lastNameInput,
                birthDateInput, addButton, deleteButton);

        layout.getChildren().addAll(tableView, form);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("resources/css/style.css");
        stage.setScene(scene);
        stage.show();
    }
}
