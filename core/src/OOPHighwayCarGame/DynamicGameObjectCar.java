package OOPHighwayCarGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class DynamicGameObjectCar extends DynamicGameObject {
    private static final int SPEED = 600;
    private static final int STILL = 0;
    private static final int LEFT = -1;
    private static final int RIGHT = 1;
    private long shieldCounter = 500;
    private boolean shieldActive = false;

    private int state;
    public DynamicGameObjectCar() {
        super(Gdx.graphics.getWidth() / 2f - Assets.carImage.getWidth() / 2f, 20, Assets.carImage.getWidth(), Assets.carImage.getHeight());
        state = STILL;
    }
    @Override
    public void update(float deltaTime) {
        velocity.x= SPEED * state;
        bounds.x = bounds.x + velocity.x * deltaTime;
        if (bounds.x < 0){
            bounds.x = 0;
        }
        if (bounds.x+bounds.width > Gdx.graphics.getWidth()){
            bounds.x = Gdx.graphics.getWidth()-bounds.width;
        }
        state=STILL;
    }
    public void pauseShield() {
        shieldCounter++;
    }
    public void setShieldActive() {
        shieldActive = true;
    }
    @Override
    public void render(SpriteBatch batch) {
        if (shieldActive) {
            shieldCounter--;
            batch.draw(Assets.shieldedCarImage, bounds.x, bounds.y, (bounds.width)/2, (bounds.height)/2, bounds.width, bounds.height, 1, 1, 0,0,0, (int)bounds.width, (int)bounds.height,false,false);
            if (shieldCounter==0) {
                shieldCounter=500;
                shieldActive=false;
            }
        } else {
            batch.draw(Assets.carImage, bounds.x, bounds.y, (bounds.width)/2, (bounds.height)/2, bounds.width, bounds.height, 1, 1, 0,0,0, (int)bounds.width, (int)bounds.height,false,false);
        }
    }
    public void commandMoveLeft() {
        state = LEFT;
    }

    public void commandMoveRight() {
        state = RIGHT;
    }
    public void commandTouched(Vector3 touchPos) {
        bounds.x = touchPos.x - Assets.carImage.getWidth() / 2f;
    }
}
