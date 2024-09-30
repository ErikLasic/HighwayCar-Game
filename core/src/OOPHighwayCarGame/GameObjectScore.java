package OOPHighwayCarGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameObjectScore extends GameObject{
    private int fuelPercentageScore = 100;
    private int carHealthScore = 3;
    private long counter = (long) Gdx.graphics.getDeltaTime();
    private long shieldCounter = 500;
    private boolean shieldActive = false;

    public GameObjectScore(float x, float y, float width, float height)  {
        super(x, y, width, height);
    }
    public int getCarHealthScore() {
        return carHealthScore;
    }
    public void setCarHealthScore(int carHealthScore) {
        this.carHealthScore = carHealthScore;
    }
    public int getFuelPercentageScore() {
        return fuelPercentageScore;
    }
    public void setFuelPercentageScore(int fuelPercentageScore) {
        this.fuelPercentageScore = fuelPercentageScore;
    }
    public void stopCounter() {
        counter--;
    }
    public void setShieldTimer() {
        shieldActive=true;
    }
    public void stopShieldCountdown() {
        shieldCounter++;
    }

    public void render(SpriteBatch batch, Viewport viewport) {
        counter++;
        Assets.font.setColor(Color.YELLOW);
        Assets.font.draw(batch, "" + fuelPercentageScore, viewport.getWorldWidth() - 50, viewport.getWorldHeight() - 20);
        Assets.font.setColor(Color.ORANGE);
        Assets.font.draw(batch, "" + carHealthScore, 20, viewport.getWorldHeight() - 20);
        Assets.font.setColor(Color.GREEN);
        Assets.font.draw(batch, "" + counter, viewport.getWorldWidth()/2f - 25 , viewport.getWorldHeight() - 20);
        if (shieldActive) {
            shieldCounter--;
            Assets.font.setColor(Color.PURPLE);
            Assets.font.draw(batch, "SHIELD TIMER:" + shieldCounter, viewport.getWorldWidth()/2f - 120 , viewport.getWorldHeight() - 50);
        }
        if (shieldCounter==0) {
            shieldCounter=500;
            shieldActive=false;
        }
    }
    public boolean isEnd() {
        return (carHealthScore<=0 || fuelPercentageScore<=0);
    }
}
