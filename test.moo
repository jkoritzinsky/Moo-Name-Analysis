void other() {
    int x;
    int y;
    bool one;
    bool two;
    bool three;
    one = true;
    two = false;
    three = true;
    x = 0;
    y = 0;
    if(one && two && three) {
        x = 1;
    }
    if(one || two || three) {
        y = 1;
    } 
    else {
        y = -1;
    }
}
struct p {
    int i;
};
struct q {
    int p;
    struct p d;
};
struct r {
    int i;
};
void main() {
    int r;
    struct r e;
}
struct Point {
    int x;
    int y;
};
int f(int x, bool b) { 
  if(b) {
    int x;
  }
  while(true) {
    bool b;
    bool x;
    if(b) {
      int x;
      return;
    }
    else {
      x = true;
      return 1;
    }
  }
}
void g() {
    int a;
    bool b;
    struct Point p;
    struct q test;
    test.d.i = 4;
    b = !b;
    p.x = a;
    b = a == 3;
    f(a + p.y*2, b);
    g();
}
