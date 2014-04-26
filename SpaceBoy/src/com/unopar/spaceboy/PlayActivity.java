package com.unopar.spaceboy;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.options.EngineOptions;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import com.unopar.spaceboy.base.IXCollisionListener;
import com.unopar.spaceboy.base.XApplication;
import com.unopar.spaceboy.character.Enemy;
import com.unopar.spaceboy.character.Sattelite;
import com.unopar.spaceboy.character.SpaceBoy;
import com.unopar.spaceboy.joystick.ControlListener;

public class PlayActivity extends BaseGameActivity {
	private BitmapTextureAtlas mBackgroundTextureAtlas;
	private BitmapTextureAtlas mParallaxTopTextureAtlas;
	private BitmapTextureAtlas mSpaceBoyTextureAtlas;

	private TextureRegion mBackgroundTextureRegion;
	private TextureRegion mParallaxTopTextureRegion;
	private TiledTextureRegion mSpaceBoyTextureRetion;

	private BitmapTextureAtlas mJoystickTextureAtlas;
	private TextureRegion mJoystickBaseTexture;
	private TextureRegion mJoystickKnobTexture;

	private BitmapTextureAtlas mEnemyTextureAtlas;
	private TiledTextureRegion mSatteliteTexture;

	private Sound mImplosion;

	@Override
	public EngineOptions onCreateEngineOptions() {
		Engine engine = XApplication.getInstance().createDefaultEngine();

		return engine.getEngineOptions();
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		XApplication.getInstance().configureAssetsFactory();

		mBackgroundTextureAtlas = new BitmapTextureAtlas(getTextureManager(),
				1024, 1024);
		mParallaxTopTextureAtlas = new BitmapTextureAtlas(getTextureManager(),
				1024, 1024);

		mBackgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBackgroundTextureAtlas, getAssets(),
						"level1-background.png", 0, 0);
		mParallaxTopTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mParallaxTopTextureAtlas, getAssets(),
						"level1-parallax-top.png", 0, 0);

		mSpaceBoyTextureAtlas = new BitmapTextureAtlas(getTextureManager(),
				1024, 1024);
		mSpaceBoyTextureRetion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mSpaceBoyTextureAtlas, getAssets(),
						"character-spaceboy.png", 0, 0, 4, 2);

		mJoystickTextureAtlas = new BitmapTextureAtlas(getTextureManager(),
				1024, 1024);
		mJoystickBaseTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mJoystickTextureAtlas, getAssets(),
						"controle-base.png", 0, 0);
		mJoystickKnobTexture = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mJoystickTextureAtlas, getAssets(),
						"controle-knob.png", 0, 512);

		mEnemyTextureAtlas = new BitmapTextureAtlas(getTextureManager(), 1024,
				1024);
		mSatteliteTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(mEnemyTextureAtlas, getAssets(),
						"character-satellite.png", 0, 0, 1, 1);

		mBackgroundTextureAtlas.load();
		mParallaxTopTextureAtlas.load();
		mSpaceBoyTextureAtlas.load();
		mJoystickTextureAtlas.load();
		mEnemyTextureAtlas.load();
		// getTextureManager().loadTexture(mJoystickTextureAtlas);

		mImplosion = SoundFactory.createSoundFromAsset(
				getSoundManager(), this,
				"implosion.wav");

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {
		Scene scene = new Scene();

		pOnCreateSceneCallback.onCreateSceneFinished(scene);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {

		int screenWidth = XApplication.getInstance().getScreenWidth();
		int screenHeight = XApplication.getInstance().getScreenHeight();

		Sprite spriteBackground = new Sprite(0, 0, screenWidth, screenHeight,
				mBackgroundTextureRegion, getVertexBufferObjectManager());
		Sprite spriteParallaxTop = new Sprite(0, 0, mParallaxTopTextureRegion,
				getVertexBufferObjectManager());

		AutoParallaxBackground parallax = new AutoParallaxBackground(0, 0, 0, 5);
		parallax.attachParallaxEntity(new ParallaxEntity(-5, spriteBackground));
		parallax.attachParallaxEntity(new ParallaxEntity(-25, spriteParallaxTop));

		pScene.setBackground(parallax);

		final SpaceBoy boy = new SpaceBoy(20, 250, mSpaceBoyTextureRetion,
				mEngine);
		pScene.attachChild(boy);

		IXCollisionListener collisionListener = new IXCollisionListener() {
			@Override
			public void onCollision(AnimatedSprite obstacle) {
				if(obstacle instanceof Sattelite) {
					mImplosion.play();
				}
			}

			@Override
			public AnimatedSprite getMainCharacter() {
				return boy;
			}
		};

		Sattelite st1 = new Sattelite(mSatteliteTexture, mEngine,
				collisionListener);
		Sattelite st2 = new Sattelite(mSatteliteTexture, mEngine,
				collisionListener);
		Sattelite st3 = new Sattelite(mSatteliteTexture, mEngine,
				collisionListener);

		pScene.attachChild(st1);
		pScene.attachChild(st2);
		pScene.attachChild(st3);

		float controlPositionY = screenHeight
				- mJoystickBaseTexture.getHeight() - 20;

		AnalogOnScreenControl control = new AnalogOnScreenControl(20,
				controlPositionY, mEngine.getCamera(), mJoystickBaseTexture,
				mJoystickKnobTexture, 0.1f, getVertexBufferObjectManager(),
				new ControlListener(boy));

		control.refreshControlKnobPosition();
		control.setAlpha(0.5f);
		pScene.setChildScene(control);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

}
