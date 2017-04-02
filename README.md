# somedamnbrain

WORK IN PROGRESS - DO NOT RUN IN PRODUCTION

# Motivation (French only)

Somedamnbrain est une application Java autonome : son objectif principal est sa survie.

Il ne s'agit pas d'un serveur, mais d'une application à l'exécution éphémère qui tente d'auto-configurer une machine hôte pour s'assurer de sa réexécution à l'aide d'une tâche Cron (par exemple tous les 10 minutes)

SDB a une notion "d'univers". Cette notion d'univers inclut un nom (par exemple, "DEV", "QUALIF", "PROD") et un peu de configuration (en particulier les informations SSH d'une machine hôte).

Somedamnbrain tente de faire abstraction de l'endroit où l'exécution actuelle a lieu : il poursuit une liste de besoins primaires qui vont amener rapidement à sa réplication sur la machine cible.

Cela signifie que pour initialiser un environnement de qualification, de préproduction, ou de production, la procédure est simple et toujours identique : je démarre une première fois SDB sur mon poste local avec initialisation d'un nouvel univers. Je réponds aux questions de SDB à propos de cet univers (nom, informations sur la machine cible, branche Git du code-source à utiliser, etc.) et l'exécution de SDB va alors tout faire pour s'auto-répliquer sur la machine cible, et me notifier de la réussite ou l'échec de son entreprise. Si ça réussit, SDB va être exécuté périodiquement sur la machine cible et continuer à me tenir informé de sa santé depuis là bas.

Quand une exécution de SDB fonctionne bien, la prochaine exécution a lieu sur la machine cible (plutôt que la machine locale) quoi qu'il arrive.

Somedamnbrain a une notion « d'humain larbin ». Derrière l'humour se cache une notion nuancée : c'est SDB qui gère sa propre infrastructure, et n'appelle un humain que quand il ne s'en sort pas tout seul.

SDB tente de combler un certain nombre de besoin primaires, dans l'ordre :

    Est-ce que mon humain larbin a répondu à toutes mes questions ?
    Est-ce que j'ai suffisamment de configuration pour contacter mon humain larbin en cas de problème ? (l'email est le canal primaire, la sortie standard console est le canal dégradé)
    Est-ce que j'ai suffisamment de configuration pour me connecter à ma machine hôte ?
    Est-ce que la configuration de mon univers est présente sur la machine hôte ? Dans une version moins à jour ? Dans une version plus à jour ?
    Est-ce que Java est correctement installé sur ma machine hôte ?
    Est-ce que Maven est correctement installé sur ma machine hôte ?
    Est-ce que Git est correctement installé sur ma machine hôte ?
    Est-ce que mon propre code-source est présent sur la machine ?
    Est-ce que mon propre code-source est à jour sur la machine ?
    Est-ce que le crontab est correctement configuré pour ma réexécution périodique ?
    Est-ce que ma dernière exécution connue s'est déroulée il y a suffisamment peu de temps pour acter que tout se passe normalement ?

 A chaque étape, si une étape est en erreur, SDB va tenter de notifier un humain du problème, puis tenter d'opérer une correction. Certains problèmes peuvent être irréparables par SDB seul : il appelle alors à l'aide.

SDB considère qu'une situation est stable quand plusieurs exécutions consécutives retournent le même résultat (en succès ou en échec). Une situation stable n'est pas nécessairement une situation où tout va bien : cela indique simplement que l'environnement n'évolue plus. Il notifie alors périodiquement, mais à un rythme exponentiellement décroissant : d'abord tous les 5 minutes, puis 10, 20, 40, 80, 160, etc.

Au fur et à mesure que SDB gagne en confiance dans la stabilité de son infrastructure, l'humain est donc notifié de moins en moins souvent.

Ce mail de type "Tout est OK, je renotifierai dans 10240 minutes" est toutefois un petit rempart contre la pire situation possible pour SDB : une extinction complète et silencieuse de la machine hôte. SDB ne peut donc pas se rééxécuter et donc pas alerter un humain. Ce cas là ne peut pas être correctement détecté dans une configuration mono-machine, on va donc le laisser de coté dans un premier temps, et le compenser par du monitoring externe dans un premier temps.

Une fois que SDB est en production, c'est à dire qu'il est capable de s'auto-gérer sur une machine hôte, il devient une base stable sur laquelle on peut faire plein de choses.

Chaque changement dans l'infrastructure (positif, comme une évolution, ou négatif, comme une régression) est immédiatement tracé et l'humain larbin reçoit des mails à propos de la gestion de l'incident (détection et début de tentative de correction puis résultat de la tentative de correction).

SDB peut même tenter périodiquement des "exercices" : par exemple, au bout de X exécutions avec succès, il lancer un exercice "reboot" : SDB notifie l'humain larbin qu'il va redémarrer sa machine hôte pour voir si il peut s'en sortir seul et rattraper la situation. Logiquement après le reboot, SDB doit reprendre comme si de rien n'était, et corriger les éventuels problèmes survenus suite au reboot. Si SDB ne réponds pas suite au reboot, l'humain larbin intervient pour voir ce qui se passe.

Une fois que SDB s'autogère, il est trivial de lui faire gérer d'autres applications. Il suffit de modifier le code de SDB pour lui ajouter de nouveaux Diagnostics+Corrections liés à la santé de SDM, de les commiter sur Github, et elles seront naturellement reportées dans SDB qui installera progressivement SDM dans le cadre de son exécution normale.


L'étape suivante (aspirationnelle, je doute d'en arriver là), c'est de remarquer que rien dans le design de SDB ne le cantonne à une unique machine. J'aimerais le faire s'exécuter périodiquement sur un cluster de machines, où au moins l'une d'elle exécuterait SDB, à l'aide d'une synchronisation de type Paxos.
