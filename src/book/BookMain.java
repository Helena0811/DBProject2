/*
 * ���� ��� ���α׷� �����
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

// Connection�� ���� �� ���Ḧ Main�� ���
public class BookMain extends JFrame implements ItemListener, ActionListener{
	JPanel p_west;		// ���� ��� ��
	JPanel p_content;	// ���� ���� ��ü
	JPanel p_north;		// ���� ���� ��� ����
	JPanel p_table;		// JTable�� ������ �г�
	JPanel p_grid;		// GridLayout ������� ������ �г�
	
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
	
	// �߻�Ŭ���� �̹Ƿ� new �Ұ���, ������ �̱��� ������� �����Ǿ� ����
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
		
		t_name=new JTextField("������",10);
		t_price=new JTextField("����",10);
		
		bt_regist=new JButton("���");
		
		group=new CheckboxGroup();
		ch_table=new Checkbox("���̺� ���",true,group);
		ch_grid=new Checkbox("�׸��� ���",false,group);
		
		URL url=this.getClass().getResource("/default.jpg");
		
		// ImageIO.read(URL url)
		// url�� ����ϸ� ������ full ��� �ʿ�X, resource file �̿�
		// ���ø����̼� ������ ���Ǵ� �̹����� ����� ���� url�� ���� ��!
		
		// toolkit����ؾ� ���ø����̼� �ܺο��� �ҷ����� �̹��� ��� ����
		
		// ���� res ����(���ø����̼� ��)���� �̹����� �����ϹǷ� url ��� ���� 
		try {
			img=ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// �̹����� ������ canvas
		can=new Canvas(){
			public void paint(Graphics g) {
				// �̹��� ����
				g.drawImage(img, 0, 0, 140, 140, this);
			}
		};
		// canvas�� ũ�Ⱑ �����Ǿ�� �׸��� ��� ����
		can.setPreferredSize(new Dimension(150, 150));
		
		// choice ������Ʈ�� ũ�� �� ����
		ch_top.setPreferredSize(new Dimension(130, 45));
		ch_sub.setPreferredSize(new Dimension(130, 45));
		
		// File Chooser �ø���
		chooser=new JFileChooser("C:/Users/sist110/Pictures");
		
		// choice ������Ʈ�� ItemListener ����
		ch_top.addItemListener(this);
		
		// image�� button�� ActionListener ����
		// image, canvas ��� ActionListener�� �ȵǹǷ� MouseListener�� ���� -> �ʹ� override�� �޼ҵ尡 �����Ƿ� adapter ���
		can.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {
				//System.out.println("���콺 Ŭ���ߴ�");
				// FileOpenDialog ����
				openFile();
			}

			
		});
		
		/*
		 * DB�����ؼ� �޾ƿ;� ��, init()���� ���� ī�װ���, getSub()���� ���� ī�װ� �ҷ����� ����
		ch_top.add("�� ���� ī�װ�");
		ch_top.add("���� ����");
		ch_top.add("�ܱ� ����");
		*/
		/*
		 * sub�� ������ ���õǾ�� ���
		ch_sub.add("�Ҽ�/������");
		ch_sub.add("�ι�");
		ch_sub.add("��");
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
	
	// �ʱ�ȭ �۾�
	public void init(){
		// choice ������Ʈ�� �ֻ��� ��� �߰�
		ch_top.add("�� ���� ī�װ�");
		ch_sub.add("�� ���� ī�װ�");
		
		//manager=DBManager.getInstance();
		// �����ϰ� ����� �� �ޱ�
		con=manager.getConnection();
		
		// �����ͺ��̽����� columnName�� ���� ����
		String sql="select * from topcategory";
		
		// try-catch�� �ȿ� ����� ���ÿ� �ʱ�ȭ�� �ϸ� ������ ���� �����Ƿ� �ۿ� ����
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			// Ŀ���� �����Ӱ� ������ �ʿ�X, �迭�� ũ�⸦ ���� �����ϱ� ���ؼ��� Ŀ���� �̿��ؾ� ��
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
	
	// ���� ī�װ� ��������
	public void getSub(String value){
		// ������ �̹� �����Ǿ� �ִ� item�� �ִٸ� ����� �����ϱ�
		ch_sub.removeAll();
		
		// String sql="";
		// sql+="";
		// String ��ü 2�� ������, StringBuffer�� �������!
		StringBuffer sb=new StringBuffer();
		sb.append("select * from subcategory");
		sb.append(" where topcategory_id=(");
		sb.append(" select topcategory_id from");
		sb.append(" topcategory where category_name='"+value+"')");
		
		System.out.println(sb.toString());
		
		// try-catch�� �ȿ� ����� ���ÿ� �ʱ�ȭ�� �ϸ� ������ ���� �����Ƿ� �ۿ� ����
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
	
	// canvas�� Ŭ���ϸ� �׸� ���� �ҷ�����
	public void openFile(){
		int result=chooser.showOpenDialog(this);
		
		// dialog���� ����� �۾��� 'Ȯ��' �̶��
		if(result==JFileChooser.APPROVE_OPTION){
			// ������ �̹����� ĵ������ �׸���
			File file=chooser.getSelectedFile();
			// img=kit.getImage(url)
			img=kit.getImage(file.getAbsolutePath());
			// ĵ���� �ٽ� �׸���
			can.repaint();
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		String value=ch.getSelectedItem();
		getSub(value);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("�� ������");
		//e.getSource();
		
	}
	
	public static void main(String[] args) {
		new BookMain();

	}




}
