/*
 * (c) 2015, Muthiah Annamalai
 */

package org.ezhillang.RPNCalculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

//represent a token within the calculator

class Operand extends Token {
	Operand(String raw, double val) {
		super(raw,val);
	}
	
	Operand(Token tok) {				
		super(tok);
		Token.Kinds kind = tok.getKind();
		assert kind == Token.Kinds.NUMBER;
	}
}

class Operator extends Token {
	Operator(Token tok) {				
		super(tok);
		
		Token.Kinds kind = tok.getKind(); 
		//token kind cannot be number
		assert kind != Token.Kinds.NUMBER;
		this.m_val = Double.NaN;		
	}
}

public class RPNCalculator {
	boolean DEBUG = !true;
	List<Token> m_toks;
	Stack<Operand> operand_stack;
	Stack<Operator> operator_stack;
	
	public RPNCalculator(List<Token> toks) {
		m_toks = toks;
		operand_stack  = new Stack<Operand>();
		operator_stack  = new Stack<Operator>();		
	}
	
	void display_stacks() {
		if ( !DEBUG )
			return;
		
		System.out.println("#############");
		System.out.println("OPRTOR ="+operator_stack.size());		
		Iterator<Operator> tok = operator_stack.iterator();
		while(tok.hasNext()){
			System.out.println(tok.next());
		}
		System.out.println("OPRND = "+operand_stack.size());
		Iterator<Operand> tok2 = operand_stack.iterator();
		while(tok2.hasNext()){
			System.out.println(tok2.next());
		}		
		System.out.println("#############");
		
		return;
	}
	
	protected void simple_eval() throws Exception {
		display_stacks();
		
		Operator op = operator_stack.pop();
		Token.Kinds op_kind = op.getKind();
			
		Operand op_B = null, op_result = null;
		Operand op_A = operand_stack.pop();
		double dbl_op_A = op_A.getNumericValue(),dbl_op_B = Double.NaN,result = Double.NaN;
		
		if ( op_kind == Token.Kinds.ADD_OP ) {
			op_B = operand_stack.pop();
			dbl_op_B = op_B.getNumericValue();
			result = dbl_op_A + dbl_op_B;				
		} else if (  op_kind == Token.Kinds.SUB_OP ) {
			op_B = operand_stack.pop();				
			dbl_op_B = op_B.getNumericValue();
			result =  dbl_op_B - dbl_op_A;
		} else if ( op_kind == Token.Kinds.MUL_OP ) {
			op_B = operand_stack.pop();				
			dbl_op_B = op_B.getNumericValue();
			result = dbl_op_A * dbl_op_B;				
		} else if ( op_kind == Token.Kinds.DIV_OP ) {
			op_B = operand_stack.pop();				
			dbl_op_B = op_B.getNumericValue();
			result = dbl_op_B/dbl_op_A ;
		} else if ( op_kind == Token.Kinds.LOG10_OP ) {
			result = Math.log10(dbl_op_A);
		} else if ( op_kind == Token.Kinds.LOG_OP ) {
			result = Math.log(dbl_op_A);
		} else if ( op_kind == Token.Kinds.EXP_OP ) {
			result = Math.exp(dbl_op_A);
		} else if ( op_kind == Token.Kinds.SIN_OP ) {
			result = Math.sin(dbl_op_A);
		} else if ( op_kind == Token.Kinds.COS_OP ) {
			result = Math.cos(dbl_op_A);
			
		} else if ( op_kind == Token.Kinds.PERC_OP ) {
			op_B = operand_stack.pop();
			dbl_op_B = op_B.getNumericValue();
			result = (100.00*dbl_op_B)/dbl_op_A ;
		} else if ( op_kind == Token.Kinds.RPAREN ){
			//RPARENS trigger calculations
			while( operator_stack.peek().getKind() != Token.Kinds.LPAREN ) {
				this.simple_eval();			
			}
			assert operator_stack.pop().getKind() == Token.Kinds.LPAREN;
		} else if ( op_kind == Token.Kinds.LPAREN ) {
			//consume LPARENS
			return;
		} else {
			throw new Exception("Unknown operand kind "+op_kind.toString());
		}
		
		op_result = new Operand(Double.toString(result),result);
		operand_stack.push(op_result);
		
		if ( DEBUG )
			System.out.println("Result => "+operand_stack.peek());
		
		return ;
	}
	
