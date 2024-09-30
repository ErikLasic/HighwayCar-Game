package OOPHighwayCarGame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class GameObject {
    public final Rectangle bounds;

    public GameObject (float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void render(SpriteBatch batch) {}
}