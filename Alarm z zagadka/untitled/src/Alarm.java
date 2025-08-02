//Projekt wykonany przez:
// importuje różne klasy potrzebne do GUI, obsługi czasu, dźwięku i plików
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.util.Calendar;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

// Główna klasa aplikacji Alarm
public class Alarm {

    /* ---------- POLA ---------- */

    // Komponenty GUI
    JFrame okno;
    JComboBox<String> godzinyBox, minutyBox, poziomBox, listaAlarmow,dniBox;
    JCheckBox codziennieBox;
    JLabel statusLabel, pozostaloLabel;
    JSlider suwakGlosnosci;



    Timer timer;    // Timer do sprawdzania czasu co sekundę
    Clip dzwiek;    // Obiekt dźwięku alarmu


    // Dane alarmu
    int godzinaAlarmu = -1,minutaAlarmu = -1,dzienAlarmu   = -1;//// -1 = brak alarmu,zeruje
    float aktualnaGlosnosc = 1.0f;


    // Flagi stanu
    boolean alarmAktywny = false;
    boolean drzemkaUzyta = false;
    boolean zagadkaWTrakcie = false;
    boolean alarmZDrzemki = false;


    //KONSTRUKTOR – tworzy GUI i podpina akcje
    public Alarm() {
        zbudujGUI();
        wczytajAlarmyDoListy();

        /* timer odświeża co sekundę */
        timer = new Timer(1000, e -> sprawdzCzas());
        timer.start();
    }

    /* ---------- budowanie interfejsu ---------- */
    private void zbudujGUI() {
        okno = new JFrame("Mój Alarm");
        okno.setSize(450, 600);
        okno.setLayout(new GridLayout(12,2,10,10));
        okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* listy wyboru czasu i poziomu */
        godzinyBox = new JComboBox<>();
        for(int i=0;i<24;i++) godzinyBox.addItem(String.valueOf(i));

        minutyBox = new JComboBox<>();
        for(int i=0;i<60;i++) minutyBox.addItem(String.format("%02d",i));

        poziomBox = new JComboBox<>(new String[]{"dziecko","dorosly"});

        dniBox = new JComboBox<>(new String[]{
                "Poniedziałek","Wtorek","Środa","Czwartek",
                "Piątek","Sobota","Niedziela"
        });


        codziennieBox = new JCheckBox("Codziennie");

        /* przycisk ustaw */
        JButton ustawBtn = new JButton("Ustaw alarm");
        ustawBtn.addActionListener(e -> ustawAlarm());

        /* lista zapisanych alarmów + przyciski */
        listaAlarmow = new JComboBox<>();

        JButton wczytajBtn = new JButton("Wczytaj zaznaczony");
        wczytajBtn.addActionListener(e -> {
            String l=(String)listaAlarmow.getSelectedItem();
            if(l!=null) wczytajWybranyAlarm(l);
        });

        JButton usunBtn = new JButton("Usuń zaznaczony");
        usunBtn.addActionListener(e -> usunZaznaczonyAlarm());

        /* etykiety stanu */
        statusLabel    = new JLabel("Ustaw alarm");
        pozostaloLabel = new JLabel("Pozostało: --");

        /* suwak głośności */
        suwakGlosnosci = new JSlider(0,100,100);
        suwakGlosnosci.setMajorTickSpacing(20);
        suwakGlosnosci.setPaintTicks(true);
        suwakGlosnosci.setPaintLabels(true);
        aktualnaGlosnosc = wczytajGlosnosc();
        suwakGlosnosci.setValue((int)(aktualnaGlosnosc*100));
        suwakGlosnosci.addChangeListener(e -> {
            aktualnaGlosnosc = suwakGlosnosci.getValue()/100f;
            ustawGlosnosc(aktualnaGlosnosc);
            zapiszGlosnosc(aktualnaGlosnosc);
        });

        /* tryb nocny */
        JButton nocnyBtn = new JButton("Tryb nocny");
        nocnyBtn.addActionListener(new ActionListener() {
            boolean nocny=false;
            public void actionPerformed(ActionEvent e){
                nocny=!nocny;
                Color tlo=nocny?Color.BLACK:Color.WHITE;
                Color f  =nocny?Color.WHITE:Color.BLACK;
                okno.getContentPane().setBackground(tlo);
                for(Component c: okno.getContentPane().getComponents()){
                    c.setBackground(tlo); c.setForeground(f);
                    if(c instanceof JSlider)((JSlider)c).setOpaque(true);
                }
            }
        });

        /* układ komponentów */
        okno.add(new JLabel("Godzina:"));            okno.add(godzinyBox);
        okno.add(new JLabel("Minuta:"));             okno.add(minutyBox);
        okno.add(new JLabel("Poziom:"));             okno.add(poziomBox);
        okno.add(new JLabel("Dzień tygodnia:"));     okno.add(dniBox);
        okno.add(new JLabel("")); okno.add(codziennieBox);
        okno.add(new JLabel(""));                    okno.add(ustawBtn);
        okno.add(new JLabel("Status:"));             okno.add(statusLabel);
        okno.add(new JLabel("Odliczanie:"));         okno.add(pozostaloLabel);
        okno.add(new JLabel("Głośność:"));           okno.add(suwakGlosnosci);
        okno.add(new JLabel("Zapisane alarmy:"));    okno.add(listaAlarmow);
        okno.add(wczytajBtn);                        okno.add(usunBtn);
        okno.add(new JLabel(""));                    okno.add(nocnyBtn);

        okno.setVisible(true);
    }
/* =========================================================
       GŁÓWNA LOGIKA
========================================================= */

