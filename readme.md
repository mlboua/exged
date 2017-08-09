# Exged

Exged est une application pour exécuter des migrations.

## TODO:

Principal:
- Module delivery (0.5)
- Finir de refaire les configurations (1)
- debug la barre de progression durant le traitement (0.25)
- Améliorer le parser des fichiers de config pour lire les JSON/YAML avec une seule classe

Secondaire:
- Changer le parser csv pour en utiliser un avec des Stream, ce qui permettra d'éviter de remplir la ram pour la vider ensuite à chaque fichier. (0.5)
- Simplifier le code des validateurs avec des Predicats quand c'est possible (0.5)
- Réaliser un module permettant de faire du KotlinScript afin de Simplifier la partie exged pricipale. (Ou faire des DSL Kotlin) (4)
- Enlever le moteur de rendu pour faire notre propre sysème à l'aide des DSL Kotlin (4)

A voir:
- Refaire le code en Kotlin pour faire marcher l'application sur toutes les machines avec Java 6 d'installé. (Conversion du code automotisé grâce à IntelliJ) (3)


## Configuration

Exged possède un fichier de configuration principal qui peut être renseigner avec l'argument **-c**, par défaut l'emplacement du fichier de config est le même que celui du jar.

Les fichiers de configurations dans le dossier de configuration peuvent être en JSON ou YAML.

### Identifiers

Nom du ficiher: **identifiers.json/yaml**  

Ce fichier de configuration permet de configurer la détection des plis à l'aide de différents identifiant avec leurs règles.

Pour chaque entrée du fichier, l'application va chercher quel est l'identifiant de celle-ci. Il faut donc définir comment trouver l'identifiant de ce pli car il peut y en avoir plusieurs.  

Comme on peut le voir dans les exemples, il y a 3 champs à remplir, qui sont:
- **name**: ceci correspond au nom du champ dans le fichier d'entrée, ce nom sera utilisé pour la recherche dans le pli
- **rejectedValues**: c'est est un tableau contenant les divers valeurs rejetés pour cet identifiant.
- **replacedBy**: cette valeur correspond à l'identifiant qui remplacera cet identifiant si il n'est pas valide

#### Exemples

**json:**
```json
[
  {
    "name": "IDENTIFIANT_PRINCIPALE",
    "rejectedValues": ["000000", ""],
    "replacedBy": "IDENTIFIANT_SECONDAIRE"
  },
  {
    "name": "IDENTIFIANT_SECONDAIRE",
    "rejectedValues": []
  }
]
```

**yaml:**
```yaml
---
- name: IDENTIFIANT_PRINCIPALE  # Identifiant principal du pli
  rejectedValues:               # Si cette Identifiant correspond
  - '000000'                    # à une de ces valeurs, le prochain
  - ''                          # identifiant est utilisé
  replacedBy: IDENTIFIANT_SECONDAIRE
- name: IDENTIFIANT_SECONDAIRE
  rejectedValues: []
```

### validations

Nom du ficiher: **validations.json/yaml**  

Ce fichier de configuration permet de configurer la validations des plis à l'aide de différents critères.

Pour chaque pli, l'application va tester le pli si il correspond aux attentes de la migration.  

Comme on peut le voir dans les exemples, il y a 2 groupes principaux:
- **global**, ce qui correspond à tout les test qui ont besoins de plusieurs en-têtes afin de réaliser le test.
- **uniqueHeader**, tout les tests qui ont besoin d'une seule en-tête

#### global
Le tableau **global** contient une liste d'entrée permettant de réaliser des validations sur une seule en-tête à la fois, ces tests sont réalisés dans l'ordre définit dans le fichier.

#### uniqueHeader

Le tableau **uniqueHeader** contient une liste d'entrée permettant de réaliser des validations sur une seule en-tête à la fois, ces tests ne sont pas réalisés dans l'ordre définit dans le fichier.

Comme on peut le voir dans les exemples, il y a 2 champs principaux à remplir, qui sont:
- **name**: ceci correspond au nom du champ dans le fichier d'entrée, ce nom sera utilisé pour la recherche dans le pli
- **validators**: c'est un objet qui contient une liste de validateur simple et de validateur complexe. Ces deux listes sont optionnelles, on est donc pas obligé de mettre un validateur simple et complexe en même temps. (Voir les examples)
  * **simple**: ces validateurs n'ont pas besoin d'arguments particulier, comme par exemple le validateur **notNull** qui vérifie que la valeur n'est nulle.
  * **complex**: ces validateur ont besoin d'arguments pour fonctionner, ils sont donc complexes. Par example le validateur **notEgalTo** vérifie que la valeur du pli ne correspond pas à l'un des arguments donné.

