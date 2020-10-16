# simplu3D-grenoble


## Sous Linux

### Prérequis  

- un JRE (Java Runtime Environnement) java pas trop vieux, par exemple openJDK-11
- maven 

### Construction du programme


`maven` est un utilitaire qui va construire un éxécutable à partir du code source du projet

Pour construire l'éxécutable, dans un terminal , à la racine du repertoire `simplu3D-grenoble`, taper 

```
mvn install
``` 

Si tout ce passe bien, cette commande construit plusieurs éxécutables jar dans le repertoire `target`



### Lancement du programme de base

Pour lancer l'éxécutable généré à l'étape précédente , il faut se positionner à la racine du répertoire de simplu3D-grenoble et entrer la commande : 
```
#> java -jar target/simplu3d-grenoble-1.0-SNAPSHOT-shaded.jar
```
Le code se déclenche sur les parcelles dont l'attribut SIMUL vaut 1.

### Paramétrage des règles.

Il est possible de paramétrer les valeurs de règles dans la ligne de commande en ajoutant **-nom_attribut valeur**

Dans la version actuelle, il est possible de modifier les paramètres suivants.

| Nom de l'attribut | Rôle | Valeur par défaut |
|-------------------|------|-------------------|
|distReculVoirie    | Recul par rapport à la voirie     |     0.0              |
|distReculFond                   |Recul par rapport à la bordure de fond de parcelle      |0.0                   |
|distReculLat                   |Recul par rapport à la bordure latérale de parcelle      |0.0                   |
|distInterBati                   |Distance interbat      |0.0                   |
|maximalCES                   |CES maximum     |0.6                   |


On peut ainsi modifier les paramètres de la manière suivante :
```
#> java -jar simplu3d-grenoble-1.0-SNAPSHOT-shaded.jar -maximalCES 1.0 -distReculVoirie 5.0
```
Les paramètres non spécifiés prennent la valeur par défaut. Un petit texte précise dans la fenêtre de commande les valeurs utilisées :
``
Les règles sont : 
Distance de recul à la voirie : 5.0
Distance de recul au fond de parcel : 0.0
Distance de recul latéral : 0.0
Distance de recul entre bâtiments : 0.0
CES max : 1.0
``



### Selection des parcelles à simuler 

l'attribut SIMUL de la couche shapefile parcel.shp du repertoire data/test/ indique si on simule un bâtiment sur la parcelle (1) ou si la parcelle est laissée vide (0)


### Résultats


Les résultats sont écrits  par défaut dans le répertoire `/tmp/`. (`out.shp`)

On peut changer l'endroit où ils sont écrits en modifiant la variable  `result` du fichier `scenario/building_parameters.json`


## Sous windows


cett doc peut être utile pour l'installation de java et maven sous windows, et surtout la configuration des variables d'environnement: 
https://mkyong.com/maven/how-to-install-maven-in-windows/


