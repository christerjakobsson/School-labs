#include <stdio.h>
#include <stdlib.h>
#include "dlist.h"
#include <string.h>
#include <ctype.h>

/* Struktur som kommer innehålla de fyra kolumnernas innehåll */
typedef struct {
    char artist[41];
    char album[41];
    char Type[3];
    char grade[4];
}fileInfo;

void sortMeny();
int compareArtist(dlist *left, dlist *right);
int compareAlbum(dlist *left, dlist *right);
int compareType(dlist *left, dlist *right);
int compareGrade(dlist *left, dlist *right);
void readFile(dlist *lista);
void printList(dlist *lista);
void printMenu();
void mergeSort(dlist *lista, int (*f)(dlist*, dlist*));
void merge(dlist *left, dlist *right, dlist *l, int (*f)(dlist*, dlist*));
int length(dlist *list);
void freeElements(dlist *l);

int main(void) {
    dlist *lista = dlist_empty();
    int exit = 0, choice, s, len;

    do {
        printMenu();
        printf("Make your choice >> ");
        scanf("%d", &choice);
        printf("\n");

        switch(choice) {
        case 1:
            readFile(lista);
            printf("\n");
            getc(stdin);
            break;
        case 2:
            printList(lista);
            getc(stdin);
            break;
        case 3:
            sortMeny();
            scanf("%d", &s);
            if(s == 1) {
                mergeSort(lista, compareArtist);
            }
            if(s == 2) {
                mergeSort(lista, compareAlbum);
            }
            if(s == 3) {
               mergeSort(lista, compareType);
            }
            if(s == 4) {
                mergeSort(lista, compareGrade);
            }
            getc(stdin);
            break;
        case 4:
            getc(stdin);
            printf("Are you sure you want to quit (y/n)?");
            scanf("%d", &choice);

            if(choice == 4) {
                exit = 1;
            }
            break;
        default:
            exit = 1;
            break;
        }
    }while(exit != 1);

    /* Friar varje element i listan
     * så allt minne avallokeras. */
    len = length(lista);
    dlist_position pos = dlist_first(lista);

    for(int i = 0; i < len; i++) {
        free(dlist_inspect(lista, pos));
        pos = dlist_next(lista, pos);
    }

    dlist_free(lista);
    return 0;
}


/* Funktion som tar en lista och returnerar längden på listan. */
int length(dlist *list) {
    int i = 0;
    dlist_position position = dlist_first(list);

    while(!dlist_isEnd(list, position)) {
        i++;
        position = dlist_next(list, position);
    }
    return i;
}

/* Syfte: Dela upp listan i mindre listor
 *  Returvärde Pekare till de nya listorna.
 */
void mergeSort(dlist *l, int (*f)(dlist*, dlist*)) {
    int len = length(l);

    if(len <= 1) {
     return;
    }
    else {
        dlist *left =  dlist_empty();
        dlist *right = dlist_empty();


        int middle = (len / 2);

        dlist_position p = dlist_first(l);
        dlist_position pleft = dlist_first(left);

        for(int i = 0; i < middle; i++) {
            pleft = dlist_insert(left, pleft, dlist_inspect(l, p));
            p = dlist_next(l, p);
            pleft = dlist_next(left, pleft);
        }

        dlist_position pright = dlist_first(right);

        for(int i = middle; i < len; i++) {
            pright = dlist_insert(right, pright, dlist_inspect(l, p));
            p = dlist_next(l, p);
            pright = dlist_next(right, pright);
        }

        mergeSort(left, f);
        mergeSort(right, f);

        merge(left, right, l, f);
    }
}

/* Syfte: Stoppa in värdena ifrån de små listorna tillbaka till den stora.
 * Returvärde: Den nya listan har blivit sorterad efter den kolumn användaren
 * valt
 */
void merge(dlist *left, dlist *right, dlist *l, int (*f)(dlist*, dlist*)) {
    dlist_position posM  = dlist_first(l);
    dlist_position posL = dlist_first(left);
    dlist_position posR = dlist_first(right);

    int lleft = length(left);
    int lright = length(right);

    while(lleft > 0 || lright > 0) {
        if(lleft > 0 && lright > 0) {
            if(f(left, right) == 2) {
                posM = dlist_insert(l, posM, dlist_inspect(left, posL));
                posL = dlist_remove(left, posL);
                posM = dlist_next(l, posM);
                posM = dlist_remove(l, posM);
                lleft--;
                }
            else {
                posM = dlist_insert(l, posM, dlist_inspect(right, posR));
                posR = dlist_remove(right, posR);
                posM = dlist_next(l, posM);
                posM = dlist_remove(l, posM);
                lright--;
            }
        }
        else if(lleft > 0) {
            posM = dlist_insert(l, posM, dlist_inspect(left, posL));
            posL = dlist_remove(left, posL);
            posM = dlist_next(l, posM);
            posM = dlist_remove(l, posM);
            lleft--;
        }
        else if(lright > 0) {
            posM = dlist_insert(l, posM, dlist_inspect(right, posR));
            posR = dlist_remove(right, posR);
            posM = dlist_next(l, posM);
            posM = dlist_remove(l, posM);
            lright--;
        }
    }
    dlist_free(left);
    dlist_free(right);
}

