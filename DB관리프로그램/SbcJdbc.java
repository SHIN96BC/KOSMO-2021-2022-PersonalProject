package jtable;


import javax.swing.*;
import javax.swing.plaf.PanelUI;
import javax.swing.table.DefaultTableModel;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;  // 테이블명, 컬럼명, 컬럼갯수 가져올때
import java.sql.Types;
import java.sql.Date;
import java.util.Vector;
import java.util.HashMap;
import java.util.Set;

public class SbcJdbc extends JFrame implements MouseListener, ActionListener, WindowListener{
	Container cp;
	JTable jt;
	DefaultTableModel dtm;
	JScrollPane jp;
	JPanel jTablePanel, jMenuPanel, jMainPanel, jTextPanel, jMenuTextPanel, jLablePanel;
	JComboBox tableCombo, columnCombo;
	JTextField searchText;
	JButton insertB, updateB, deleteB;
	JLabel jTableLabel, jColumnLabel, jSearchLabel;
	
	SbcJdbcLogin sjl;
	Connection con;
	Statement stmt;
	Vector<String> tableName = new Vector<String>();
	Vector<Vector> rowData = new Vector<Vector>();
	Vector<String> columnName = new Vector<String>();
	Vector<Integer> columnType = new Vector<Integer>();
	//HashMap<String, JTextField> jtextNum = new HashMap<String, JTextField>();
	//Vector<JTextField> jtextNum1 = new Vector<JTextField>(); //텍스트필드 셋팅 백터사용
	SbcJdbc(SbcJdbcLogin sjl, Connection con){
		this.sjl = sjl;
		this.con = con;
		setTable();
		setDataEarly(tableName.get(0));
		init();
		setUI();
	}
	void setTable() {
		String sql = "select * from tab";
		ResultSet rs = null;
		try {
			tableName.clear();
			stmt = con.createStatement();
			//stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE); // rs.next할때 위 아래 둘다 움직일수 있게 해주는 것
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				tableName.add(rs.getString(1));
			}
		}catch(SQLException se) {
		}finally {
			try {
				if(rs != null)rs.close();
			}catch(SQLException se) {
			}
		}
	}
	void setDataEarly(String tname) {
		String pKeyCloumn = primarykeySearch(tname);
		String sql = "select * from "+ tname;
		if(pKeyCloumn.length()!=0) sql = "select * from "+ tname + " order by "+pKeyCloumn;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		try {
			columnType.clear();
			columnName.clear();
			rowData.clear();
			//jtextNum1.clear();  //텍스트필드 셋팅 백터사용
			//jtextNum.clear();
			rs = stmt.executeQuery(sql);
			rsmd = rs.getMetaData();
			int columnNum = rsmd.getColumnCount();
			for(int j=1; j<columnNum+1; j++) {  
				//JTextField jtext = new JTextField();    // 패널에 updateUI() 써서 업데이트되나 해보자
				columnName.add(rsmd.getColumnName(j));
				columnType.add(rsmd.getColumnType(j));
				//jtextNum1.add(jtext);  //텍스트필드 셋팅 백터사용
				//jtextNum.put(rsmd.getColumnName(j), jtext);
			}
			while(rs.next()) {
				Vector<String> data = new Vector<String>();
				for(int k=1; k<columnNum+1; k++) {
					data.add(rs.getString(k));
				}
				rowData.add(data);
			}
		}catch(SQLException se) {
			sjl.pln("se: " + se);
		}finally {
			try {
				if(rs != null)rs.close();
			}catch(SQLException se) {
			}
		}
	}
	void init() {
		cp = getContentPane();
		dtm = new DefaultTableModel(rowData, columnName);
		jt = new JTable(dtm);
		jp = new JScrollPane(jt);
		jt.addMouseListener(this);
		tableCombo = new JComboBox(tableName);
		tableCombo.addActionListener(this);
		columnCombo = new JComboBox(columnName);
		searchText = new JTextField();
		insertB = new JButton("추가");
		insertB.addActionListener(this);
		updateB = new JButton("수정");
		updateB.addActionListener(this);
		deleteB = new JButton("삭제");
		deleteB.addActionListener(this);
		jTableLabel = new JLabel("테이블 변경");
		jColumnLabel = new JLabel("컬럼 변경");
		jSearchLabel = new JLabel("검색");

		jTablePanel = new JPanel(new GridLayout(1,1));
		jTablePanel.add(jp);
		jMenuPanel = new JPanel(new GridLayout(2,3));
		jTextPanel = new JPanel();
		jTextPanel.setLayout(new GridLayout(1, columnName.size()));
		setTextField();
		jLablePanel = new JPanel(new GridLayout(1,3));
		jLablePanel.add(jTableLabel);
		jLablePanel.add(jColumnLabel);
		jLablePanel.add(jSearchLabel);
		
		jMenuPanel.add(tableCombo);
		jMenuPanel.add(columnCombo);
		jMenuPanel.add(searchText);
		jMenuPanel.add(insertB);
		jMenuPanel.add(updateB);
		jMenuPanel.add(deleteB);
		jMenuTextPanel = new JPanel(new GridLayout(2,1));
		jMenuTextPanel.add(jTextPanel);
		jMenuTextPanel.add(jMenuPanel);
		
		jMainPanel =new JPanel(new GridLayout(2,1));
		jMainPanel.add(jTablePanel);
		jMainPanel.add(jMenuTextPanel);
		cp.add(jMainPanel);
	}
	void setUI() {
		setTitle("sbcJdbc");
		setVisible(true);
		setSize(1400, 800);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //DO_NOTHING_ON_CLOSE란  x버튼을 눌러도 아무것도 하지않는것이다.
		addWindowListener(this);   //JFrame에 윈도우 리스너 적용
	}
