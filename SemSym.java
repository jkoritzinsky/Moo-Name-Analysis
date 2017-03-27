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
