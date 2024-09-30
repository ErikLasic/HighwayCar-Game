package OOPHighwayCarGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

public class DynamicGameObjectPoliceCar extends DynamicGameObject implements Pool.Poolable {
    private static long  lastPoliceCarTime;
    private static final long CREATE_POLICE_CAR_TIME = 300000000;
    //private final float scale;

    public static final Pool<DynamicGameObjectPoliceCar> policeCarPool =
            Pools.get(DynamicGameObjectPoliceCar.class, 6);

    public DynamicGameObjectPoliceCar() {
        super(getRandomTopPosition(), Gdx.graphics.getHeight(), Assets.policeCarImage.getWidth(), Assets.policeCarImage.getHeight());
        velocity.y= (float) -300;
        //scale=MathUtils.random((float)1, (float)1.35);
    }
    public static float getRandomTopPosition() {
        return MathUtils.random(0, Gdx.graphics.getWidth() - Assets.policeCarImage.getWidth());
    }
    public static boolean isTimeToCreateNew() {
        return (TimeUtils.nanoTime() - lastPoliceCarTime > CREATE_POLICE_CAR_TIME);
    }
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(Assets.policeCarImage, bounds.x, bounds.y, (bounds.width)/2, (bounds.height)/2, bounds.width, bounds.height, 1, 1, 0,0,0, (int)bounds.width, (int)bounds.height,false,false);
    }
    @Override
    public boolean outOfScreen() {
        return (bounds.y + Assets.policeCarImage.getHeight() < 0);
    }
    @Override
    public void updateScore(GameObjectScore gameObjectScore) {
        Assets.carCrashSound.play();
        gameObjectScore.setCarHealthScore(gameObjectScore.getCarHealthScore()-1);
    }
    public void updateShieldedScore(GameObjectScore gameObjectScore) {
        Assets.carCrushSound.play();
    }
    public static DynamicGameObjectPoliceCar Create() {
        DynamicGameObjectPoliceCar policeCar = policeCarPool.obtain();
        lastPoliceCarTime = TimeUtils.nanoTime();
        return policeCar;
    }
    @Override
    public void reset() {
        bounds.x = getRandomTopPosition();
        bounds.y = Gdx.graphics.getHeight();
    }
}