	double eval() throws Exception {					
		
		Iterator<Token> tok_itr = m_toks.iterator();
		while( tok_itr.hasNext() ) {
			Token tok = tok_itr.next();
			if ( tok.m_kind == Token.Kinds.NUMBER ) {
				operand_stack.push(new Operand(tok));
				continue;
			}
			
			//no precedence yet. this tok is an operator
			// *, /,% have higher precedence over +,-
			if ( !operator_stack.isEmpty() ) {
				Operator top_operator = operator_stack.peek();
				while ( operator_stack.size() > 0 						
						&& Token.isPrecedent(top_operator,tok)) {
					//System.out.println("Precedence --->");					
					this.simple_eval();					
				}					
			} 
			
			operator_stack.push(new Operator(tok));			
			
			if ( !operator_stack.isEmpty() && tok.getKind() == Token.Kinds.RPAREN ) {
				while(     operator_stack.size() > 0 
						&& operator_stack.peek().getKind() != Token.Kinds.LPAREN ) {
					this.simple_eval();
				}
				assert operator_stack.pop().getKind() == Token.Kinds.LPAREN;
			}
		}
		
		while( !operator_stack.isEmpty() ) {
			this.simple_eval();			
		}
		
		//overflow/underflow of stack indicates error in expr
		assert operand_stack.size() == 1;
		assert operator_stack.size() == 0;
		
		Operand val = operand_stack.pop();
		return val.getNumericValue();
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {		
		 
		//basic trig: C^(0) + S^(0) =  1
		test_pair( new String [] {"sin","3.1415","*","sin","3.1415"},1,true);//verbose
		test_pair( new String [] {"sin","3.1415","*","sin","3.1415","+","cos","3.1415","*","cos","3.1415"},1,true);//verbose
		
		//test brackets
		//test_pair(new String [] {"(","5","-","3",")","*","2"},4);
		test_pair(new String [] {"log10","10"},1);
		test_pair( new String [] {"log",Double.toString(Math.E)}, 1.0 );
				
		test_pair( new String [] {"55","+","95","-","10"}, 140.0 );
		test_pair( new String [] {"55","%","100.0","-","10"},45.0);
		test_pair( new String [] {"5","*","4","-","3"}, +17.0);
		test_pair( new String [] {"-37","-","10","-","5"},-52.0);
		test_pair( new String [] {"100","/","10","/","5"},2.0);
		test_pair(new String [] {"100","/","10","-","1","+", "1","/","5"},9.2);

		//sub is left associative
		test_pair(new String [] {"5","-","6","*","7","-","10"}, -47); 
		//String [] chunks = {"(","5","-","6",")","*","(","7","-","10",")"}; //3
		
		//% % and / are also left associative
		test_pair(new String [] {"5","%","10","%","20"}, 250);
	
	}
	
	static void test_pair(String [] chunks,double expected_val) throws Exception {
		test_pair(chunks,expected_val,false);
	}
	
	static void test_pair(String [] chunks,double expected_val,boolean verbose) throws Exception {
		List<Token> toks = new ArrayList<Token>();
		for(String chunk:chunks) {
			toks.add( Token.parseToken(chunk) );
			if ( verbose )
				System.out.println(toks.get(toks.size()-1).toString());
		}
		RPNCalculator rpnc = new RPNCalculator(toks);
		double actual = rpnc.eval();
		if ( verbose )
			System.out.println("Testing => "+chunks[0]+chunks[1]+"|actual =>"+actual);
		
		assert actual == expected_val;
	}

}