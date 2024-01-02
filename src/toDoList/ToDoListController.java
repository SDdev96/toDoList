package toDoList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@SuppressWarnings("all")

public class ToDoListController implements Initializable {

    @FXML
    private Button addButton;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField tfDescription;

    @FXML
    private MenuItem openFile;

    @FXML
    private MenuItem removeEvento;

    @FXML
    private MenuItem saveFile;

    @FXML
    private TableView<Evento> tableView;

    @FXML
    private TableColumn<Evento, LocalDate> dateColumn;

    @FXML
    private TableColumn<Evento, String> descriptionColumn;

    private ObservableList<Evento> eventList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventList = FXCollections.observableArrayList();
        tableView.setItems(eventList);

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("data"));

        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        datePicker.setValue(LocalDate.now());

        initButtons();
        // initList();

        loadBackupList();

        Thread t = new Thread(new TimedSaving(eventList));
        t.setDaemon(true);
        t.start();
    }

    private void initList() {
        eventList.add(new Evento(LocalDate.parse("2023-12-01"), "Evento 1"));
        eventList.add(new Evento(LocalDate.parse("2023-12-02"), "Evento 2"));
        eventList.add(new Evento(LocalDate.parse("2023-12-04"), "Evento 3"));
        eventList.add(new Evento(LocalDate.parse("2023-12-11"), "Evento 4"));
        eventList.add(new Evento(LocalDate.parse("2023-12-07"), "Evento 5"));
        eventList.add(new Evento(LocalDate.parse("2023-12-04"), "Evento 6"));

        // Decommentare se si vuole ordinare la lista dall'inizializzatore
        // sort(new Comparator<Evento>() {
        // @Override
        // public int compare(Evento o1, Evento o2) {
        // if (o1.getId().equals(o2.getId()))
        // return o1.compareTo(o2);
        // return o1.getId().compareTo(o2.getId());
        // }
        // });
    }

    private void initButtons() {
        addButton.disableProperty().bind(tfDescription.textProperty().isEmpty());
        // saveFile.disableProperty().bind(Bindings.isEmpty(eventList));

        // In alternativa è possibile usare una SimpleListProperty
        SimpleListProperty spl = new SimpleListProperty(eventList);
        saveFile.disableProperty().bind(spl.emptyProperty());
        removeEvento.disableProperty().bind(
                tableView.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    void addEvent(ActionEvent event) {
        synchronized (eventList) {
            eventList.add(new Evento(datePicker.getValue(), tfDescription.getText()));
            eventList.notifyAll();
        }

        sort(new Comparator<Evento>() {
            @Override
            public int compare(Evento o1, Evento o2) {
                if (o1.getData().equals(o2.getData()))
                    return o1.compareTo(o2);
                return o1.getData().compareTo(o2.getData());
            }
        });
        tfDescription.clear();
    }

    @FXML
    void openEvent(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new ExtensionFilter("CSV files", "*.csv"));
        File file = fc.showOpenDialog(tfDescription.getScene().getWindow());

        if (file != null) {
            try (Scanner sc = new Scanner(new BufferedReader(new FileReader(file)))) {
                sc.useDelimiter("[;\n]");
                sc.useLocale(Locale.ITALY);

                eventList.clear();
                while (sc.hasNext()) {
                    String data = sc.next();
                    String description = sc.next().replaceAll("\\|", ";");

                    Evento evento = new Evento(LocalDate.parse(data), description);
                    eventList.add(evento);

                }

                sort(new Comparator<Evento>() {
                    @Override
                    public int compare(Evento o1, Evento o2) {
                        if (o1.getData().equals(o2.getData()))
                            return o1.compareTo(o2);
                        return o1.getData().compareTo(o2.getData());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void removeEvent(ActionEvent event) {
        Evento e = tableView.getSelectionModel().getSelectedItem();
        synchronized (eventList) {
            eventList.remove(e);
        }
    }

    @FXML
    void saveEvent(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new ExtensionFilter("CSV files", "*.csv"));
        File file = fc.showSaveDialog(tfDescription.getScene().getWindow());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Evento e : eventList) {
                String replace = e.getDescrizione().replaceAll(";", "|");
                bw.write(e.getData().toString() + ";" + replace + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void updateDescription(TableColumn.CellEditEvent<Evento, String> event) {
        Evento e = tableView.getSelectionModel().getSelectedItem();
        e.setDescrizione(event.getNewValue());
    }

    private void sort(Comparator<Evento> c) {
        FXCollections.sort(eventList, c);
    }

    private void loadBackupList() {
        final String PATH = "src/files/"; // To be changed

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(PATH + "listBackup.dat")))) {
            ArrayList<Evento> backupList = (ArrayList<Evento>) ois.readObject();

            eventList.addAll(backupList);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
