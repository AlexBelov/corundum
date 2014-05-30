import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.Stack;
import java.util.LinkedList;

public class Walker {
    public static class Evaluator extends CorundumBaseListener {
        ParseTreeProperty<Integer> int_values = new ParseTreeProperty<Integer>();
        ParseTreeProperty<Float> float_values = new ParseTreeProperty<Float>();
        ParseTreeProperty<String> string_values = new ParseTreeProperty<String>();
        ParseTreeProperty<String> which_value = new ParseTreeProperty<String>();

        Stack<ByteArrayOutputStream> stack_output_streams = new Stack<ByteArrayOutputStream>();

        ByteArrayOutputStream main_stream = new ByteArrayOutputStream();
        ByteArrayOutputStream func_stream = new ByteArrayOutputStream();
        ByteArrayOutputStream error_stream = new ByteArrayOutputStream();
        PrintStream ps_error = new PrintStream(error_stream);

        public int SemanticErrorsNum = 0;
        public int NumStr = 1;
        public int Num_reg = 0;
        public int Num_reg_int = 0;
        public int Num_label = 0;
        LinkedList<String> definitions = new LinkedList<String>();
        LinkedList<String> func_definitions = new LinkedList<String>();

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
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            switch(ctx.op.getType()) {
                case CorundumParser.ASSIGN:
                    String var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        ps.println(".local int " + ctx.var_id.getText());
                        definitions.add(ctx.var_id.getText());                        
                    }
                    break;
                default:
                    var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        ps_error.println("line " + NumStr + " Error! Undefined variable " + var + "!");
                        SemanticErrorsNum++;
                    }
                    break;
            }

            stack_output_streams.push(out); 
        }

        public void exitInt_assignment(CorundumParser.Int_assignmentContext ctx) {
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            String str = ctx.var_id.getText() + " " + ctx.op.getText() + " " + int_values.get(ctx.getChild(2));
            ps.println(str);

            stack_output_streams.push(out);
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
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            switch(ctx.op.getType()) {
                case CorundumParser.ASSIGN:
                    String var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        ps.println(".local num " + ctx.var_id.getText());
                        definitions.add(ctx.var_id.getText());
                    }
                    break;
                default:
                    var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        ps_error.println("line " + NumStr + " Error! Undefined variable " + var + "!");
                        SemanticErrorsNum++;
                    }
                    break;
            }

            stack_output_streams.push(out);
        }

        public void exitFloat_assignment(CorundumParser.Float_assignmentContext ctx) {
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            String str = ctx.var_id.getText() + " " + ctx.op.getText() + " " + float_values.get(ctx.getChild(2));
            ps.println(str);

            stack_output_streams.push(out);
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
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            switch(ctx.op.getType()) {
                case CorundumParser.ASSIGN:
                    String var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        ps.println(".local string " + ctx.var_id.getText());
                        definitions.add(ctx.var_id.getText());
                    }
                    break;
                default:
                    var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        ps_error.println("line " + NumStr + " Error! Undefined variable " + var + "!");
                        SemanticErrorsNum++;
                    }
                    break;
            }

            stack_output_streams.push(out);
        }

        public void exitString_assignment(CorundumParser.String_assignmentContext ctx) {
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            String str = ctx.var_id.getText() + " " + ctx.op.getText() + " \"" + string_values.get(ctx.getChild(2)) + "\"";
            ps.println(str);

            stack_output_streams.push(out);
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
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            switch(ctx.op.getType()) {
                case CorundumParser.ASSIGN:
                    String var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        ps.println(".local pmc " + ctx.var_id.getText());
                        ps.println(ctx.var_id.getText() + " = new \"Integer\"");
                        definitions.add(ctx.var_id.getText());
                        Num_reg = 0;
                    }
                    break;
                default:
                    var = ctx.var_id.getText();
                    if (!is_defined(definitions, var)) {
                        ps_error.println("line " + NumStr + " Error! Undefined variable " + var + "!");
                        SemanticErrorsNum++;
                    }
                    break;
            }

            stack_output_streams.push(out);
        }

        public void exitDynamic_assignment(CorundumParser.Dynamic_assignmentContext ctx) {
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            String str = ctx.var_id.getText() + " " + ctx.op.getText() + " " + string_values.get(ctx.getChild(2));
            Num_reg = 0;
            ps.println(str);

            stack_output_streams.push(out);
        }

        public void exitDynamic_result(CorundumParser.Dynamic_resultContext ctx) {
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

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

                ps.println("$P" + Num_reg + " = new \"Integer\"");
                ps.println(str_output);
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

            stack_output_streams.push(out);
        }

        public void exitDynamic(CorundumParser.DynamicContext ctx) {
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            String str_dyn_term = string_values.get(ctx.getChild(0));
            ps.println("$P" + Num_reg + " = new \"Integer\"");
            ps.println("$P" + Num_reg + " = " + str_dyn_term);
            string_values.put(ctx, "$P" + Num_reg);
            Num_reg++;
            which_value.put(ctx, which_value.get(ctx.getChild(0)));

            stack_output_streams.push(out);
        }

        public void exitId(CorundumParser.IdContext ctx) {
            string_values.put(ctx, string_values.get(ctx.getChild(0)));
            which_value.put(ctx, which_value.get(ctx.getChild(0)));
        }

        // ======================================== Array definition ========================================

        public void enterInitial_array_assignment(CorundumParser.Initial_array_assignmentContext ctx) {
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            String var = ctx.var_id.getText();

            if (!is_defined(definitions, var)) {
                ps.println(var + " = new \"ResizablePMCArray\"");
                definitions.add(var);
            }

            stack_output_streams.push(out);
        }

        public void exitArray_assignment(CorundumParser.Array_assignmentContext ctx) {
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            String var = ctx.var_id.getText();

            ps.println(var + ctx.arr_def.getText() + " = " + ctx.arr_val.getText());

            if (!is_defined(definitions, var)) {
                ps_error.println("line " + NumStr + " Error! Undefined variable " + var + "!");
                SemanticErrorsNum++;
            }

            stack_output_streams.push(out);
        }

        public void exitArray_selector(CorundumParser.Array_selectorContext ctx) {
            String var_selector = ctx.getText();
            string_values.put(ctx, var_selector);
            which_value.put(ctx, "Dynamic");
        }

        // ======================================== IF statement ========================================

        public void exitIf_statement(CorundumParser.If_statementContext ctx) {
            ByteArrayOutputStream else_body = new ByteArrayOutputStream();

            if (ctx.getChild(4).getText().contains("else")) {
                else_body = stack_output_streams.pop();     
            }

            ByteArrayOutputStream body = stack_output_streams.pop();
            ByteArrayOutputStream cond = stack_output_streams.pop();
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);
            String condition_var = string_values.get(ctx.getChild(1));

            Num_label++;
            ps.println(cond.toString());
            ps.println("if " + condition_var + " goto label_" + Num_label);
            Num_label++;
            ps.println("goto label_" + Num_label + ":");
            ps.println("label_" + (Num_label - 1) + ":");

            ps.println(body.toString());

            if (ctx.getChild(4).getText().contains("else")) {
                Num_label++;
                ps.println("goto label_" + Num_label + ":");
                ps.println("label_" + (Num_label - 1) + ":");              
                ps.println(else_body.toString());     
            }

            ps.println("label_" + Num_label + ":");

            stack_output_streams.push(out);
        }

        // ======================================== UNLESS statement ========================================

        public void exitUnless_statement(CorundumParser.Unless_statementContext ctx) {
            ByteArrayOutputStream else_body = new ByteArrayOutputStream();

            if (ctx.getChild(4).getText().contains("else")) {
                else_body = stack_output_streams.pop();     
            }

            ByteArrayOutputStream body = stack_output_streams.pop();
            ByteArrayOutputStream cond = stack_output_streams.pop();
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);
            String condition_var = string_values.get(ctx.getChild(1));

            Num_label++;
            ps.println(cond.toString());
            ps.println("unless " + condition_var + " goto label_" + Num_label);
            Num_label++;
            ps.println("goto label_" + Num_label + ":");
            ps.println("label_" + (Num_label - 1) + ":");

            ps.println(body.toString());

            if (ctx.getChild(4).getText().contains("else")) {
                Num_label++;
                ps.println("goto label_" + Num_label + ":");
                ps.println("label_" + (Num_label - 1) + ":");              
                ps.println(else_body.toString());     
            }

            ps.println("label_" + Num_label + ":");

            stack_output_streams.push(out);
        }

        // ======================================== FOR loop ========================================

        public void exitFor_statement(CorundumParser.For_statementContext ctx) {
            ByteArrayOutputStream temp4 = stack_output_streams.pop();
            ByteArrayOutputStream temp3 = stack_output_streams.pop();
            ByteArrayOutputStream temp2 = stack_output_streams.pop();
            ByteArrayOutputStream temp1 = stack_output_streams.pop();
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            if (ctx.getChildCount() == 11) {
                ps.println(temp1.toString());
                String cond = string_values.get(ctx.getChild(4));
                Num_label++;
                ps.println("label_" + Num_label + ":");
                Num_label++;
                ps.println(temp2.toString());
                ps.println("unless " + cond + " goto label_" + Num_label);
                ps.println(temp4.toString());
                ps.println(temp3.toString());
                ps.println("goto label_" + (Num_label - 1));
                ps.println("label_" + Num_label + ":");
            }
            else if (ctx.getChildCount() == 9) {
                ps.println(temp1.toString());
                String cond = string_values.get(ctx.getChild(3));
                Num_label++;
                ps.println("label_" + Num_label + ":");
                Num_label++;
                ps.println(temp2.toString());
                ps.println("unless " + cond + " goto label_" + Num_label);
                ps.println(temp4.toString());
                ps.println(temp3.toString());
                ps.println("goto label_" + (Num_label - 1));
                ps.println("label_" + Num_label + ":");
            }

            stack_output_streams.push(out);
        }

        public void enterInit_expression(CorundumParser.Init_expressionContext ctx) {
            ByteArrayOutputStream temp_1 = new ByteArrayOutputStream();
            stack_output_streams.push(temp_1);
        }

        public void enterCond_expression(CorundumParser.Cond_expressionContext ctx) {
            ByteArrayOutputStream temp_2 = new ByteArrayOutputStream();
            stack_output_streams.push(temp_2);
        }

        public void exitCond_expression(CorundumParser.Cond_expressionContext ctx) {
            string_values.put(ctx, string_values.get(ctx.getChild(0)));
        }

        public void enterLoop_expression(CorundumParser.Loop_expressionContext ctx) {
            ByteArrayOutputStream temp_3 = new ByteArrayOutputStream();
            stack_output_streams.push(temp_3);
        }

        public void enterStatement_body(CorundumParser.Statement_bodyContext ctx) {
            ByteArrayOutputStream temp_4 = new ByteArrayOutputStream();
            stack_output_streams.push(temp_4);
        }

        public void exitComparison_list(CorundumParser.Comparison_listContext ctx) {
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            if ( ctx.getChildCount() == 3 ) {
                String left = string_values.get(ctx.getChild(0));
                String right = string_values.get(ctx.getChild(2));     

                switch(ctx.op.getType()) {
                case CorundumParser.BIT_AND:
                    ps.println("$I" + Num_reg_int + " = " + left + " && " + right);
                    break;
                case CorundumParser.AND:
                    ps.println("$I" + Num_reg_int + " = " + left + " && " + right);
                    break;
                case CorundumParser.BIT_OR:
                    ps.println("$I" + Num_reg_int + " = " + left + " || " + right);
                    break;
                case CorundumParser.OR:
                    ps.println("$I" + Num_reg_int + " = " + left + " || " + right);
                    break;
                }

                string_values.put(ctx, "$I" + Num_reg_int);
                Num_reg_int++;
            }
            else if ( ctx.getChildCount() == 1 ) {
                string_values.put(ctx, string_values.get(ctx.getChild(0)));
            }

            stack_output_streams.push(out);
        }

        public void exitComparison(CorundumParser.ComparisonContext ctx) {
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            String left = ctx.left.getText();
            String right = ctx.right.getText();

            switch(ctx.op.getType()) {
                case CorundumParser.LESS:
                    ps.println("$I" + Num_reg_int + " = islt " + left + ", " + right);
                    break;
                case CorundumParser.GREATER:
                    ps.println("$I" + Num_reg_int + " = isgt " + left + ", " + right);
                    break;
                case CorundumParser.LESS_EQUAL:
                    ps.println("$I" + Num_reg_int + " = isle " + left + ", " + right);
                    break;
                case CorundumParser.GREATER_EQUAL:
                    ps.println("$I" + Num_reg_int + " = isge " + left + ", " + right);
                    break;
                case CorundumParser.EQUAL:
                    ps.println("$I" + Num_reg_int + " = iseq " + left + ", " + right);
                    break;
                case CorundumParser.NOT_EQUAL:
                    String temp = "\n$I" + Num_reg_int + " = not " + "$I" + Num_reg_int;
                    ps.println("$I" + Num_reg_int + " = iseq " + left + ", " + right + temp);
                    
                    break;
            }
            string_values.put(ctx, "$I" + Num_reg_int);
            Num_reg_int++;

            stack_output_streams.push(out);
        }

        // ======================================== WHILE loop ========================================

        public void exitWhile_statement(CorundumParser.While_statementContext ctx) {
            ByteArrayOutputStream body = stack_output_streams.pop();
            ByteArrayOutputStream cond = stack_output_streams.pop();
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            String condition_var = string_values.get(ctx.getChild(1));
            Num_label++;
            ps.println("label_" + Num_label + ":");
            Num_label++;
            ps.println(cond.toString());
            ps.println("unless " + condition_var + " goto label_" + Num_label);
            ps.println(body.toString());
            ps.println("goto label_" + (Num_label - 1));
            ps.println("label_" + Num_label + ":");

            stack_output_streams.push(out);
        }

        // ======================================== PIR inline ========================================

        public void enterPir_expression_list(CorundumParser.Pir_expression_listContext ctx) {
            ByteArrayOutputStream empty_stream = new ByteArrayOutputStream();
            stack_output_streams.push(empty_stream);
        }

        public void exitPir_expression_list(CorundumParser.Pir_expression_listContext ctx) {
            ByteArrayOutputStream empty_stream = stack_output_streams.pop();
            ByteArrayOutputStream out = stack_output_streams.pop();
            PrintStream ps = new PrintStream(out);

            ps.println(ctx.getText());

            stack_output_streams.push(out);
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
        eval.stack_output_streams.push(eval.main_stream);
        walker.walk(eval, tree);

        System.out.println("\n======================================================");
        ByteArrayOutputStream out = eval.stack_output_streams.pop();
        System.out.println(out.toString());
    }
}