# Les Stratégies Financières Chartistes : une simulation multi-agents
Ici sont disponibles les ressources afin de pouvoir faire la simulation du mémoire et les scripts permettant de traiter les données Big-Data obtenues.

## La simulation
Afin de lancer la simulation, il faut exécuter le script Kotlin présent dans `main/SimulationChartist.kt`. Il est possible de modifier certains paramètres de la simulation en modifiant les constantes présentes dans le script.
La simulation, une fois lancée, peut durer entre 4 et 7 heures selon la puissance de l'ordinateur.

## Traitement des données
Etant donné que la taille des fichiers obtenues vascillent entre 70-200 GO, il est difficile, voir impossible, de traiter directement les données. Il faut pour cela les splitter et analyser partie par partie puis compiler les résultats.
Pour cela un script python permettant de séparer les données est disponible et se nomme `generatecsv.py`. Pour savoir les arguments, exécutez `generatecsv.py -h`.
Enfin un script R est disponible afin de traiter les données partie par partie et se nomme `data-analysis.R`.