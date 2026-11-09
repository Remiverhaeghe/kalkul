# Calculatrice intelligente

Calculatrice de bureau en Java Swing : quatre opérations, pourcentage, changement de signe, virgule et retour arrière. Le clavier fonctionne comme les boutons.

Stack : Java 17, Maven, JUnit 5.

## Lancement

Prérequis : JDK 17 ou plus récent et Maven.

```bash
mvn exec:java
```

Pour produire un JAR exécutable :

```bash
mvn package
java -jar target/calculatrice-intelligente-1.0-SNAPSHOT.jar
```

## Tests

```bash
mvn verify
```

## Organisation

| Fichier | Rôle |
|---|---|
| `MoteurCalcul.java` | Logique du calcul, sans Swing : reçoit les touches et renvoie le texte à afficher |
| `CalculatriceIntelligente.java` | Fenêtre, boutons et clavier ; transmet chaque touche au moteur |
| `MoteurCalculTest.java` | Tests du moteur |

Les opérations s'enchaînent de gauche à droite, sans priorité : `2 + 3 * 4` donne 20.

## Licence

MIT, voir [LICENSE](LICENSE).
