package BouncingBall;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import java.util.Random;


public class BouncingBall {
    private final float x;
    private float y;
    private final float radius;
    private final Color color;
    Random rand = new Random();
    private float SPEED;
    private static final float G = 6969;

    public BouncingBall(float x, float y) {
        this.x = x;
        this.y = y;
        this.radius = MathUtils.random((float)25,(float)50);
        this.color = new Color(rand.nextFloat(),rand.nextFloat(),rand.nextFloat(),rand.nextFloat());
        SPEED=-300;
    }
    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        shape.circle(x, y, radius);
    }
    public void update(float deltaTime) {
        SPEED=-G*deltaTime+SPEED;
        y=y+deltaTime*SPEED;
        if (y < radius) {
            y = radius;
            SPEED=-1*SPEED;
            SPEED=4/5f*SPEED;
        }
    }
}
