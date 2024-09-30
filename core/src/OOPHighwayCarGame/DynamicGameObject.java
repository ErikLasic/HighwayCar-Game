package OOPHighwayCarGame;

import com.badlogic.gdx.math.Vector2;

public class DynamicGameObject extends GameObject {
    public final Vector2 velocity;

    public DynamicGameObject (float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity = new Vector2();
    }

    public void update(float deltaTime) {
        bounds.x=bounds.x+deltaTime*velocity.x;
        bounds.y=bounds.y+deltaTime*velocity.y;
    }

    public void updateScore(GameObjectScore gameObjectScore) {}

    public boolean outOfScreen(){return false;}
}