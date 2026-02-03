package androidx.constraintlayout.core.parser;

import androidx.constraintlayout.widget.ConstraintLayout;

/* loaded from: classes.dex */
public class CLParser {
    static boolean DEBUG = false;
    private boolean hasComment = false;
    private int lineNumber;
    private String mContent;

    enum TYPE {
        UNKNOWN,
        OBJECT,
        ARRAY,
        NUMBER,
        STRING,
        KEY,
        TOKEN
    }

    public static CLObject parse(String string) throws CLParsingException {
        return new CLParser(string).parse();
    }

    public CLParser(String content) {
        this.mContent = content;
    }

    public CLObject parse() throws CLParsingException {
        char c;
        int startIndex;
        char c2;
        char c3;
        char[] content = this.mContent.toCharArray();
        int length = content.length;
        int i = 1;
        this.lineNumber = 1;
        int startIndex2 = -1;
        int i2 = 0;
        while (true) {
            c = '\n';
            if (i2 >= length) {
                break;
            }
            char c4 = content[i2];
            if (c4 == '{') {
                startIndex2 = i2;
                break;
            }
            if (c4 == '\n') {
                this.lineNumber++;
            }
            i2++;
        }
        if (startIndex2 == -1) {
            throw new CLParsingException("invalid json content", null);
        }
        CLObject root = CLObject.allocate(content);
        root.setLine(this.lineNumber);
        root.setStart(startIndex2);
        CLElement currentElement = root;
        int i3 = startIndex2 + 1;
        while (i3 < length) {
            char c5 = content[i3];
            if (c5 == c) {
                this.lineNumber += i;
            }
            if (this.hasComment) {
                if (c5 == c) {
                    this.hasComment = false;
                } else {
                    startIndex = startIndex2;
                    c3 = c;
                    i3++;
                    c = c3;
                    startIndex2 = startIndex;
                    i = 1;
                }
            }
            if (currentElement == null) {
                break;
            }
            if (currentElement.isDone()) {
                currentElement = getNextJsonElement(i3, c5, currentElement, content);
                startIndex = startIndex2;
                c3 = c;
            } else if (currentElement instanceof CLObject) {
                if (c5 == '}') {
                    currentElement.setEnd(i3 - 1);
                    startIndex = startIndex2;
                    c3 = c;
                } else {
                    currentElement = getNextJsonElement(i3, c5, currentElement, content);
                    startIndex = startIndex2;
                    c3 = c;
                }
            } else if (currentElement instanceof CLArray) {
                if (c5 == ']') {
                    currentElement.setEnd(i3 - 1);
                    startIndex = startIndex2;
                    c3 = c;
                } else {
                    currentElement = getNextJsonElement(i3, c5, currentElement, content);
                    startIndex = startIndex2;
                    c3 = c;
                }
            } else if (currentElement instanceof CLString) {
                if (content[(int) currentElement.start] == c5) {
                    currentElement.setStart(currentElement.start + 1);
                    currentElement.setEnd(i3 - 1);
                }
                startIndex = startIndex2;
                c3 = c;
            } else {
                if (!(currentElement instanceof CLToken)) {
                    startIndex = startIndex2;
                } else {
                    CLToken token = (CLToken) currentElement;
                    startIndex = startIndex2;
                    if (!token.validate(c5, i3)) {
                        throw new CLParsingException("parsing incorrect token " + token.content() + " at line " + this.lineNumber, token);
                    }
                }
                if ((currentElement instanceof CLKey) || (currentElement instanceof CLString)) {
                    char ck = content[(int) currentElement.start];
                    if ((ck != '\'' && ck != '\"') || ck != c5) {
                        c2 = c5;
                    } else {
                        c2 = c5;
                        currentElement.setStart(currentElement.start + 1);
                        currentElement.setEnd(i3 - 1);
                    }
                } else {
                    c2 = c5;
                }
                if (currentElement.isDone()) {
                    c3 = '\n';
                } else {
                    if (c2 == '}' || c2 == ']' || c2 == ',' || c2 == ' ' || c2 == '\t' || c2 == '\r') {
                        c3 = '\n';
                    } else {
                        c3 = '\n';
                        if (c2 == '\n' || c2 == ':') {
                        }
                    }
                    currentElement.setEnd(i3 - 1);
                    if (c2 == '}' || c2 == ']') {
                        currentElement = currentElement.getContainer();
                        currentElement.setEnd(i3 - 1);
                        if (currentElement instanceof CLKey) {
                            currentElement = currentElement.getContainer();
                            currentElement.setEnd(i3 - 1);
                        }
                    }
                }
            }
            if (currentElement.isDone() && (!(currentElement instanceof CLKey) || ((CLKey) currentElement).mElements.size() > 0)) {
                currentElement = currentElement.getContainer();
            }
            i3++;
            c = c3;
            startIndex2 = startIndex;
            i = 1;
        }
        while (currentElement != null && !currentElement.isDone()) {
            if (currentElement instanceof CLString) {
                currentElement.setStart(((int) currentElement.start) + 1);
            }
            currentElement.setEnd(length - 1);
            currentElement = currentElement.getContainer();
        }
        if (DEBUG) {
            System.out.println("Root: " + root.toJSON());
        }
        return root;
    }

