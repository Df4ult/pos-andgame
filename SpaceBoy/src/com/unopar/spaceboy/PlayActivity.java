package com.unopar.spaceboy;

import org.andengine.engine.Engine;
import org.andengine.engine.options.EngineOptions;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import com.unopar.spaceboy.base.XApplication;
import com.unopar.spaceboy.character.SpaceBoy;

public class PlayActivity extends BaseGameActivity {
	private BitmapTextureAtlas mBackgroundTextureAtlas;
	private BitmapTextureAtlas mParallaxTopTextureAtlas;
	private BitmapTextureAtlas mSpaceBoyTextureAtlas;

	private TextureRegion mBackgroundTextureRegion;
	private TextureRegion mParallaxTopTextureRegion;
	private TiledTextureRegion mSpaceBoyTextureRetion;

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

		mBackgroundTextureAtlas.load();
		mParallaxTopTextureAtlas.load();
		mSpaceBoyTextureAtlas.load();

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
		parallax.attachParallaxEntity(new ParallaxEntity(0, spriteBackground));
		parallax.attachParallaxEntity(new ParallaxEntity(-25, spriteParallaxTop));

		pScene.setBackground(parallax);

		SpaceBoy boy = new SpaceBoy(20, 250, mSpaceBoyTextureRetion, mEngine);
		boy.move(1, 0);
		
		pScene.attachChild(boy);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

}