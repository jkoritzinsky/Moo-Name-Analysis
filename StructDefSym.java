public class StructDefSym extends SemSym {
    private SymTable fields;

    public StructDefSym(String type, SymTable fields) {
        super(type);
        this.fields = fields;
    }

    public SymTable getFields() {
        return this.fields;
    }
}
