package jtable;

import javax.swing.*;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

/*
jdbc:oracle:thin:@127.0.0.1:1521:JAVA     
scott    
tiger  
*/

public class SbcJdbcLogin extends JFrame implements ActionListener{
	Container cp;
	JPanel jpMain;
	JButton loginB;
	JTextField jtUrl, jtId, jtPwd;
	JLabel jlUri, jlId, jlPwd, jlTitle;
	Font f = new Font("������� ExtraBold", Font.BOLD, 20);
	SbcJdbcLogin(){
		init();
		setUI();
	}
	
	void connect(String url, String id, String pwd) {
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url,id,pwd);
			JOptionPane.showMessageDialog(null, "�����ͺ��̽���  ���ӵǾ����ϴ�.", "���Ӽ���!", JOptionPane.INFORMATION_MESSAGE);
			new SbcJdbc(this, con);
			setVisible(false);
			
		}catch(ClassNotFoundException cnf) {
			JOptionPane.showMessageDialog(null, "�����ͺ��̽� ���ӿ� �����߽��ϴ�.", "���ӽ���!", JOptionPane.WARNING_MESSAGE);
		}catch(SQLException se) {
			JOptionPane.showMessageDialog(null, "�α��� ������ �߸��Ǿ����ϴ�.", "�α��� ����!", JOptionPane.WARNING_MESSAGE);
		}/*finally {
			try {
				con.close();
			}catch(SQLException se) {
			}
		}*/
	}
	void init() {
		cp = getContentPane();
		
		jpMain = new JPanel();
		jpMain.setLayout(null);
		
		jlTitle = new JLabel("L O G I N");
		jlTitle.setFont(f);
		jlTitle.setBounds(100, 15, 100,30);
		jtUrl = new JTextField();
		jtUrl.setBounds(80, 60, 180,30);
		jtUrl.setText("jdbc:oracle:thin:@127.0.0.1:1521:JAVA");
		jtId = new JTextField();
		jtId.setBounds(80, 100, 180,30);
		jtId.setText("scott");
		jtPwd = new JTextField();
		jtPwd.setBounds(80, 140, 180,30);
		jtPwd.setText("tiger");
		jlUri = new JLabel("URL : "); 
		jlUri.setBounds(20, 60, 80,30);
		jlId  = new JLabel("ID : ");
		jlId.setBounds(20, 100, 80,30);
		jlPwd = new JLabel("PWD : ");
		jlPwd.setBounds(20, 140, 80,30);
		loginB = new JButton("Login");
		loginB.setBounds(20, 190, 240,30);
		loginB.addActionListener(this);
		
		jpMain.add(jlTitle);
		jpMain.add(jtUrl);
		jpMain.add(jtId);
		jpMain.add(jtPwd);
		jpMain.add(jlUri);
		jpMain.add(jlId);
		jpMain.add(jlPwd);
		jpMain.add(loginB);
		
		cp.add(jpMain);
	}
	void setUI() {
		setTitle("Login");
		setVisible(true);
		setSize(300, 280);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == loginB) {
			 String url = jtUrl.getText();
			 url = url.strip();
			 String id = jtId.getText();
			 id = id.strip();
			 String pwd = jtPwd.getText();
			 pwd = pwd.strip();
			 if(url.length() == 0) {
				 JOptionPane.showMessageDialog(null,"URL�� �ݵ�� �Է��ؾ��մϴ�.", "URL ����", JOptionPane.WARNING_MESSAGE);
				 pln("url����");
			 }else if(id.length() == 0) {
				 JOptionPane.showMessageDialog(null,"ID�� �ݵ�� �Է��ؾ��մϴ�.", "ID ����", JOptionPane.WARNING_MESSAGE);
			 }else if(pwd.length() == 0) {
				 JOptionPane.showMessageDialog(null,"PWD�� �ݵ�� �Է��ؾ��մϴ�.", "PWD ����", JOptionPane.WARNING_MESSAGE);
			 }else {
				 connect(url, id, pwd);
			 }
		}
	} 
	void p(String str){
		System.out.print(str);
	}
	void pln(String str){
		System.out.println(str);
	}
	public static void main(String[] args) {
		new SbcJdbcLogin();
	}
}