// 실험용 (백터, 해쉬맵 없이도 성공)
	void setTextField() {        
		jTextPanel.removeAll();
		for(int i=0; i<columnName.size(); i++) {   // 패널에 updateUI() 써서 실시간 UI 업데이트가 된다	
			JTextField jtext = new JTextField();
			jTextPanel.add(jtext);    
		}
		jTextPanel.updateUI();
	}
	void JTableEvent() {  // 같은 키값으로 다른벨류값을 넣으면 업데이틑 될까?
		jTextPanel.removeAll();
		String tname = (String)tableCombo.getSelectedItem();
		String pKeyCloumn = primarykeySearch(tname);
		int row = jt.getSelectedRow();            // getColumnName(int);
		for(int i=0; i<columnName.size(); i++) {
			String val = (String)jt.getValueAt(row, i);
			String columnName = jt.getColumnName(i);
			if(pKeyCloumn.equalsIgnoreCase(columnName)) {
				JTextField jtext = new JTextField();
				jtext.setEditable(false);
				jtext.setText(val);
				jTextPanel.add(jtext);
				continue;
			}
			JTextField jtext = new JTextField();
			jtext.setText(val);
			jTextPanel.add(jtext);
		}
		jTextPanel.updateUI();
	}	
// 유저가 컬럼명을 이동시키고 작업을 하고있을때 유저의 눈에 보이는 위치를 그대로 유지시켜주기위한 임시 테이블 셋팅 메소드
	void setDataTemp(String tname) {
		String pKeyCloumn = primarykeySearch(tname);
		String sql = "select * from "+tname+" order by "+pKeyCloumn;
		Vector<String> columnNameTemp = new Vector<String>();
		Vector<Vector> rowDataTemp = new Vector<Vector>();
		Vector<Integer> columnIndexTemp = new Vector<Integer>();
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		try {
			rs = stmt.executeQuery(sql);
			rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for(int i=0; i<columnCount; i++) {
				String colJtName = jt.getColumnName(i);
				for(int j=1; j<columnCount+1; j++) {
					String colRsName = rsmd.getColumnName(j);
					if(colJtName.equalsIgnoreCase(colRsName)) {
						columnNameTemp.add(colRsName);
						columnIndexTemp.add(j);
					}
				}
			}
			while(rs.next()) {
				Vector<String> row = new Vector<String>();
				for(int j=0; j<columnIndexTemp.size(); j++) {
					row.add(rs.getString(columnIndexTemp.get(j)));
				}
				rowDataTemp.add(row);
			}
			dtm.setDataVector(rowDataTemp, columnNameTemp);
		}catch(SQLException se) {
			sjl.pln("setData 에러 : "+ se);
		}
		
	}
	
