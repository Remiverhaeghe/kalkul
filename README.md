# Calculatrice intelligente

Calculatrice de bureau en Java Swing, avec un onglet par famille de calculs. Le clavier fonctionne comme les boutons.

| Onglet | Contenu |
|---|---|
| Base | Quatre opérations, reste (`mod`), pourcentage, carré, racine carrée, changement de signe, retour arrière |
| Scientifique | sin, cos, tan et leurs réciproques en degrés ou en radians, exp, ln, log, puissance, factorielle |
| Outils | Moyenne, variance et écart-type ; équation du second degré ; conversion de longueurs, masses, volumes et températures |

Prévu ensuite : formules financières (intérêts, VAN, TRI, amortissement) puis graphiques, matrices, dérivées et formules avec variables.

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
| `MoteurCalcul.java` | Logique de l'écran : reçoit les touches et renvoie le texte à afficher |
| `FonctionsScientifiques.java` | Trigonométrie, logarithmes, factorielle, second degré, statistiques |
| `Conversions.java` | Conversion d'unités |
| `CalculatriceIntelligente.java` | Fenêtre, onglets et clavier ; transmet chaque touche au moteur |
| `PanneauTouches.java` | Grille de boutons réutilisée par les onglets Base et Scientifique |
| `PanneauOutils.java` | Formulaires de l'onglet Outils |
| `src/test/java/...` | Tests du moteur, des fonctions et des conversions |

Les opérations s'enchaînent de gauche à droite, sans priorité : `2 + 3 * 4` donne 20. L'écran garde 12 chiffres significatifs, donc `0,1 + 0,2` affiche 0,3. La variance est celle d'une population (division par n).

## Licence

MIT, voir [LICENSE](LICENSE).
