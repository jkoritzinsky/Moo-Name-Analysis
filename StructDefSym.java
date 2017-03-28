public class StructDefSym extends SemSym {
    private SymTable fields;
    private String name;

    public StructDefSym(String name, SymTable fields) {
        super(null);
        this.fields = fields;
        this.name = name;
    }

    public SymTable getFields() {
        return this.fields;
    }
    
    public String getName() {
        return this.name;
    }
}
