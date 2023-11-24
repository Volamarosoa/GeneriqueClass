package main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import generique.GeneriqueClass;

public class Main {
    public static void main(String[] args) {
        try {
            GeneriqueClass geniqueClass = new GeneriqueClass("properties.xml", "java", "D:/", "test", "Pointage");
            geniqueClass.genererClass();
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
