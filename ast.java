import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a Moo program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of 
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Kids
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
//
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          -- none --
//       StrLitNode          -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode     
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of kids, or
// internal nodes with a fixed number of kids:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of kids:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode   
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// ASTnode class (base class for all other kinds of nodes)
// **********************************************************************

abstract class ASTnode { 
    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);
    
    abstract public void nameAnalysis(SymTable table);

    // this method can be used by the unparse methods to do indenting
    protected void doIndent(PrintWriter p, int indent) {
        for (int k=0; k<indent; k++) p.print(" ");
    }
}

// **********************************************************************
// ProgramNode,  DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode
// **********************************************************************

class ProgramNode extends ASTnode {
    public ProgramNode(DeclListNode L) {
        myDeclList = L;
    }

    /**
     * Sample name analysis method. 
     * Creates an empty symbol table for the outermost scope, then processes
     * all of the globals, struct defintions, and functions in the program.
     */
    public void nameAnalysis() {
        SymTable symTab = new SymTable();
        nameAnalysis(symTab);
    }
    
    public void nameAnalysis(SymTable table) {
	    myDeclList.nameAnalysis(table);
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    // 1 kid
    private DeclListNode myDeclList;
}

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }
    
    public void nameAnalysis(SymTable table) {
        for(DeclNode node: myDecls) {
            node.nameAnalysis(table);
        }
    }
    
    public void nameAnalysis(SymTable globalTable, SymTable typeScopeTable) {
        for(DeclNode node: myDecls) {
            node.nameAnalysis(globalTable, typeScopeTable);
        }
    }

    // list of kids (DeclNodes)
    private List<DeclNode> myDecls;
}

class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }
    
    public void nameAnalysis(SymTable table) {
        for(FormalDeclNode node: myFormals) {
            node.nameAnalysis(table);
        }
    }
    
    public String[] getTypes() {
      String[] types = new String[myFormals.size()];
      for(int i = 0; i < myFormals.size(); ++i) {
        types[i] = myFormals.get(i).getType();
      }
      return types;
    }

    // list of kids (FormalDeclNodes)
    private List<FormalDeclNode> myFormals;
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }
    
    public void nameAnalysis(SymTable table) {
        myDeclList.nameAnalysis(table);
        myStmtList.nameAnalysis(table);
    }

    // 2 kids
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }
    
    public void nameAnalysis(SymTable table) {
        for(StmtNode node: myStmts) {
            node.nameAnalysis(table);
        }
    }

    // list of kids (StmtNodes)
    private List<StmtNode> myStmts;
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) {  // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        } 
    }
    
    public void nameAnalysis(SymTable table) {
        for(ExpNode node: myExps) {
            node.nameAnalysis(table);
        }
    }

    // list of kids (ExpNodes)
    private List<ExpNode> myExps;
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
    public abstract void nameAnalysis(SymTable table);
    public void nameAnalysis(SymTable typeTable, SymTable nameTable) {}
}

class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        myType = type;
        myId = id;
        mySize = size;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        p.print(myId.getId());
        p.println(";");
    }
    
    public void nameAnalysis(SymTable table) {
        myType.nameAnalysis(table);
        if(myType instanceof VoidNode) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Non-function declared void");
        }
        try {
            if(myType instanceof StructNode) {
                StructNode myStructType = (StructNode)myType;
                table.addDecl(myId.getId(), new StructSym(myStructType.getDefinition(table)));
            } else {
                table.addDecl(myId.getId(), new SemSym(myType.getType()));
            }
        } catch(DuplicateSymException ex) {
          ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
        } catch(EmptySymTableException ex) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Internal Compiler Error. Empty Sym Table");
        }
        
        myId.nameAnalysis(table);
    }
    
    public void nameAnalysis(SymTable typeTable, SymTable nameTable) {
        myType.nameAnalysis(typeTable);
        if(myType instanceof VoidNode) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Non-function declared void");
        }
        try {
            if(myType instanceof StructNode) {
                StructNode myStructType = (StructNode)myType;
                nameTable.addDecl(myId.getId(), new StructSym(myStructType.getDefinition(typeTable)));
            } else {
                nameTable.addDecl(myId.getId(), new SemSym(myType.getType()));
            }
        } catch(DuplicateSymException ex) {
          ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
        } catch(EmptySymTableException ex) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Internal Compiler Error. Empty Sym Table");
        }
        
        myId.nameAnalysis(nameTable);
    }

    // 3 kids
    private TypeNode myType;
    private IdNode myId;
    private int mySize;  // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;
}

