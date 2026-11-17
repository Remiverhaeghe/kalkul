# Changelog

## Non publié

### Fonctionnalités
- Formules enregistrées dans l'onglet Graphique, gardées d'une session à l'autre et appliquées au nombre de l'écran depuis l'onglet Scientifique
- Onglet Chantier : paquets de revêtement avec chutes, béton d'une dalle, pente et dénivelé
- Unités de chantier : surfaces, m³, tonne, brouette, pente en %, mm/m et degrés
- Onglets sur une seule rangée, fenêtre de 800 px
- Onglet Programmeur : entiers de 8 à 64 bits en bases 16, 10, 8 et 2, opérations bit à bit, décalages, complément à deux
- Historique des 50 derniers calculs dans un onglet, résultat repris d'un double-clic, rappel du calcul en petit sur l'écran
- Thème clair qui suit le réglage des applications de Windows, ou imposé par `-Dkalkul.theme=clair|sombre`
- Nouvel habillage : fond graphite, écran à chiffres cyan avec le mode d'angle, touches arrondies colorées par rôle (chiffres, opérateurs ambre, « = » plein, effacement rouge brique), signes ÷ × −, graphique sur fond sombre
- Paquets avec Java embarqué par jpackage : dossier Windows zippé et `.deb` Linux, produits par le workflow « Paquets »
- Onglets Base, Scientifique, Financier, Graphique et Outils
- Onglet Graphique : tracé de courbes en Java2D, formules avec variables et paramètres, tangente, dérivée, intégrale et aire sous la courbe
- Matrices : somme, différence, produit, transposée, déterminant, inverse, résolution de systèmes, produits scalaire et vectoriel, norme
- Régression linéaire avec corrélation, r² et p-value de la pente (loi de Student), tracée avec son nuage de points
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
