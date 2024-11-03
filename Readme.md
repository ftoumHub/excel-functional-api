## excel-utils

Functional API over Apache POI

Parsing Excel file functionally

## Comment construire une API "Fonctionnelle"

# Définition d'une fonction

Une fonction fait correspondre à chaque élément d'un ensemble A, un et un seul élément de l'ensemble B.

Si pour certaine valeurs de A, on ne trouve pas de correspondance dans l'ensemble B on est en présence d'une 
fonction partielle.

Typiquement, en Java, pour traiter ce genre de problème, on va lancer une exception.

En programmation fonctionnelle, on proscrit l'utilisation de fonctions partielles.

# Générer la documentation

```bash
mvn process-resources
```

La doc est généré dans le répertoire target\generated-docs