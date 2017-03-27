public class StructSym extends SemSym {
    private StructDefSym def;

    public StructSym(StructDefSym def) {
        super(def.getType());
    }
    
    public StructDefSym getDef() {
        return def;
    }
}

