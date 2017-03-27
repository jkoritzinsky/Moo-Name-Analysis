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


class FnSym extends SemSym {
    private String[] parameterTypes;

    public FnSym(String returnType, String[] types) {
        super(returnType);
        parameterTypes = types;
    }
    
    public String[] getParamTypes() {
        return parameterTypes;
    }
}

class StructDefSym extends SemSym {
    private SymTable fields;

    public StructDefSym(String type, SymTable fields) {
        super(type);
        this.fields = fields;
    }

    public SymTable getFields() {
        return this.fields;
    }
}

class StructSym extends SemSym {
    private StructDefSym def;

    public StructSym(String type, StructDefSym def) {
        super(type);
        this.def = def;
    }
    
    public StructDefSym getDef() {
        return def;
    }
}
