//Written by Johan Eliasson <johane@cs.umu.se>.
//May be used in the course Datastrukturer och Algoritmer (C) at Umeå University.
//Usage exept those listed above requires permission by the author.

/*
Datatypen Kö enligt definitionen på sidan 155 i Lars-Erik Janlert,
Torbjörn Wiberg Datatyper och algoritmer 2., [rev.] uppl.,Lund,
Studentlitteratur, 2000, x, 387 s. ISBN 91-44-01364-7

Implementerar kö med hjälp av en array

Som standard så är användaren av datatypen ansvarig för att avallokera
minnet för datavärdena. Genom att anropa queue_setMemHandler och till denna
skicka med en funktion för att avallokera ett dataobjekt så kommer kön
ta över ansvaret för minneshanteringen och automatiskt avallokera värdena
då de tas bort från kön. OBS i detta fall så måste användaren kopiera
värdena om de ska finnas kvar efter att ha tagits bort från kön.
*/

#include <stdlib.h>
#include "queue_array.h"

/*
Syfte: Skapa en ny kö
Returvärde: Den nyskapade kö (queue *)
Kommentarer: Då man använt kön färdigt så måste minnet för kön
             avallokeras via funktionen queue_free
*/
queue *queue_empty() {
   queue *q = calloc(1, sizeof(queue));
   q->data=array_create(1,0,128);
   q->maxSize=128;
   q->start=q->stop=-1;
   return q;
}

/*
Syfte: Installera en minneshanterare för kön så att den kan ta över
       ansvaret för att avallokera minnet för värdena då de ej finns kvar
       i kön mer.
Parametrar: q - Kön (queue *)
            f - en funktionspekare till en funktion som tar in ett värde
                av typen data (void pekare) och avallokerar minnet för värdet
                den pekar på (ett av den typ av värden som lagras i kön).
Kommentarer: Bör anropas direkt efter att kön skapats om funktionaliteten
             ska användas. Kön funkar även utan att denna funktion anropas,
             men det är då upp till användaren av datatypen att avallokera allt
             minne för datat som lagras i kön.
*/
void queue_setMemHandler(queue *q, memFreeFunc *f) {
    q->freeFunc=f;
}

/*
Syfte: Avallokera minne för kön.
Parametrar: q - Kön (queue *)
Kommentarer: Minne för ev värden i kön avallokeras bara om en funktion
             för detta är satt via queue_setMemFunc. Annars är det användarens
             uppgift att avallokera minnet för alla värden i kön.
*/
void queue_free(queue *q) {
   if(q->freeFunc!=NULL) {
      while(!queue_isEmpty(q)) {
         queue_dequeue(q);
      }
   }
   array_free(q->data);
   free(q);
}

/*
Syfte: Lägga till ett värde sist i kön
Parametrar: q - Kön (queue *)
            p - värdet som ska läggas till (data)
Kommentarer:
*/
void queue_enqueue(queue *q, data p) {
   q->size++;
   if(q->size == q->maxSize) {
       array *arr=array_create(1,0,q->maxSize*2);
       for(int i=0;i<q->maxSize;i++)
          array_setValue(arr,array_inspectValue(q->data,i),i);
       array_free(q->data);
       q->data=arr;
       q->maxSize=q->maxSize*2;
   }
   q->stop = (q->stop+1)%q->maxSize;
   array_setValue(q->data,p,q->stop);
}

/*
Syfte: Konrollera om kön är tom
Parametrar: q - Kön (queue *)
Returvärde: true om kön är tom false annars
Kommentarer:
*/
bool queue_isEmpty(queue *q) {
   return q->size == 0;
}

/*
Syfte: Hämtar första värdet i kön
Parametrar: q - Kön (queue *)
Returvärde: värdet som fanns längst ram i kön
Kommentarer: ej definierad för tom kö.
*/
data queue_front(queue *q) {
   if(q->size==0)
      return NULL;
   return array_inspectValue(q->data,(q->start+1)%q->maxSize);
}

/*
Syfte: Ta bort första värdet i kön
Parametrar: q - Kön (queue *)
Kommentarer: odefinierad för en tom kö
*/
void queue_dequeue(queue *q) {
   if(q->size==0)
      abort();
   if(q->freeFunc!=NULL) 
      q->freeFunc(array_inspectValue(q->data,(q->start+1)%q->maxSize));
   q->size--;
   q->start = (q->start+1)%q->maxSize;
}

