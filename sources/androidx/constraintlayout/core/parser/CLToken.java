package androidx.constraintlayout.core.parser;

/* loaded from: classes.dex */
public class CLToken extends CLElement {
    int index;
    char[] tokenFalse;
    char[] tokenNull;
    char[] tokenTrue;
    Type type;

    enum Type {
        UNKNOWN,
        TRUE,
        FALSE,
        NULL
    }

    public boolean getBoolean() throws CLParsingException {
        if (this.type == Type.TRUE) {
            return true;
        }
        if (this.type == Type.FALSE) {
            return false;
        }
        throw new CLParsingException("this token is not a boolean: <" + content() + ">", this);
    }

    public boolean isNull() throws CLParsingException {
        if (this.type == Type.NULL) {
            return true;
        }
        throw new CLParsingException("this token is not a null: <" + content() + ">", this);
    }

    public CLToken(char[] content) {
        super(content);
        this.index = 0;
        this.type = Type.UNKNOWN;
        this.tokenTrue = "true".toCharArray();
        this.tokenFalse = "false".toCharArray();
        this.tokenNull = "null".toCharArray();
    }

    public static CLElement allocate(char[] content) {
        return new CLToken(content);
    }

    @Override // androidx.constraintlayout.core.parser.CLElement
    protected String toJSON() {
        if (CLParser.DEBUG) {
            return "<" + content() + ">";
        }
        return content();
    }

    @Override // androidx.constraintlayout.core.parser.CLElement
    protected String toFormattedJSON(int indent, int forceIndent) {
        StringBuilder json = new StringBuilder();
        addIndent(json, indent);
        json.append(content());
        return json.toString();
    }

    public Type getType() {
        return this.type;
    }

    public boolean validate(char c, long position) {
        boolean isValid = false;
        switch (this.type) {
            case TRUE:
                isValid = this.tokenTrue[this.index] == c;
                if (isValid && this.index + 1 == this.tokenTrue.length) {
                    setEnd(position);
                    break;
                }
                break;
            case FALSE:
                isValid = this.tokenFalse[this.index] == c;
                if (isValid && this.index + 1 == this.tokenFalse.length) {
                    setEnd(position);
                    break;
                }
                break;
            case NULL:
                isValid = this.tokenNull[this.index] == c;
                if (isValid && this.index + 1 == this.tokenNull.length) {
                    setEnd(position);
                    break;
                }
                break;
            case UNKNOWN:
                if (this.tokenTrue[this.index] == c) {
                    this.type = Type.TRUE;
                    isValid = true;
                    break;
                } else if (this.tokenFalse[this.index] == c) {
                    this.type = Type.FALSE;
                    isValid = true;
                    break;
                } else if (this.tokenNull[this.index] == c) {
                    this.type = Type.NULL;
                    isValid = true;
                    break;
                }
                break;
        }
        this.index++;
        return isValid;
    }
}
