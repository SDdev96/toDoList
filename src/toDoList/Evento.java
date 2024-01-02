package toDoList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Evento implements Comparable<Evento>, java.io.Serializable {
    private LocalDate data;
    private String descrizione;
    private static int count;
    private Integer id;

    public Evento(LocalDate data, String descrizione) {
        count++;
        this.data = data;
        this.descrizione = descrizione;
        this.id = count;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Integer getId() {
        return id;
    }

    /*
     * Per la serializzazione di un attributo statico
     * public static Integer getCount(){
     * return count;
     * }
     * 
     * public static void setCount(int count) {
     * Evento.count = count;
     * }
     */

    @Override
    public int compareTo(Evento o) {
        return id.compareTo(o.getId());
    }

    // Se non si vuole usare il Comparator nel controller
    // @Override
    // public int compareTo(Evento o) {
    // if (data.equals(o.getData()))
    // return this.getId().compareTo(o.getId());
    // return data.compareTo(o.getData());
    // }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime + ((id == 0) ? 0 : id.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final Evento other = (Evento) obj;
        return id.equals(other.getId());
    }

    @Override
    public String toString() {
        return "Evento [data=" + data + ", descrizione=" + descrizione + "]";
    }

    public static void main(String arg[]) {
        List<Evento> list = new ArrayList<>();

        list.add(new Evento(null, "Evento 1"));
        list.add(new Evento(null, "Evento 2"));
        list.add(new Evento(null, "Evento 3"));
        list.add(new Evento(null, "Evento 4"));
        list.add(new Evento(null, "Evento 5"));
        list.add(new Evento(null, "Evento 6"));
        list.add(new Evento(null, "Evento 7"));
        list.add(new Evento(null, "Evento 8"));
        list.add(new Evento(null, "Evento 9"));
        list.add(new Evento(null, "Evento 10"));

        for (Evento e : list)
            System.out.println(e);
    }
}
