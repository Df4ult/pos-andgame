package com.unopar.spaceboy.character;

import java.util.ArrayList;

public class WeaponPool<T extends Weapon> {
	public interface WeaponFactory<T extends Weapon> {
		T create();
	}
	private final ArrayList<T> mItems;
	private WeaponFactory<T> mFactory;
	
	public WeaponPool(WeaponFactory<T> factory) {
		mItems = new ArrayList<T>();
		mFactory = factory;
	}
	
	public synchronized T shoot(float x, float y) {
		T locked = null;
		for(T weapon : mItems) {
			if(weapon.shoot(x, y)) {
				locked = weapon;
				
				break;
			}
		}
		
		if(locked == null) {
			locked = mFactory.create();
			locked.shoot(x, y);
			
			mItems.add(locked);
		}
		
		return locked;
	}

}