Les validateurs disponible actuellement sont:
- **simple**
  * **notNull**
  * **unique**
- **complex**
  * **notEgalTo**
  * **matchLength**
  * **maxLength**

Validateurs prévu dans le futur:
- **simple**
  * **null**
- **complex**
  * **egalTo**
  * **matchWithFile**

##### Examples

**json:**
```json
{
  "uniqueHeader": [
    {
      "name": "SERVICE",
      "validators": {
        "simple": [
          "notNull"
        ],
        "complex": [
          {
            "name": "notEgalTo",
            "arguments": ["EIM", "TEST"]
          }
        ]
      }
    },
    {
      "name": "DB_DATE_NUM",
      "validators": {
        "simple": [
          "notNull",
          "unique"
        ]
      }
    }
  ]
}
```

**yaml:**
```yaml
---
uniqueHeader:
- name: SERVICE
  validators:
    simple:
    - notNull
    complex:
    - name: notEgalTo
      arguments:
      - EIM
      - TEST
- name: DB_DATE_NUM
  validators:
    simple:
    - notNull
    - unique
```


#### Exemples global
**json:**
```json
{
  "multiHeaders": [],
  "uniqueHeader": [
    {
      "name": "SERVICE",
      "validators": {
        "simple": [
          "notNull"
        ],
        "complex": [
          {
            "name": "notEgalTo",
            "arguments": [
              "ACTION SOCIALE",
              "SANTE",
              "CARRIERE",
              "RECOUVREMENT",
              "RETRAITE"
            ]
          }
        ]
      }
    },
    {
      "name": "DB_DATE_NUM",
      "validators": {
        "simple": [
          "notNull",
          "unique"
        ]
      }
    },
    {
      "name": "SIRET_AFE",
      "validators": {
        "complex": [
          {
            "name": "matchLength",
            "arguments": [
              "14"
            ]
          }
        ]
      }
    },
    {
      "name": "NUMSS",
      "validators": {
        "complex": [
          {
            "name": "maxLength",
            "arguments": [
              "15"
            ]
          }
        ]
      }
    }
  ]
}
```

**yaml:**
```yaml
---
multiHeaders: []
uniqueHeader:
- name: SERVICE
  validators:
    simple:
    - notNull
    complex:
    - name: notEgalTo
      arguments:
      - ACTION SOCIALE
      - SANTE
      - CARRIERE
      - RECOUVREMENT
      - RETRAITE
- name: DB_DATE_NUM
  validators:
    simple:
    - notNull
    - unique
- name: SIRET_AFE
  validators:
    complex:
    - name: matchLength
      arguments:
      - '14'
- name: NUMSS
  validators:
    complex:
    - name: maxLength
      arguments:
      - '15'
```

### Rejects

Nom du ficiher: **rejects.json/yaml**  

Ce fichier de configuration permet de configurer les codes d'erreurs pour chaque validateur.

Comme on peut le voir dans les exemples, il y a 2 groupes principaux:
- **code**: ce qui correspond au code d'erreur qui sera donner en cas de rejet.
- **detail**: le détail de l'erreur, c'est le message texte qui sera associé au code d'erreur.
- **validators**: tout les tests qui correspondent au code de rejet

#### Amélioration possible

Faire en sorte de pouvoir définir une erreur pour un validateur d'une en-tête en particulier.

#### Examples

**json:**
```json
[
  {
    "code": "Generic-1",
    "detail": "Valeur Obligatoire non présente",
    "validators": {
      "simple": [
        "notNull",
        "notEmpty"
      ]
    }
  },
  {
    "code": "Generic-2",
    "detail": "Pli incohérent (valeurs différentes pour un même attribut)",
    "validators": {
      "simple": [
        "unique"
      ]
    }
  },
  {
    "code": "Generic-3",
    "detail": "Plusieurs chemins PDF pour le même document",
    "validators": {
      "simple": [
        "isSingleFile"
      ]
    }
  },
  {
    "code": "Generic-4",
    "detail": "Fichier physiquement absent sur le système",
    "validators": {
      "simple": [
        "fileExist"
      ]
    }
  },
  {
    "code": "Generic-5",
    "detail": "Des valeurs ne correspondent pas à la taille indiquée",
    "validators": {
      "complex": [
        {
          "name": "matchLength"
        }
      ]
    }
  },
  {
    "code": "Generic-6",
    "detail": "Des valeurs sont trop longues",
    "validators": {
      "complex": [
        {
          "name": "maxLength"
        }
      ]
    }
  },
  {
    "code": "Generic-7",
    "detail": "Des valeurs ne sont pas autorisées",
    "validators": {
      "complex": [
        {
          "name": "notEgalTo"
        }
      ]
    }
  }
]
```

