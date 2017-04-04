/*
 * 도서 목록 프로그램 만들기
 * */
package book;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Connection의 연결 및 종료를 Main이 담당
public class BookMain extends JFrame implements ItemListener, ActionListener{
	JPanel p_west;		// 좌측 등록 폼
	JPanel p_content;	// 우측 영역 전체
	JPanel p_north;		// 우측 선택 모드 영역
	JPanel p_center;	// FlowLayout이 적용되어 p_table, p_grid를 모두 존재할 수 있게 설정
	JPanel p_table;		// JTable이 부착될 패널(BorderLayout)
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
	
	File file;
	JFileChooser chooser;
	
	FileInputStream fis;
	FileOutputStream fos;
	
	// subcategory_id는 현재 한글만 들어있고 id값이 없기 때문에(html option과는 다름)
	// choice 컴포넌트의 값을 미리 저장해놓아야 쓸 수 있음
	// DB연동에 의존적이므로 현재 new를 이용해서 생성할 수 없음
	// 상위 choice 컴포넌트가 선택되는 때가 생성 시점
	// String[][] subcategory;
	
	// java와 DB가 현실을 바라보는 관점은 비슷하므로 
	// 레코드 하나하나를 하나의 인스턴스로 간주
	// 따라서 SubCategory 각 객체를 담을 수 있는 arrayList로 저장
	// 이 collection은 rs 객체를 대체할 수 있음
	// -> 더 이상 rs.last, rs.getRow등 귀찮은 작업 필요 X, 2차원 배열보다 좋음!!!
	ArrayList<SubCategory> subcategory=new ArrayList<SubCategory>();
	
	
	
	public BookMain() {
		p_west=new JPanel();
		p_content=new JPanel();
		p_north=new JPanel();
		p_center=new JPanel();
		
		// 이 시점에서는 con이 생성된 적이 없기 때문에 null이 넘어감
		// 따라서 생성자의 인수로 넘겨받을 수 없으니 메소드로 넘겨주자!
		//p_table=new TablePanel(con);
		p_table=new TablePanel();
		p_grid=new GridPanel();
		
		ch_top=new Choice();
		ch_sub=new Choice();
		
		t_name=new JTextField(10);
		t_price=new JTextField(10);
		
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
		chooser=new JFileChooser("C:/Users/sist110/Pictures/images");
		
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
		
		// 버튼과 ActionListener 연결
		bt_regist.addActionListener(this);
		
		// 테이블/grid choice와 ItemListener 연결
		ch_table.addItemListener(this);
		ch_grid.addItemListener(this);
		
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
		
		p_center.add(p_table);
		p_center.add(p_grid);
		p_center.setBackground(Color.GREEN);
		
		p_content.setLayout(new BorderLayout());
		p_content.add(p_center);
		p_content.add(p_north, BorderLayout.NORTH);
		
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
		String sql="select * from topcategory order by topcategory_id asc";
		
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
		
		// JTable 패널과 Grid 패널에게 connection 전달
		// p_table은 JPanel로 선언되어 있으므로 자식인 TablePanel로 형변환
		((TablePanel)p_table).setConnection(con);
		((GridPanel)p_grid).setConnection(con);
	

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
		sb.append(" topcategory where category_name='"+value+"') order by subcategory_id asc");
		
		//System.out.println(sb.toString());
		
		// try-catch문 안에 선언과 동시에 초기화를 하면 선언이 되지 않으므로 밖에 빼기
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sb.toString());
			rs=pstmt.executeQuery();
			
			// choice 컴포넌트에 add해서 출력
			/*
			while(rs.next()){
				ch_sub.add(rs.getString("category_name"));
			}
			*/		
			// subcategory의 정보를 2차원 배열에 저장 + 출력
			// subcategory -> 레코드 수 x 컬럼 수
			// subcategory=new String[][];
			// 하지만 2차원배열보다 SubCategory 클래스를 담는 collection Framework를 사용하면 더 편함!
			
			//ch_sub.add("▼ 하위 카테고리");
			
