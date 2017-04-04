/*
 * GridLayout�� ����� �г�
 * DB������ �� �о�� �����͸� �� ���ڵ��� ������ ����ϴ� BookItem�� ������ �μ��� �־� ����
 * */
package book;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GridPanel extends JPanel{
	private Connection con;
	
	String path="C:/java_workspace2/DBProject2/data/";
	
	// DTO
	ArrayList<Book> list=new ArrayList<Book>();
	
	ArrayList<BookItem> bookList=new ArrayList<BookItem>();
	
	public GridPanel() {
		
		
		this.setVisible(false);
		this.setBackground(Color.cyan);
		setPreferredSize(new Dimension(650, 550));
	}
	
	// ������ ���ÿ� connection�� �޾ƿ� �� ����, BookMain���� �����Ǳ� ������ �ҷ����� ������ null��
	// ���� setter �̿�
	public void setConnection(Connection con) {
		this.con = con;
		
		// ���� �� DB ����
		loadData();
	}
	
	// SetConnection ���Ŀ� DB ���� ����
	public void loadData(){
		String sql="select * from book order by book_id asc";
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		list.removeAll(list);
	
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();	// ���� ����
			
			while(rs.next()){
				// ���ڵ� 1���� ��� ���� �ν��Ͻ�
				Book dto=new Book();
				
				dto.setBook_id(rs.getInt("book_id"));
				dto.setBook_name(rs.getString("book_name"));
				dto.setPrice(rs.getInt("price"));
				dto.setSubcategory_id(rs.getInt("subcategory_id"));
				dto.setImg(rs.getString("img"));
				
				list.add(dto);	// �� ���ڵ忡 ���� ������ list�� ���
			}
			
			// �����ͺ��̽��� ���� ������ ��� ���������Ƿ� ������ �ݿ�
			init();
			
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
	
	public void init(){
		/*
		Image img=null;
		
		try {
			img = ImageIO.read(new File(path+"cat1.jpg"));
		
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i=0; i<10; i++){
			BookItem item=new BookItem(img, "�ƹ��ų�", "10000");
			add(item);
		}
		*/
		
		// Grid Table�� ����ϸ� ����
		/*
		 * ������ �����ϴ� ��ü�� ����� �ٽ� ������ ��!
		 * */
		for(int j=0; j<bookList.size(); j++){
			remove(bookList.get(j));
		}
		
		// list�� ����ִ� ��ü ��ŭ BookItem�� �����ؼ� ȭ�鿡 �����ֱ�
		for(int i=0; i<list.size(); i++){
			
			Book book=list.get(i);
			try {
				// �̹��� ��� �ޱ�
				// ImageIO.read(File input)
				Image img=ImageIO.read(new File(path+book.getImg()));
				String name=book.getBook_name();
				String price=Integer.toString(book.getPrice());
				
				BookItem item=new BookItem(img, name, price);
				
				bookList.add(item);
				
				add(item);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
