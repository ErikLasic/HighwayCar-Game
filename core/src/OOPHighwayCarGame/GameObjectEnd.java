package OOPHighwayCarGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameObjectEnd extends GameObject{
    private long counter = (long) Gdx.graphics.getDeltaTime();

    public GameObjectEnd(float x, float y, float width, float height)  {
        super(x, y, width, height);
    }
    public long getCounter() {
        return counter;
    }
    public void setCounter(long counter) {
        this.counter = counter;
    }
    public void startCounter() {
        this.counter++;
    }
    public void stopCounter() {
        this.counter--;
    }

    public void render(SpriteBatch batch, Viewport viewport) {
        Assets.font.setColor(Color.RED);
        Assets.font.draw(batch, "Game ended!", viewport.getWorldWidth() / 2f-100f, viewport.getWorldHeight()/ 2f);
        Assets.font.setColor(Color.BLACK);
        Assets.font.draw(batch, "SCORE:" + counter, viewport.getWorldWidth() / 2f-100f, viewport.getWorldHeight() / 3f);
    }
}
