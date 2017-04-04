/*
 * JTable�� ������ �г�
 * 
 * TableModel�� �׳� ���� �͸� Ŭ������ ����
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
	// DB ������ �Ƿ��� PreparedStatement�� �־�� �ϴµ�, PreparedStatement�� �������̽��� connection�� ������
	// BookMain�� con=manager.getConnection();
	// ����, BookMain�� Connection�� �������� �μ��� �Ѱܹ���! -> JTable�� �����Ǵ� �������� con�� �����Ǿ� ���� �����Ƿ� �޼ҵ�� ��������!
	Connection con;
	JTable table;
	JScrollPane scroll;
	TableModel model;
	
	// list�� ����ȭ�� �������� �ʱ� ������, ����� �ӵ� ��� ����
	// Vector�� ����ȭ�� ��������(����������, ����� �ӵ��� ����ϱ� ����)
	// ����ȭ(Synchronization) : Ư�� �����尡 ������� ��� ���ÿ� �����̴� �ٸ� �����尡 ������ �� ����
	
	// Vector�� ArrayList�� �� �� ����
	// ������ : ����ȭ  ���� ����
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
		
		// DB ������ ���� �Ǿ�� ��
		init();
		
		// TableModel�� ���� �͸� Ŭ������ ����
		// ���̺� ���� JTable�� ����
		model=new AbstractTableModel() {
					
			public int getRowCount() {
				return list.size();
			}
			
			// �̹� ���̺��� �����Ǿ� ����
			// column�� ���� �����Ǿ� �ֱ� ������ ���α׷������� �����°� ���� ���� �� ����
			// ������ metaData�� ���� ���� ����
			public int getColumnCount() {
				return cols;
			}
			// �ڴٽ� �����
			public Object getValueAt(int row, int col) {
				// public JTable(Vector row, Vector columnNames)
				// vectors of Vectors
				/*
				 * ������ x[row], y[row] -> x[data], x[col]�� 
				 * */	
				
				// list vector�ȿ� data vector�� �������
				// data�� rs�κ��� �������� ��	ex) rs.getString("book_name")
				// ����, row�� list�� ��
				Vector vec=(Vector)list.get(row);	
				return vec.elementAt(col);
			}
		
		};
		
		// ���̺� model ����
		table.setModel(model);
	}
	
	// DB���� - book ���̺��� ���ڵ� ��������
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
			
			// rs�� ������ collection�� DTO�� �Ű� �����ϱ�
			while(rs.next()){
				Vector<String> data=new Vector<String>();
				/*
				 *  private int book_id;
					private int subcategory_id;
					private String book_name;
					private int price;
					private String img;
				 * */
				// rs�� ��� book�� ������ ������ ����
				// Book �ڷ������� dto ���� -> JTable�� collectionFramework�� ������ �μ��� ���� ���ϹǷ�
				// Collection Framework�� Vector���
				// Book dto=new Book();
				
				// Book�ڷ��� dto�� ��������Ƿ� rs�κ��� ���� ����
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