class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        myType = type;
        myId = id;
        myFormalsList = formalList;
        myBody = body;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myType.unparse(p, 0);
        p.print(" ");
        p.print(myId.getId());
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent+4);
        p.println("}\n");
    }
    
    public void nameAnalysis(SymTable table) {
        myType.nameAnalysis(table);
        String[] types = myFormalsList.getTypes();
        try {
        table.addDecl(myId.getId(), new FnSym(myType.getType(), types));
        } catch(DuplicateSymException ex) {
          ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
        } catch(EmptySymTableException ex) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Internal Compiler Error. Empty Sym Table");
        }
        table.addScope();
        myFormalsList.nameAnalysis(table);
        myBody.nameAnalysis(table);
        try {
            table.removeScope();
        } catch(EmptySymTableException ex) {
            ErrMsg.fatal(0, 0, "Internal Compiler Error: Empty Sym table");
        }
        
        myId.nameAnalysis(table);
    }

    // 4 kids
    private TypeNode myType;
    private IdNode myId;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        myType = type;
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        myType.unparse(p, 0);
        p.print(" ");
        p.print(myId.getId());
    }
    
    public void nameAnalysis(SymTable table) {
        myType.nameAnalysis(table);
        if(myType instanceof VoidNode) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Non-function declared void");
        }
        try {
            table.addDecl(myId.getId(), new SemSym(myType.getType()));
        } catch(DuplicateSymException ex) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
        } catch(EmptySymTableException ex) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Internal Compiler Error. Empty Sym Table");
        }
        
        myId.nameAnalysis(table);
    }
    
    public String getType() {
        return myType.getType();
    }

    // 2 kids
    private TypeNode myType;
    private IdNode myId;
}

class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        myId = id;
        myDeclList = declList;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("struct ");
		    p.print(myId.getId());
		p.println(" {");
        myDeclList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("};\n");

    }
    
    public void nameAnalysis(SymTable table) {
        SymTable memberTable = new SymTable();
        myDeclList.nameAnalysis(table, memberTable);
        try {
            table.addDecl(myId.getId(), new StructDefSym(myId.getId(), memberTable));
        } catch(DuplicateSymException ex) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Multiply declared identifier");
        } catch(EmptySymTableException ex) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Internal Compiler Error. Empty Sym Table");
        }
        
        myId.nameAnalysis(table);
    }

    // 2 kids
    private IdNode myId;
	private DeclListNode myDeclList;
}

// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
    public abstract String getType();
}

