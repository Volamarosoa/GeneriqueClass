# GeneriqueClass
Projet pour faire un Scaffolding 
C'est une framework pour generer une class java ou c# a partir d'une relation dans un base de donné: Postgres, SqlServer, MySQL

Il faut remplir les donnees necessaire pour faire une connexion dans votre base de donnees:
    * Creer un fichier xml, et copier ce qu'il y a dans le fichier properties.xml et remplacer les valeurs de chaque elements dans votre fichier xml pour faire la connexion dans votre base de donnees

Apres creer une instance de l'objet GeneriqueClass: package: generique.GeneriqueClass
    Par exemple: 
        * new GeneriqueClass(String properties, String extension, String path, String nomPackage, String table, String nomClass)
        * new GeneriqueClass(String properties, String extension, String path, String nomPackage, String table)
        - properties: c'est le chemain de votre fichier XML
        - extension: c'est le fichier que vous voulez generer: .java ou .cs seulement
        - path: c'est qu'on va creer votre class
        - nomPackage: c'est le nom du package de votre class
        - table: c'est le nom du table ou vue qu'on va generer votre class
        - nomClass: c'est le nom du class qu'on va generer
        Rmq: * Par defaut le nom du class a generer est le meme que nom de votre relation si vous ne donnez pas un nom specifique pour votre class   
             * Par defaut le nom de des attributs dans le class est le meme nom de colonne dans votre relation