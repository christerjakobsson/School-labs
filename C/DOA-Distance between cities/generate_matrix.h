#include "queue_array.h"
#include "array.h"
#include "graph_nav.h"

//Strukt ni ska använda för noderna om ni tänkt använda generate matrix
typedef struct {
    bool visited; //bör vara false för ny nod
    char *cityName; //minnet för strängen bör ha allokerats dynamiskt
} mapvertice;

//Struct för resultatet från generateMatrixRepresentation
typedef struct arrayResult {
    array *verticeData; //innehåller stadsnamnen (på samma index som de svarar
                        //mot i förbindelsematrisen)
    array *matrix; //innehåller förbindelsematris
} arrayResult;

/*
Syfte: Generera en förbindelsematris med pekare int-värden som innehåller avstånd
       mellan noderna finns ingen förbindelse så har motsvarande position värdet 0.
Parametrar: g- grafen. Grafen ska ha pekare till int värden på kanterna och mapVertice
               pekare som värden för noderna. Minne för dessa ska vara allokerat
               dynamiskt (funktionen avallokerar sedan detta minne)
            numNodes - antalet noder i grafen
Returvärde: Ett värde av typen arrayresultat (en struct) som innehåller en förbindelsematris
            (numNodes x numNodes stor indexerad från 0) och en array (numNodes stor indexerat
            från 0) som innehåller stadsnamnen i grafen för respektive index
            i förbindelsematrisen (eg på index 0 ligger stadsnamnet som hör till index 0 i
            matrisen osv).
Kommentarer: Förbindelsematrisen och arrayen med stadsnamnen har båda minneshanterare satta.
             Grafen kommer efter anrop att vara borttagen.
             Minneshaterare sätts internt för grafen så detta behöver ej göras innan anrop av
             funktionen.
*/
arrayResult generateMatrixRepresentation(graph *g,int numNodes);

//Funktion som behövs för att skapa en graf med mapVertice-noder
bool mapVerticeEqual(vertice v1,vertice v2);

/********************Hjälpfunktioner*********************************/
//Används internt i generateMatrixRepresentation

int indexOfVertice(array *a,vertice v,int maxIndex,equalityFunc *verticeEQ);
bool mapVerticeIsVisited(vertice v);
void mapVerticeVisit(vertice v);
void mapVerticeFree(vertice v);

bool strcmp2(void *str1,void *str2);

//strdup not available in c99 so providing my own implementation

/* Memory is allocated for the copy. Users of this function is responsible
 * for deallocating this memory */
char *strdup(const char *str);