class IntNode extends TypeNode {
    public IntNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }
    
    public String getType() {
        return "int";
    }
    
    public void nameAnalysis(SymTable table) {
    }
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }
    
    public String getType() {
        return "bool";
    }

    public void nameAnalysis(SymTable table) {
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }
    
    public String getType() {
        return "void";
    }

    public void nameAnalysis(SymTable table) {
    }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
		myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
		myId.unparse(p, 0);
    }

    public void nameAnalysis(SymTable table) {
        if(getDefinition(table) == null) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Invalid name of struct type");
        }
    }
    
    public StructDefSym getDefinition(SymTable table) {
        SemSym def = table.lookupStruct(myId.getId());
        if(def instanceof StructDefSym) {
            return (StructDefSym)def;
        }
        return null;
    }
    
    
    public String getType() {
        return myId.getId();
    }
	
	// 1 kid
    private IdNode myId;
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }
    
    public void nameAnalysis(SymTable table) {
        myAssign.nameAnalysis(table);
    }

    // 1 kid
    private AssignNode myAssign;
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }
    
    public void nameAnalysis(SymTable table) {
        myExp.nameAnalysis(table);
    }

    // 1 kid
    private ExpNode myExp;
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }
    
    public void nameAnalysis(SymTable table) {
        myExp.nameAnalysis(table);
    }

    // 1 kid
    private ExpNode myExp;
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("cin >> ");
        myExp.unparse(p, 0);
        p.println(";");
    }
    
    public void nameAnalysis(SymTable table) {
        myExp.nameAnalysis(table);
    }

    // 1 kid (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;
}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("cout << ");
        myExp.unparse(p, 0);
        p.println(";");
    }
    
    public void nameAnalysis(SymTable table) {
        myExp.nameAnalysis(table);
    }

    // 1 kid
    private ExpNode myExp;
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
    }
    
    public void nameAnalysis(SymTable table) {
        myExp.nameAnalysis(table);
        table.addScope();
        myDeclList.nameAnalysis(table);
        myStmtList.nameAnalysis(table);
        try {
        table.removeScope();
        } catch(EmptySymTableException ex) {
            ErrMsg.fatal(0, 0, "Internal Compiler Error: Empty Sym table");
        }
    }

    // e kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
                          StmtListNode slist1, DeclListNode dlist2,
                          StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent+4);
        myThenStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
        doIndent(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent+4);
        myElseStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");        
    }
    
    public void nameAnalysis(SymTable table) {
        myExp.nameAnalysis(table);
        table.addScope();
        myThenDeclList.nameAnalysis(table);
        myThenStmtList.nameAnalysis(table);
        try {
            table.removeScope();
        } catch(EmptySymTableException ex) {
            ErrMsg.fatal(0, 0, "Internal Compiler Error: Empty Sym table");
        }
        table.addScope();
        myElseDeclList.nameAnalysis(table);
        myElseStmtList.nameAnalysis(table);
        try {
            table.removeScope();
        } catch(EmptySymTableException ex) {
            ErrMsg.fatal(0, 0, "Internal Compiler Error: Empty Sym table");
        }
    }


    // 5 kids
    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }
	
    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent+4);
        myStmtList.unparse(p, indent+4);
        doIndent(p, indent);
        p.println("}");
    }
    
    public void nameAnalysis(SymTable table) {
        myExp.nameAnalysis(table);
        table.addScope();
        myDeclList.nameAnalysis(table);
        myStmtList.nameAnalysis(table);
        try {
            table.removeScope();
        } catch(EmptySymTableException ex) {
            ErrMsg.fatal(0, 0, "Internal Compiler Error: Empty Sym table");
        }
    }

    // 3 kids
    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }
    
    public void nameAnalysis(SymTable table) {
        myCall.nameAnalysis(table);
    }

    // 1 kid
    private CallExpNode myCall;
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        doIndent(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(";");
    }
    
    public void nameAnalysis(SymTable table) {
        if(myExp != null) {
            myExp.nameAnalysis(table);
        }
    }

    // 1 kid
    private ExpNode myExp; // possibly null
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myIntVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }
    
    public void nameAnalysis(SymTable table) {
    }

    private int myLineNum;
    private int myCharNum;
    private int myIntVal;
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }
    
    public void nameAnalysis(SymTable table) {
    }


    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }
    
    public void nameAnalysis(SymTable table) {
    }

    private int myLineNum;
    private int myCharNum;
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        myLineNum = lineNum;
        myCharNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }
    
    public void nameAnalysis(SymTable table) {
    }

    private int myLineNum;
    private int myCharNum;
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        myLineNum = lineNum;
        myCharNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
        if(sym != null && sym.getType() != null) {
            p.print("(");
            p.print(sym.getType());
            p.print(")");
        }
    }
    
    public void nameAnalysis(SymTable table) {
        sym = table.lookupGlobal(myStrVal);
        if (sym == null)
            ErrMsg.fatal(myLineNum, myCharNum, "Undeclared identifier");
    }
    
    public int getLineNum() {
        return myLineNum;
    }
    
    public int getCharNum() {
        return myCharNum;
    }
    
    public String getId() {
        return myStrVal;
    }

    public SemSym getSym() {
        return sym;
    }

    private int myLineNum;
    private int myCharNum;
    private String myStrVal;
    private SemSym sym;
}

