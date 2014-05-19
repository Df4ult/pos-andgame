package com.unopar.spaceboy.character;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.unopar.spaceboy.base.IXCollisionListener;
import com.unopar.spaceboy.base.XApplication;

public abstract class Weapon extends AnimatedSprite {
	public enum Stages {
		Available,
		
		Launch, Moving, Collision
	}
	
	private final IXCollisionListener mCollisionListener;
	private final PhysicsHandler mPhysicsHandler;
	private final IAnimationListener mAnimationListener;
	
	private final float mLimiteX;
	private Stages mStage;	
	
	public Weapon(ITiledTextureRegion textureRegion,
			VertexBufferObjectManager vertexBuffer,
			IXCollisionListener collisionListener) {
		super(0, 0, textureRegion, vertexBuffer);
		
		mCollisionListener = collisionListener;
		
		mPhysicsHandler = new PhysicsHandler(this);
		registerUpdateHandler(mPhysicsHandler);
		
		mAnimationListener = new IAnimationListener() {
			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
					int pInitialLoopCount) {
			}
			
			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
					int pRemainingLoopCount, int pInitialLoopCount) {
			}
			
			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
					int pOldFrameIndex, int pNewFrameIndex) {
			}
			
			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				moveToNextStage();
			}
		};
	
		mLimiteX = XApplication.getInstance().getScreenWidth() +
				getWidth();
		
		mStage = Stages.Available;
		setVisible(false);
	}

	protected abstract boolean animateLaunch();
	protected abstract void animateMove();
	protected abstract boolean animateCollides();
	protected abstract int getSpeed();
	protected abstract int getFrameDurations(int frameIndex);
	
	protected void animate(int firstFrameIndex, int lastFrameIndex, 
			boolean loop) {
		int frames = (lastFrameIndex - firstFrameIndex) + 1;
		long[] framesDurations = new long[frames];
		for(int i = 0; i < frames; i++) {
			framesDurations[i] = getFrameDurations(i + firstFrameIndex);
		}
		
		animate(framesDurations, firstFrameIndex, 
				lastFrameIndex, loop, mAnimationListener);
	}
	
	protected void moveToNextStage() {
		switch(mStage) {
			case Available:
				mStage = Stages.Launch;
				
				setVisible(true);
				
				if(!animateLaunch()) {
					moveToNextStage();
				}
				
				break;
			case Launch:
				mStage = Stages.Moving;
				
				animateMove();
				break;
			case Moving:
				mStage = Stages.Collision;
				
				mPhysicsHandler.setVelocity(0);
				
				if(!animateCollides()) {
					moveToNextStage();
				}
				break;
			case Collision:
				resetWeapon();
				break;
		}
	}

	private void resetWeapon() {
		setVisible(false);
		setPosition(-1000, - 1000);
		stopAnimation();
		
		mStage = Stages.Available;
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		if(mX > mLimiteX) {
			resetWeapon();
		} else {
			for(Enemy enemy : mCollisionListener.getEnemyCharacters()) {
				if(collidesWith(enemy)) {
					mCollisionListener.onCollision(this, enemy);
					moveToNextStage();
					
					break;
				}
			}
		}
		
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	public synchronized boolean shoot(float x, float y) {
		if(mStage != Stages.Available) {
			return false;
		}
		
		setPosition(x, y);
		moveToNextStage();
		
		return true;
	}

	protected void move(float x, float y) {
		mPhysicsHandler.setVelocity(x * getSpeed(), y * getSpeed());
	}
}
