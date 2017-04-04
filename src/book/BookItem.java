/*
 * 책 1권을 표현하는 UI 컴포넌트(Customizing)
 * */
package book;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class BookItem extends JPanel{
	Image img;
	String name;
	String price;
	
	Canvas can;
	JLabel la_name, la_price;
	
	public BookItem(Image img, String name, String price) {
		// 이미지는 결정되어 있지 않음, DB로부터 연동받아와서 결정되어야 함
		this.img=img;
		this.name=name;
		this.price=price;
		
		can=new Canvas(){
			public void paint(Graphics g) {
				g.drawImage(img, 0, 0, 110, 120, this);
			}
			
		};
		can.setPreferredSize(new Dimension(110, 120));
		
		la_name=new JLabel(name);
		la_price=new JLabel(price);
		
		add(can);
		add(la_name);
		add(la_price);
		
		setPreferredSize(new Dimension(120, 180));
		setBackground(Color.GRAY);
	}
}
