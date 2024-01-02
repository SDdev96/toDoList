package toDoList;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

public class TimedSaving implements Runnable, java.io.Serializable {
    private final static String PATH = "src/files/"; // To be changed
    private ObservableList<Evento> eventi;

    public TimedSaving(ObservableList<Evento> eventi) {
        this.eventi = eventi;
    }

    public List<Evento> getEventi() {
        return eventi;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                salvaFile();
                System.out.println("BACKUP EFFETTUATO");
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void salvaFile() {
        synchronized (eventi) {
            while (eventi.isEmpty()) {
                try {
                    eventi.wait();
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    System.err.println("InterrutpedException attivata");
                }
            }
        }

        try (final ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(PATH + "listBackup.dat")))) {
            oos.writeObject(new ArrayList<Evento>(eventi));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
