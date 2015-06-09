#ifndef _GOL_H_
#define _GOL_H_


void gol_init(int height, int width);

void gol_finalize(void);

void gol_randomize(void);

void gol_advance(void);

int gol_count_alive(void);

void gol_print(int height, int width);


#endif
