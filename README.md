# Kalkul

Calculatrice de bureau en Java Swing, avec un onglet par famille de calculs. Le clavier fonctionne comme les boutons.

| Onglet | Contenu |
|---|---|
| Base | Quatre opérations, reste (`mod`), pourcentage, carré, racine carrée, changement de signe, retour arrière |
| Scientifique | sin, cos, tan et leurs réciproques en degrés ou en radians, exp, ln, log, puissance, factorielle |
| Financier | Intérêts simples et composés, mensualité et tableau d'amortissement d'un prêt, VAN, TRI, trésorerie cumulée, conversion monétaire avec un taux saisi |
| Graphique | Courbes à partir d'une formule avec variables (`a*sin(x) + b`), tangente et dérivée en un point, intégrale et aire ; matrices (somme, produit, transposée, déterminant, inverse), systèmes d'équations, produits scalaire et vectoriel ; régression linéaire avec corrélation et seuil de significativité |
| Outils | Moyenne, variance et écart-type ; équation du second degré ; conversion de longueurs, masses, volumes et températures |

Prévu ensuite : nuage de points de la régression, enregistrement des formules, taux de change en ligne.

Stack : Java 17, Maven, JUnit 5.

## Lancement

Prérequis : JDK 17 ou plus récent et Maven.

```bash
mvn exec:java
```

Pour produire un JAR exécutable :

```bash
mvn package
java -jar target/kalkul-1.0-SNAPSHOT.jar
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
| `FormulesFinancieres.java` | Intérêts, prêt, amortissement, VAN, TRI, trésorerie |
| `PanneauFinance.java` | Formulaires de l'onglet Financier |
| `Expression.java` | Lecture et calcul d'une formule avec variables, constantes et fonctions |
| `Analyse.java` | Dérivée (différence centrée) et intégrale (Simpson) |
| `Matrice.java`, `Vecteurs.java`, `CalculMatriciel.java` | Algèbre linéaire et opérations de l'onglet Matrices |
| `Regression.java`, `StudentT.java` | Droite des moindres carrés, corrélation et p-value de la pente |
| `EchantillonCourbe.java`, `TraceCourbe.java` | Points d'une courbe et dessin Java2D |
| `PanneauGraphique.java`, `PanneauCourbe.java`, `PanneauMatrices.java`, `PanneauRegression.java` | Écrans de l'onglet Graphique |
| `Kalkul.java` | Fenêtre, onglets et clavier ; transmet chaque touche au moteur |
| `PanneauTouches.java` | Grille de boutons réutilisée par les onglets Base et Scientifique |
| `PanneauOutils.java` | Formulaires de l'onglet Outils |
| `src/test/java/...` | Tests du moteur, des fonctions et des conversions |

Les opérations s'enchaînent de gauche à droite, sans priorité : `2 + 3 * 4` donne 20. L'écran garde 12 chiffres significatifs, donc `0,1 + 0,2` affiche 0,3. La variance est celle d'une population (division par n). Dans les formules, les angles sont en radians, la puissance se lit de droite à gauche (`2^3^2` donne 512) et le séparateur décimal est le point. La significativité est le test bilatéral de Student sur la pente : sous 0,05, la pente est dite significative.

## Licence

MIT, voir [LICENSE](LICENSE).
