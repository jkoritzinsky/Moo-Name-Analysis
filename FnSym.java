public class FnSym extends SemSym {
    private String[] parameterTypes;

    public FnSym(String returnType, String[] types) {
        super(returnType);
        parameterTypes = types;
    }
    
    public String[] getParamTypes() {
        return parameterTypes;
    }
}
