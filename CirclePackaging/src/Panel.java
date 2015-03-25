import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


public class Panel extends JPanel implements ActionListener {
	
	private Configuration configuration;
	private int frameSize = 960;
	private Timer tm = new Timer(5,this);
	
	public Panel() {
	}
	
	public void setConfiguration(Configuration config) {
		this.configuration = config;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		int r = (int) Math.round(configuration.getOuterCircle().getRadius());
		drawCenteredCircle(g, frameSize/2 ,frameSize/2, r);	
		//System.out.println(r);
		for (Circle circle : configuration.getInnerCircles()) {
			g.setColor(Color.RED);
			int radius = (int) Math.round(circle.getRadius());
			int x = (int) Math.round(frameSize/2 + circle.getX());
			int y = (int) Math.round(frameSize/2 + circle.getY());
			//System.out.println(radius + " " + x + " " + y);
			drawCenteredCircle(g, x ,y, radius);		
		}
		tm.start();
	}
	
	public void drawCenteredCircle(Graphics g, int x, int y, int r) {
		x = x-(r/2);
		y = y-(r/2);
		g.drawOval(x,y,r,r);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		configuration.getOuterCircle().setRadius(configuration.getOuterCircle().getRadius() + 0.1);
		repaint();
	}
	
	public static void main(String[] args) {
		Circle outerCircle = new Circle(100, 0, 0);
		List<Circle> innerCircles = new ArrayList<Circle>();
		innerCircles.add(new Circle(50,0,0));
		innerCircles.add(new Circle(20, 10, 10));
		Configuration configuration = new Configuration(outerCircle, innerCircles);
		Panel p = new Panel();
		p.setConfiguration(configuration);
		JFrame jf = new JFrame();
		jf.setTitle("Circle Packing Problem");
		jf.setSize(960,960);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.add(p);
	}

}
