# Kalkul

Calculatrice Swing en Java 17 et Maven. Suivre les conventions de Rémi (skill `conventions-remi`) : préfixes p/v/r/a, commentaires en français. Toujours répondre en français.

- La logique va dans `MoteurCalcul`, `FonctionsScientifiques`, `Conversions`, `FormulesFinancieres`, `Expression`, `Analyse`, `Matrice`, `Regression`, `Programmeur`, `Chantier`, `Devises`, `Historique` et `ThemeSysteme`, jamais dans les classes Swing, pour rester testable.
- Un onglet par famille de calculs (Base, Scientifique, Financier, Graphique, Programmeur, Chantier, Outils, Historique). Taux de change : hors ligne, saisis ou importés d'un fichier.
- Chaque correction de bug s'accompagne d'un test dans `MoteurCalculTest`.
- Avant de valider : `mvn verify`.
- `README.md` et `CHANGELOG.md` à jour après chaque évolution.
