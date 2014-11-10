package com.jkgames.gta;

public interface CollisionListener 
{
	public void onCollisionContact(Entity a, Entity b);
	public void onCollisionDetected(Entity a, Entity b);
}
