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
int f(int x, bool b) { }
void g() {
    int a;
    bool b;
    struct Point p;
    p.x = a;
    b = a == 3;
    f(a + p.y*2, b);
    g();
}
