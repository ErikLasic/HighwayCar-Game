package OOPHighwayCarGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Rectangle;

public class DynamicGameObjectFireball extends DynamicGameObject implements Pool.Poolable{
    private static long lastFireballTime;
    private static final long FIREBALL_RELOAD = 1000000000;

    public static final Pool<DynamicGameObjectFireball> fireballPool =
            Pools.get(DynamicGameObjectFireball.class, 2);

    public DynamicGameObjectFireball() {
        super(0, 0, Assets.fireballImage.getWidth(), Assets.fireballImage.getHeight());
        velocity.y = (float) 200;
    }
    public static boolean fireballReloaded() {
        return (TimeUtils.nanoTime() - lastFireballTime > FIREBALL_RELOAD);
    }
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(Assets.fireballImage, bounds.x, bounds.y, (bounds.width)/2, (bounds.height)/2, bounds.width, bounds.height, 1, 1, 0,0,0, (int)bounds.width, (int)bounds.height,false,false);
    }
    @Override
    public boolean outOfScreen() {
        return (bounds.y > Gdx.graphics.getHeight());
    }
    public static DynamicGameObjectFireball Create(Rectangle car) {
        DynamicGameObjectFireball fireball = fireballPool.obtain();
        fireball.bounds.x = car.x + (float) Assets.carImage.getWidth() / 2f - (float) Assets.fireballImage.getWidth() / 2f;
        fireball.bounds.y = car.y + Assets.carImage.getHeight();
        lastFireballTime = TimeUtils.nanoTime();
        return fireball;
    }
    @Override
    public void reset() {

    }
}
