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

	private String expression;//��¼���ʽ
	private final String[] keys = {"(",")","CE","Back",//����
								     "7","8","9","+",
								     "4","5","6","-",			
								     "1","2","3","*",	
								     ".","0","=","/"};
	private JTextField result = new JTextField("");//��� 	
	private JButton[] buttons = new JButton[20];//��ť
	
	Main(){
		this.setTitle("�򵥼�����");
		this.setLocation(500,300);
		JPanel panel = new JPanel();//����������
		panel.setLayout(new GridLayout(6,1));//���ַ�6�У���һ���ı���
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
			buttons[i].setFont(buttons[i].getFont().deriveFont((float)25));//���������С
			containers[i/4].add(buttons[i]);
			buttons[i].addActionListener(this);
		}
		getContentPane().add(panel);
		expression="";//���ʽΪ��
	}
	
	public static void main(String[] args) {
		Main calculator = new Main();
		calculator.setSize(400,400);//����������
		calculator.setVisible(true);	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if(action.equals(keys[2])){//��ռ�c
			expression = "";
		}else if(action.equals(keys[3])){//ɾ��һ���ַ�back
			expression = expression.substring(0,expression.length()-1);
		}else if(action.equals(keys[18])){//������=
			expression = Calculator.solve(expression);
		}else {
			expression += action;
		}
		result.setText(expression);
	}
}

class Calculator{
	
	public static String solve(String expression){//������ʽ

		double sum=0,flag=0;
		int top=-1;// ������ջ��ָ��
		//Stack<Double> stack1 = new Stack<Double>();Double���͵Ķ�ջ����ʧ���ȣ������2.1ȡ��ʱ����2.0��
		double[] stack1 =  new double[100];// �����Ķ�ջ,������ģ���ջ
		Stack<Character> stack2 = new Stack<Character>();//��������Ķ�ջ
		
		for(int i=0 ; i<expression.length(); i++){
			char p = expression.charAt(i);
			flag = sum = 0;
			if(p >= '0' && p <= '9'){//��������
				while(true){
					p = expression.charAt(i);
					if(p == '.') flag = 0.1;
					else {
						if(flag == 0) sum = sum*10+(p-'0');
						else {//С��λ����
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
				else if(p == '*' || p == '/') {// ��ʱ�������*��/ʱ��ջ�����ȼ��ߵ��ȼ���
					while(stack2.size()>0 && (stack2.peek()=='*'||stack2.peek()=='/')) {
						double temp = compute(stack1[top--],stack1[top--],stack2.pop());
						stack1[++top] = temp;
					}
					stack2.push(p);//������
				} 
				else if(p=='+'||p=='-') {// ��ʱ�������+��-ʱ����֮ǰ�����ݶ�ȡ����
					while(stack2.size()>0&&stack2.peek()!='(') {
						double temp=compute(stack1[top--],stack1[top--],stack2.pop());
						stack1[++top]=temp;
					}
					stack2.push(p);
				}
				else if(p==')') {// ��ʱ�������)ʱ��ջ���ݶ�ȡ����
					while(stack2.size()>0&&stack2.peek()!='(') {
						double temp = compute(stack1[top--],stack1[top--],stack2.pop());
						stack1[++top] = temp;
					}
					stack2.pop();//ȡ��������
				}
			}
		}
		while(stack2.size()>0) {//����ʣ�µ�ʽ��
			double temp=compute(stack1[top--],stack1[top--],stack2.pop());
			stack1[++top] = temp;
		}
		return "" + stack1[top];//double����ǿ��תΪString����
	}
	
	public static double compute(double a,double b, char p) {//�ں���������
		double sum = 0;
		if(p=='+') {
			sum = b+a;
		}else if(p=='-') {//Ҫע����ջ�������Ǳ��������߱�����
			sum = b-a;
		}else if(p=='*') {
			sum = b*a;
		}else if(p=='/') {
			sum = b/a;
		}
		return sum;
	}
}