/*텍스트필드 셋팅 해쉬맵사용
	void setTextField() {
		jTextPanel.removeAll();
		for(int i=0; i<jtextNum.size(); i++) {
			for(String key: jtextNum.keySet()) {   // 패널에 updateUI() 써서 실시간 UI 업데이트가 된다	
				String columnName = jt.getColumnName(i);
				if(columnName.equalsIgnoreCase(key)) {
					jTextPanel.add(jtextNum.get(key));    
				}
			}
		}
		jTextPanel.updateUI();
	}
	void JTableEvent() {  // 같은 키값으로 다른벨류값을 넣으면 업데이틑 될까?
		jTextPanel.removeAll();
		String pKeyCloumn = primarykeySearch();
		int row = jt.getSelectedRow();            // getColumnName(int);
		for(int i=0; i<jtextNum.size(); i++) {
			for(String key: jtextNum.keySet()) {   // 패널에 updateUI() 써서 실시간 UI 업데이트가 된다	
				String val = (String)jt.getValueAt(row, i);
				String columnName = jt.getColumnName(i);
				if(columnName.equalsIgnoreCase(key)) {
					if(pKeyCloumn.equalsIgnoreCase(key)){
						JTextField jtext = jtextNum.get(key);
						jtext.setEditable(false);
						jtext.setText(val);
						jTextPanel.add(jtext);   
						continue;
					}
					JTextField jtext = jtextNum.get(key);
					jtext.setText(val);
					jTextPanel.add(jtext);
				}
			}
		}
		jTextPanel.updateUI();
	}
*/
	
/* 텍스트필드 셋팅 백터사용  (setEditable(false)가 안풀리는 버그 있음 )
	void setTextField() {        
		jTextPanel.removeAll();
		for(int i=0; i<jtextNum1.size(); i++) {   // 패널에 updateUI() 써서 실시간 UI 업데이트가 된다
			jTextPanel.add(jtextNum1.get(i));
		}
		jTextPanel.updateUI();
	}
	void JTableEvent() {        
		jTextPanel.removeAll();
		String pKeyCloumn = primarykeySearch();
		int row = jt.getSelectedRow();            // getColumnName(int);
		for(int i=0; i<jtextNum1.size(); i++) {   // 패널에 updateUI() 써서 실시간 UI 업데이트가 된다
			String val = (String)jt.getValueAt(row, i);
			String columnName = jt.getColumnName(i);
			
			if(pKeyCloumn.equalsIgnoreCase(columnName)){
				JTextField jtext = jtextNum1.get(i);
				jtext.setEditable(false);
				jtext.setText(val);
				jTextPanel.add(jtext);
				continue;
			}
			JTextField jtext = jtextNum1.get(i);
			jtext.setText(val);
			jTextPanel.add(jtext);
		}
		jTextPanel.updateUI();
	}
*/
	void columnSelect() { 
		
	}
	// 인서트 할때 into(컬럼이름) 해서 입력한 컬럼만 인서트 되게 할지 고민중...
	// DATE 타입에선 SYSDATE 랑 년도, 월, 일, 입력했을때랑 시간까지 입력했을때 차이가 있어서 if문으로 나눠줘야겠다.
	void insertSql(String tname) {   // valchar 타입은 '' 을 적어줘야되서 sql 에러가 발생함. 그렇다면 타입검사를 모두 해줄 필요가 있을 것 같다.
		String pKeyColumn = primarykeySearch(tname);
		String sql = "insert into "+tname+"(";
		String sqlVal = " values(";
		boolean flag = false;
		
		for(int i=0; i<columnName.size(); i++) {
			String colName = columnName.get(i);
			int colType = columnType.get(i);
			for(int j=0; j<columnName.size(); j++) {
				String colJtName = jt.getColumnName(j);
				if(colName.equalsIgnoreCase(colJtName)) {
					JTextField jtext = (JTextField)jTextPanel.getComponent(j); // 패널 
					String str = jtext.getText();
					str = str.strip();
					if(str.length() == 0 && pKeyColumn.equalsIgnoreCase(colJtName)) {
						JOptionPane.showMessageDialog(null, "키본키는 반드시 입력해야합니다.", "SQL에러!", JOptionPane.WARNING_MESSAGE);
						return;
					}
					if(str.length() == 0) break;
					if(colType == Types.CHAR || colType == Types.VARCHAR || colType == Types.NVARCHAR) {
						if(flag == true) {
							sql += ", ";
							sqlVal += ", ";
						}
						sql += colJtName;
						sqlVal += "'"+str+"'";
						flag = true;
						break;
					}else if(colType == Types.DATE || colType ==Types.TIME|| colType ==Types.TIMESTAMP) {
						if(flag == true) {
							sql += ", ";
							sqlVal += ", ";
						}
						if(str.equalsIgnoreCase("SYSDATE")) {
							sql += colJtName;
							sqlVal += str;
						}else {
							sql += colJtName;
							sqlVal += "TO_DATE('"+str+"', 'YYYY-MM-DD HH24:MI:SS')";
						}
						flag = true;
						break;
					}else {
						if(flag == true) {
							sql += ", ";
							sqlVal += ", ";
						}
						sql += colJtName;
						sqlVal += str;
						flag = true;
						break;
					}
				}
			}
		}
		sql += ")";
		sqlVal += ")";
		sql += sqlVal;
		try {
			int i = stmt.executeUpdate(sql);
			if(i>0) sjl.pln("입력성공");
			else sjl.pln("입력실패");
		}catch(SQLException se) {
			sjl.pln("입력실패: "+ se);
		}
	}
