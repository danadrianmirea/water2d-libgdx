package com.dream.water;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.dream.water.effect.MyContactListener;
import com.dream.water.effect.Water;
import com.dream.water.effect.WaterColumn;

public class GameMain extends ApplicationAdapter {

	Stage stage;
	OrthographicCamera camera;

	World world;
	Water water;
	MyContactListener contacts;
	Box2DDebugRenderer debugRenderer;
	
	ShapeRenderer shapeRenderer;

	@Override
	public void create() {
		camera = new OrthographicCamera();
		stage = new Stage(new StretchViewport(Gdx.graphics.getWidth() / 100, Gdx.graphics.getHeight() / 100, camera));

		// Create box2d world
		world = new World(new Vector2(0, -10), true);
		contacts = new MyContactListener();
		world.setContactListener(contacts);
		debugRenderer = new Box2DDebugRenderer();
		
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(0,0.5f,0.5f,1);
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		water = new Water(true);
		water.createBody(world, 4f, 0, 7, 2, 1); //world, x, y, width, height, density
		water.setContactListener(contacts);
	}
	
	private void createBody() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		
		// Set our body's starting position in the world
		bodyDef.position.set(Gdx.input.getX() / 100f, camera.viewportHeight - Gdx.input.getY() / 100f);
		
		// Create our body in the world using our body definition
		Body body = world.createBody(bodyDef);

		// Create a circle shape and set its radius to 6
		PolygonShape square = new PolygonShape();
		square.setAsBox(0.2f, 0.6f);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = square;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0.5f;

		// Create our fixture and attach it to the body
		body.createFixture(fixtureDef);

		square.dispose();
	}

	@Override
	public void render () {
		// Clean screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Input
		if(Gdx.input.justTouched()){
			createBody();
		}
		
		world.step(1/60f, 6, 2);
		water.update();
		// draw columns
		if(water.hasWaves()){
			shapeRenderer.begin(ShapeType.Line);
			for(int i = 0; i< water.getColumns().size(); i++){
				WaterColumn column = water.getColumns().get(i);
				shapeRenderer.line(column.x(), column.y(), column.x(), water.getColumns().get(i).getHeight());
			}
			shapeRenderer.end();
			water.updateWaves();
		}
		
		debugRenderer.render(world, camera.combined);
		
	}

	

	@Override
	public void dispose() {
		world.dispose();
		debugRenderer.dispose();
	}
}
