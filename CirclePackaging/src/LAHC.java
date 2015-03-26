import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;


public class LAHC {
	
	private double moveAmount = 0.2;

	/*
	 * Produce an initial solution s
	 * Calculate initial cost function C(s)
	 * Let the initial number of steps I = 0
	 * Fora all k in ( 0 .. L-1 ) Ck = C(s)
	 * Do until a stopping condition
	 * 		Construct a candidate solution s*
	 * 		Calculate its cost function C(s*)
	 * 		v = I mod L
	 * 		If C(s*) <= Cv
	 * 		Then accept s*
	 * 		Insert cost value into the list Cv = C(s)
	 * 		Increment a number of steps I = I+1
	 * End do
	 */
	public void doLAHC(int costArrayLength) {
		CostFunction costFunction = new CostFunction();
		Configuration configuration = null; 
		List<Double> radii = new Reader().readRadii("/home/katrijne/git/CirclePackaging/CirclePackaging/src/testInstances/NR10_1-10.txt");//"C:\\Bestanden\\School\\Capita Selecta\\NR10_1-10.txt");
		configuration = createInitialConfig( radii );
		
		Panel panel = createPanel();
		panel.setConfiguration(configuration);
		
		double initialCost = costFunction.calculateCostFunction(configuration);
		double[] costArray = new double[costArrayLength];
		for(int i = 0; i < costArrayLength; i++){
			costArray[i] = initialCost;
		}
		int steps = 0;
		while(true) { 
			Configuration candidate = constructCandidate(new Configuration(configuration));
			double candidateCost = costFunction.calculateCostFunction(candidate);
			int v = steps % costArrayLength;
			if( candidateCost <= costArray[v] ) {
				configuration = candidate;
				panel.setConfiguration(configuration);
				System.out.println(configuration);
			}
			if ( candidateCost == 0 )
				break;
			
			costArray[v] = candidateCost; //Ik denk dat dit hier moet maar ben niet zeker of het niet in de if moet.
			steps++;
		}
	}
	
	public Panel createPanel() {
		Panel p = new Panel();
		JFrame jf = new JFrame();
		jf.setTitle("Circle Packing Problem");
		jf.setSize(960,960);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.add(p);
		return p;
	}
	
	public Configuration createInitialConfig(List<Double> radii)
	{
		Circle outerCircle = new Circle(1,0,0);
		Configuration config = new Configuration(outerCircle);
		
		for ( double radius : radii )
		{
			double x = 0;
			double y = 0;
			do {
				x = Math.random()*2 - 1;
				y = Math.random()*2 - 1;
			} while (Math.sqrt( Math.pow(x, 2)+Math.pow(y, 2)) > outerCircle.getRadius());
			Circle circle = new Circle(radius,x,y);
			config.addInner(circle);
		}
		
		return config;
	}
	
	public Configuration constructCandidate(Configuration config)
	{
		List<Circle> circles = config.getInnerCircles();

		int indexCircle = (int) Math.round(Math.random()*(circles.size()-1));
		Circle circle = circles.get(indexCircle);
		List<Circle> copyCircles = new ArrayList<Circle>(circles);
		copyCircles.remove(indexCircle);
		
		double moveX = 0;
		double moveY = 0;
		
		int index = selectMove(circle, copyCircles);
		
		if ( index == -1 ) {
			return constructCandidate(config);
		}
		else {
			Circle otherCircle = null;
			if (index == 0 ) {
				otherCircle = new Circle(1,0,0);
			}
			else {
				index = index - 1;
				otherCircle = copyCircles.get(index);
			}			
			
			double changeY;
			double changeX;
			double tan;
			double angle;
			
			changeY = circle.getY() - otherCircle.getY();
			changeX = circle.getX() - otherCircle.getX();
			tan = changeY/changeX; //TODO: what if changeX == 0??
			angle = Math.atan(tan);
			
			double localMoveAmount = moveAmount;
			double dist = moveAmount;
			if(index == 0) {
				dist = calculateDistanceCenters(circle, otherCircle) + circle.getRadius() - otherCircle.getRadius();
			}
			else {
				dist = circle.getRadius() + otherCircle.getRadius() - calculateDistanceCenters(circle, otherCircle);
			}
			if(dist < moveAmount ) {
				localMoveAmount = dist;
			}
			
			moveX = localMoveAmount * Math.cos(angle);
			moveY = localMoveAmount * Math.sin(angle);
			
			// move fixed amount if overlap is bigger than the fixed amount
			if ( circle.getX() < otherCircle.getX() )
			{
				if(index == 0) {
					circle.setX(circle.getX() + moveX);
					circle.setY(circle.getY() + moveY);
				}
				else {
					circle.setX(circle.getX() - moveX);
					circle.setY(circle.getY() - moveY);
				}
			}
			else
			{
				if(index == 0) {
					circle.setX(circle.getX() - moveX);
					circle.setY(circle.getY() - moveY);
				}
				else {
					circle.setX(circle.getX() + moveX);
					circle.setY(circle.getY() + moveY);
				}
				
			}
			
		}
		
//		System.out.println("Move amount x: " + moveX);
//		System.out.println("Move amount Y: " + moveY + "\n");

		copyCircles.add(circle);
		Configuration newConfig = new Configuration(config.getOuterCircle(), copyCircles);
		return newConfig;
	}
	
	public int selectMove(Circle testCircle, List<Circle> restCircles)
	{
		double cost = 0;
		Map<Integer,Double> indices = new HashMap<Integer,Double>();
		
		for ( int i = 0; i < restCircles.size(); i++ )
		{
			cost = restCircles.get(i).getRadius() + testCircle.getRadius() 
					- Math.sqrt(Math.pow((testCircle.getX() - restCircles.get(i).getX()), 2) 
								+ Math.pow((testCircle.getY() - restCircles.get(i).getY()), 2));
			if ( cost > 0 )
				indices.put(i+1, cost);
		}
		
		
		cost = Math.sqrt(Math.pow(testCircle.getX(),2) + Math.pow(testCircle.getY(), 2)) + testCircle.getRadius() - 1;
		if ( cost > 0 )
			indices.put(0, cost);
				
		MoveStrategy ms = new MoveStrategy(indices);
		int index = ms.selectIndex();
		
		return index;
	}
	
	public double calculateDistanceCenters(Circle c1, Circle c2) {
		return  Math.sqrt(Math.pow(( c1.getX() - c2.getX()), 2) 
						+ Math.pow(( c1.getY() - c2.getY()), 2));
	}
}
