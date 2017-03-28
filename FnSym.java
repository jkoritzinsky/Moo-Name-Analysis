public class FnSym extends SemSym {
    private String[] parameterTypes;
    private String returnType;

    public FnSym(String returnType, String[] types) {
        super(null);
        parameterTypes = types;
        this.returnType = returnType;
    }
    
    public String[] getParamTypes() {
        return parameterTypes;
    }
    
    public String getReturnType() {
        return returnType;
    }
}
