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
    Stack<Integer> stack = new Stack<Integer>();
    //Integer need_pop = 1;

    public void enterAssignment(CorundumParser.AssignmentContext ctx) {
        System.out.println(".local int " + ctx.lvalue.getText());
    }

    public void exitAssignment(CorundumParser.AssignmentContext ctx) {
        System.out.println(ctx.lvalue.getText() + " " + ctx.op.getText() + " " + stack.pop());
    }

    // public void exitRvalue(CorundumParser.RvalueContext ctx) {
    //     if (need_pop == 1) {
    //        System.out.println(stack.pop()); 
    //        need_pop = 0;
    //     }
    // }

    public void exitInt_result(CorundumParser.Int_resultContext ctx) {
        if ( ctx.getChildCount()==3 ) { // operations have 3 children
            int right = stack.pop();
            int left = stack.pop();

            switch(ctx.op.getType()) {
                case CorundumParser.MUL:
                    stack.push( left * right );
                    break;
                case CorundumParser.DIV:
                    stack.push( left / right );
                    break;
                case CorundumParser.MOD:
                    stack.push( left % right );
                    break;
                case CorundumParser.PLUS:
                    stack.push( left + right );
                    break;
                case CorundumParser.MINUS:
                    stack.push( left - right );
                    break;
            }
        }
    }

    public void visitTerminal(TerminalNode node) {
        Token symbol = node.getSymbol();
        if ( symbol.getType()==CorundumParser.INT ) {
            stack.push(Integer.valueOf(symbol.getText()));
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