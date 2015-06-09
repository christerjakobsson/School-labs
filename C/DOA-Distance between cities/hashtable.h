#ifndef __HASHTABLE_H
#define __HASHTABLE_H
#include <stdlib.h>
#include <stdbool.h>

//Written by Johan Eliasson <johane@cs.umu.se>.
//May be used in the course Datastrukturer och Algoritmer (C) at Ume� University.
//Usage exept those listed above requires permission by the author.

//Datatypes for values and keys in the hashtable
typedef void * ht_key_t;
typedef void * ht_value_t;


//Function types for functions needed by the hashtable.
//Semantics of functions described in connection with hashtable_empty
typedef unsigned hash_fnk_t(ht_key_t key);
typedef bool cmp_fnk_t(ht_key_t key,ht_key_t key2);
typedef void memfree_fnk_t(ht_key_t key,ht_value_t value);

//Datatype for internal usage by the hashtable. Not intended or external usage
typedef struct {
    ht_key_t key;
    ht_value_t value;
} key_valuestore_t;

//Hashtable datastructure. Values should only be modifyed by the supplied
//functions.
typedef struct {
    key_valuestore_t **data;
    unsigned size;
    int numelem;
    int numinsertions;
    cmp_fnk_t *cmp_fnk;
    memfree_fnk_t *memfree_fnk;
    hash_fnk_t *hash_fnk;
}hashtable_t;

//Function for creating a new empty hashtable
//Paramerers:
//   size:Starting size of table should be approx 2*estimated amount of
//        values to insert (the size will grow if needed but that operation
//        is expensive)
//   hash_fkn:A function for computing a hash from a key. The return value
//            of the function should be in the range 0-UINT_MAX. The value
//            will internaly be converted to a value in the range 0-size
//   cmp_fnk:A function for comparing two keys. Should return false for keys
//           that are not equal and a value true for equal keys
//Return value:
//   The function will return an empty hashtable.
hashtable_t *hashtable_empty(unsigned size,hash_fnk_t hash_fnk,cmp_fnk_t cmp_fnk);

/*
Function for installing a memory handler so that it can manage dynamic memory used for keys and/or values.
When a memory handler is installed the hashtable will free all keys and/or values that it no longer needs using this function,
Parameters: t - The hashtable
            f - A funktion that should free memory for one key and one
                value (The function may release only the key or the value if needed).
                Used by the hashtable to manage memory. 
Comments:  
       Should be called directly after creation of the hashtable. The hastable will work
       even if this function is not used, but the responsibility to free the memory
       for the keys and values then falls on the users of the table.
*/
void hashtable_setMemHandler(hashtable_t *t, memfree_fnk_t *f);

//Function for looking up a value in a hashtable.
//Parameters:
//    t:The hashtable
//    key:The key
//Return value:
//    Will return NULL if the key is not found or else a pointer to a value
//    corresponding to the key. The hashtable manages the memory for the
//    return value. If the value is to be used after removal or insertions using
//    the same key, or removal of the table, the caller should copy the value.
ht_value_t hashtable_lookup(hashtable_t *t, ht_key_t key);

//Function for inserting a value into the hashtable. The hashtable will
//take ownership of and manage the memory used by the value and key using
//the function sent to hashtable_empty.
void hashtable_insert(hashtable_t *t, ht_key_t key, ht_value_t value);

//Function for checking i the hashtable is empty.
bool hashtable_isEmpty(hashtable_t *t);

//Function for removing the mapping between a key and a value in the hashtable
//Memory used by the inserted key and value will be freed.
void hashtable_remove(hashtable_t *t, ht_key_t key);

//Function for releasing all the memory used by the hashtable. The
//hashtable is invalid after a call to this function.
void hashtable_free(hashtable_t *t);
#endif