    // Ustawia nowy alarm na podstawie danych z formularza
    private void ustawAlarm(){
        godzinaAlarmu = Integer.parseInt(godzinyBox.getSelectedItem().toString());// Pobranie wartości z GUI i przypisanie do zmiennych
        minutaAlarmu  = Integer.parseInt(minutyBox.getSelectedItem().toString());
        String dStr   = dniBox.getSelectedItem().toString();
        dzienAlarmu   = mapujDzien(dStr);

        statusLabel.setText("Ustawiono na "+godzinaAlarmu+":"+
                String.format("%02d",minutaAlarmu)+" ("+dStr+")");

        String codziennyTag = codziennieBox.isSelected() ? "codziennie" : dniBox.getSelectedItem().toString();
        zapiszAlarmDoListy(godzinaAlarmu, minutaAlarmu, poziomBox.getSelectedItem().toString(), codziennyTag);
        if (!codziennieBox.isSelected()) {
            dzienAlarmu = mapujDzien(dStr);
            statusLabel.setText("Ustawiono na " + godzinaAlarmu + ":" +
                    String.format("%02d", minutaAlarmu) + " (" + dStr + ")");
        } else {
            dzienAlarmu = -2; // specjalna flaga na codziennie
            statusLabel.setText("Ustawiono codziennie na " + godzinaAlarmu + ":" +
                    String.format("%02d", minutaAlarmu));
        }

        alarmAktywny=false; zagadkaWTrakcie=false; drzemkaUzyta=false; alarmZDrzemki=false;
    }

    // // Sprawdza czas co sekundę – jeśli czas pasuje do ustawionego alarmu, uruchamia alarm
    private void sprawdzCzas() {
        aktualizujOdliczanie();

        Calendar teraz = Calendar.getInstance();
        int dzien = teraz.get(Calendar.DAY_OF_WEEK);
        int godz  = teraz.get(Calendar.HOUR_OF_DAY);
        int min   = teraz.get(Calendar.MINUTE);

        if (alarmAktywny) return;                 // już gra

        if (!codziennieBox.isSelected()) {        // alarm tylko w wybrany dzień
            if (dzienAlarmu < 0 || dzien != dzienAlarmu) return;
        }

        if (godz == godzinaAlarmu && min == minutaAlarmu) {
            alarmAktywny = true;
            zagadkaWTrakcie = true;
            uruchomAlarm();
        }
    }