    private CLElement getNextJsonElement(int position, char c, CLElement currentElement, char[] content) throws CLParsingException {
        switch (c) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
            case ',':
            case ':':
                return currentElement;
            case '\"':
            case '\'':
                if (currentElement instanceof CLObject) {
                    return createElement(currentElement, position, TYPE.KEY, true, content);
                }
                return createElement(currentElement, position, TYPE.STRING, true, content);
            case '+':
            case '-':
            case '.':
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE /* 48 */:
            case ConstraintLayout.LayoutParams.Table.LAYOUT_EDITOR_ABSOLUTEX /* 49 */:
            case '2':
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_TAG /* 51 */:
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_BASELINE_TO_TOP_OF /* 52 */:
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_BASELINE_TO_BOTTOM_OF /* 53 */:
            case ConstraintLayout.LayoutParams.Table.LAYOUT_MARGIN_BASELINE /* 54 */:
            case ConstraintLayout.LayoutParams.Table.LAYOUT_GONE_MARGIN_BASELINE /* 55 */:
            case '8':
            case '9':
                return createElement(currentElement, position, TYPE.NUMBER, true, content);
            case '/':
                if (position + 1 < content.length && content[position + 1] == '/') {
                    this.hasComment = true;
                    return currentElement;
                }
                return currentElement;
            case '[':
                return createElement(currentElement, position, TYPE.ARRAY, true, content);
            case ']':
            case '}':
                currentElement.setEnd(position - 1);
                CLElement currentElement2 = currentElement.getContainer();
                currentElement2.setEnd(position);
                return currentElement2;
            case '{':
                return createElement(currentElement, position, TYPE.OBJECT, true, content);
            default:
                if ((currentElement instanceof CLContainer) && !(currentElement instanceof CLObject)) {
                    CLElement currentElement3 = createElement(currentElement, position, TYPE.TOKEN, true, content);
                    CLToken token = (CLToken) currentElement3;
                    if (!token.validate(c, position)) {
                        throw new CLParsingException("incorrect token <" + c + "> at line " + this.lineNumber, token);
                    }
                    return currentElement3;
                }
                return createElement(currentElement, position, TYPE.KEY, true, content);
        }
    }

    private CLElement createElement(CLElement currentElement, int position, TYPE type, boolean applyStart, char[] content) {
        CLElement newElement = null;
        if (DEBUG) {
            System.out.println("CREATE " + type + " at " + content[position]);
        }
        switch (type) {
            case OBJECT:
                newElement = CLObject.allocate(content);
                position++;
                break;
            case ARRAY:
                newElement = CLArray.allocate(content);
                position++;
                break;
            case STRING:
                newElement = CLString.allocate(content);
                break;
            case NUMBER:
                newElement = CLNumber.allocate(content);
                break;
            case KEY:
                newElement = CLKey.allocate(content);
                break;
            case TOKEN:
                newElement = CLToken.allocate(content);
                break;
        }
        if (newElement == null) {
            return null;
        }
        newElement.setLine(this.lineNumber);
        if (applyStart) {
            newElement.setStart(position);
        }
        if (currentElement instanceof CLContainer) {
            CLContainer container = (CLContainer) currentElement;
            newElement.setContainer(container);
        }
        return newElement;
    }
}
