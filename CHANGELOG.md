# Changelog

## Non publié

### Fonctionnalités
- Onglets Base, Scientifique, Financier, Graphique et Outils
- Onglet Graphique : tracé de courbes en Java2D, formules avec variables et paramètres, tangente, dérivée, intégrale et aire sous la courbe
- Matrices : somme, différence, produit, transposée, déterminant, inverse, résolution de systèmes, produits scalaire et vectoriel, norme
- Régression linéaire avec corrélation, r² et p-value de la pente (loi de Student)
- Onglet Financier : intérêts simples et composés, prêt avec tableau d'amortissement, VAN, TRI, trésorerie cumulée et délai de retour, conversion monétaire à taux saisi
- Carré, racine carrée, reste et puissance
- Trigonométrie et réciproques en degrés ou en radians, exp, ln, log, factorielle
- Statistiques de base, équation du second degré, conversion d'unités
- Écran arrondi à 12 chiffres significatifs

### Technique
- Moteur de calcul séparé de l'interface Swing, avec tests JUnit 5
- Projet renommé Kalkul (package `com.kalkul`, classe `Kalkul`)
- Classe principale du `pom.xml` corrigée : `mvn exec:java` et le JAR exécutable démarraient sur un nom qui n'existait pas
- Intégration continue GitHub Actions et Dependabot

### Corrections
- La touche Entrée ne lançait pas le calcul
- Les touches de retour arrière et de saisie après une erreur repartent d'un écran propre

### Documentation
- README, licence MIT et CLAUDE.md
