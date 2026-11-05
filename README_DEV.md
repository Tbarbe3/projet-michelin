Globalement, j'ai repris la base de l'exercice fait en cours et je l'ai adaptée au sujet.

J'ai rencontré plusieurs petits obstacles durant mon développement :
- ``rate`` dans Evaluation est passé de ``int`` à ``Integer`` pour éviter le problème lors de l'update qui réinitilisé systématiquement ``rate`` à 0 quand il était absent. Avec ``Integer``, ``rate`` peut etre null et gade l'ancienne valeur.
- la durée de vie des auth tokens étant courte, je n'ai pas arrêté de les créer copier/coller dans Insomnia pour tester durant otu le dev de ce projet.
- l'authentification des roles USER/ADMIN a été le plus compliqué pour moi. Après plusieurs recherches sur internet, j'ai dû créer une fonction ``isAdmin`` qui descend dans les différents éléments du token pour retrouver le rôle et voir s'il contenait ADMIN. J'en ai déduit que si ce n'était pas le cas et si l'utilisateur était connecté, alors c'était un USER. J'ai aussi dû créer un nouveau type d'erreur ``ForbiddenException``.
- pour les tests j'ai utilisé ``Mockito``, que j'avais découvert dans mon stage de L3, avec une base de tests reprenant celle de ``DataSeeder``.
- pour ``addDishPicture``, j'avais d'abord mis PUT, puis je suis passé à POST car chaque appel crée une nouvelle photo, donc appeler deux fois la route ajoute deux photos. PUT est censé pouvoir être appeler autant de fois qu'on veut et obtenir le même résultat, ce qui n'est pas le cas ici.

En échangeant avec mes camarades, je me suis rendu compte que beaucoup d'entre eux avaient fait le choix de créer un ErreurDTO pour renvoyer le code et le message d'erreur. Selon moi, c'est redondant car le code peut être récupéré directement depuis la réponse. Cela revient donc au même.