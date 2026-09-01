package addressbook;

import javax.swing.SwingUtilities;

import addressbook.ui.Login;

public class Main {
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            @Override 
            public void run(){
                new Login();
            }
        });
    }
}
