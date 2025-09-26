import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.toedter.calendar.JDateChooser;
public class OknoRezerwacji extends  JFrame{
    DBConnect db = new DBConnect();
    Connection con = null;
    ResultSet rs = null;
    Statement state = null;
    JDateChooser podanaOd = new JDateChooser();
    JDateChooser podanaDo = new JDateChooser();
    private JPanel rezerwa;
    private JButton rezerwuj;
    private JFormattedTextField imie;
    private JFormattedTextField nazwisko;
    private JPanel odData;
    private JPanel doData;
    private JButton dodaj;
    private JCheckBox kuchnia;
    private JButton reset;
    private JButton filtruj;
    private JSpinner odCena;
    private JCheckBox balkon;
    private JCheckBox telewizor;
    private JComboBox lozka;
    private JSpinner doCena;
    private JButton cofnij;
    private JTable lista;
    private JButton odwolanie;

    public OknoRezerwacji (){
        setContentPane(rezerwa);
        setTitle("Rezerwacja hotelowa");
        setSize(600,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        odData.add(podanaOd);
        doData.add(podanaDo);

        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        Calendar c = Calendar.getInstance();
        podanaOd.setMinSelectableDate(date);
        podanaOd.setDate(date);
        c.setTime(date);
        c.add(Calendar.DATE,1);
        podanaDo.setMinSelectableDate(c.getTime());
        c.add(Calendar.DATE,1);
        podanaDo.setDate(c.getTime());


        odCena.setValue(70);
        doCena.setValue(270);
        fillJList(lista);



        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imie.setValue("");
                nazwisko.setValue("");
                odCena.setValue(70);
                doCena.setValue(270);
                lozka.setSelectedIndex(0);
                kuchnia.setSelected(false);
                balkon.setSelected(false);
                telewizor.setSelected(false);

                podanaOd.setDate(date);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DATE,2);
                podanaDo.setDate(c.getTime());
                fillJList(lista);
            }
        });

        odCena.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if((int)doCena.getValue()< (int)odCena.getValue()){
                    doCena.setValue((int)odCena.getValue());
                }
            }
        });
        doCena.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if((int)doCena.getValue()< (int)odCena.getValue()){
                    odCena.setValue((int)doCena.getValue());
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
        filtruj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.extra=" AND cena >= "+odCena.getValue()+" AND cena <= "+ doCena.getValue();
                if(kuchnia.isSelected()){
                    Main.extra+=" AND kuchnia=1";
                }
                if(balkon.isSelected()){
                    Main.extra+=" AND balkon=1";
                }
                if(telewizor.isSelected()){
                    Main.extra+=" AND telewizor=1";
                }
                switch(lozka.getSelectedIndex()) {
                    case 1:
                        Main.extra+=" AND lozko_1os=1 AND lozko_2os=0";
                        break;
                    case 2:
                        Main.extra+=" AND lozko_1os=2 AND lozko_2os=0";
                        break;
                    case 3:
                        Main.extra+=" AND lozko_1os=3 AND lozko_2os=0";
                        break;
                    case 4:
                        Main.extra+=" AND lozko_1os=4 AND lozko_2os=0";
                        break;
                    case 5:
                        Main.extra+=" AND lozko_1os=0 AND lozko_2os=1";
                        break;
                    case 6:
                        Main.extra+=" AND lozko_1os=0 AND lozko_2os=2";
                        break;
                    case 7:
                        Main.extra+=" AND lozko_1os=1 AND lozko_2os=1";
                        break;
                    case 8:
                        Main.extra+=" AND lozko_1os=2 AND lozko_2os=1";
                        break;
                    default:
                        break;
                }

                fillJList(lista);
            }
        });
        rezerwuj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Main.licznik==0){
                    JOptionPane.showMessageDialog(null,
                            "Brak wybranych pokoi do rezerwacji",
                            "Brak danych",
                            JOptionPane.WARNING_MESSAGE);
                } else {

                    new Potwierdzenie().setVisible(true);

                }
            }
        });
        odwolanie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    new OknoOdwolania().setVisible(true);
            }
        });
        dodaj.addActionListener(new ActionListener() {
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
                                    "Wybierz pokoj",
                                    "Nie zaznaczono pokoju",
                                    JOptionPane.WARNING_MESSAGE);
                        }else {
                            Main.licznik++;
                            insert(Integer.parseInt(lista.getValueAt(lista.getSelectedRow(), 0).toString()));
                            fillJList(lista);
                        }
                    }
                }

            }
        });
        cofnij.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Main.licznik>0){
                    Main.licznik--;
                    usuwanie();
                    fillJList(lista);
                }
            }
        });
    }
    public void fillJList (JTable lista){
        SimpleDateFormat forma = new SimpleDateFormat("yyyyMMdd");
        String dataOd = forma.format(podanaOd.getDate());
        String dataDo = forma.format(podanaDo.getDate());
        String query = "SELECT p.pokoje_id, p.cena, p.lozko_1os, p.lozko_2os, s.kuchnia, s.balkon, s.telewizor" +
                " FROM pokoje AS p JOIN standard AS s ON s.standard_id=p.standard_id "+
                " LEFT JOIN rezerwacja AS r ON r.pokoje_id=p.pokoje_id"+
                " WHERE (('"+dataOd+"'<poczatek AND '"+dataDo+"'<poczatek)" +
                " OR ('"+dataOd+"'>koniec AND '"+dataDo+"'>koniec)" +
                " OR r.pokoje_id IS NULL)" +  Main.extra + ";";
        String[] head = {"ID","Cena", "Lozka 1os", "Lozka 2os", "Kuchnia", "Balkon", "Telewizor"};
        try{
            DefaultTableModel model = new DefaultTableModel(null,head);
            lista.setModel(model);
            lista.setAutoCreateRowSorter(true);
            JTableHeader header = lista.getTableHeader();
            header.setBackground(Color.CYAN);
            con = db.conect();
            state = con.createStatement();
            rs = state.executeQuery(query);
            while(rs.next()) {
                String kuchniaStr,balkonStr,telewizorStr;
                int ID = rs.getInt("pokoje_id");
                int cena = rs.getInt("cena");
                int lozko1 = rs.getInt("lozko_1os");
                int lozko2 = rs.getInt("lozko_2os");
                int kuchniaInt = rs.getInt("kuchnia");
                if(kuchniaInt == 0){
                    kuchniaStr="---";
                } else {
                    kuchniaStr="Tak";
                }
                int balkonInt = rs.getInt("balkon");
                if (balkonInt==0){
                    balkonStr="---";
                }else{
                    balkonStr="Tak";
                }
                int telewizorInt = rs.getInt("telewizor");
                if(telewizorInt==0){
                    telewizorStr="---";
                }else{
                    telewizorStr="Tak";
                }

                model = (DefaultTableModel) lista.getModel();
                model.addRow(new Object[] {ID,cena,lozko1,lozko2,kuchniaStr,balkonStr,telewizorStr});
            }
        }catch(SQLException e){
            System.out.println(e);
        }
    }

    public void insert (int id){
        SimpleDateFormat forma = new SimpleDateFormat("yyyy-MM-dd");
        String dataOd = forma.format(podanaOd.getDate());
        String dataDo = forma.format(podanaDo.getDate());
        String sql;

        try {
            con = db.conect();
            state = con.createStatement();


            sql=("INSERT INTO klienci (klienci_id, imie ,nazwisko)" +
                    " SELECT NULL,'"+imie.getText()+"', '"+nazwisko.getText()+"'"+
                    " WHERE NOT EXISTS (SELECT 1 FROM klienci WHERE imie='"+imie.getText()+"' AND nazwisko='"+nazwisko.getText()+"') ");
            state.executeUpdate(sql);

            sql=("INSERT INTO rezerwacja (rezerwacja_id,klienci_id,pokoje_id,poczatek,koniec)" +
                    " SELECT NULL, k.klienci_id, " + id + ", '"+dataOd+"', '"+dataDo+"'" +
                    " FROM klienci AS k" +
                    " WHERE imie='"+imie.getText()+"' AND nazwisko='"+nazwisko.getText()+"';");
            state.executeUpdate(sql);
        }catch(SQLException e) {
            System.out.println(e);
        }
    }

    public void usuwanie (){
        String sql;
        try {
            con = db.conect();
            state = con.createStatement();

            sql=("DELETE FROM rezerwacja ORDER BY rezerwacja_id DESC limit 1");
            state.executeUpdate(sql);
        }catch(SQLException e) {
            System.out.println(e);
        }
    }
}