/* Syfte: Läsa in en godtycklig fil och stoppa in värden i en riktad lista */
void readFile(dlist *lista)  {
    char string[100];
    char *tk;

    char filename[20];
    printf("Vilken fil vill du öppna? (filnamn.txt): ");
    scanf("%s", filename);

    FILE *fp = fopen(filename, "r");

    if(!fp) {
        fprintf(stderr, "ERRO0R: FILE COULD NOT BE OPENED");
        exit(1);
    } else {
        printf("File Loaded Ok!");
        }

    dlist_position pos = dlist_first(lista);

    while (fgets(string, 110, fp) !=  NULL) {
        fileInfo *strukt = malloc(sizeof(fileInfo));

        tk = strtok (string, ";\n");
        strcpy(strukt->artist, tk);

        tk = strtok (NULL, ";\n");
        strcpy(strukt->album, tk);

        tk = strtok (NULL, ";\n");
        strcpy(strukt->Type, tk);

        tk = strtok (NULL, ";\n");
        strcpy(strukt->grade, tk);

        dlist_insert(lista, pos, strukt);
        pos = dlist_next(lista, pos);
    }
    fclose(fp);
}

void sortMeny() {

    printf("Efter vilken kolumn vill du sortera listan?\n"
            "1. Artist\n"
            "2. Album\n"
            "3. Typ\n"
            "4. Grade\n");
}
/* Syfte: jämför två strängar och returnerar ett värde som visar vilken
 * som är större än den andra.
 * Returvärde: 1 om första värdet är störst, annars 2.
 */
int compareArtist(dlist *left, dlist *right) {
    fileInfo *first = (fileInfo*)dlist_inspect(left, dlist_first(left));
    fileInfo *sec = (fileInfo*)dlist_inspect(right, dlist_first(right));

    if(strcmp(first->artist, sec->artist) > 0) {
        return 1;
    }
    else {
        return 2;
    }
}
/* Syfte: jämför två strängar och returnerar ett värde som visar vilken
 * som är större än den andra.
 * Returvärde: 1 om första värdet är störst, annars 2.
 */
int compareAlbum(dlist *left, dlist *right) {
    fileInfo *first = (fileInfo*)dlist_inspect(left, dlist_first(left));
    fileInfo *sec = (fileInfo*)dlist_inspect(right, dlist_first(right));

    if(strcmp(first->album, sec->album) > 0) {
        return 1;
    }
    else {
        return 2;
    }
}

/* Syfte: jämför två strängar och returnerar ett värde som visar vilken
 * som är större än den andra.
 * Returvärde: 1 om första värdet är störst, annars 2.
 */
int compareType( dlist *left, dlist *right) {
    fileInfo *first = (fileInfo*)dlist_inspect(left, dlist_first(left));
    fileInfo *sec = (fileInfo*)dlist_inspect(right, dlist_first(right));

    if(strcmp(first->Type, sec->Type) > 0) {
        return 1;
    }
    else {
        return 2;
    }
}

/* Syfte: jämför två strängar och returnerar ett värde som visar vilken
 * som är större än den andra.
 * Returvärde: 1 om första värdet är störst, annars 2.
 */
int compareGrade(dlist *left, dlist *right) {
    char checkfirst[10][4] = {"NR", "G-", "G", "G+", "VG-", "VG", "VG+", "M-", "M", "M+"};
    int f = 0, s = 0, k;

    fileInfo *first = (fileInfo*)dlist_inspect(left, dlist_first(left));
    fileInfo *sec = (fileInfo*)dlist_inspect(right, dlist_first(right));

    for(int i = 0; i < 10; i++) {
        k = strcmp(first->grade, checkfirst[i]);
        if(k == 0) {
            f = i;
        }
        k = strcmp(sec->grade, checkfirst[i]);
        if(k == 0) {
            s = i;
        }
    }
    if(f >= s) {
        return 1;
    }
    else {
        return 2;
    }
}

void printMenu() {

    printf("\n1. Read File\n"
           "2. Print List\n"
           "3. Sort List\n"
           "4. Quit\n");

}

/* Syfte: skriva ut den aktuella listan.
 * Indata: Pekare till listan.
 */
void printList(dlist *lista) {
    dlist_position pos = dlist_first(lista);
    int k = 1, j = 30;
    char choice[] = "y";
    char line[7] = {"-------"};

    printf("%-40s%-40s%-5s%-5s\n%-40s%-40s%-5s%-5s\n",
           "Artist", "Album",
           "Type", "Grade", line, line, line, line);

    do {
        if(!dlist_isEnd(lista, pos)) {


            fileInfo *f = (fileInfo*)dlist_inspect(lista, pos);
            printf("%-40s%-40s%-5s%-5s\n", f->artist, f->album, f->Type,
                    f->grade);
            pos = dlist_next(lista, pos );

            if(k == j) {
                getc(stdin);
                printf("\nContinue (y/n)? ");
                scanf("%s", choice);
                if(!strcmp("y", choice)) {
                    printf("%-40s%-40s%-5s%-4s\n%-40s%-40s%-5s%-5s\n",
                    "Artist", "Album",
                    "Type", "grade", line, line, line, line);
                    j = j + 30;
                }
            }
            k++;
        }
        else {
            return;
        }

    }while(!strcmp("y", choice));
}
