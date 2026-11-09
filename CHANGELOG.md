# Changelog

## Non publié

### Technique
- Moteur de calcul séparé de l'interface Swing, avec tests JUnit 5
- Package et classes renommés sans faute (`com.calculatrice`, `CalculatriceIntelligente`)
- Classe principale du `pom.xml` corrigée : `mvn exec:java` et le JAR exécutable démarraient sur un nom qui n'existait pas
- Intégration continue GitHub Actions et Dependabot

### Corrections
- La touche Entrée ne lançait pas le calcul
- Les touches de retour arrière et de saisie après une erreur repartent d'un écran propre

### Documentation
- README, licence MIT et CLAUDE.md