**yaml:**
```yaml
---
- code: Generic-1
  detail: Valeur Obligatoire non présente
  validators:
    simple:
    - notNull
    - notEmpty
- code: Generic-2
  detail: Pli incohérent (valeurs différentes pour un même attribut)
  validators:
    simple:
    - unique
- code: Generic-3
  detail: Plusieurs chemins PDF pour le même document
  validators:
    simple:
    - isSingleFile
- code: Generic-4
  detail: Fichier physiquement absent sur le système
  validators:
    simple:
    - fileExist
- code: Generic-5
  detail: Des valeurs ne correspondent pas à la taille indiquée
  validators:
    complex:
    - name: matchLength
- code: Generic-6
  detail: Des valeurs sont trop longues
  validators:
    complex:
    - name: maxLength
- code: Generic-7
  detail: Des valeurs ne sont pas autorisées
  validators:
    complex:
    - name: notEgalTo

```

### Creators

Nom du ficiher: **creators.json/yaml**  

Ce fichier de configuration permet de configurer les nouvelles valeurs à créer dans le pli.

Comme on peut le voir dans les exemples, il y a 2 groupes principaux:
- **name**: Nom de l'en-têtes à créer, attention ce n'est pas le cas de tous.
- **creator**: Le nom du créateur.
- **headers**: La liste des en-têtes
- **arguments**: Les arguments pour le créateur.

#### Les différents créateurs

##### concat

##### subString

##### fileMapperList

##### counter

#### Examples

**json:**
```json
[
  {
    "name": "DB_DATE_NUM",
    "creator": "counter",
    "headers": [
      "NUM_PLI",
      "NUM_LOT"
    ],
    "arguments": [
      "999",
      "9999"
    ]
  },
  {
    "name": "ID_LOT",
    "creator": "concat",
    "headers": [
      "DB_DATE_NUM",
      "1_MI_ARC",
      "NUM_LOT"
    ],
    "arguments": [
      "_"
    ]
  },
  {
    "name": "ID_PLI",
    "creator": "concat",
    "headers": [
      "DB_DATE_NUM",
      "1_MI_ARC",
      "NUM_LOT",
      "NUM_PLI"
    ],
    "arguments": [
      "_"
    ]
  },
  {
    "name": "NUM_BOITE",
    "creator": "concat",
    "headers": [
      "DB_DATE_NUM",
      "SERVICE"
    ],
    "arguments": [
      "_"
    ]
  },
  {
    "name": "TITRE",
    "creator": "concat",
    "headers": [
      "SERVICE",
      "TYPE_PLI_COL",
      "TYPE_PIECE_COL"
    ],
    "arguments": [
      "_"
    ]
  },
  {
    "name": "NUMSS_VALUE",
    "creator": "subString",
    "headers": [
      "NUMSS"
    ],
    "arguments": [
      "0",
      "12"
    ]
  },
  {
    "name": "NUMSS_KEY",
    "creator": "subString",
    "headers": [
      "NUMSS"
    ],
    "arguments": [
      "13",
      "15"
    ]
  },
  {
    "name": "FILE_NAME",
    "creator": "concat",
    "headers": [
      "ID_PLI",
      ".tif"
    ],
    "arguments": [
      ""
    ]
  },
  {
    "name": "TYPE_PIECE",
    "creator": "fileMapperList",
    "headers": [
      "TYPE_PIECE_COL"
    ],
    "arguments": [
      "config/configMetier.csv",
      ",",
      "0"
    ]
  },
  {
    "name": "SOUS_TYPE_PIECE",
    "creator": "fileMapperList",
    "headers": [
      "TYPE_PIECE_COL"
    ],
    "arguments": [
      "config/configMetier.csv",
      ",",
      "1"
    ]
  }
]
```

**yaml:**
```yaml
---
- name: DB_DATE_NUM
  creator: counter
  headers:
  - NUM_PLI
  - NUM_LOT
  arguments:
  - '999'
  - '9999'
- name: ID_LOT
  creator: concat
  headers:
  - DB_DATE_NUM
  - 1_MI_ARC
  - NUM_LOT
  arguments:
  - _
- name: ID_PLI
  creator: concat
  headers:
  - DB_DATE_NUM
  - 1_MI_ARC
  - NUM_LOT
  - NUM_PLI
  arguments:
  - _
- name: NUM_BOITE
  creator: concat
  headers:
  - DB_DATE_NUM
  - SERVICE
  arguments:
  - _
- name: TITRE
  creator: concat
  headers:
  - SERVICE
  - TYPE_PLI_COL
  - TYPE_PIECE_COL
  arguments:
  - _
- name: NUMSS_VALUE
  creator: subString
  headers:
  - NUMSS
  arguments:
  - '0'
  - '12'
- name: NUMSS_KEY
  creator: subString
  headers:
  - NUMSS
  arguments:
  - '13'
  - '15'
- name: FILE_NAME
  creator: concat
  headers:
  - ID_PLI
  - ".tif"
  arguments:
  - ''
- name: TYPE_PIECE
  creator: fileMapperList
  headers:
  - TYPE_PIECE_COL
  arguments:
  - config/configMetier.csv
  - ","
  - '0'
- name: SOUS_TYPE_PIECE
  creator: fileMapperList
  headers:
  - TYPE_PIECE_COL
  arguments:
  - config/configMetier.csv
  - ","
  - '1'
```

