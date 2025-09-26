import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.*;
import static java.lang.Math.abs;

public class Main {
    public static JTextField [] cell = new JTextField[100];
    public static void main(String[] args) {
        for(int i = 0;i<100;i++) {
            cell[i] = new JTextField();
        }
        JFrame frame = new JFrame("Kwadraty magiczne");
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JPanel panel1 = new JPanel(new FlowLayout());
        JPanel panel2 = new JPanel(new GridLayout(5, 5));
        JPanel panel3 = new JPanel(new GridLayout(2, 1));
        JPanel panel4 = new JPanel(new GridLayout(2, 1));
        JPanel panel5 = new JPanel(new GridLayout(2, 1));

        JSlider suwak1 = new JSlider(JSlider.HORIZONTAL, 9, 99, 9);
        suwak1.setPaintTrack(true);
        suwak1.setPaintTicks(true);
        suwak1.setPaintLabels(true);
        suwak1.setMajorTickSpacing(30);
        suwak1.setMinorTickSpacing(10);
        JLabel label_suwak1 = new JLabel();
        label_suwak1.setText("Zakres od 0 do " + suwak1.getValue());

        suwak1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                label_suwak1.setText("Zakres od 0 do " + suwak1.getValue());
            }
        });

        JSlider suwak2 = new JSlider(JSlider.HORIZONTAL, 2, 10, 5);
        suwak2.setPaintTrack(true);
        suwak2.setPaintTicks(true);
        suwak2.setPaintLabels(true);
        suwak2.setMajorTickSpacing(1);
        JLabel label_suwak2 = new JLabel();
        label_suwak2.setText("Kwadrat " + suwak2.getValue() + "x" + suwak2.getValue());

        suwak2.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                label_suwak2.setText("Kwadrat " + suwak2.getValue() + "x" + suwak2.getValue());
                for (int i = 0; i < 100; i++) {
                    cell[i].setText("");
                }
                panel2.setLayout(new GridLayout(suwak2.getValue(), suwak2.getValue()));
                komorki(panel2, suwak2.getValue(), cell);
            }
        });

        JButton button1 = new JButton();
        button1.setText("Start");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                selekcja(suwak2.getValue(), cell,suwak1.getValue());
            }
        });

        JButton button2 = new JButton();
        button2.setText("Reset");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 100; i++) {
                    cell[i].setText("");
                }
                suwak1.setValue(9);
                suwak2.setValue(5);
            }
        });

        panel3.add(button1);
        panel3.add(button2);
        panel4.add(label_suwak1);
        panel4.add(suwak1);
        panel5.add(label_suwak2);
        panel5.add(suwak2);
        panel1.add(panel3);
        panel1.add(panel4);
        panel1.add(panel5);
        panel.add(panel1);
        panel.add(panel2);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        komorki(panel2,suwak2.getValue(),cell);
    }
    public static void komorki(JPanel panel, int x, JTextField [] cell) {
        panel.removeAll();
        for (int i = 0; i < x*x; i++) {
                panel.add(cell[i]);
        }
    }
    public static void poczatek (int x, int [] tab, int max) {
        for (int i = 0; i < x*x; i++) {
            tab[i] = (int)(Math.random()*(max+1));
        }
    }
    public static void nowe (int x, JTextField [] cell, int [] tab){
        Font czcionka = new Font("SansSerif", Font.BOLD, 20);
        for (int i = 0; i < x*x; i++) {
            cell[i].setText(""+tab[i]);
            cell[i].setFont(czcionka);
            cell[i].setHorizontalAlignment(JTextField.CENTER);
        }
    }

    protected static void selekcja (int x, JTextField [] cell, int max) {
        ArrayList<int[]> list = new ArrayList<int[]>();
        int[] tab1 = new int[100];
        int[] tab2 = new int[100];
        int[] tab3 = new int[100];
        int[] tab4 = new int[100];
        int[] tab5 = new int[100];
        int[] tab6 = new int[100];
        int[] tab7 = new int[100];
        int[] tab8 = new int[100];
        int[] temp1 = new int[100];
        int[] temp2 = new int[100];
        int[] temp3 = new int[100];
        int[] temp4 = new int[100];
        list.add(tab1);
        list.add(tab2);
        list.add(tab3);
        list.add(tab4);
        list.add(tab5);
        list.add(tab6);
        list.add(tab7);
        list.add(tab8);
        int i,j=0,k=0, a, b, c, d, e, f, g, h,dane=0,win=0,start=0;

        //while (dane<500){
        while (dane==0){
            i=0;
            k=0;
            j++;

            poczatek(x, list.get(0), max);
            poczatek(x, list.get(1), max);
            poczatek(x, list.get(2), max);
            poczatek(x, list.get(3), max);
            poczatek(x, list.get(4), max);
            poczatek(x, list.get(5), max);
            poczatek(x, list.get(6), max);
            poczatek(x, list.get(7), max);

            while (koszt(x, list.get(0)) !=0 &&  i<max*x*20) {
                a = (int) (Math.random() * (8));
                do {
                    b = (int) (Math.random() * (8));
                } while (a == b);
                do {
                    c = (int) (Math.random() * (8));
                } while (a == c || b == c);
                do {
                    d = (int) (Math.random() * (8));
                } while (a == d || b == d || c == d);
                do {
                    e = (int) (Math.random() * (8));
                } while (a == e || b == e || c == e || d == e);
                do {
                    f = (int) (Math.random() * (8));
                } while (a == f || b == f || c == f || d == f || e == f);
                do {
                    g = (int) (Math.random() * (8));
                } while (a == g || b == g || c == g || d == g || e == g || f == g);
                do {
                    h = (int) (Math.random() * (8));
                } while (a == h || b == h || c == h || d == h || e == h || f == h || g == h);

                turniej(x, list.get(a), list.get(b));
                turniej(x, list.get(c), list.get(d));
                turniej(x, list.get(e), list.get(f));
                turniej(x, list.get(g), list.get(h));

                transport(x, temp1, list.get(a));
                transport(x, temp2, list.get(c));
                transport(x, temp3, list.get(e));
                transport(x, temp4, list.get(g));
                transport(x, list.get(0), temp1);
                transport(x, list.get(1), temp2);
                transport(x, list.get(2), temp3);
                transport(x, list.get(3), temp4);

                a = (int) (Math.random() * (4));
                do {
                    b = (int) (Math.random() * (4));
                } while (a == b);
                do {
                    c = (int) (Math.random() * (4));
                } while (a == c || b == c);
                do {
                    d = (int) (Math.random() * (4));
                } while (a == d || b == d || c == d);

                turniej(x, list.get(a), list.get(b));
                turniej(x, list.get(c), list.get(d));

                transport(x, temp1, list.get(a));
                transport(x, temp2, list.get(c));
                transport(x, list.get(0), temp1);
                transport(x, list.get(1), temp2);

                dziecko(x, list.get(0), list.get(1), list.get(2), i);
                dziecko(x, list.get(1), list.get(0), list.get(3), i);
                turniej(x, list.get(0), list.get(1));
                if(k==0){
                    start=koszt(x, list.get(0));
                    k++;
                }

                mutacja(x, list.get(0), max, list.get(4));
                mutacja(x, list.get(0), max, list.get(5));
                poczatek(x, list.get(6), max);
                poczatek(x, list.get(7), max);

                i++;
            }

            if (koszt(x, list.get(0)) == 0) {
                System.out.println(start + "\t"+ i + "\t" + x + "\t" + max );
                nowe(x, cell, list.get(0));
                win++;
                dane++;
            }

        }
        //System.out.println(j + "\t" +win + "\t" + x + "\t" + max );

    }
    public static void transport (int x,int [] tab1,int [] tab2){
        for (int i = 0; i < x*x; i++){
            tab1[i]=tab2[i];
        }
    }

    public static void turniej (int x,int [] tab1,int [] tab2){
        int [] tabtemp = new int[100];
        if(koszt(x,tab2)<koszt(x,tab1)){
            for (int i = 0; i < x*x; i++) {
                tabtemp[i] =tab1[i];
                tab1[i] = tab2[i];
                tab2[i] = tabtemp[i];
            }
        }
    }

    public static void dziecko (int x,int [] tab1,int [] tab2,int [] tabd,int temp){
        int rng;
        for (int i = 0; i < x*x; i++) {
            rng = (int)(Math.random()*(10));
            if(rng<5){
                tabd[i]=tab1[i];
            }
            else {
                tabd[i]=tab2[i];
            }
        }
    }

    public static int koszt (int x, int [] tab){
        int [] sumy = new int[22];
        int temp =0, porownanie=0;

        for (int i = 0; i < (2*x+2); i++) {
            for (int j =0; j < x; j++){
                if(i<x){
                    temp+= tab[j+i*x];
                } else if (i<2*x) {
                    temp+= tab[j*x+(i-x)];
                } else if (i==2*x){
                    temp+= tab[j+j*x];
                } else if (i==2*x+1){
                    temp+= tab[j*(x-1)+(x-1)];
                }
            }
            sumy[i]=temp;
            temp=0;
        }
        for(int i = 0; i < (2*x+2); i++){
            for(int j = 0; j < (2*x+2); j++){
                porownanie+= abs(sumy[i]-sumy[j]);
            }
        }
        return porownanie;
    }
    public static void mutacja (int x, int [] source, int max,int [] tab){
        int rngnumber,rngcell,temp;
        rngcell = (int)(Math.random()*(x*x));
        temp= source[rngcell];
        do {
            rngnumber = (int)(Math.random()*(max+1));
        }while (source[rngcell]==rngnumber);
        source[rngcell]=rngnumber;
        for (int i = 0; i < x*x; i++) {
            tab[i]= source[i];
        }
        source[rngcell]=temp;
    }
}