import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class OknoOdwolania extends JFrame{
    DBConnect db = new DBConnect();
    Connection con = null;
    ResultSet rs = null;
    Statement state = null;
    JDateChooser podanaOd = new JDateChooser();
    JDateChooser podanaDo = new JDateChooser();
    private JFormattedTextField imie;
    private JFormattedTextField nazwisko;
    private JButton zakoncz;
    private JButton kontynuuj;
    private JButton szukaj;
    private JButton odwolaj;
    private JPanel odwolanie;
    private JTable lista;
    private JButton zmien;

    public OknoOdwolania (){
        setContentPane(odwolanie);
        setTitle("Zarzadzanie rezerwacjami");
        setSize(750,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] opcje = {"Tak", "Nie"};

        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        Calendar c = Calendar.getInstance();
        podanaOd.setMinSelectableDate(date);
        podanaOd.setDate(date);
        c.setTime(date);
        c.add(Calendar.DATE,1);
        podanaDo.setMinSelectableDate(c.getTime());
        c.add(Calendar.DATE,1);
        podanaDo.setDate(c.getTime());

        kontynuuj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OknoOdwolania().setVisible(false);
                dispose();

            }
        });
        zakoncz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        szukaj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (imie.getText().equals("")){
                    JOptionPane.showMessageDialog(null,
                            "Wpisz imie",
                            "Brak danych",
                            JOptionPane.WARNING_MESSAGE);

                } else {
                    if (nazwisko.getText().equals("")) {
                        JOptionPane.showMessageDialog(null,
                                "Wpisz nazwisko",
                                "Brak danych",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        fillJList(lista);
                    }
                }
            }
        });
        zmien.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (imie.getText().equals("")){
                    JOptionPane.showMessageDialog(null,
                            "Wpisz imie",
                            "Brak danych",
                            JOptionPane.WARNING_MESSAGE);

                } else {
                    if (nazwisko.getText().equals("")){
                        JOptionPane.showMessageDialog(null,
                                "Wpisz nazwisko",
                                "Brak danych",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        if(lista.getSelectedRow()==-1){
                            JOptionPane.showMessageDialog(null,
                                    "Wybierz rezerwacje",
                                    "Nie zaznaczono rezerwacji",
                                    JOptionPane.WARNING_MESSAGE);
                        }else {
                            data(Integer.parseInt(lista.getValueAt(lista.getSelectedRow(), 0).toString()));
                            fillJList(lista);
                        }
                    }
                }
            }
        });
        odwolaj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (imie.getText().equals("")){
                    JOptionPane.showMessageDialog(null,
                            "Wpisz imie",
                            "Brak danych",
                            JOptionPane.WARNING_MESSAGE);

                } else {
                    if (nazwisko.getText().equals("")){
                        JOptionPane.showMessageDialog(null,
                                "Wpisz nazwisko",
                                "Brak danych",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        if(lista.getSelectedRow()==-1){
                            JOptionPane.showMessageDialog(null,
                                    "Wybierz rezerwacje",
                                    "Nie zaznaczono rezerwacji",
                                    JOptionPane.WARNING_MESSAGE);
                        }else {
                            int wartosc = JOptionPane.showOptionDialog(null,"Tej akcji nie mozna cofnac.\nCzy chcesz kontynuowac?","Potwierdzenie",
                                    JOptionPane.WARNING_MESSAGE,0,null,opcje,2);
                            if(wartosc==0){
                                delete(Integer.parseInt(lista.getValueAt(lista.getSelectedRow(), 0).toString()));
                            }
                            fillJList(lista);
                        }
                    }
                }
            }
        });
        podanaOd.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Calendar c = Calendar.getInstance();
                c.setTime(podanaOd.getDate());
                c.add(Calendar.DATE,1);
                if((podanaOd.getDate()).after(podanaDo.getDate()) || (podanaOd.getDate()).equals(podanaDo.getDate())){
                    podanaDo.setDate(c.getTime());
                }
                podanaDo.setMinSelectableDate(c.getTime());
            }
        });
    }

    public void fillJList (JTable suma){
        String query = "SELECT r.rezerwacja_id, p.cena, p.lozko_1os, p.lozko_2os, p.standard_id, r.poczatek, r.koniec" +
                " FROM klienci AS k JOIN rezerwacja AS r ON r.klienci_id=k.klienci_id JOIN pokoje AS p ON r.pokoje_id=p.pokoje_id" +
                " WHERE k.imie='"+imie.getText()+"' AND k.nazwisko='"+nazwisko.getText()+"';";
        String[] head = {"ID","Cena", "Lozka 1os", "Lozka 2os", "Standard", "Poczatek", "Koniec"};
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
                int ID = rs.getInt("rezerwacja_id");
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
                model.addRow(new Object[] {ID,cena,lozko1,lozko2,standard,poczatek,koniec});
            }
        }catch(SQLException e){
            System.out.println(e);
        }
    }
    public void delete (int id){
        String sql;

        try {
            con = db.conect();
            state = con.createStatement();

            sql=("DELETE r "+
            " FROM rezerwacja AS r "+
            " JOIN klienci AS k ON r.klienci_id=k.klienci_id "+
            " WHERE k.imie='"+imie.getText()+"' AND k.nazwisko='"+nazwisko.getText()+"'"+
            " AND r.rezerwacja_id="+id+";");
            state.executeUpdate(sql);

        }catch(SQLException e) {
            System.out.println(e);
        }
    }
    public void data (int id){
        String sql;
        String query="SELECT r.poczatek, r.koniec"+
                " FROM rezerwacja AS r"+
                " WHERE r.rezerwacja_id="+id+";";
        try {
            con = db.conect();
            state = con.createStatement();
            rs = state.executeQuery(query);

            while(rs.next()){
                podanaOd.setDate(rs.getDate("poczatek"));
                podanaDo.setDate(rs.getDate("koniec"));
            }

            JDateChooser [] daty ={podanaOd,podanaDo};
            JOptionPane.showConfirmDialog(null,daty,"Nowy termin", JOptionPane.PLAIN_MESSAGE);

            SimpleDateFormat forma = new SimpleDateFormat("yyyy-MM-dd");
            String dataOd = forma.format(podanaOd.getDate());
            String dataDo = forma.format(podanaDo.getDate());

            sql=("UPDATE rezerwacja "+
                    " SET poczatek='"+dataOd+"',koniec='"+dataDo+"'"+
                    " WHERE rezerwacja_id="+id+";");
            state.executeUpdate(sql);

        }catch(SQLException e) {
            System.out.println(e);
        }
    }
}