class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;	
        myId = id;
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myLoc.unparse(p, 0);
		p.print(").");
		myId.unparse(p, 0);
    }
    
    public void nameAnalysis(SymTable table) {
        getSym(table);
    }
    
    public SemSym getSym(SymTable table) {
        StructSym locSym = null;
        if(myLoc instanceof IdNode) {
            IdNode myId = (IdNode)myLoc;
            myId.nameAnalysis(table);
            SemSym sym = myId.getSym();
            if(!(sym instanceof StructSym)) {
                ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Dot-access of a non-struct type");
                return null;
            }
            locSym = (StructSym)sym;
        }
        if(myLoc instanceof DotAccessExpNode) {
            DotAccessExpNode myAccess = (DotAccessExpNode)myLoc;
            SemSym sym = myAccess.getSym(table);
            IdNode myId = myAccess.getId();
            if(!(sym instanceof StructSym)) {
                ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Dot-access of a non-struct type");
                return null;
            }
            locSym = (StructSym)sym;
        }
        myId.nameAnalysis(locSym.getDef().getFields());
        SemSym accessedSym = myId.getSym();
        if(accessedSym == null) {
            ErrMsg.fatal(myId.getLineNum(), myId.getCharNum(), "Invalid struct field name");
            return null;
        }
        return accessedSym;
    }
    
    private IdNode getId() {
        return myId;
    }

    // 2 kids
    private ExpNode myLoc;	
    private IdNode myId;
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
		if (indent != -1)  p.print("(");
	    myLhs.unparse(p, 0);
		p.print(" = ");
		myExp.unparse(p, 0);
		if (indent != -1)  p.print(")");
    }
    
    public void nameAnalysis(SymTable table) {
      myLhs.nameAnalysis(table);
      myExp.nameAnalysis(table);
    }

    // 2 kids
    private ExpNode myLhs;
    private ExpNode myExp;
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        myId = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        myId = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

    // ** unparse **
    public void unparse(PrintWriter p, int indent) {
	    myId.unparse(p, 0);
		p.print("(");
                String [] paramTypes = ((FnSym)myId.getSym()).getParamTypes();
                for (int i=0; i<paramTypes.length; i++) {
                    p.print(paramTypes[i]);
                    if (i != paramTypes.length - 1)
                        p.print(",");
                }
                p.print("->");
                p.print(((FnSym)myId.getSym()).getReturnType());
                p.print(")(");
		if (myExpList != null) {
                    myExpList.unparse(p, 0);
		}
		p.print(")");
    }
    
    public void nameAnalysis(SymTable table) {
        myId.nameAnalysis(table);

        if(myExpList != null) {
            myExpList.nameAnalysis(table);
        }
    }

    // 2 kids
    private IdNode myId;
    private ExpListNode myExpList;  // possibly null
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }
    
    public void nameAnalysis(SymTable table) {
        myExp.nameAnalysis(table);
    }

    // one child
    protected ExpNode myExp;
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }
    
    public void nameAnalysis(SymTable table) {
        myExp1.nameAnalysis(table);
        myExp2.nameAnalysis(table);
    }

    // two kids
    protected ExpNode myExp1;
    protected ExpNode myExp2;
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(-");
		myExp.unparse(p, 0);
		p.print(")");
    }
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(!");
		myExp.unparse(p, 0);
		p.print(")");
    }
}

// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

class PlusNode extends BinaryExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" + ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class MinusNode extends BinaryExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" - ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class TimesNode extends BinaryExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" * ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class DivideNode extends BinaryExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" / ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class AndNode extends BinaryExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" && ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class OrNode extends BinaryExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" || ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class EqualsNode extends BinaryExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" == ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class NotEqualsNode extends BinaryExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" != ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class LessNode extends BinaryExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" < ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class GreaterNode extends BinaryExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" > ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class LessEqNode extends BinaryExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" <= ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}

class GreaterEqNode extends BinaryExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
	    p.print("(");
		myExp1.unparse(p, 0);
		p.print(" >= ");
		myExp2.unparse(p, 0);
		p.print(")");
    }
}
