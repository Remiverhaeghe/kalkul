# Changelog

## Non publié

### Habillage des formulaires et clavier
- Mémoires nommées (Outils, sous-onglet Mémoires) : une valeur gardée sous un nom en lettres (« prix »), calculée à partir d'une formule qui peut citer d'autres mémoires (« prix * 3 »), conservée d'une session à l'autre ; les noms de fonctions, de constantes et « x » sont refusés
- Boutons des formulaires à plat, comme les touches de chiffres ; champs de saisie enfoncés comme l'écran ; sections titrées sur un simple trait, barres de défilement discrètes
- Une touche tapée au clavier s'allume un instant sur le pavé (Entrée pour « = », retour arrière pour « ⌫ », c pour « C »)
- Déclic de touche en option (Outils > Réglages, coupé au départ), fabriqué à la volée sans fichier son ; muet sans sortie son

### Habillage
- Nouvel habillage inspiré des calculatrices de bureau des années 70 : corps noir mat, touches rondes en relief (dôme éclairé par le haut, cuvette au centre, enfoncées à l'appui), opérateurs olive, « = » moutarde, effacement orange
- Écran à cristaux liquides gris-vert enfoncé dans un cadre noir, dans les deux thèmes ; la courbe de l'onglet Graphique se trace sur le même fond
- Chiffres de l'écran dessinés en sept segments, légèrement penchés, avec les cases éteintes visibles comme sur un vrai afficheur ; les textes que les segments ne savent pas écrire gardent la police
- Onglets à plat, l'onglet ouvert souligné de moutarde
- Pavé en cases carrées centrées : les touches restent serrées quelle que soit la taille de la fenêtre, chiffres dans une police sans empattement
- Thème clair en gris chaud, touches de chiffres blanches
- Captures du README refaites

### Corrections
- Graphique : le repère 0 de l'axe affichait un reste d'arrondi (1.2E-16, tronqué) ; les repères sont recalculés depuis leur rang

### Fonctionnalités
- Devises dans l'onglet Outils, sans connexion : taux tapés à la main (1 EUR = …) ou importés d'un fichier (eurofxref.csv de la BCE, ou lignes « USD;1,08 »), gardés d'une session à l'autre et toujours affichés avec leur date ; alerte quand ils ont plus d'une semaine
- Historique : champ de recherche (calcul ou résultat, « * », « / » et « - » trouvent « × », « ÷ » et « − ») et export en CSV de ce qui est affiché, séparé par des points-virgules pour un tableur en français
- Onglet Outils en sous-onglets (Statistiques, Second degré, Unités, Proportions) ; les sous-onglets d'Outils et de Chantier défilent quand la fenêtre est trop petite, au lieu de couper le bas des formulaires
- Onglet Feuille : jusqu'à 12 cellules nommées qui se reprennent entre elles, gardées d'une session à l'autre, utilisables comme variables dans les formules enregistrées (onglet Feuille, « f(écran) », courbes)
- Règle de trois et pourcentages dans l'onglet Outils : pourcentage, part, variation, remises successives, prix avant remise, prix au m²
- Onglet Chantier en sous-onglets : Surfaces et dalle, Coupes, Toiture, Terrassement et mélanges
- Angles de coupe : onglet de plinthe selon l'angle du mur, coupe composée d'une pièce inclinée (table et lame), chevron contre un arêtier
- Toiture : pente en %, degrés et cm par mètre, rampant, pente minimale indicative par couverture ; unité cm/m dans les conversions
- Terrassement : volume d'une fouille avec foisonnement, remblai de sable, gravier ou tout-venant en tonnes et en big bags (densités indicatives)
- Gâchée de béton, de mortier ou d'enduit selon le dosage : sacs de ciment, seaux de sable et de gravier, eau, et proportions pour un sac
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
