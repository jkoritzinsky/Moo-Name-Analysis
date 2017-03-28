struct p {
    int i;
};
void main() {
    int p;
    struct p e;
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
    }
    else {
      x = true;
    }
  }
}
void g() {
    int a;
    bool b;
    struct Point p;
    p.x = a;
    b = a == 3;
    f(a + p.y*2, b);
    g();
}
