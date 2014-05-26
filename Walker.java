import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Stack;

public class Walker {
    public static class Evaluator extends CorundumBaseListener {
        ParseTreeProperty<Integer> values = new ParseTreeProperty<Integer>();

        public int SemanticErrorsNum = 0;
        public int NumStr = 1;
        java.util.LinkedList<String> definitions = new java.util.LinkedList<String>();

        public static boolean is_defined(java.util.LinkedList<String> definitions, String variable) {
            int index = definitions.indexOf(variable);
            if (index == -1) {
                return false;
            }
            return true;
        }

        public void enterInt_assignment(CorundumParser.Int_assignmentContext ctx) {
            switch(ctx.op.getType()) {
                case CorundumParser.ASSIGN:
                    String var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        System.out.println(".local int " + ctx.var_id.getText());
                        definitions.add(ctx.var_id.getText());
                    }
                    break;
                default:
                    var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        System.out.println("line " + NumStr + " Error! Undefined variable " + var + "!");
                        SemanticErrorsNum++;
                    }
                    break;
            }
        }

        public void exitInt_assignment(CorundumParser.Int_assignmentContext ctx) {
            String str = ctx.var_id.getText() + " " + ctx.op.getText() + " " + values.get(ctx.getChild(2));
            System.out.println(str);
        }

        public void exitInt_result(CorundumParser.Int_resultContext ctx) {
            if ( ctx.getChildCount() == 3 && ctx.op != null ) { // operation node

                int left = values.get(ctx.getChild(0));
                int right = values.get(ctx.getChild(2));

                switch(ctx.op.getType()) {
                    case CorundumParser.MUL:
                        values.put(ctx, left * right);
                        break;
                    case CorundumParser.DIV:
                        values.put(ctx, left / right);
                        break;
                    case CorundumParser.MOD:
                        values.put(ctx, left % right);
                        break;
                    case CorundumParser.PLUS:
                        values.put(ctx, left + right);
                        break;
                    case CorundumParser.MINUS:
                        values.put(ctx, left - right);
                        break;
                }
            }
            else if ( ctx.getChildCount() == 1 ) { // near-terminal node
                values.put(ctx, values.get(ctx.getChild(0)));
            }
            else if ( ctx.getChildCount() == 3 && ctx.op == null ) { // node with brackets
                values.put(ctx, values.get(ctx.getChild(1)));
            }
        }

        public void exitInt_t(CorundumParser.Int_tContext ctx) {
            values.put(ctx, values.get(ctx.getChild(0)));
        }

        public void visitTerminal(TerminalNode node) {
            Token symbol = node.getSymbol();
            if ( symbol.getType()==CorundumParser.INT ) {
                values.put(node, Integer.valueOf(symbol.getText()));
            }
        }

        public void exitCrlf(CorundumParser.CrlfContext ctx) {
            NumStr++;
        }
    }

    public static void main(String[] args) throws Exception {
        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) {
            is = new FileInputStream(inputFile);
        }
        ANTLRInputStream input = new ANTLRInputStream(is);
        CorundumLexer lexer = new CorundumLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CorundumParser parser = new CorundumParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.prog();

        ParseTreeWalker walker = new ParseTreeWalker();

        Evaluator eval = new Evaluator();
        walker.walk(eval, tree);
    }
}