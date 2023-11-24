package connexion;
import java.sql.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import java.io.File;
import java.util.List;

public class Connexion{
	String utilisateur;
	String password;
    String host;
    String port;	
    String dataBase;
    String base;
    String driver;
    Connection connection;

    public Connexion() {}

	public Connexion(String path){
		try{ 
            this.readProperties(path);
			this.connexion(); 
			this.getConnection().setAutoCommit(false);
		}
		catch(Exception io){ 
            System.out.println(io);
        }
	}

	public String getUtilisateur(){
		return this.utilisateur;
	}

	public void setUtilisateur(String n){
		this.utilisateur = n;
	}

	public String getPassword(){
		return this.password;
	}

	public void setPassword(String p){
		this.password = p;
	}

	public Connection getConnection(){
		return this.connection;
	}
	public void setConnection(Connection t){
		this.connection = t;	
	}

    public String getHost() {
        return this.host;
    }
    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return this.port;
    }
    public void setPort(String port) {
        this.port = port;
    }

    public String getDataBase() {
        return this.dataBase;
    }
    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public String getBase() {
        return this.base;
    }
    public void setBase(String base) {
        this.base = base;
    }

    public String getDriver() {
        return this.driver;
    }
    public void setDriver(String driver) {
        this.driver = driver;
    }

	public void connexion() throws Exception {	// connexion du java avec oracle
		if(this.getConnection()==null){
			try{
				Class.forName(this.getDriver());
		        Connection conn = DriverManager.getConnection(this.url(), this.getUtilisateur(), this.getPassword());
		            // @//machineName:port/SID,   userid,  password	
		        this.setConnection(conn);
		    }
			catch(Exception io){
				io.printStackTrace();
				// System.out.println("Erreur");
			}
		}
	}

    public String url() {
        String url = "jdbc:"+ this.getBase() +"://"+ this.getHost() +":"+ this.getPort() +"/"+ this.getDataBase();
        return url;
    }

    public void readProperties(String path) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();

            // Chargement du fichier XML
            File fichier = new File(path);
            Document document = saxBuilder.build(fichier);

            // Obtention de la racine du document
            Element racine = document.getRootElement();

            // Obtention de la liste des éléments "personne"
            List<Element> listePersonnes = racine.getChildren("ConnexionBase");

            // Parcours des éléments "personne"
            for (Element personne : listePersonnes) {
                // Obtention du nom et de l'âge
                String Base = personne.getChildText("Base");
                String Host = personne.getChildText("Host");
                String Port = personne.getChildText("Port");
                String UserName = personne.getChildText("UserName");
                String Password = personne.getChildText("Password");
                String DataBase = personne.getChildText("DataBase");
                String Driver = personne.getChildText("Driver-class-name");

                this.setUtilisateur(UserName);
                this.setPassword(Password);
                this.setHost(Host);
                this.setPort(Port);
                this.setBase(Base);
                this.setDataBase(DataBase);
                this.setDriver(Driver);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

// String jdbcUrl = "jdbc:sqlserver://(LocalDb)\\MSSQLLocalDB:1433;databaseName=sante";
