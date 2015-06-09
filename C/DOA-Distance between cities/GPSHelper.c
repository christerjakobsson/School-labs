#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "graph_nav.h"
#include "hashtable.h"
#include "generate_matrix.h"
#include "array.h"
#include "queue_array.h"
#include "dlist.h"

int min(int a, int b);
unsigned strhash(void *str2);
int readFile(graph *theGraph, FILE *fp);
void floyd(arrayResult matris, array *newMatris, int n);
void userInput(hashtable_t *h, arrayResult matris, array *newMatris);

int main(int argc, char *argv[]) {
    int nodeCount;
    FILE *fp = fopen(argv[1], "r");

    /* Koll ifall filen öppnades korrekt */
    if(!fp) {
        fprintf(stderr, "ERROR, FILE COULD NOT BE LOADED");
        exit(1);
    }
    else {
        printf("%s Loaded\n", argv[1]);
    }

    /* Skapa en graf */
    graph *theGraph = graph_empty(mapVerticeEqual);

    /* Läsa in ifrån filen till grafen */
    nodeCount = readFile(theGraph, fp);

    /* Skapa en avståndsmatris utifrån grafen*/
    arrayResult matris;
    matris = generateMatrixRepresentation(theGraph, nodeCount);

    /* Skapa en hashtabell och sätt memhanlder till den */
    hashtable_t *h = hashtable_empty((2*nodeCount), strhash, strcmp2);
//hashtable_setMemHandler(h, freeStringInt);

    /* Sätta in Index ifrån avståndsmatrisen till hashtabell */
    for(int i = 0; i < nodeCount; i++) {
        int *pek = malloc(sizeof(int));
        *pek = i;
        hashtable_insert(h, array_inspectValue(matris.verticeData, i), pek);
    }

    /* Skapa en ny matris */
    array *newMatris = array_create(2, 0, 0, nodeCount-1, nodeCount-1);
    array_setMemHandler(newMatris, free);

    floyd(matris, newMatris, nodeCount);

    userInput(h, matris, newMatris);


    for(int i = 0; i < nodeCount; i++) {
        free(hashtable_lookup(h, array_inspectValue(matris.verticeData, i)));
    }

    array_free(matris.verticeData);
    array_free(matris.matrix);
    array_free(newMatris);
    hashtable_free(h);
    return 0;
}

/* Funktion: Svara på frågor från användare.
 * Input: Hashtabell med index, modifierad avståndsmatris och index matris
 * Output Inget.
 */
void userInput(hashtable_t *h, arrayResult matris, array *newMatris) {
    bool quit = false;

    while(quit == false) {
        char c1[41], c2[41], q[] = "quit";

        printf("Enter origin and destination: ");
        scanf("%s", c1);


        if(*c1 == *q) {
            quit = true;
        }
        else{
            int *pathIndex = 0;
            scanf("%s", c2);

            if(hashtable_lookup(h, c1) != NULL && hashtable_lookup(h, c2) != NULL) {
                int *fromC = (int*)hashtable_lookup(h, c1);
                int *toC = (int*)hashtable_lookup(h, c2);

                pathIndex = (int*)array_inspectValue(newMatris, *toC, *fromC);
                if(*pathIndex == -1) {
                    printf("No route\n");
                }

                while(*pathIndex != -1) {
                    char *city1, *city2;
                    pathIndex = (int*)array_inspectValue(newMatris, *toC, *fromC);

                    int *length = array_inspectValue(matris.matrix, *fromC, *pathIndex);
                    city1 = (char*)array_inspectValue(matris.verticeData, *fromC);
                    city2 = (char*)array_inspectValue(matris.verticeData, *pathIndex);
                    printf("%s %s %d\n", city1, city2, *length);

                    *fromC = *pathIndex;
                    pathIndex = array_inspectValue(newMatris, *toC, *fromC);
                }
            }
            else {
                printf("%s Or %s doesnt exist\n", c1, c2);
            }
        }
    }
}

/* Funktion: Kollar igenom avståndsmatrisen efter kortaste väg och sätter index i den nya matrisen
 * tillhörande den kortaste vägen.
 * Input Avståndsmatris, Path matris, antalet noder städer.
 * Output: En matris med sträckor och en matris med tillhörande index.
 */
