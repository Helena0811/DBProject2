/*
 * JTable이 부착될 패널
 * 
 * TableModel을 그냥 내부 익명 클래스로 구현
 * */
package book;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class TablePanel extends JPanel{
	// DB 연동이 되려면 PreparedStatement가 있어야 하는데, PreparedStatement는 인터페이스로 connection에 의존적
	// BookMain의 con=manager.getConnection();
	// 따라서, BookMain의 Connection을 생성자의 인수로 넘겨받자! -> JTable이 생성되는 시점에서 con은 생성되어 있지 않으므로 메소드로 변경하자!
	Connection con;
	JTable table;
	JScrollPane scroll;
	TableModel model;
	
	// list는 동기화를 지원하지 않기 때문에, 고속의 속도 기대 가능
	// Vector는 동기화를 지원해줌(안전하지만, 고속의 속도는 기대하기 힘듬)
	// 동기화(Synchronization) : 특정 쓰레드가 사용중인 경우 동시에 움직이는 다른 쓰레드가 접근할 수 없음
	
	// Vector와 ArrayList는 둘 다 같다
	// 차이점 : 동기화  지원 여부
	Vector list=new Vector();
	Vector<String> columnName=new Vector<String>();
	
	int cols;
	
	public TablePanel() {
		table=new JTable();
		scroll=new JScrollPane(table);
		this.setLayout(new BorderLayout());
		this.add(scroll);
		
		this.setBackground(Color.PINK);
		setPreferredSize(new Dimension(650, 550));
	}
	
	public void setConnection(Connection con){
		this.con=con;
		
		// DB 연동이 먼저 되어야 함
		init();
		
		// TableModel은 내부 익명 클래스로 구현
		// 테이블 모델을 JTable에 적용
		model=new AbstractTableModel() {
					
			public int getRowCount() {
				return list.size();
			}
			
			// 이미 테이블은 결정되어 있음
			// column은 거의 고정되어 있기 때문에 프로그램적으로 얻어오는건 옳지 않을 수 있음
			// 하지만 metaData로 얻어올 수도 있음
			public int getColumnCount() {
				return cols;
			}
			// ★다시 보기★
			public Object getValueAt(int row, int col) {
				// public JTable(Vector row, Vector columnNames)
				// vectors of Vectors
				/*
				 * 원래는 x[row], y[row] -> x[data], x[col]로 
				 * */	
				
				// list vector안에 data vector가 들어있음
				// data는 rs로부터 가져오는 값	ex) rs.getString("book_name")
				// 따라서, row는 list의 행
				Vector vec=(Vector)list.get(row);	
				return vec.elementAt(col);
			}
		
		};
		
		// 테이블에 model 적용
		table.setModel(model);
	}
	
	// DB연동 - book 테이블의 레코드 가져오기
	public void init(){
		String sql="select * from book order by book_id asc";
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			cols=rs.getMetaData().getColumnCount();
			
			int index;
			
			list.removeAll(list);
			
			// rs의 정보를 collection의 DTO로 옮겨 저장하기
			while(rs.next()){
				Vector<String> data=new Vector<String>();
				/*
				 *  private int book_id;
					private int subcategory_id;
					private String book_name;
					private int price;
					private String img;
				 * */
				// rs는 모든 book의 정보를 가지고 있음
				// Book 자료형으로 dto 생성 -> JTable이 collectionFramework를 생성자 인수로 받지 못하므로
				// Collection Framework인 Vector사용
				// Book dto=new Book();
				
				// Book자료형 dto는 비어있으므로 rs로부터 정보 저장
				data.add(Integer.toString(rs.getInt("book_id")));
				data.add(rs.getString("book_name"));
				data.add(Integer.toString(rs.getInt("price")));
				data.add(Integer.toString(rs.getInt("subcategory_id")));
				data.add(rs.getString("img"));
				
				list.add(data);
				//((Vector)rowData.elementAt(1)).elementAt(5);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
