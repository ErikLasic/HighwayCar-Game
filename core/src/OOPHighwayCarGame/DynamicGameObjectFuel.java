package OOPHighwayCarGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

public class DynamicGameObjectFuel extends DynamicGameObject implements Pool.Poolable {
    private static long lastFuelTime;
    private static final long CREATE_FUEL_TIME = 1000000000;
    private static long lastFuelDropTime = TimeUtils.nanoTime();
    private static final long FUEL_DROP_TIME = 1000000000;

    public static final Pool<DynamicGameObjectFuel> fuelPool =
            Pools.get(DynamicGameObjectFuel.class, 3);

    public DynamicGameObjectFuel() {
        super(getRandomTopPosition(), Gdx.graphics.getHeight(), Assets.fuelImage.getWidth(), Assets.fuelImage.getHeight());
        velocity.y= (float) -200;
    }
    public static float getRandomTopPosition() {
        return MathUtils.random(0, Gdx.graphics.getWidth() - Assets.policeCarImage.getWidth());
    }
    public static boolean isTimeToCreateNew() {
        return (TimeUtils.nanoTime() - lastFuelTime > CREATE_FUEL_TIME);
    }
    public static boolean isTimeToDropFuel() {
        return (TimeUtils.nanoTime() - lastFuelDropTime > FUEL_DROP_TIME);
    }
    public static void dropFuel(GameObjectScore gameObjectScore) {
        gameObjectScore.setFuelPercentageScore(gameObjectScore.getFuelPercentageScore()-10);
        lastFuelDropTime = TimeUtils.nanoTime();
    }
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(Assets.fuelImage, bounds.x, bounds.y, (bounds.width)/2, (bounds.height)/2, bounds.width, bounds.height, 1, 1, 0,0,0, (int)bounds.width, (int)bounds.height,false,false);
    }
    @Override
    public boolean outOfScreen() {
        return (bounds.y + Assets.fuelImage.getHeight() < 0);
    }
    @Override
    public void updateScore(GameObjectScore gameObjectScore) {
        Assets.fuelSound.play();
        gameObjectScore.setFuelPercentageScore(100);
    }
    public static DynamicGameObjectFuel Create() {
        DynamicGameObjectFuel fuel = fuelPool.obtain();
        lastFuelTime = TimeUtils.nanoTime();
        return fuel;
    }
    @Override
    public void reset() {
        bounds.x = getRandomTopPosition();
        bounds.y = Gdx.graphics.getHeight();
    }
}