void floyd(arrayResult matris,array *newMatris, int nodeCount) {
    int i , j , k;

    for(i = 0; i < nodeCount; i++) {
        for(j = 0; j < nodeCount; j++) {
            int *ok = array_inspectValue(matris.matrix, i, j);
            if(i == j || *ok == 0) {
                int *tempPekare = malloc(sizeof(int));
                *tempPekare = -1;
                array_setValue(newMatris, tempPekare, i, j);
            }
            else {
                int *tempPekare2 = malloc(sizeof(int));
                *tempPekare2 = i;
                array_setValue(newMatris, (data)tempPekare2, i , j);
            }
        }
    }

    for(int i = 0; i < nodeCount; i++) {
        for(int j = 0; j < nodeCount; j++) {
            int *temp;
            temp = array_inspectValue(matris.matrix, i, j);
            if((*temp == 0) && !(i == j)) {
                int *temp1 = malloc(sizeof(int));
                *temp1 = -1;
                array_setValue(matris.matrix, (data)temp1, i, j);
            }
            else if(*temp == -1 || (i==j)) {
                int *temp2 = malloc(sizeof(int));
                *temp2 = 0;
                array_setValue(matris.matrix, temp2, i, j);
            }
        }
    }

    int t1, t2;
    for(k = 0; k < nodeCount; k++) {
        for(i = 0; i < nodeCount; i++) {
            for(j = 0; j < nodeCount; j++) {
                int *ij = array_inspectValue(matris.matrix, i,j);
                int *ik = array_inspectValue(matris.matrix, i,k);
                int *kj = array_inspectValue(matris.matrix, k,j);

                t1 = *ij; t2 = *ik + *kj;
                    if(*ij < 0) {
                        t1 = 9999999;
                    }
                    if(*ik < 0 || *kj < 0) {
                        t2 = 9999999;
                    }
                    int *p_kj = array_inspectValue(newMatris, k, j);
                if(t1 > t2) {
                    int *tempPekare3 = malloc(sizeof(int));
                    *tempPekare3 = *p_kj;
                    array_setValue(newMatris, tempPekare3, i, j);
                }
                int *tempPekare4 = malloc(sizeof(int));
                *tempPekare4 = min(t1, t2);
                array_setValue(matris.matrix, tempPekare4, i, j);

            }
        }
    }
}

/* Funktion: Hitta det minsta av två värden.
 * Input, 2 Tal.
 * Output: det minsta av talen.
 */
int min(int a, int b) {
    if(a < b) {
        return a;
    }
    else {
        return b;
    }
}

/* Funktion: lösa in städer och noder från filen och skapa en graf.
 * Input: en tom graf och en filpekare.
  *Output: En graf byggd från filen.
  */
int readFile(graph *theGraph, FILE *fp) {
    edge k;
    int count = 0, rader;

    fscanf(fp, "%d", &rader);

    hashtable_t *hash= hashtable_empty((2*rader), strhash, strcmp2);

        for(int i = 0; i < rader; i++) {
            bool if1, if2;
            char *c1 = calloc(41, sizeof(char));
            char *c2 = calloc(41, sizeof(char));
            int *e = malloc(sizeof(int));

            fscanf(fp, "%s %s %d", c1, c2, e);

            mapvertice *nod1 = malloc(sizeof(mapvertice));
            mapvertice *nod2 = malloc(sizeof(mapvertice));
            nod1->cityName = c1;
            nod2->cityName = c2;
            k.v1 = nod1;
            k.v2 = nod2;



            if1 = false;
            if2 = false;

            if(hashtable_lookup(hash, c1) == NULL) {
                nod1->visited=false;
                hashtable_insert(hash, c1, c1);
                graph_insertNode(theGraph, nod1);
                count++;
                if1 = true;
            }

            if(hashtable_lookup(hash, c2) == NULL) {
                nod2->visited=false;
                hashtable_insert(hash, c2, c1);
                graph_insertNode(theGraph, nod2);
                count++;
                if2 = true;
            }

            graph_insertEdge(theGraph, k);
            graph_setEdgeLabel(theGraph, k, e);


            if(if1 == false) {
                free(c1);
                free(nod1);
            }
            if(if2 == false) {
                free(c2);
                free(nod2);
            }
    }

    if(graph_isEmpty(theGraph)) {
        printf("Grafen är tom!");
    }

    hashtable_free(hash);
    fclose(fp);
    return count;
}

/* Till Hashtabell */
unsigned strhash(void *str2) {
    char *str=str2;
    unsigned hash=0;
    int i=0;
    while(str[i]) {
        hash ^= str[i]<<(i*4%(sizeof(int)*8));
        i++;
    }
    return hash;
}
