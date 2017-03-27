public class SemSym {
    private String type;
    
    public SemSym(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    public String toString() {
        return type;
    }
}

class StructDefSym {
    private String name;
    private SymTable fields;

    public StructDefSym(String name, SymTable fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return this.name;
    }

    public SymTable getFields() {
        return this.fields;
    }

    public String toString() {
        return this.name;
    }
}

class StructSym {
    private String name;
    private StructDefSym def;

    public StructSym(String name, StructDefSym def) {
        this.name = name;
        this.def = def;
    }
    
    public String getName() {
        return name;
    }

    public StructDefSym getDef() {
        return def;
    }
    
    public String toString() {
        return name;
    }
}