			// rs에 담겨진 레코드 1개는 SubCategory 클래스의 인스턴스 1개로 받을 수 있음			
			while(rs.next()){
				// 각 인스턴스에 해당하는 SubCategory 클래스 생성
				SubCategory dto=new SubCategory();
				
				// SubCategory 인스턴스에 subcategory_id 정보 담기
				dto.setSubcategory_id(rs.getInt("subcategory_id"));
				// SubCategory 인스턴스에 category_name 정보 담기
				dto.setCategory_name(rs.getString("category_name"));
				// SubCategory 인스턴스에 topcategory_id 정보 담기
				dto.setTopcategory_id(rs.getInt("topcategory_id"));
				
				subcategory.add(dto);	// collection에 저장
				// subcategory choice에 붙이고 출력
				
				ch_sub.add(dto.getCategory_name());
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
	
	// 상품 등록 메소드
	public void regist(){
		// 현재 선택한 subcategory choice의 index를 구하고, 
		// 그 index로 ArrayList를 접근하여 객체를 반환받으면 정보를 유용하게 사용 가능
		int index=ch_sub.getSelectedIndex();
		SubCategory dto=subcategory.get(index);
		
		// 책 이름 입력한 대로 가져오기
		String book_name=t_name.getText();
		// 가격 입력한 대로 가져오기
		int price=Integer.parseInt(t_price.getText());	// 어차피 string으로 들어가기는 하지만 자료형을 명시해서 혼란을 없애기!
		// 이미지 경로 올린 대로 가져오기 + file에서 경로를 받아와야 하므로 File file 멤버변수로 선언
		String img=file.getName();	// 지금 file을 선택하지 않으면 에러 발생, file.getName()으로 얻어오는 값이 없기 때문에 에러!
		
		StringBuffer sb=new StringBuffer();
		
		sb.append("insert into book(book_id, subcategory_id, book_name, price, img)");
		// subcategory_id는 현재 한글만 들어있고 id값이 없기 때문에(html option과는 다름)
		// choice 컴포넌트의 값을 미리 저장해놓아야 쓸 수 있음
	
		sb.append(" values(seq_book.nextval,"+dto.getSubcategory_id()+",'"+book_name+"',"+price+",'"+img+"')");
		System.out.println(sb.toString());
		// oracle에 이미지 정보가 자체적으로 들어갈 수 있나?
		// Yes!(이미지를 해석해놓은 byte자료 - Blob)
		// 하지만 우리는 용량을 확보하기 위해 이미지 파일의 이름만 넣을 예정
		
		// 쿼리 수행
		PreparedStatement pstmt=null;
		try {
			pstmt=con.prepareStatement(sb.toString());
			// select문이 아니므로 rs는 필요없음
			// sql문이 DML(insert, delete, update)인 경우 executeUpdate()
			int result=pstmt.executeUpdate();
			
			// executeUpdate()는 숫자값을 반환하고, 이 숫자값은 해당 쿼리에 의해 영향을 받는 레코드 수를 반환함
			// insert인 경우 항상 1을 반환(insert를 통해 오직 1건만 넣을 수 있음)
			if(result!=0){
				copy();		
				
				// 등록을 완료하면 JTable 갱신
				// 테이블에 model 적용
				((TablePanel)p_table).init();				// 조회
				((TablePanel)p_table).table.updateUI();		// UI 갱신
				
				((GridPanel)p_grid).loadData();
				
			}
			else{
				JOptionPane.showMessageDialog(this, "등록 실패");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
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
			file=chooser.getSelectedFile();
			// img=kit.getImage(url)
			img=kit.getImage(file.getAbsolutePath());
			// 캔버스 다시 그리기
			can.repaint();
		}
	}
	/*
	 * 이미지 복사하기
	 * 유저가 선택한 이미지를, 개발자가 지정한 위치로 복사! -> 현재 프로젝트의 data폴더
	 * */
	public void copy(){
		// 유저가 선택한 이미지 불러오기
		try {
			fis=new FileInputStream(file.getAbsolutePath());
			// fis=new FileInputStream(file);
			// String dest="C:/java_workspace2/DBProject2/data/"+file.getName()";
			//fos=new FileOutputStream("C:/java_workspace2/DBProject2/data/"+file.getName());
			fos=new FileOutputStream("data/"+file.getName());
			
			int data;					// 데이터가 있는지 없는지만 판단(얼마나 읽어들였는지 데이터의 갯수)
			byte[] b=new byte[1024];	// 읽어들인 데이터는 여기에 저장됨!!!
			
			while(true){
				// public int read(byte[] b)
				// b의 크기만큼 읽어들임, return값은 읽어들인 갯수 & 더 이상 읽어들일 data가 없으면 -1 반환
				data=fis.read(b);
				// 만약 읽은 파일에 데이터가 없다면
				if(data==-1){
					break;
				}
				// if문을 만나지 않을 때 
				// public void write(byte[] b)
				fos.write(b);
				fos.flush();
			}
			JOptionPane.showMessageDialog(this, " 등록 성공");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	
	}
	
	// choice와 checkbox 이벤트 구현
	public void itemStateChanged(ItemEvent e) {
		Object obj=e.getSource();
		
		// choice
		if(obj==ch_top){
			Choice ch=(Choice)e.getSource();
			String value=ch.getSelectedItem();
			getSub(value);
		}
		
		// checkbox
		else if(obj==ch_table){
			p_table.setVisible(true);
			p_grid.setVisible(false);
		}
		else if(obj==ch_grid){
			p_table.setVisible(false);
			p_grid.setVisible(true);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("나 눌렀닝");
		regist();
		
	}
	
	public static void main(String[] args) {
		new BookMain();

	}
}
