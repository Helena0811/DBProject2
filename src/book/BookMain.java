/*
 * 도서 목록 프로그램 만들기
 * */
package book;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Connection의 연결 및 종료를 Main이 담당
public class BookMain extends JFrame implements ItemListener, ActionListener{
	JPanel p_west;		// 좌측 등록 폼
	JPanel p_content;	// 우측 영역 전체
	JPanel p_north;		// 우측 선택 모드 영역
	JPanel p_table;		// JTable이 부착될 패널
	JPanel p_grid;		// GridLayout 방식으로 보여질 패널
	
	Choice ch_top;
	Choice ch_sub;
	
	JTextField t_name;
	JTextField t_price;
	
	Canvas can;
	
	JButton bt_regist;
	
	CheckboxGroup group;
	Checkbox ch_table,ch_grid;
	
	DBManager manager=DBManager.getInstance();
	Connection con;
	//PreparedStatement pstmt;
	//ResultSet rs;
	
	// 추상클래스 이므로 new 불가능, 일종의 싱글톤 방식으로 구현되어 있음
	Toolkit kit=Toolkit.getDefaultToolkit();
	Image img;
	
	JFileChooser chooser;
	
	public BookMain() {
		p_west=new JPanel();
		p_content=new JPanel();
		p_north=new JPanel();
		p_table=new JPanel();
		p_grid=new JPanel();
		
		ch_top=new Choice();
		ch_sub=new Choice();
		
		t_name=new JTextField("도서명",10);
		t_price=new JTextField("가격",10);
		
		bt_regist=new JButton("등록");
		
		group=new CheckboxGroup();
		ch_table=new Checkbox("테이블 목록",true,group);
		ch_grid=new Checkbox("그리드 목록",false,group);
		
		URL url=this.getClass().getResource("/default.jpg");
		
		// ImageIO.read(URL url)
		// url을 사용하면 파일의 full 경로 필요X, resource file 이용
		// 어플리케이션 내에서 사용되는 이미지를 등록할 때는 url이 사용될 것!
		
		// toolkit사용해야 어플리케이션 외부에서 불러오는 이미지 등록 가능
		
		// 현재 res 폴더(어플리케이션 내)내에 이미지가 존재하므로 url 사용 가능 
		try {
			img=ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 이미지를 부착할 canvas
		can=new Canvas(){
			public void paint(Graphics g) {
				// 이미지 부착
				g.drawImage(img, 0, 0, 140, 140, this);
			}
		};
		// canvas의 크기가 지정되어야 그림도 출력 가능
		can.setPreferredSize(new Dimension(150, 150));
		
		// choice 컴포넌트의 크기 폭 조정
		ch_top.setPreferredSize(new Dimension(130, 45));
		ch_sub.setPreferredSize(new Dimension(130, 45));
		
		// File Chooser 올리기
		chooser=new JFileChooser("C:/Users/sist110/Pictures");
		
		// choice 컴포넌트와 ItemListener 연결
		ch_top.addItemListener(this);
		
		// image와 button에 ActionListener 연결
		// image, canvas 모두 ActionListener가 안되므로 MouseListener로 연결 -> 너무 override할 메소드가 많으므로 adapter 사용
		can.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				//System.out.println("마우스 클릭했닝");
				// FileOpenDialog 출현
				openFile();
			}

			
		});
		
		/*
		 * DB연동해서 받아와야 함, init()에서 상위 카테고리를, getSub()에서 하위 카테고리 불러오기 참고
		ch_top.add("▼ 상위 카테고리");
		ch_top.add("국내 도서");
		ch_top.add("외국 도서");
		*/
		/*
		 * sub는 상위가 선택되어야 출력
		ch_sub.add("소설/에세이");
		ch_sub.add("인문");
		ch_sub.add("시");
		*/
		
		p_west.setPreferredSize(new Dimension(150, 600));
		p_west.add(ch_top);
		p_west.add(ch_sub);
		p_west.add(t_name);
		p_west.add(t_price);
		p_west.add(can);
		p_west.add(bt_regist);
		
		add(p_west,BorderLayout.WEST);

		p_north.add(ch_table);
		p_north.add(ch_grid);
		
		p_content.setLayout(new BorderLayout());
		p_content.add(p_north, BorderLayout.NORTH);
		p_content.add(p_table);
		
		add(p_content);
		
		init();
		
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	// 초기화 작업
	public void init(){
		// choice 컴포넌트에 최상위 목록 추가
		ch_top.add("▼ 상위 카테고리");
		ch_sub.add("▼ 하위 카테고리");
		
		//manager=DBManager.getInstance();
		// 연결하고 연결된 값 받기
		con=manager.getConnection();
		
		// 데이터베이스에서 columnName을 구해 저장
		String sql="select * from topcategory";
		
		// try-catch문 안에 선언과 동시에 초기화를 하면 선언이 되지 않으므로 밖에 빼기
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			// 커서를 자유롭게 움직일 필요X, 배열의 크기를 먼저 지정하기 위해서는 커서를 이용해야 함
			while(rs.next()){
				ch_top.add(rs.getString("category_name"));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	
	// 하위 카테고리 가져오기
	public void getSub(String value){
		// 기존에 이미 생성되어 있는 item이 있다면 지우고 시작하기
		ch_sub.removeAll();
		
		// String sql="";
		// sql+="";
		// String 객체 2개 생성됨, StringBuffer로 사용하자!
		StringBuffer sb=new StringBuffer();
		sb.append("select * from subcategory");
		sb.append(" where topcategory_id=(");
		sb.append(" select topcategory_id from");
		sb.append(" topcategory where category_name='"+value+"')");
		
		System.out.println(sb.toString());
		
		// try-catch문 안에 선언과 동시에 초기화를 하면 선언이 되지 않으므로 밖에 빼기
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sb.toString());
			rs=pstmt.executeQuery();

			while(rs.next()){
				ch_sub.add(rs.getString("category_name"));
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
	
	// canvas를 클릭하면 그림 파일 불러오기
	public void openFile(){
		int result=chooser.showOpenDialog(this);
		
		// dialog에서 실행된 작업이 '확인' 이라면
		if(result==JFileChooser.APPROVE_OPTION){
			// 선택한 이미지를 캔버스에 그리기
			File file=chooser.getSelectedFile();
			// img=kit.getImage(url)
			img=kit.getImage(file.getAbsolutePath());
			// 캔버스 다시 그리기
			can.repaint();
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		String value=ch.getSelectedItem();
		getSub(value);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("나 눌렀닝");
		//e.getSource();
		
	}
	
	public static void main(String[] args) {
		new BookMain();

	}




}
