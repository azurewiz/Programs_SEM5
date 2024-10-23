#include <stdio.h>

#define LOOP_VAR 25

int main(void) {
  int x, y;
  y = 1;

  for (x = 0; x < LOOP_VAR; x++) {
    printf("%d", y);  y *= x;
  }
  return 1;
}
