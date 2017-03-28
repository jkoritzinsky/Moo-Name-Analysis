###
# This Makefile can be used to make a parser for the moo language
# (parser.class) and to make a program (P3.class) that tests the parser and
# the unparse methods in ast.java.
#
# make clean removes all generated files.
#
###

JC = javac
CP = ~cs536-1/public/tools/deps_src/java-cup-11b.jar:~cs536-1/public/tools/deps_src/java-cup-11b-runtime.jar:~cs536-1/public/tools/deps:.
CP2 = ~cs536-1/public/tools/deps:.

P4.class: P4.java parser.class Yylex.class ASTnode.class
	$(JC)    P4.java

parser.class: parser.java ASTnode.class Yylex.class ErrMsg.class
	$(JC)      parser.java

parser.java: moo.cup
	java   java_cup.Main < moo.cup

Yylex.class: moo.jlex.java sym.class ErrMsg.class
	$(JC)   moo.jlex.java

ASTnode.class: ast.java ErrMsg.java FnSym.java StructDefSym.java StructSym.java
	$(JC)  ast.java

moo.jlex.java: moo.jlex sym.class
	java    JLex.Main moo.jlex

sym.class: sym.java
	$(JC)    sym.java

sym.java: moo.cup
	java    java_cup.Main < moo.cup

ErrMsg.class: ErrMsg.java SemSym.class
	$(JC) ErrMsg.java

FnSym.class: FnSym.java SemSym.class
	$(JC) FnSym.java

StructDefSym.class: StructDefSym.java SemSym.class
	$(JC) StructDefSym.java

StructSym.class: StructSym.java SemSym.class
	$(JC) StructSym.java

SemSym.class: SemSym.java
	$(JC) SemSym.java

##test
test:
	java   P4 test.moo test.out
	java   P4 nameErrors.moo nameErrors.out

###
# clean
###
clean:
	rm -f *~ *.class parser.java moo.jlex.java
