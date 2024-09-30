package BouncingBall;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class BouncingBallGame extends ApplicationAdapter {
    ShapeRenderer shape;
    private Array<BouncingBall> balls;
    private OrthographicCamera camera;
    @Override
    public void create() {
        shape = new ShapeRenderer();
        balls = new Array<BouncingBall>();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        if (Gdx.input.justTouched()) commandTouched();
        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (BouncingBall ball : balls) {
            ball.draw(shape);
            ball.update(Gdx.graphics.getDeltaTime());
        }
        shape.end();
    }
    @Override
    public void dispose() {
        shape.dispose();
    }
    private void commandTouched() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos); // transform the touch/mouse coordinates to our camera's coordinate system
        balls.add(new BouncingBall(touchPos.x, touchPos.y));
    }
}
