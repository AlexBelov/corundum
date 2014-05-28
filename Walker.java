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
        ParseTreeProperty<Integer> int_values = new ParseTreeProperty<Integer>();
        ParseTreeProperty<Float> float_values = new ParseTreeProperty<Float>();
        ParseTreeProperty<String> string_values = new ParseTreeProperty<String>();
        ParseTreeProperty<String> which_value = new ParseTreeProperty<String>();

        Stack<Integer> stack_labels = new Stack<Integer>();

        public int SemanticErrorsNum = 0;
        public int NumStr = 1;
        public int Num_reg = 0;
        public int Num_label = 0;
        java.util.LinkedList<String> definitions = new java.util.LinkedList<String>();

        public static boolean is_defined(java.util.LinkedList<String> definitions, String variable) {
            int index = definitions.indexOf(variable);
            if (index == -1) {
                return false;
            }
            return true;
        }

        public static String repeat(String s, int times) {
            if (times <= 0) return "";
            else return s + repeat(s, times-1);
        }

        // ======================================== Integer ========================================

        public void enterInt_assignment(CorundumParser.Int_assignmentContext ctx) {
            switch(ctx.op.getType()) {
                case CorundumParser.ASSIGN:
                    String var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        System.out.println(".local pmc " + ctx.var_id.getText());
                        System.out.println(ctx.var_id.getText() + " = new \"Integer\"");
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
            String str = ctx.var_id.getText() + " " + ctx.op.getText() + " " + int_values.get(ctx.getChild(2));
            System.out.println(str);
        }

        public void exitInt_result(CorundumParser.Int_resultContext ctx) {
            if ( ctx.getChildCount() == 3 && ctx.op != null ) { // operation node

                int left = int_values.get(ctx.getChild(0));
                int right = int_values.get(ctx.getChild(2));

                switch(ctx.op.getType()) {
                    case CorundumParser.MUL:
                        int_values.put(ctx, left * right);
                        which_value.put(ctx, "Integer");
                        break;
                    case CorundumParser.DIV:
                        int_values.put(ctx, left / right);
                        which_value.put(ctx, "Integer");
                        break;
                    case CorundumParser.MOD:
                        int_values.put(ctx, left % right);
                        which_value.put(ctx, "Integer");
                        break;
                    case CorundumParser.PLUS:
                        int_values.put(ctx, left + right);
                        which_value.put(ctx, "Integer");
                        break;
                    case CorundumParser.MINUS:
                        int_values.put(ctx, left - right);
                        which_value.put(ctx, "Integer");
                        break;
                }
            }
            else if ( ctx.getChildCount() == 1 ) { // near-terminal node
                int_values.put(ctx, int_values.get(ctx.getChild(0)));
                which_value.put(ctx, "Integer");
            }
            else if ( ctx.getChildCount() == 3 && ctx.op == null ) { // node with brackets
                int_values.put(ctx, int_values.get(ctx.getChild(1)));
                which_value.put(ctx, "Integer");
            }
        }

        public void exitInt_t(CorundumParser.Int_tContext ctx) {
            int_values.put(ctx, int_values.get(ctx.getChild(0)));
            which_value.put(ctx, which_value.get(ctx.getChild(0)));
        }

        // ======================================== Float ========================================

        public void enterFloat_assignment(CorundumParser.Float_assignmentContext ctx) {
            switch(ctx.op.getType()) {
                case CorundumParser.ASSIGN:
                    String var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        System.out.println(".local pmc " + ctx.var_id.getText());
                        System.out.println(ctx.var_id.getText() + " = new \"Float\"");
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

        public void exitFloat_assignment(CorundumParser.Float_assignmentContext ctx) {
            String str = ctx.var_id.getText() + " " + ctx.op.getText() + " " + float_values.get(ctx.getChild(2));
            System.out.println(str);
        }

        public void exitFloat_result(CorundumParser.Float_resultContext ctx) {
            if ( ctx.getChildCount() == 3 && ctx.op != null ) { // operation node

                float left = 0;
                float right = 0;

                switch(which_value.get(ctx.getChild(0))) {
                    case "Integer":
                        left = (float) int_values.get(ctx.getChild(0));
                        break;
                    case "Float":
                        left = float_values.get(ctx.getChild(0));
                        break;
                }

                switch(which_value.get(ctx.getChild(2))) {
                    case "Integer":
                        right = (float) int_values.get(ctx.getChild(2));
                        break;
                    case "Float":
                        right = float_values.get(ctx.getChild(2));
                        break;
                }

                switch(ctx.op.getType()) {
                    case CorundumParser.MUL:
                        float_values.put(ctx, left * right);
                        which_value.put(ctx, "Float");
                        break;
                    case CorundumParser.DIV:
                        float_values.put(ctx, left / right);
                        which_value.put(ctx, "Float");
                        break;
                    case CorundumParser.MOD:
                        float_values.put(ctx, left % right);
                        which_value.put(ctx, "Float");
                        break;
                    case CorundumParser.PLUS:
                        float_values.put(ctx, left + right);
                        which_value.put(ctx, "Float");
                        break;
                    case CorundumParser.MINUS:
                        float_values.put(ctx, left - right);
                        which_value.put(ctx, "Float");
                        break;
                }
            }
            else if ( ctx.getChildCount() == 1 ) { // near-terminal node
                float_values.put(ctx, float_values.get(ctx.getChild(0)));
                which_value.put(ctx, "Float");
            }
            else if ( ctx.getChildCount() == 3 && ctx.op == null ) { // node with brackets
                float_values.put(ctx, float_values.get(ctx.getChild(1)));
                which_value.put(ctx, "Float");
            }
        }

        public void exitFloat_t(CorundumParser.Float_tContext ctx) {
            float_values.put(ctx, float_values.get(ctx.getChild(0)));
            which_value.put(ctx, which_value.get(ctx.getChild(0)));
        }

        // ======================================== String ========================================

        public void enterString_assignment(CorundumParser.String_assignmentContext ctx) {
            switch(ctx.op.getType()) {
                case CorundumParser.ASSIGN:
                    String var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        System.out.println(".local pmc " + ctx.var_id.getText());
                        System.out.println(ctx.var_id.getText() + " = new \"String\"");
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

        public void exitString_assignment(CorundumParser.String_assignmentContext ctx) {
            String str = ctx.var_id.getText() + " " + ctx.op.getText() + " \"" + string_values.get(ctx.getChild(2)) + "\"";
            System.out.println(str);
        }

        public void exitString_result(CorundumParser.String_resultContext ctx) {
            if ( ctx.getChildCount() == 3 && ctx.op != null ) { // operation node

                int times = 0;
                String left_s = "";
                String right_s = "";
                String str = "";

                switch(which_value.get(ctx.getChild(0))) {
                    case "Integer":
                        times = int_values.get(ctx.getChild(0));
                        break;
                    case "String":
                        left_s = string_values.get(ctx.getChild(0));
                        str = left_s;
                        break;
                }

                switch(which_value.get(ctx.getChild(2))) {
                    case "Integer":
                        times = int_values.get(ctx.getChild(2));
                        break;
                    case "String":
                        right_s = string_values.get(ctx.getChild(2));
                        str = right_s;
                        break;
                }

                switch(ctx.op.getType()) {
                    case CorundumParser.MUL:
                        string_values.put(ctx, (String) repeat(str, times));
                        which_value.put(ctx, "String");
                        break;
                    case CorundumParser.PLUS:
                        string_values.put(ctx, (String) left_s + right_s);
                        which_value.put(ctx, "String");
                        break;
                }
            }
            else if ( ctx.getChildCount() == 1 ) { // near-terminal node
                string_values.put(ctx, string_values.get(ctx.getChild(0)));
                which_value.put(ctx, "String");
            }
            else if ( ctx.getChildCount() == 3 && ctx.op == null ) { // node with brackets
                string_values.put(ctx, string_values.get(ctx.getChild(1)));
                which_value.put(ctx, "String");
            }
        }

        public void exitLiteral_t(CorundumParser.Literal_tContext ctx) {
            string_values.put(ctx, string_values.get(ctx.getChild(0)));
            which_value.put(ctx, which_value.get(ctx.getChild(0)));
        }

        // ======================================== Dynamic assignment ========================================

        public void enterDynamic_assignment(CorundumParser.Dynamic_assignmentContext ctx) {
            switch(ctx.op.getType()) {
                case CorundumParser.ASSIGN:
                    String var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        System.out.println(".local pmc " + ctx.var_id.getText());
                        System.out.println(ctx.var_id.getText() + " = new \"Integer\"");
                        definitions.add(ctx.var_id.getText());
                        Num_reg = 0;
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

        public void exitDynamic_assignment(CorundumParser.Dynamic_assignmentContext ctx) {
            String str = ctx.var_id.getText() + " " + ctx.op.getText() + " " + string_values.get(ctx.getChild(2));
            Num_reg = 0;
            System.out.println(str);
        }

        public void exitDynamic_result(CorundumParser.Dynamic_resultContext ctx) {
            if ( ctx.getChildCount() == 3 && ctx.op != null ) { // operation node

                int int_dyn = 0;
                float float_dyn = 0;
                String str_dyn = "";
                String str_dyn_1 = "";
                String another_node = "";
                String str_output = "";

                switch(which_value.get(ctx.getChild(0))) {
                    case "Integer":
                        int_dyn = int_values.get(ctx.getChild(0));
                        another_node = string_values.get(ctx.getChild(2));
                        str_output = "$P" + Num_reg + " = " + another_node + " " + ctx.op.getText() + " " + int_dyn;                  
                        break;
                    case "Float":
                        float_dyn = float_values.get(ctx.getChild(0));
                        another_node = string_values.get(ctx.getChild(2));
                        str_output = "$P" + Num_reg + " = " + another_node + " " + ctx.op.getText() + " " + float_dyn;
                        break;
                    case "String":
                        str_dyn = string_values.get(ctx.getChild(0));
                        another_node = string_values.get(ctx.getChild(2));
                        str_output = "$P" + Num_reg + " = " + another_node + " " + ctx.op.getText() + " " + str_dyn;
                        break;
                    case "Dynamic":
                        str_dyn_1 = string_values.get(ctx.getChild(0));
                        switch(which_value.get(ctx.getChild(2))) {
                            case "Integer":
                                int_dyn = int_values.get(ctx.getChild(2));
                                str_output = "$P" + Num_reg + " = " + str_dyn_1 + " " + ctx.op.getText() + " " + int_dyn;                  
                                break;
                            case "Float":
                                float_dyn = float_values.get(ctx.getChild(2));
                                str_output = "$P" + Num_reg + " = " + str_dyn_1 + " " + ctx.op.getText() + " " + float_dyn;
                                break;
                            case "String":
                                str_dyn = string_values.get(ctx.getChild(2));
                                str_output = "$P" + Num_reg + " = " + str_dyn_1 + " " + ctx.op.getText() + " " + str_dyn;
                                break;
                            case "Dynamic":
                                str_dyn = string_values.get(ctx.getChild(2));
                                str_output = "$P" + Num_reg + " = " + str_dyn_1 + " " + ctx.op.getText() + " " + str_dyn;
                                break;
                        }
                        break;
                }

                System.out.println("$P" + Num_reg + " = new \"Integer\"");
                System.out.println(str_output);
                string_values.put(ctx, "$P" + Num_reg);
                which_value.put(ctx, "Dynamic");
                Num_reg++;
                
            }
            else if ( ctx.getChildCount() == 1 ) { // near-terminal node
                string_values.put(ctx, string_values.get(ctx.getChild(0)));
                which_value.put(ctx, "Dynamic");
            }
            else if ( ctx.getChildCount() == 3 && ctx.op == null ) { // node with brackets
                string_values.put(ctx, string_values.get(ctx.getChild(1)));
                which_value.put(ctx, "Dynamic");
            }
        }

        public void exitDynamic(CorundumParser.DynamicContext ctx) {
            String str_dyn_term = string_values.get(ctx.getChild(0));
            System.out.println("$P" + Num_reg + " = new \"Integer\"");
            System.out.println("$P" + Num_reg + " = " + str_dyn_term);
            string_values.put(ctx, "$P" + Num_reg);
            Num_reg++;
            which_value.put(ctx, which_value.get(ctx.getChild(0)));
        }

        public void exitId(CorundumParser.IdContext ctx) {
            string_values.put(ctx, string_values.get(ctx.getChild(0)));
            which_value.put(ctx, which_value.get(ctx.getChild(0)));
        }

        // ======================================== Array definition ========================================

        public void enterInitial_array_assignment(CorundumParser.Initial_array_assignmentContext ctx) {
            String var = ctx.var_id.getText();

            if (!is_defined(definitions, var)) {
                System.out.println(var + " = new \"ResizablePMCArray\"");
                definitions.add(var);
            }
        }

        public void exitArray_assignment(CorundumParser.Array_assignmentContext ctx) {
            String var = ctx.var_id.getText();

            System.out.println(var + ctx.arr_def.getText() + " = " + ctx.arr_val.getText());
            if (!is_defined(definitions, var)) {
                System.out.println("line " + NumStr + " Error! Undefined variable " + var + "!");
                SemanticErrorsNum++;
            }
        }

        public void exitArray_selector(CorundumParser.Array_selectorContext ctx) {
            String var_selector = ctx.getText();
            string_values.put(ctx, var_selector);
            which_value.put(ctx, "Dynamic");
        }

        // ======================================== IF statement ========================================

        public void enterIf_statement(CorundumParser.If_statementContext ctx) {
            Num_label++;
            System.out.println("if " + ctx.getChild(1).getText() + " goto label_" + Num_label);
            Num_label++;
            System.out.println("goto label_" + Num_label + ":");
            System.out.println("label_" + (Num_label - 1) + ":");
            stack_labels.push(Num_label);
        }

        public void exitIf_statement(CorundumParser.If_statementContext ctx) {      
            if (!ctx.getChild(4).getText().contains("else")) {
                System.out.println("label_" + stack_labels.pop() + ":");
            }
        }

        public void exitElse_token(CorundumParser.Else_tokenContext ctx) {
            System.out.println("label_" + stack_labels.pop() + ":");
        }

        // ======================================== UNLESS statement ========================================

        public void enterUnless_statement(CorundumParser.Unless_statementContext ctx) {
            Num_label++;
            System.out.println("unless " + ctx.getChild(1).getText() + " goto label_" + Num_label);
            Num_label++;
            System.out.println("goto label_" + Num_label + ":");
            System.out.println("label_" + (Num_label - 1) + ":");
            stack_labels.push(Num_label);
        }

        public void exitUnless_statement(CorundumParser.Unless_statementContext ctx) {      
            if (!ctx.getChild(4).getText().contains("else")) {
                System.out.println("label_" + stack_labels.pop() + ":");
            }
        }

        // ======================================== FOR loop ========================================

        public void enterFor_statement(CorundumParser.For_statementContext ctx) {
            System.out.println("FOR");
        }

        // ======================================== Terminal node ========================================

        public void visitTerminal(TerminalNode node) {
            Token symbol = node.getSymbol();
            switch(symbol.getType()) {
                case CorundumParser.INT:
                    int_values.put(node, Integer.valueOf(symbol.getText()));
                    which_value.put(node, "Integer");
                    break;
                case CorundumParser.FLOAT:
                    float_values.put(node, Float.valueOf(symbol.getText()));
                    which_value.put(node, "Float");
                    break;
                case CorundumParser.LITERAL:
                    String str_terminal;
                    str_terminal = String.valueOf(symbol.getText());
                    str_terminal = str_terminal.replaceAll("\"", "");
                    str_terminal = str_terminal.replaceAll("\'", "");
                    string_values.put(node, str_terminal);
                    which_value.put(node, "String");
                    break;
                case CorundumParser.ID:
                    str_terminal = String.valueOf(symbol.getText());
                    string_values.put(node, str_terminal);
                    which_value.put(node, "Dynamic");
                    //System.out.println(str_terminal);
                    break;
            }
        }

        // ======================================== New line ========================================

        public void exitCrlf(CorundumParser.CrlfContext ctx) {
            NumStr++;
        }
    }

    // ======================================== Main ========================================

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