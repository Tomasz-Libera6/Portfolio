import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Potwierdzenie extends  JFrame{
    DBConnect db = new DBConnect();
    Connection con = null;
    ResultSet rs = null;
    Statement state = null;
    private JButton kont;
    private JTable suma;
    private JPanel potwierdz;
    private JFormattedTextField cenaKoniec;
    private JButton zakoncz;


    public Potwierdzenie (){
        setContentPane(potwierdz);
        setTitle("Potwierdzenie rezerwacji");
        setSize(750,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        fillJList(suma,Main.licznik);



        kont.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Potwierdzenie().setVisible(false);
                dispose();
            }
        });
        zakoncz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public void fillJList (JTable suma,int licznik){
        String query = "SELECT k.imie, k.nazwisko, p.cena, p.lozko_1os, p.lozko_2os, p.standard_id, r.poczatek, r.koniec" +
                " FROM klienci AS k JOIN rezerwacja AS r ON r.klienci_id=k.klienci_id JOIN pokoje AS p ON r.pokoje_id=p.pokoje_id " +
                "ORDER BY r.rezerwacja_id DESC LIMIT "+licznik+";";
        String[] head = {"Imie","Nazwisko","Cena", "Lozka 1os", "Lozka 2os", "Standard", "Poczatek", "Koniec"};
        String standard = "";
        try{
            DefaultTableModel model = new DefaultTableModel(null,head);
            suma.setModel(model);
            suma.setAutoCreateRowSorter(true);
            JTableHeader header = suma.getTableHeader();
            header.setBackground(Color.CYAN);
            con = db.conect();
            state = con.createStatement();
            rs = state.executeQuery(query);
            int cenaK = 0;
            while(rs.next()) {
                String imie = rs.getString("imie");
                String nazwisko = rs.getString("nazwisko");
                int cena = rs.getInt("cena");
                int lozko1 = rs.getInt("lozko_1os");
                int lozko2 = rs.getInt("lozko_2os");
                int standard_id = rs.getInt("standard_id");
                switch (standard_id){
                    case 1:
                        standard="---";
                        break;
                    case 2:
                        standard="T";
                        break;
                    case 3:
                        standard="B";
                        break;
                    case 4:
                        standard="BT";
                        break;
                    case 5:
                        standard="KT";
                        break;
                    case 6:
                        standard="KBT";
                        break;
                    default:
                        break;
                }
                Date poczatek = rs.getDate("poczatek");
                Date koniec = rs.getDate("koniec");

                long diff = TimeUnit.DAYS.convert(koniec.getTime() - poczatek.getTime(), TimeUnit.MILLISECONDS);
                cenaK+=(int)diff * cena;


                model = (DefaultTableModel) suma.getModel();
                model.addRow(new Object[] {imie,nazwisko,cena,lozko1,lozko2,standard,poczatek,koniec});
                cenaKoniec.setValue(cenaK);
            }
        }catch(SQLException e){
            System.out.println(e);
        }

        Main.licznik = 0;
    }
}