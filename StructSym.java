public class StructSym extends SemSym {
    private StructDefSym def;

    public StructSym(StructDefSym def) {
        super(def == null ? null : def.getName());
        this.def = def;
    }
    
    public StructDefSym getDef() {
        return def;
    }
}

