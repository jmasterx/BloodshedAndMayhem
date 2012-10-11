package com.jkgames.gta;

import java.util.LinkedList;
import java.util.List;

public class GraphNode
{
	//these are public because it makes them more useful that way
	public float x;
	public float y;
	//position in the city grid array for quick reference
	private int posX;
	private int posY;
	
	private List<GraphNode> nodes = new LinkedList<GraphNode>();
	
	public GraphNode()
	{
		this(0.0f,0.0f,-1,-1);
	}
	
	public GraphNode(float x, float y,int arrayPosX,int arrayPosY)
	{
		this.x = x;
		this.y = y;
		setArrayPosX(arrayPosX);
		setArrayPosY(arrayPosY);
	}
	
	public float getX() 
	{
		return x;
	}
	public void setX(float x) 
	{
		this.x = x;
	}
	public float getY() 
	{
		return y;
	}
	public void setY(float y)
	{
		this.y = y;
	}

	public int getArrayPosX() 
	{
		return posX;
	}

	public void setArrayPosX(int posX) 
	{
		this.posX = posX;
	}

	public int getArrayPosY() 
	{
		return posY;
	}

	public void setArrayPosY(int posY) 
	{
		this.posY = posY;
	}
	
	public void add(GraphNode node)
	{
		if(node != null)
		{
			nodes.add(node);
		}
		
	}
	
	public void remove(GraphNode node)
	{
		nodes.remove(node);
	}
	
	public boolean contains(GraphNode node)
	{
		return nodes.contains(node); 
	}
	
	public int getSize()
	{
		return nodes.size();
	}
	
	public GraphNode at(int index)
	{
		return nodes.get(index);
	}
	
	public void clear()
	{
		nodes.clear();
	}
}
