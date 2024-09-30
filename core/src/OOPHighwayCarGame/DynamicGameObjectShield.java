package OOPHighwayCarGame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

public class DynamicGameObjectShield extends DynamicGameObject implements Pool.Poolable {
    private static long lastShieldTime;

    public static final Pool<DynamicGameObjectShield> shieldPool =
            Pools.get(DynamicGameObjectShield.class, 1);

    public DynamicGameObjectShield() {
        super(getRandomTopPosition(), Gdx.graphics.getHeight(), Assets.shieldImage.getWidth(), Assets.shieldImage.getHeight());
        velocity.y = (float) -300;
    }
    public static float getRandomTopPosition() {
        return MathUtils.random(0, Gdx.graphics.getWidth() - Assets.shieldImage.getWidth());
    }
    public static boolean CreateNew() {
        return (TimeUtils.nanoTime() - lastShieldTime > 1000000000L * 50);
    }
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(Assets.shieldImage, bounds.x, bounds.y, (bounds.width)/2, (bounds.height)/2, bounds.width, bounds.height, 1, 1, 0,0,0, (int)bounds.width, (int)bounds.height,false,false);
    }
    @Override
    public boolean outOfScreen() {
        return (bounds.y + Assets.shieldImage.getHeight() < 0);
    }
    @Override
    public void updateScore(GameObjectScore gameObjectScore) {
        Assets.shieldSound.play();
        gameObjectScore.setShieldTimer();
    }
    public static DynamicGameObjectShield Create() {
        DynamicGameObjectShield shield = shieldPool.obtain();
        lastShieldTime = TimeUtils.nanoTime();
        return shield;
    }
    @Override
    public void reset() {
        bounds.x = getRandomTopPosition();
        bounds.y = Gdx.graphics.getHeight();
    }
}
