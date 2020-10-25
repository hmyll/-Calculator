package main;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Main extends JFrame implements ActionListener {

	private String expression;//记录表达式
	private final String[] keys = {"(",")","CE","Back",//按键
								     "7","8","9","+",
								     "4","5","6","-",			
								     "1","2","3","*",	
								     ".","0","=","/"};
	private JTextField result = new JTextField("");//结果 	
	private JButton[] buttons = new JButton[20];//按钮
	
	Main(){
		this.setTitle("简单计算器");
		this.setLocation(500,300);
		JPanel panel = new JPanel();//计算器布局
		panel.setLayout(new GridLayout(6,1));//布局分6行，第一行文本框
		panel.add(result);
		result.setFont(result.getFont().deriveFont((float)30));
		Container[] containers = new Container[5];
		for(int i=0 ; i<5; i++){
			containers[i] = new Container();
			containers[i].setLayout(new GridLayout(1,4));
			panel.add(containers[i]);
		}
		for(int i=0; i<buttons.length; i++){
			buttons[i] = new JButton(keys[i]);
			buttons[i].setFont(buttons[i].getFont().deriveFont((float)25));//设置字体大小
			containers[i/4].add(buttons[i]);
			buttons[i].addActionListener(this);
		}
		getContentPane().add(panel);
		expression="";//表达式为空
	}
	
	public static void main(String[] args) {
		Main calculator = new Main();
		calculator.setSize(400,400);//计算器长宽
		calculator.setVisible(true);	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if(action.equals(keys[2])){//清空键c
			expression = "";
		}else if(action.equals(keys[3])){//删除一个字符back
			expression = expression.substring(0,expression.length()-1);
		}else if(action.equals(keys[18])){//计算结果=
			expression = Calculator.solve(expression);
		}else {
			expression += action;
		}
		result.setText(expression);
	}
}

class Calculator{
	
	public static String solve(String expression){//计算表达式

		double sum=0,flag=0;
		int top=-1;// 存数堆栈的指针
		//Stack<Double> stack1 = new Stack<Double>();Double类型的堆栈会损失精度，如存入2.1取出时就是2.0了
		double[] stack1 =  new double[100];// 存数的堆栈,用数组模拟堆栈
		Stack<Character> stack2 = new Stack<Character>();//存运算符的堆栈
		
		for(int i=0 ; i<expression.length(); i++){
			char p = expression.charAt(i);
			flag = sum = 0;
			if(p >= '0' && p <= '9'){//存入数字
				while(true){
					p = expression.charAt(i);
					if(p == '.') flag = 0.1;
					else {
						if(flag == 0) sum = sum*10+(p-'0');
						else {//小数位处理
							sum += ((p-'0')*flag);
							flag *= 0.1;
						}
					}					
					if(i+1<expression.length()&&((expression.charAt(i+1)>='0'&&expression.charAt(i+1)<='9')||expression.charAt(i+1)=='.')) i++;
					else {
						stack1[++top]=sum;
						break;
					}
				} 
			}
			else {
				if(p=='(') stack2.push(p);
				else if(p == '*' || p == '/') {// 此时运算符是*或/时堆栈中优先级高的先计算
					while(stack2.size()>0 && (stack2.peek()=='*'||stack2.peek()=='/')) {
						double temp = compute(stack1[top--],stack1[top--],stack2.pop());
						stack1[++top] = temp;
					}
					stack2.push(p);//最后放入
				} 
				else if(p=='+'||p=='-') {// 此时运算符是+或-时括号之前的内容都取出来
					while(stack2.size()>0&&stack2.peek()!='(') {
						double temp=compute(stack1[top--],stack1[top--],stack2.pop());
						stack1[++top]=temp;
					}
					stack2.push(p);
				}
				else if(p==')') {// 此时运算符是)时堆栈内容都取出来
					while(stack2.size()>0&&stack2.peek()!='(') {
						double temp = compute(stack1[top--],stack1[top--],stack2.pop());
						stack1[++top] = temp;
					}
					stack2.pop();//取出左括号
				}
			}
		}
		while(stack2.size()>0) {//计算剩下的式子
			double temp=compute(stack1[top--],stack1[top--],stack2.pop());
			stack1[++top] = temp;
		}
		return "" + stack1[top];//double类型强制转为String类型
	}
	
	public static double compute(double a,double b, char p) {//在函数中运算
		double sum = 0;
		if(p=='+') {
			sum = b+a;
		}else if(p=='-') {//要注意用栈顶的数是被减数或者被除数
			sum = b-a;
		}else if(p=='*') {
			sum = b*a;
		}else if(p=='/') {
			sum = b/a;
		}
		return sum;
	}
}