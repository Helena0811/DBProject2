/*
 * GridLayout이 적용될 패널
 * DB연동한 후 읽어온 데이터를 각 레코드의 정보를 출력하는 BookItem의 생성자 인수로 넣어 구현
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
	
	// 생성과 동시에 connection을 받아올 수 없음, BookMain에서 생성되기 이전에 불러오기 때문에 null값
	// 따라서 setter 이용
	public void setConnection(Connection con) {
		this.con = con;
		
		// 연결 후 DB 연동
		loadData();
	}
	
	// SetConnection 이후에 DB 연동 가능
	public void loadData(){
		String sql="select * from book order by book_id asc";
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		list.removeAll(list);
	
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();	// 쿼리 실행
			
			while(rs.next()){
				// 레코드 1건을 담기 위한 인스턴스
				Book dto=new Book();
				
				dto.setBook_id(rs.getInt("book_id"));
				dto.setBook_name(rs.getString("book_name"));
				dto.setPrice(rs.getInt("price"));
				dto.setSubcategory_id(rs.getInt("subcategory_id"));
				dto.setImg(rs.getString("img"));
				
				list.add(dto);	// 한 레코드에 대한 정보를 list에 담기
			}
			
			// 데이터베이스에 대한 정보를 모두 가져왔으므로 디자인 반영
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
			BookItem item=new BookItem(img, "아무거나", "10000");
			add(item);
		}
		*/
		
		// Grid Table도 등록하면 갱신
		/*
		 * 기존에 존재하는 객체를 지우고 다시 만들어야 함!
		 * */
		for(int j=0; j<bookList.size(); j++){
			remove(bookList.get(j));
		}
		
		// list에 들어있는 객체 만큼 BookItem을 생성해서 화면에 보여주기
		for(int i=0; i<list.size(); i++){
			
			Book book=list.get(i);
			try {
				// 이미지 경로 받기
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
