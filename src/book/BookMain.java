/*
 * ���� ��� ���α׷� �����
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

// Connection�� ���� �� ���Ḧ Main�� ���
public class BookMain extends JFrame implements ItemListener, ActionListener{
	JPanel p_west;		// ���� ��� ��
	JPanel p_content;	// ���� ���� ��ü
	JPanel p_north;		// ���� ���� ��� ����
	JPanel p_center;	// FlowLayout�� ����Ǿ� p_table, p_grid�� ��� ������ �� �ְ� ����
	JPanel p_table;		// JTable�� ������ �г�(BorderLayout)
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
	
	File file;
	JFileChooser chooser;
	
	FileInputStream fis;
	FileOutputStream fos;
	
	// subcategory_id�� ���� �ѱ۸� ����ְ� id���� ���� ������(html option���� �ٸ�)
	// choice ������Ʈ�� ���� �̸� �����س��ƾ� �� �� ����
	// DB������ �������̹Ƿ� ���� new�� �̿��ؼ� ������ �� ����
	// ���� choice ������Ʈ�� ���õǴ� ���� ���� ����
	// String[][] subcategory;
	
	// java�� DB�� ������ �ٶ󺸴� ������ ����ϹǷ� 
	// ���ڵ� �ϳ��ϳ��� �ϳ��� �ν��Ͻ��� ����
	// ���� SubCategory �� ��ü�� ���� �� �ִ� arrayList�� ����
	// �� collection�� rs ��ü�� ��ü�� �� ����
	// -> �� �̻� rs.last, rs.getRow�� ������ �۾� �ʿ� X, 2���� �迭���� ����!!!
	ArrayList<SubCategory> subcategory=new ArrayList<SubCategory>();
	
	
	
	public BookMain() {
		p_west=new JPanel();
		p_content=new JPanel();
		p_north=new JPanel();
		p_center=new JPanel();
		
		// �� ���������� con�� ������ ���� ���� ������ null�� �Ѿ
		// ���� �������� �μ��� �Ѱܹ��� �� ������ �޼ҵ�� �Ѱ�����!
		//p_table=new TablePanel(con);
		p_table=new TablePanel();
		p_grid=new GridPanel();
		
		ch_top=new Choice();
		ch_sub=new Choice();
		
		t_name=new JTextField(10);
		t_price=new JTextField(10);
		
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
		chooser=new JFileChooser("C:/Users/sist110/Pictures/images");
		
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
		
		// ��ư�� ActionListener ����
		bt_regist.addActionListener(this);
		
		// ���̺�/grid choice�� ItemListener ����
		ch_table.addItemListener(this);
		ch_grid.addItemListener(this);
		
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
	
	// �ʱ�ȭ �۾�
	public void init(){
		// choice ������Ʈ�� �ֻ��� ��� �߰�
		ch_top.add("�� ���� ī�װ�");
		ch_sub.add("�� ���� ī�װ�");
		
		//manager=DBManager.getInstance();
		// �����ϰ� ����� �� �ޱ�
		con=manager.getConnection();
		
		// �����ͺ��̽����� columnName�� ���� ����
		String sql="select * from topcategory order by topcategory_id asc";
		
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
		
		// JTable �гΰ� Grid �гο��� connection ����
		// p_table�� JPanel�� ����Ǿ� �����Ƿ� �ڽ��� TablePanel�� ����ȯ
		((TablePanel)p_table).setConnection(con);
		((GridPanel)p_grid).setConnection(con);
	

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
		sb.append(" topcategory where category_name='"+value+"') order by subcategory_id asc");
		
		//System.out.println(sb.toString());
		
		// try-catch�� �ȿ� ����� ���ÿ� �ʱ�ȭ�� �ϸ� ������ ���� �����Ƿ� �ۿ� ����
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sb.toString());
			rs=pstmt.executeQuery();
			
			// choice ������Ʈ�� add�ؼ� ���
			/*
			while(rs.next()){
				ch_sub.add(rs.getString("category_name"));
			}
			*/		
			// subcategory�� ������ 2���� �迭�� ���� + ���
			// subcategory -> ���ڵ� �� x �÷� ��
			// subcategory=new String[][];
			// ������ 2�����迭���� SubCategory Ŭ������ ��� collection Framework�� ����ϸ� �� ����!
			
			//ch_sub.add("�� ���� ī�װ�");
			
			// rs�� ����� ���ڵ� 1���� SubCategory Ŭ������ �ν��Ͻ� 1���� ���� �� ����			
			while(rs.next()){
				// �� �ν��Ͻ��� �ش��ϴ� SubCategory Ŭ���� ����
				SubCategory dto=new SubCategory();
				
				// SubCategory �ν��Ͻ��� subcategory_id ���� ���
				dto.setSubcategory_id(rs.getInt("subcategory_id"));
				// SubCategory �ν��Ͻ��� category_name ���� ���
				dto.setCategory_name(rs.getString("category_name"));
				// SubCategory �ν��Ͻ��� topcategory_id ���� ���
				dto.setTopcategory_id(rs.getInt("topcategory_id"));
				
				subcategory.add(dto);	// collection�� ����
				// subcategory choice�� ���̰� ���
				
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
	
	// ��ǰ ��� �޼ҵ�
	public void regist(){
		// ���� ������ subcategory choice�� index�� ���ϰ�, 
		// �� index�� ArrayList�� �����Ͽ� ��ü�� ��ȯ������ ������ �����ϰ� ��� ����
		int index=ch_sub.getSelectedIndex();
		SubCategory dto=subcategory.get(index);
		
		// å �̸� �Է��� ��� ��������
		String book_name=t_name.getText();
		// ���� �Է��� ��� ��������
		int price=Integer.parseInt(t_price.getText());	// ������ string���� ����� ������ �ڷ����� ����ؼ� ȥ���� ���ֱ�!
		// �̹��� ��� �ø� ��� �������� + file���� ��θ� �޾ƿ;� �ϹǷ� File file ��������� ����
		String img=file.getName();	// ���� file�� �������� ������ ���� �߻�, file.getName()���� ������ ���� ���� ������ ����!
		
		StringBuffer sb=new StringBuffer();
		
		sb.append("insert into book(book_id, subcategory_id, book_name, price, img)");
		// subcategory_id�� ���� �ѱ۸� ����ְ� id���� ���� ������(html option���� �ٸ�)
		// choice ������Ʈ�� ���� �̸� �����س��ƾ� �� �� ����
	
		sb.append(" values(seq_book.nextval,"+dto.getSubcategory_id()+",'"+book_name+"',"+price+",'"+img+"')");
		System.out.println(sb.toString());
		// oracle�� �̹��� ������ ��ü������ �� �� �ֳ�?
		// Yes!(�̹����� �ؼ��س��� byte�ڷ� - Blob)
		// ������ �츮�� �뷮�� Ȯ���ϱ� ���� �̹��� ������ �̸��� ���� ����
		
		// ���� ����
		PreparedStatement pstmt=null;
		try {
			pstmt=con.prepareStatement(sb.toString());
			// select���� �ƴϹǷ� rs�� �ʿ����
			// sql���� DML(insert, delete, update)�� ��� executeUpdate()
			int result=pstmt.executeUpdate();
			
			// executeUpdate()�� ���ڰ��� ��ȯ�ϰ�, �� ���ڰ��� �ش� ������ ���� ������ �޴� ���ڵ� ���� ��ȯ��
			// insert�� ��� �׻� 1�� ��ȯ(insert�� ���� ���� 1�Ǹ� ���� �� ����)
			if(result!=0){
				copy();		
				
				// ����� �Ϸ��ϸ� JTable ����
				// ���̺� model ����
				((TablePanel)p_table).init();				// ��ȸ
				((TablePanel)p_table).table.updateUI();		// UI ����
				
				((GridPanel)p_grid).loadData();
				
			}
			else{
				JOptionPane.showMessageDialog(this, "��� ����");
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
	
	// canvas�� Ŭ���ϸ� �׸� ���� �ҷ�����
	public void openFile(){
		int result=chooser.showOpenDialog(this);
		
		// dialog���� ����� �۾��� 'Ȯ��' �̶��
		if(result==JFileChooser.APPROVE_OPTION){
			// ������ �̹����� ĵ������ �׸���
			file=chooser.getSelectedFile();
			// img=kit.getImage(url)
			img=kit.getImage(file.getAbsolutePath());
			// ĵ���� �ٽ� �׸���
			can.repaint();
		}
	}
	/*
	 * �̹��� �����ϱ�
	 * ������ ������ �̹�����, �����ڰ� ������ ��ġ�� ����! -> ���� ������Ʈ�� data����
	 * */
	public void copy(){
		// ������ ������ �̹��� �ҷ�����
		try {
			fis=new FileInputStream(file.getAbsolutePath());
			// fis=new FileInputStream(file);
			// String dest="C:/java_workspace2/DBProject2/data/"+file.getName()";
			//fos=new FileOutputStream("C:/java_workspace2/DBProject2/data/"+file.getName());
			fos=new FileOutputStream("data/"+file.getName());
			
			int data;					// �����Ͱ� �ִ��� �������� �Ǵ�(�󸶳� �о�鿴���� �������� ����)
			byte[] b=new byte[1024];	// �о���� �����ʹ� ���⿡ �����!!!
			
			while(true){
				// public int read(byte[] b)
				// b�� ũ�⸸ŭ �о����, return���� �о���� ���� & �� �̻� �о���� data�� ������ -1 ��ȯ
				data=fis.read(b);
				// ���� ���� ���Ͽ� �����Ͱ� ���ٸ�
				if(data==-1){
					break;
				}
				// if���� ������ ���� �� 
				// public void write(byte[] b)
				fos.write(b);
				fos.flush();
			}
			JOptionPane.showMessageDialog(this, " ��� ����");
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
	
	// choice�� checkbox �̺�Ʈ ����
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
		System.out.println("�� ������");
		regist();
		
	}
	
	public static void main(String[] args) {
		new BookMain();

	}
}