    // Aktualizuje etykietę z informacją ile pozostało do alarmu
    private void aktualizujOdliczanie() {
        if (godzinaAlarmu < 0 || minutaAlarmu < 0) {
            pozostaloLabel.setText("Alarm nie ustawiony");
            return;
        }

        Calendar teraz = Calendar.getInstance();
        Calendar cel   = (Calendar) teraz.clone();
        cel.set(Calendar.HOUR_OF_DAY, godzinaAlarmu);
        cel.set(Calendar.MINUTE,      minutaAlarmu);
        cel.set(Calendar.SECOND, 0);  cel.set(Calendar.MILLISECOND, 0);

        if (codziennieBox.isSelected()) {
            if (cel.before(teraz)) cel.add(Calendar.DAY_OF_YEAR, 1);
        } else {
            if (dzienAlarmu < 0) { pozostaloLabel.setText("Alarm nie ustawiony"); return; }
            int diffDay = (dzienAlarmu - teraz.get(Calendar.DAY_OF_WEEK) + 7) % 7;
            if (diffDay == 0 && cel.before(teraz)) diffDay = 7;
            cel.add(Calendar.DAY_OF_YEAR, diffDay);
        }

        long diff = cel.getTimeInMillis() - teraz.getTimeInMillis();
        long s = diff / 1000;
        long d = s / 86400;  s %= 86400;
        long h = s / 3600;   s %= 3600;
        long m = s / 60;     s %= 60;

        String txt = (d > 0 ? d + " d " : "") + h + " h " + m + " m " + s + " s";
        pozostaloLabel.setText("Pozostało: " + txt);
    }


    // Zamienia nazwę dnia tygodnia na odpowiadający numer (wg Calendar)
    private int mapujDzien(String d){
        switch(d){
            case "Poniedziałek": return Calendar.MONDAY;
            case "Wtorek":       return Calendar.TUESDAY;
            case "Środa":        return Calendar.WEDNESDAY;
            case "Czwartek":     return Calendar.THURSDAY;
            case "Piątek":       return Calendar.FRIDAY;
            case "Sobota":       return Calendar.SATURDAY;
            default:             return Calendar.SUNDAY;
        }
    }


    // Uruchamia alarm (dźwięk + zagadka)
    private void uruchomAlarm() {
        grajDzwiek();
        SwingUtilities.invokeLater(() -> zadanieZagadki());
    }