// 기본키로만 삭제할지, 어떤 컬럼이든 삭제 가능하게할지 고민중... (어떤 컬럼이든 가능하게하면 여러개 동시 삭제 가능)
	void deleteSql(String tname) {
		//String pKeyCloumn = primarykeySearch(tname);
		//String sql = "delete from "+tname+" where "+pKeyCloumn+"=";
		String sql = "delete from "+tname+" where ";
		boolean flag = false;
		
		for(int i=0; i<columnName.size(); i++) {
			String colName = columnName.get(i);
			int colType = columnType.get(i);
			for(int j=0; j<columnName.size(); j++) {
				String colJtName = jt.getColumnName(j);
				if(colName.equalsIgnoreCase(colJtName)) {
					JTextField jtext = (JTextField)jTextPanel.getComponent(j);
					String str = jtext.getText();
					str = str.strip();
					if(str.length() == 0) break;
					if(colType == Types.CHAR || colType == Types.VARCHAR || colType == Types.NVARCHAR ) {
						if(flag == true) sql += " or ";
						sql += colJtName+" = '"+str+"'";
						flag = true;
						break;
					}else if(colType == Types.DATE || colType ==Types.TIME|| colType ==Types.TIMESTAMP) {
						if(flag == true) sql += " or ";
						if(str.equalsIgnoreCase("SYSDATE")) sql += str;
						else sql += colJtName+" = TO_DATE('"+str+"', 'YYYY-MM-DD HH24:MI:SS')";
						flag = true;
						break;
					}else {
						if(flag == true) sql += " or ";
						sql += colJtName+" = "+str;
						flag = true;
						break;
					}
				}
			}
		}
		try {
			int i = stmt.executeUpdate(sql);
			if(i>0) sjl.pln("삭제 성공");
			else sjl.pln("삭제 실패");
		}catch(SQLException se) {
			sjl.pln("삭제 실패: " + se);
		}
/*	
 기본키로 하나씩만 지울때 썼던 로직
		for(int i=0; i<columnName.size(); i++) {
			if(pKeyCloumn.equalsIgnoreCase(columnName.get(i))) {
				colType = columnType.get(i);
				break;
			}
		}
		for(int j=0; j<columnName.size(); j++) {
			if(pKeyCloumn.equalsIgnoreCase(jt.getColumnName(j))) {
				JTextField jtext = (JTextField)jTextPanel.getComponent(j); // 패널 
				str = jtext.getText();
				str = str.strip();
				if(colType == Types.CHAR || colType == Types.VARCHAR || colType == Types.NVARCHAR) {
					str = "'"+str+"'";
				}else if(colType == Types.DATE || colType ==Types.TIME|| colType ==Types.TIMESTAMP) {
					str = "TO_DATE('"+str+"', 'YYYY-MM-DD HH24:MI:SS')";
				}
			}
		}
		sql += str;
		try {
			sjl.pln("sql: " + sql);
			int i = stmt.executeUpdate(sql);
			if(i>0) sjl.pln("삭제 성공");
			else sjl.pln("삭제 실패");
		}catch(SQLException se) {
			sjl.pln("삭제 실패: " + se);
		}
		*/
	}
	void updateSql(String tname) {
		String pKeyColumn = primarykeySearch(tname);
		String sql = "update "+tname+" set ";
		String sqlWhere = "";
		boolean flag = false;
		
		for(int i=0; i<columnName.size(); i++) {
			String colName = columnName.get(i);
			int colType = columnType.get(i);
			for(int j=0; j<columnName.size(); j++) {
				String colJtName = jt.getColumnName(j);
				if(colName.equalsIgnoreCase(colJtName)) {
					if(pKeyColumn.equalsIgnoreCase(colJtName)) {
						JTextField jtext = (JTextField)jTextPanel.getComponent(j);
						String str = jtext.getText();
						str = str.strip();
						if(str.length() == 0) {
							JOptionPane.showMessageDialog(null, "키본키는 반드시 입력해야합니다.", "SQL에러!", JOptionPane.WARNING_MESSAGE);
							return;
						}
						if(colType == Types.CHAR || colType == Types.VARCHAR || colType == Types.NVARCHAR) {
							sqlWhere = " where "+colJtName+" = '"+str+"'";
							break;
						}else if(colType == Types.DATE || colType ==Types.TIME|| colType ==Types.TIMESTAMP) {
							if(str.equalsIgnoreCase("SYSDATE"))sqlWhere = "where "+colJtName+" = "+str;
							else sqlWhere = " where "+colJtName+" = "+"TO_DATE('"+str+"', 'YYYY-MM-DD HH24:MI:SS')";
							break;
						}else {
							sqlWhere = " where "+colJtName+" = "+str;
							break;
						}
					}else {
						JTextField jtext = (JTextField)jTextPanel.getComponent(j);
						String str = jtext.getText();
						str = str.strip();
						if(str.length() == 0) break;
						if(colType == Types.CHAR || colType == Types.VARCHAR || colType == Types.NVARCHAR) {
							if(flag == true) sql += ", ";
							sql += colJtName+" = '"+str+"'";
							flag = true;
							break;
						}else if(colType == Types.DATE || colType ==Types.TIME|| colType ==Types.TIMESTAMP) {
							if(flag == true) sql += ", ";
							if(str.equalsIgnoreCase("SYSDATE"))sql += colJtName+" = "+str;
							else sql += colJtName+" = "+"TO_DATE('"+str+"', 'YYYY-MM-DD HH24:MI:SS')";
							flag = true;
							break;
						}else {
							if(flag == true) sql += ", ";
							sql += colJtName+" = "+str;
							flag = true;
							break;
						}
					}
				}
			}
		}
		sql += sqlWhere;
		try {
			int i = stmt.executeUpdate(sql);
			if(i>0) sjl.pln("수정 성공");
			else sjl.pln("수정 실패");
		}catch(SQLException se) {
			sjl.pln("수정 실패: " + se);
		}
	}
