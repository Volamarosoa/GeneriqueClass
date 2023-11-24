package generique;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import connexion.Connexion;

public class GeneriqueClass {
    String extension;
    String nom;
    String path;
    String nomPackage;
    String table;

    Connexion connexion;

    public GeneriqueClass() {}

    public GeneriqueClass(String properties, String extension, String path, String nomPackage, String table, String nom) throws Exception{
        this.setExtension(extension);
        this.setNom(nom);
        this.setPath(path);
        this.setNomPackage(nomPackage);
        this.setTable(table);
        Connexion connexion = new Connexion(properties);
        this.setConnexion(connexion);
    }

    public GeneriqueClass(String properties, String extension, String path, String nomPackage, String table) throws Exception{
        this.setExtension(extension);
        this.setNom("");
        this.setPath(path);
        this.setNomPackage(nomPackage);
        this.setTable(table);
        Connexion connexion = new Connexion(properties);
        this.setConnexion(connexion);
    }

    public String getTable() {
        return this.table;
    }
    public void setTable(String table) {
        this.table = table;
    }

    public String getNomPackage() {
        return this.nomPackage;
    }
    public void setNomPackage(String nomPackage) throws Exception{
        if(nomPackage.equals(""))
            throw new Exception("Le nom du package doit contenir un nom valide");
        this.nomPackage = nomPackage;
    }

    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getNom() {
        return this.nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public Connexion getConnexion() {
        return this.connexion;
    }
    public void setConnexion(Connexion connexion) {
        this.connexion = connexion;
    }

    public String getExtension() {
        return this.extension;
    }
    public void setExtension(String extension) throws Exception{
        int test = 0;
        if(!extension.toLowerCase().equals("cs") && !extension.toLowerCase().equals("java"))
            throw new Exception("Extension invalide: "+ extension);
        this.extension = extension;
    }

    public String getType(String type) {
        if(type.startsWith("numeric") || type.startsWith("bigint") || type.startsWith("int"))
            return "int";
        else if(type.startsWith("float") || type.startsWith("double") || type.startsWith("numeric"))
            return "double";
        else if((type.startsWith("timestamp") || type.startsWith("date")) && this.getExtension().equals("cs")) {
            return "DateTime";
        }
        else if(type.startsWith("timestamp")  && this.getExtension().equals("java")) {
            return "Timestamp";
        }
        else if(type.startsWith("date")  && this.getExtension().equals("java")) {
            return "Date";
        } 
        else
            return "String";
    }    

    public String importPackage(String type) {
        if(type.equals("Timestamp"))
            return "java.sql.Timestamp";
        else if(type.equals("Date"))
            return "java.sql.Date";
        return "";
    }

    public String getNomClass() {
        if(this.getNom().equals(""))
            return this.getTable();
        return this.getNom();
    }

    public void genererClass() throws Exception {
        String[][] attributs = this.getAttribut();
        List<String> listeImports = this.getImports(attributs);
        String fichier = this.getPath() + "" + this.getNomClass() + "." + this.getExtension();
        File file = new File(fichier);

        Files.deleteIfExists(Paths.get(fichier));
        Files.copy(Paths.get("template."+this.getExtension()+".txt"), Paths.get(fichier));
        
        String contenu = Files.readString(Paths.get(fichier), StandardCharsets.UTF_8);
        contenu = contenu.replaceAll("nomPackage", this.getNomPackage());
        contenu = contenu.replaceAll("template", this.getNomClass());
        StringBuilder nouveauContenu = new StringBuilder(contenu);

        String imports = this.getImport(listeImports);
        String motAChercher = "//import";
        int index = contenu.indexOf(motAChercher);
        if (index != -1 && !imports.equals("")) {
            nouveauContenu.insert(index + motAChercher.length(), imports);
        }

        contenu = nouveauContenu.toString();
        nouveauContenu = new StringBuilder(contenu);

        String attribut = this.getAttributs(attributs);
        motAChercher = "//attribut";
        index = contenu.indexOf(motAChercher);
        if (index != -1 && !attribut.equals("")) {
            nouveauContenu.insert(index + motAChercher.length(), attribut);
        }
        
        Files.write(Paths.get(fichier), nouveauContenu.toString().getBytes(StandardCharsets.UTF_8));

        System.out.println("La class " + this.getNomClass() + " a ete bien cree avec succes.");
    }

    String getImport(List<String> listeImports) {
        String imports = "";
        for (String import_class : listeImports) 
            imports += "\nimport " + import_class + ";";
        return imports;
    }

    String getAttributs(String[][] attributs) {
        String liste_attrinut = "";
        for (String[] attribu : attributs) {
            String type = this.getType(attribu[1]);
            if(this.getExtension().equals("java"))
                liste_attrinut += "\n    " + type + " " + attribu[0] + ";";
            else
                liste_attrinut += "\n    public " + type + " " + attribu[0] + " { get; set; }";
        }
        return liste_attrinut;
    }

    public List<String> getImports(String[][] attributs) {
        List<String> imports = new ArrayList<String>();
        for (String[] attribut : attributs) {
            String type = this.getType(attribut[1]);
            String import_package = this.importPackage(type);
            if(import_package.equals("") == false && imports.contains(import_package) == false) 
                imports.add(import_package);
        }
        return imports;
    }

    public String[][] getAttribut() throws Exception{
        int taille = this.getTaille();
        if(taille == 0)
            throw new Exception("La relation " + this.getTable() + " n'existe pas dans votre base de donnees.");
        
        String[][] attribut = new String[taille][2];
        String requeteSelect = "SELECT column_name, data_type, character_maximum_length FROM information_schema.columns WHERE table_name = '"+ this.getTable() +"'";
        PreparedStatement preparedStatement = this.getConnexion().getConnection().prepareStatement(requeteSelect.toLowerCase());
        ResultSet resultSet = preparedStatement.executeQuery();
        
        int index = 0;
        while (resultSet.next()) {
            String column_name = resultSet.getString(1);
            String data_type = resultSet.getString(2);
            attribut[index][0] = column_name;
            attribut[index][1] = data_type;
            index += 1;
            // System.out.println(column_name + " : " + data_type);
        }

        resultSet.close();
        preparedStatement.close();
        this.getConnexion().getConnection().close();
        return attribut;
    }

    public int getTaille() throws Exception {
        String requeteSelect = "SELECT column_name, data_type, character_maximum_length FROM information_schema.columns WHERE table_name = '"+ this.getTable() +"'";
        PreparedStatement preparedStatement = this.getConnexion().getConnection().prepareStatement(requeteSelect.toLowerCase());
        ResultSet resultSet = preparedStatement.executeQuery();
        int taille = 0;        
        while (resultSet.next()) {
            taille += 1;
        }
        resultSet.close();
        preparedStatement.close();
        return taille;
    }


}
