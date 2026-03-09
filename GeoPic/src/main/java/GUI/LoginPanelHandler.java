package GUI;

public class LoginPanelHandler {
    private String utente;
    private String password;


    public void getlogincredits(String utente, String password){
        this.utente = utente;
        this.password = password;
    }

    public boolean check_login(){
        if(utente.equals("admin") && password.equals("admin")){ //qua ci va l'interfaccia con il db per reperire i dati per il login
            System.out.println("Login Success");
            return true;
        }
        else return false;
    }
}
