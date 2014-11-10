package com.jkgames.gta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class CityGenerator
{
	private GraphNode[][] graphNodes = null;
	private float intersectionWidth;
	
	public static final float NODE_SCALAR = 1.0f / 6.0f;
	public static final int EDGE_REMOVAL_SCALAR = 7;
	public static final float VERTEX_REMOVAL_SCALAR = 1.5f;
	public static final int MIN_REMOVE_EDGES = 1;
	public static final int MAX_REMOVE_EDGES = 6;
	
	CityGenerator(int xNodes,int yNodes, float nodeWidth, float nodeHeight)
	{
		intersectionWidth = nodeWidth * NODE_SCALAR;
		initNodes(xNodes, yNodes, nodeWidth, nodeHeight);
	
		removeVerticies();
		removeEdges();
		addMainArteries();
		removeDisconnectedSquares();
		removeDeadEnds();
	}
	
	private void addMainArteries()
	{
		int numX = JMath.randomRange(2, getNumXNodes() / 6);
		int numY = JMath.randomRange(2, getNumYNodes() / 6);
		
		for(int i = 0; i < numX; ++i)
		{
			int xNode = JMath.randomRange(1, getNumXNodes() - 2);
			
			for(int j = 0; j < getNumYNodes(); ++j)
			{
				GraphNode g = getNodeAt(xNode, j);
				if(!hasNodeBelow(g) && nodeExists(xNode, j + 1))
				{
					GraphNode below = getNodeAt(xNode, j + 1);
					below.add(g);
					g.add(below);
				}
			}
		}
		
		for(int i = 0; i < numY; ++i)
		{
			int yNode = JMath.randomRange(1, getNumYNodes() - 2);
			
			for(int j = 0; j < getNumXNodes(); ++j)
			{
				GraphNode g = getNodeAt(j, yNode);
				if(!hasNodeToRight(g) && nodeExists(j + 1, yNode))
				{
					GraphNode right = getNodeAt(j + 1, yNode);
					right.add(g);
					g.add(right);
				}
			}
		}
	}
	private void removeEdges()
	{
		int iterations = getNumXNodes() * getNumYNodes() * EDGE_REMOVAL_SCALAR;
		
		for(int i = 0; i < iterations; ++i)
		{
			//pick a random one, but not an exterior edge
			int x = JMath.randomRange(1, getNumXNodes() - 2);
			int y = JMath.randomRange(1, getNumYNodes() - 2);
			
			//pick a direction for the edge to remove
			int direction = JMath.randomRange(1, 4);
			int dirX = 0;
			int dirY = 0;
			if(direction == 1)
			{
				dirX = 1;
				dirY = 0;
			}
			else if(direction == 2)
			{
				dirX = -1;
				dirY = 0;
			}
			else if(direction == 3)
			{
				dirX = 0;
				dirY = 1;
			}
			else
			{
				dirX = 0;
				dirY = -1;
			}
			
			//remove between MIN and MAX contiguous edges
			int iters = JMath.randomRange(MIN_REMOVE_EDGES, MAX_REMOVE_EDGES);
			for(int n = 0; n < iters; ++n)
			{
				int extraX = dirX != 0 ? n : 0;
				int extraY = dirY != 0 ? n : 0;
				removeEdge(
					x + extraX,
					y + extraY,
					dirX + extraX,
					dirY + extraY);
			}
		}
		
	}
	
	private void removeVerticies()
	{
		//remove vertices to create holes in the map
		for(int i = 0; i < getNumXNodes() * VERTEX_REMOVAL_SCALAR; ++i)
		{
			int x = JMath.randomRange(1, getNumXNodes() - 2);
			int y = JMath.randomRange(1, getNumYNodes() - 2);
			
			removeVertex(x, y);
		}
	}
	
	private void removeDeadEnds()
	{
		//vertices with 1 connection are dead ends
		//keep removing them until every vertex has at least 2 connections
		//to form a valid intersection
		boolean found = true;
		while(found)
		{
			found = false;
			for(int i = 0; i < getNumXNodes(); ++i)
			{
				for(int j = 0; j < getNumYNodes(); ++j)
				{
					GraphNode a = getNodeAt(i, j);
					if(a.getSize() == 1)
					{
						if(removeVertex(i, j))
						found = true;
					}
				}
			}
		}
	}
	
	public int getNumXNodes()
	{
		return graphNodes.length;
	}
	
	public int getNumYNodes()
	{
		if(getNumXNodes() > 0)
		{
			return graphNodes[getNumXNodes() - 1].length;
		}
		
		return 0;
	}
	
	public boolean nodeExists(int x, int y)
	{
		return x >= 0 &&
				y >= 0 &&
				x < getNumXNodes() &&
				y < getNumYNodes();
	}
	
	public boolean nodeIsValid(int x, int y)
	{
		return nodeExists(x, y) && graphNodes[x][y] != null;
	}
	
	public GraphNode getNodeAt(int x, int y)
	{
		if(nodeExists(x, y))
		{
			return graphNodes[x][y];
		}
		
		return null;
	}
	
	public boolean hasNodeAt(int x, int y, int relX, int relY)
	{
		GraphNode node = getNodeAt(x, y);
		
		if(node != null)
		{
			return node.contains(getNodeAt(x + relX, y + relY));
		}
		
		return false;
	}
	
	public boolean hasNodeAt(GraphNode node, int relX, int relY)
	{
		if(node != null)
		{
			return hasNodeAt(node.getArrayPosX(),node.getArrayPosY(), relX, relY);
		}
		
		return false;
	}
	
	public boolean hasNodeAbove(int x, int y)
	{
		return hasNodeAt(x,y,0,-1);
	}
	
	public boolean hasNodeBelow(int x, int y)
	{
		return hasNodeAt(x,y,0,1);
	}
	
	public boolean hasNodeToLeft(int x, int y)
	{
		return hasNodeAt(x,y,-1,0);
	}
	
	public boolean hasNodeToRight(int x, int y)
	{
		return hasNodeAt(x,y,1,0);
	}
	
	public boolean hasNodeAbove(GraphNode node)
	{
		return hasNodeAt(node,0,-1);
	}
	
	public boolean hasNodeBelow(GraphNode node)
	{
		return hasNodeAt(node,0,1);
	}
	
	public boolean hasNodeToLeft(GraphNode node)
	{
		return hasNodeAt(node,-1,0);
	}
	
	public boolean hasNodeToRight(GraphNode node)
	{
		return hasNodeAt(node,1,0);
	}
	
	public int getNodeNumConnections(int x, int y)
	{
		GraphNode node = getNodeAt(x, y);
		if(node != null)
		{
			return node.getSize();
		}
		
		return 0;
	}
	
	public boolean removeEdge(int x, int y, int relEx, int relEy)
	{
		//removes edge x,y x+relX,y+relY
		//returns false if nulls are found or removing this edge
		//would have created a disconnected vertex
		GraphNode n1 = getNodeAt(x,y);
		GraphNode n2 = getNodeAt(x + relEx,y + relEy);
		
		if(		n1 != null &&
				n2 != null &&
				n1.contains(n2) && 
				n2.contains(n1) &&
				n1.getSize() >= 3 &&
				n2.getSize() >= 3)
		{
			n1.remove(n2);
			n2.remove(n1);
			return true;
		}
		
		return false;
	}
	
	public boolean removeVertex(int x, int y)
	{
		//removes the vertex by detaching all connections
		GraphNode n1 = getNodeAt(x,y);
		if(n1 != null)
		{
			for(int i = 0; i < n1.getSize(); ++i)
			{
				n1.at(i).remove(n1);
			}
			
			n1.clear();
			
			return true;
		}
	
		return false;
	}
	
	private void removeDisconnectedSquares()
	{
		 ArrayList<GraphNode> nodes = getDisconnectedNodes();
		 
		for(int i = 0; i < nodes.size(); ++i)
		{
			 removeVertex(nodes.get(i).getArrayPosX(), nodes.get(i).getArrayPosY());
		}
	}
	
	private ArrayList<GraphNode> getDisconnectedNodes()
	{
		ArrayList<GraphNode> visited = new ArrayList<GraphNode>();
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		
		for(int i = 0; i < getNumXNodes(); ++i)
		{
			for(int j = 0; j < getNumYNodes(); ++j)
			{
				nodes.add(graphNodes[i][j]);
			}
		}
		
		generateDisconnectedNodes(nodes,visited, nodes.get(0));
		return nodes;
	}
	
	private void generateDisconnectedNodes(ArrayList<GraphNode> allNodes,
			ArrayList<GraphNode> visited, GraphNode target)
	{
		//returns a list of nodes that cannot be reached from the target node
		//starts with list of all nodes and removes them as they are discovered
		//once all possible nodes have been visited
		//only the ones unreachable remain
		Queue<GraphNode> q = new LinkedList<GraphNode>();
		q.add(target);
		while(!q.isEmpty())
		{
			target = q.poll();
			if(visited.contains(target))
			{
				continue;
			}
			else
			{
				visited.add(target);
			}
			   if(!allNodes.contains(target))
			        return;
	
			    allNodes.remove(target);
	
			    for(int i = 0; i < target.getSize(); ++i)
			       q.add(target.at(i));
		}
	}
	
	private void initNodes(int x, int y, float nodeWidth, float nodeHeight)
	{
		graphNodes = new GraphNode[x][y];
		//current location of node
		float curX = 0.0f;
		float curY = 0.0f;
		
		//create them
		for(int i = 0; i < x; ++i)
		{
			curY = 0.0f;
			for(int j = 0; j < y; ++j)
			{
				graphNodes[i][j] = new GraphNode(curX,curY,i,j);
				curY += nodeHeight;
			}
			
			curX += nodeWidth;
		}
		
		//link them to each other
		for(int i = 0; i < x; ++i)
		{
			for(int j = 0; j < y; ++j)
			{
				graphNodes[i][j].add(getNodeAt(i, j - 1));
				graphNodes[i][j].add(getNodeAt(i, j + 1));
				graphNodes[i][j].add(getNodeAt(i + 1, j));
				graphNodes[i][j].add(getNodeAt(i - 1, j));
			}
		}
	}
	
	public boolean isNodeCrossSection(int x, int y)
	{
		//returns true if a node is only a link as part of a road
		//a road should be 1 long contiguous strip
		//and should only have intersections that split the roads
		GraphNode g = getNodeAt(x, y);
		if(g != null)
		{
			return (hasNodeAbove(g) && hasNodeBelow(g) && g.getSize() == 2) ||
			(hasNodeToLeft(g) && hasNodeToRight(g) && g.getSize() == 2);
		}	
		return false;	
	}
	
	public GraphNode findNonCrossSectionNode(int x, int y, int dx, int dy)
	{
		int posX = x + dx;
		int posY = y + dy;
		GraphNode validNode = getNodeAt(posX, posY);
		
		while(posX >= 0 && posX < getNumXNodes() &&
				posY >= 0 && posY < getNumYNodes())
		{
			if(validNode == null)
			{
				return null;
			}
			
			validNode = getNodeAt(posX, posY);
			if(!isNodeCrossSection(posX, posY))
			{
				return validNode;
			}
			
			posX += dx;
			posY += dy;
		}
		
		return null;
	}
	
	private Intersection createIntersection(RectF position, 
			GraphNode node, Bitmap fourWay, Bitmap threeWay, Bitmap twoWay)
	{
		Bitmap bmp = null;
		int direction = 0; //0 = top, 1 = right, 2 = bottom, 3 = left
		boolean t = hasNodeAbove(node);
		boolean l = hasNodeToLeft(node);
		boolean b = hasNodeBelow(node);
		boolean r = hasNodeToRight(node);
		
		if(t && l && b && r)
		{
			bmp = fourWay;
			direction = 0;
		}
		else if(t && l && !b && !r)
		{
			bmp = twoWay;
			direction = 0;
		}
		else if(t && !l && !b && r)
		{
			bmp = twoWay;
			direction = 1;
		}
		else if(!t && !l && b && r)
		{
			bmp = twoWay;
			direction = 2;
		}
		else if(!t && l && b && !r)
		{
			bmp = twoWay;
			direction = 3;
		}
		else if(t && l && b && !r)
		{
			bmp = threeWay;
			direction = 0;
		}
		else if(t && l && !b && r)
		{
			bmp = threeWay;
			direction = 1;
		}
		else if(t && !l && b && r)
		{
			bmp = threeWay;
			direction = 2;
		}
		else if(!t && l && b && r)
		{
			bmp = threeWay;
			direction = 3;
		}
		Intersection intersection = new Intersection(position, bmp);
		intersection.setAngle((float)(Math.PI / 2.0f)  * direction);
		return intersection;
	}
	private void generateRoadsAndIntersections(ArrayList<Intersection> intersections,
			ArrayList<Road> roads, Bitmap roadImg,
			Bitmap fourWay, Bitmap threeWay, Bitmap twoWay)
	{
		//memory allocations are expensive
		intersections.ensureCapacity(getNumXNodes() * getNumYNodes());
		roads.ensureCapacity(getNumXNodes() * getNumYNodes());
		
		//make a duplicate list for intersections for convenience
		Intersection intersectionMatrix[][] = 
				new Intersection[getNumXNodes()][getNumYNodes()];
		
		//reused throughout
		RectF tempRect = new RectF();
		
		//precreate all intersections
		for(int i = 0; i < getNumXNodes(); ++i)
		{
			for(int j = 0; j < getNumYNodes(); ++j)
			{
				GraphNode g = getNodeAt(i, j);
				//we won't need cross section intersections
				if(!isNodeCrossSection(i, j))
				{
					tempRect.left = g.x - (intersectionWidth / 2.0f);
					tempRect.top = g.y - (intersectionWidth / 2.0f);
					tempRect.right = g.x + (intersectionWidth / 2.0f);
					tempRect.bottom = g.y + (intersectionWidth / 2.0f);
					
					intersectionMatrix[i][j] = 
							createIntersection(tempRect, g, fourWay, threeWay, twoWay);
				}
			
			}
		}
		
		for(int i = 0; i < getNumXNodes(); ++i)
		{
			for(int j = 0; j < getNumYNodes(); ++j)
			{
				GraphNode g = getNodeAt(i, j);
				Intersection in = intersectionMatrix[i][j];
				
				if(g.getSize() > 0 && in != null)
				{
					intersections.add(in);
				}
				else
				{
					continue;
				}
				
				
				if(hasNodeBelow(g) && in.getBottomRoad() == null)
				{
					GraphNode gBelow = findNonCrossSectionNode(i, j, 0, 1);
					Intersection inBelow = intersectionMatrix
							[gBelow.getArrayPosX()][gBelow.getArrayPosY()];
					
					tempRect.left = g.x - (intersectionWidth / 2.0f);
					tempRect.top = g.y + (intersectionWidth / 2.0f);
					tempRect.right = gBelow.x + (intersectionWidth / 2.0f) ;
					//fix floating point artifacts
					tempRect.bottom = gBelow.y - (intersectionWidth / 2.0f) + 0.015f;
					
					Road road = new Road(tempRect,inBelow,in,roadImg,true);
					in.setBottomRoad(road);
					inBelow.setTopRoad(road);
					roads.add(road);
				}
			
				if(hasNodeToRight(g) && in.getRightRoad() == null)
				{
					GraphNode gToRight = findNonCrossSectionNode(i, j, 1, 0);
					Intersection inToRight = intersectionMatrix
							[gToRight.getArrayPosX()][gToRight.getArrayPosY()];
					
					tempRect.left = g.x + (intersectionWidth / 2.0f);
					tempRect.top = g.y - (intersectionWidth / 2.0f);
					//fix floating point artifacts
					tempRect.right = gToRight.x - (intersectionWidth / 2.0f) +  0.015f;
					tempRect.bottom = gToRight.y + (intersectionWidth / 2.0f);
					
					Road road = new Road(tempRect,inToRight,in,roadImg,false);
					inToRight.setLeftRoad(road);
					in.setRightRoad(road);
					roads.add(road);
				}
			}
		}
	}
	
	public City generateCity(Resources res)
	{
		Bitmap roadImg = BitmapFactory.decodeResource(res, R.drawable.main_road);
		Bitmap fourWay = BitmapFactory.decodeResource(res, R.drawable.road_intersection);
		Bitmap threeWay = BitmapFactory.decodeResource(res, R.drawable.road_intersection_three_way);
		Bitmap twoWay = BitmapFactory.decodeResource(res, R.drawable.road_intersection_two_way);
		ArrayList<Intersection> intersections = new ArrayList<Intersection>();
		ArrayList<Road> roads = new ArrayList<Road>();
		generateRoadsAndIntersections(intersections, roads, roadImg, 
				fourWay,threeWay,twoWay);
		BuildingGenerator gen = new BuildingGenerator(res, roads);
		return new City(roads,intersections,gen.generateBuildings());
	}

	public float getIntersectionWidth() 
	{
		return intersectionWidth;
	}

	public void setIntersectionWidth(float intersectionWidth) 
	{
		this.intersectionWidth = intersectionWidth;
	}

}