// 테이블별 프라이머리 키 조회 sql문  ( 대신 뷰권한을 줘야한다. )
	String primarykeySearch(String tname) {   
		String sql = "select COLUMN_NAME from ALL_CONSTRAINTS CONS, ALL_CONS_COLUMNS COLS where COLS.TABLE_NAME = '"+tname+"' and CONS.CONSTRAINT_TYPE = 'P' and CONS.CONSTRAINT_NAME = COLS.CONSTRAINT_NAME and CONS.OWNER = COLS.OWNER";
		ResultSet rs = null;
		try {
			String cloumnName = "";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				cloumnName = rs.getString(1);
			}
			return cloumnName;
		}catch(SQLException se) {
			sjl.pln("primarykeySearch 에러 se: " + se);
		}
		return "";
	}
//액션리스너
	@Override
	public void actionPerformed(ActionEvent e) {
		Object event = e.getSource();
		if(event == tableCombo) {
			String tname = (String)tableCombo.getSelectedItem();
			setDataEarly(tname);
			dtm.setDataVector(rowData, columnName);
			setTextField();
			try {
				columnCombo.setSelectedIndex(0);
			}catch(IndexOutOfBoundsException iobe) {
				JOptionPane.showMessageDialog(null, "테이블에 아무것도 존재하지 않습니다.", "테이블에러!", JOptionPane.WARNING_MESSAGE);
			}
		}else if(event == insertB) {
			String tname = (String)tableCombo.getSelectedItem();
			insertSql(tname);
			setDataTemp(tname);
			setTextField();
		}else if(event == updateB) {
			String tname = (String)tableCombo.getSelectedItem();
			updateSql(tname);
			setDataTemp(tname);
			setTextField();
		}else if(event == deleteB) {
			String tname = (String)tableCombo.getSelectedItem();
			deleteSql(tname);
			setDataTemp(tname);
			setTextField();
		}
	}
//마우스 리스너
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		Object event = e.getSource();
		if(event == jt) {
			JTableEvent();
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {}
//윈도우 리스너
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowClosing(WindowEvent e) {  // GUI의 오른쪽위에 X키 눌렀을때 이벤트처리
		int answer = JOptionPane.showConfirmDialog(null,"정말 종료하시겠습니까?", "시스템종료", JOptionPane.YES_NO_OPTION);
		if(answer == JOptionPane.YES_OPTION) { // 종료전 커밋할껀지 물어보기
			try {
				if(stmt != null) stmt.close();
				if(con != null) con.close();
			}catch(SQLException se) {
				System.exit(0);
			}
			System.exit(0);
		}
	}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {
	}
}