    // Odtwarza dźwięk alarmu w pętli
    private void grajDzwiek(){
        // Ładuje plik WAV, ustawia głośność, włącza zapętlone odtwarzanie
        try{
            File f=new File("alarm.wav");
            if(!f.exists()){JOptionPane.showMessageDialog(okno,"Brakuje alarm.wav");return;}
            try(AudioInputStream in=AudioSystem.getAudioInputStream(f)){
                dzwiek=AudioSystem.getClip();
                dzwiek.open(in);
                ustawGlosnosc(aktualnaGlosnosc);
                dzwiek.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }catch(Exception ex){System.out.println("Błąd dźwięku: "+ex);}
    }


    // Pokazuje zagadkę do rozwiązania przez użytkownika – blokuje zamknięcie okna
    private void zadanieZagadki() {
        String poziom = poziomBox.getSelectedItem().toString();
        List<String[]> zagadki = wczytajZagadkiZPliku(poziom + ".txt");     // Wczytuje pytania z pliku .txt (dziecko.txt lub dorosly.txt)

        if (zagadki.isEmpty()) {
            JOptionPane.showMessageDialog(okno, "Brak zagadek!");
            zatrzymajAlarm();
            return;
        }

        Random rand = new Random();
        final int[] index = {rand.nextInt(zagadki.size())};

        JTextField odpowiedzField = new JTextField(20);
        JButton okButton = new JButton("OK");
        JButton drzemkaButton = new JButton("Drzemka");

        JDialog dialog = new JDialog(okno, "Zagadka", true);
        dialog.setLayout(new BorderLayout());
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JOptionPane.showMessageDialog(dialog,
                        "Najpierw rozwiąż zagadkę albo użyj Drzemki!");
            }
        });

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        JLabel pytanieLabel = new JLabel(zagadki.get(index[0])[0]);
        centerPanel.add(pytanieLabel, BorderLayout.NORTH);
        centerPanel.add(odpowiedzField, BorderLayout.CENTER);
        dialog.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(drzemkaButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> {
            String userOdp = odpowiedzField.getText();
            String poprawnaOdp = zagadki.get(index[0])[1];
            if (userOdp.trim().equalsIgnoreCase(poprawnaOdp.trim())) {
                JOptionPane.showMessageDialog(dialog, "Dobra odpowiedź!");
                zatrzymajAlarm();
                statusLabel.setText("Alarm wyłączony.");
                dialog.dispose();
            } else {
                index[0] = rand.nextInt(zagadki.size());
                pytanieLabel.setText(zagadki.get(index[0])[0]);
                odpowiedzField.setText("");
                JOptionPane.showMessageDialog(dialog, "Zła odpowiedź. Spróbuj ponownie.");
            }
        });
        // Obsługuje odpowiedź użytkownika oraz możliwość "drzemki"
        drzemkaButton.addActionListener(e -> {
            if (!alarmAktywny || drzemkaUzyta) {
                JOptionPane.showMessageDialog(dialog, "Drzemka niedostępna.");
            } else {
                drzemkaUzyta = true;
                alarmZDrzemki = true;
                ustawDrzemke();
                zatrzymajAlarm();
                dialog.dispose();
            }
        });

        dialog.setSize(300, 120);
        dialog.setLocationRelativeTo(okno);
        dialog.setVisible(true);
    }


    // Wczytuje zagadki z podanego pliku tekstowego
    private List<String[]> wczytajZagadkiZPliku(String nazwaPliku) {
        // Czyta linia po linii, dzieli po ";", tworzy listę zagadek
        List<String[]> lista = new ArrayList<>();
        String sciezka = "" + nazwaPliku;
        try (BufferedReader czytnik = new BufferedReader(new FileReader(sciezka))) {
            String linia;
            while ((linia = czytnik.readLine()) != null) {
                String[] czesci = linia.split(";");
                if (czesci.length == 2) {
                    lista.add(czesci);
                }
            }
        } catch (IOException e) {
            System.out.println("Błąd czytania pliku: " + e.getMessage());
        }
        return lista;
    }


    // Zatrzymuje dźwięk i resetuje stan alarmu
    private void zatrzymajAlarm() {
        if (dzwiek != null) {
            dzwiek.stop();
            dzwiek.close();
        }
        alarmAktywny = false;
        zagadkaWTrakcie = false;

        if (!alarmZDrzemki) {
            godzinaAlarmu = -1;
            minutaAlarmu = -1;
        }
        alarmZDrzemki = false;
    }


    // Ustawia alarm o 5 minut później (drzemka)
    private void ustawDrzemke() {
        minutaAlarmu += 5;
        if (minutaAlarmu >= 60) {
            minutaAlarmu -= 60;
            godzinaAlarmu = (godzinaAlarmu + 1) % 24;
        }
        statusLabel.setText("Drzemka - nowy alarm o " + godzinaAlarmu + ":" + String.format("%02d", minutaAlarmu));
    }


    // Ustawia głośność dźwięku (jeśli możliwe)
    private void ustawGlosnosc(float poziom) {
        if (dzwiek != null && dzwiek.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) dzwiek.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainControl.getMinimum();
            float gain = (poziom == 0.0f) ? min : Math.max((float) (Math.log10(poziom) * 20.0), min);
            gainControl.setValue(gain);
        }
    }


    // Zapisuje aktualną głośność do pliku(glosnosc.txt)
    private void zapiszGlosnosc(float poziom) {
        try (PrintWriter writer = new PrintWriter("glosnosc.txt")) {
            writer.println(poziom);
        } catch (IOException e) {
            System.out.println("Błąd zapisu głośności: " + e.getMessage());
        }
    }


    // Wczytuje głośność z pliku
    private float wczytajGlosnosc() {
        File plik = new File("glosnosc.txt");
        if (plik.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(plik))) {
                return Float.parseFloat(reader.readLine());
            } catch (Exception e) {
                System.out.println("Błąd odczytu głośności: " + e.getMessage());
            }
        }
        return 1.0f;
    }



    // Zapisuje alarm do pliku(alarmy.txt) i dodaje do listy rozwijanej, jeśli jeszcze nie ma
    private void zapiszAlarmDoListy(int godzina, int minuta, String poziom, String dzien) {
        String wpis = godzina + ":" + String.format("%02d", minuta) + ", " + poziom + ", " + dzien;
        for (int i = 0; i < listaAlarmow.getItemCount(); i++) {
            if (listaAlarmow.getItemAt(i).equals(wpis)) return;
        }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("alarmy.txt", true)))) {
            out.println(wpis);
            listaAlarmow.addItem(wpis);
        } catch (IOException e) {
            System.out.println("Błąd zapisu alarmu: " + e.getMessage());
        }
    }


    // Wczytuje zapisane alarmy do listy rozwijanej z pliku
    private void wczytajAlarmyDoListy() {
        File plik = new File("alarmy.txt");
        if (plik.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(plik))) {
                String linia;
                while ((linia = reader.readLine()) != null) {
                    listaAlarmow.addItem(linia);
                }
            } catch (IOException e) {
                System.out.println("Błąd wczytania listy alarmów: " + e.getMessage());
            }
        }
    }


    // Usuwa zaznaczony alarm z listy i pliku
    private void usunZaznaczonyAlarm() {
        String zaznaczony = (String) listaAlarmow.getSelectedItem();
        if (zaznaczony == null) return;

        List<String> noweLinie = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("alarmy.txt"))) {
            String linia;
            while ((linia = reader.readLine()) != null) {
                if (!linia.trim().equals(zaznaczony.trim())) {
                    noweLinie.add(linia);
                }
            }
        } catch (IOException e) {
            System.out.println("Błąd odczytu: " + e.getMessage());
            return;
        }

        try (PrintWriter writer = new PrintWriter("alarmy.txt")) {
            for (String l : noweLinie) {
                writer.println(l);
            }
        } catch (IOException e) {
            System.out.println("Błąd zapisu: " + e.getMessage());
        }

        listaAlarmow.removeItem(zaznaczony);
        statusLabel.setText("Usunięto alarm");
    }



    // Wczytuje zaznaczony alarm z listy i ustawia go jako aktywny
    private void wczytajWybranyAlarm(String linia) {
        try {
            String[] czesci = linia.split(",");
            String czas = czesci[0].trim();
            String poziom = czesci[1].trim();
            String dzien = czesci.length >= 3 ? czesci[2].trim() : "Poniedziałek";
            dniBox.setSelectedItem(dzien);
            String[] godzMin = czas.split(":");
            godzinaAlarmu = Integer.parseInt(godzMin[0]);
            minutaAlarmu = Integer.parseInt(godzMin[1]);
            godzinyBox.setSelectedItem(String.valueOf(godzinaAlarmu));
            minutyBox.setSelectedItem(String.format("%02d", minutaAlarmu));
            poziomBox.setSelectedItem(poziom);
            statusLabel.setText("Wybrano alarm " + godzinaAlarmu + ":" + String.format("%02d", minutaAlarmu));
        } catch (Exception e) {
            System.out.println("Błąd wczytywania alarmu: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Alarm();
    }
}
