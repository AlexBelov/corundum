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
    //Integer need_pop = 1;

    public void enterInt_assignment(CorundumParser.Int_assignmentContext ctx) {
        System.out.println(".local int " + ctx.var_id.getText());
    }

    public void exitInt_assignment(CorundumParser.Int_assignmentContext ctx) {
        String str = ctx.var_id.getText() + " " + ctx.op.getText() + " " + values.get(ctx.getChild(2));
        System.out.println(str);
    }

    // public void exitRvalue(CorundumParser.RvalueContext ctx) {
    //     if (need_pop == 1) {
    //        System.out.println(stack.pop()); 
    //        need_pop = 0;
    //     }
    // }

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
        else if ( ctx.getChildCount() == 1 ) {
            values.put(ctx, values.get(ctx.getChild(0)));
        }
        else if ( ctx.getChildCount() == 3 && ctx.op == null ) {
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
        parser.setBuildParseTree(true);      // tell ANTLR to build a parse tree
        ParseTree tree = parser.prog(); // parse
        // show tree in text form
        //System.out.println(tree.toStringTree(parser));

        ParseTreeWalker walker = new ParseTreeWalker();

        Evaluator eval = new Evaluator();
        walker.walk(eval, tree);
    }
}