### delivery

Nom du ficiher: **delivery.json/yaml**  

Ce fichier de configuration permet de configurer la méthode de livraison.

**MODULE A DEFINIR**

#### Exemples

**json:**
```json

```

**yaml:**
```yaml

```

### reports

Nom du ficiher: **reports.json/yaml**  

Ce fichier de configuration permet de configurer les fichiers de rapports en plus du fichier résumé.

**MODULE A DEFINIR, les examples sont suceptible d'être modifié **

#### Exemples

**json:**
```json
[
  {
    "name": "rejects",
    "type": "csv",
    "foldStatus": "reject",
    "headers": [
      "DOCIDX",
      "DOCIDXGED",
      "DB_NUMPLI",
      "FILENAMED",
      "ID_PLI",
      "CODE_REJET",
      "DETAIL_REJET"
    ]
  },
  {
    "name": "trace",
    "type": "csv",
    "foldStatus": "accept",
    "headers": [
      "DOCIDX",
      "DOCIDXGED",
      "DB_NUMPLI",
      "FILENAMED",
      "ID_PLI",
      "FILE_NAME"
    ]
  }
]
```

**yaml:**
```yaml
---
- name: rejects
  type: csv
  foldStatus: reject
  headers:
  - DOCIDX
  - DOCIDXGED
  - DB_NUMPLI
  - FILENAMED
  - ID_PLI
  - CODE_REJET
  - DETAIL_REJET
- name: trace
  type: csv
  foldStatus: accept
  headers:
  - DOCIDX
  - DOCIDXGED
  - DB_NUMPLI
  - FILENAMED
  - ID_PLI
  - FILE_NAME
```

## Modifications du code

### Ajout d'un validateurs non existant


### Ajout d'un créateur non existant


### Ajout d'une nouvelle méthode de livraison


### Modifications trop complexe ou erreurs obscures

Contacter Charles Delorme à l'adresse email: charlesdelormefr@gmail.com

## Contribution au projet

### IntelliJ

#### Installation du projet

- Aller dans le menu de selection de projets puis cliquer sur **Import Project**
- Choisissez ensuite **Import project from external model** puis cliquer sur **Gradle**
- Cochez les cases suivantes:
  * **Use auto-import**
  * **Create sepate module per source set**
- Selectionner **Use default wrapper (recommended)**
- Patienter quelques secondes le temps qu'IntelliJ detecte tout les modules et télécharge les librairies

#### Lancement du projet

 - Aller dans l'onglet **Run** puis cliquer sur **Edit Configuration**
 - Cliquer sur le **+** et ensuite sur **Gradle**
 - Donner un nom à la configuration de lancement
 - Chosir le project gradle **exged** en cliquant sur l'icone du dossier
 - Vous avez deux choix pour lancer l'application:
    * Créer un jar executable, mettez dans **Tasks** la tâche **shadowJar**. Le fichier jar sera dans le dossier **build/libs**
    * Executer l'application directement à l'aide de tâches **run**

### Eclipse

Télécharger la version préparé d'eclipse avec les plugins et configuration pour le projet.

**LIEN**

#### Installation du projet

- Clic droit sur la panneau **Project Explorer** et cliquer sur **Import** -> **Import**
- Dans le dossier **Gradle**, selectionner **Existing Gradle Project**
- Entrer le chemin vers le dossier du projet et passer à l'étape suivante
- Appuyer sur **Finish** et attendre quelques minutes le temps que le projet s'initialise

Le projet **exged** est la racine du projet, les autres projets sont des sous-modules de l'application

#### Lancement du projet
- Cliquer sur le menu **Run**, puis sur l'item **Run configuration**
- Cliquer sur **Gradle Project** et ensuite sur l'icone **+** pour créer une nouvelle configuration
- Nommer la configuration
- Selectionner le projet **exged** en cliquant sur le boutton **Workspace...**
- Vous avez deux choix pour lancer l'application:
   * Créer un jar executable, mettez dans **Gradle Tasks** la tâche **shadowJar**. Le fichier jar sera dans le dossier **build/libs**
   * Executer l'application directement à l'aide de tâches **run**
