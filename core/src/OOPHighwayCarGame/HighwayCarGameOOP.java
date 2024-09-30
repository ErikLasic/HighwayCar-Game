package OOPHighwayCarGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;


import java.util.Iterator;

import OOPHighwayCarGame.util.debug.DebugCameraController;
import OOPHighwayCarGame.util.debug.MemoryInfo;
import WorldUnits.ViewportUtils;

public class HighwayCarGameOOP extends ApplicationAdapter {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private DynamicGameObjectCar car;
    private Array<DynamicGameObjectPoliceCar> policeCars;
    private Array<DynamicGameObjectFuel> fuels;
    private Array<DynamicGameObjectFireball> fireballs;
    private Array<DynamicGameObjectShield> shields;
    private GameObjectScore gameObjectScore;
    private GameObjectEnd gameObjectEnd;
    float width, height;
    boolean paused = false;
    boolean shielded = false;
    private long shieldCounter = 500;
    private ParticleEffect explosion;
    private ParticleEffect smoke;
    private ParticleEffect smokeCar;

    private DebugCameraController debugCameraController;
    private MemoryInfo memoryInfo;
    private boolean debug = false;

    private ShapeRenderer shapeRenderer;
    public Viewport viewport;

    @Override
    public void create() {
        Assets.load();
        Assets.font.getData().setScale(2);
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        gameObjectScore = new GameObjectScore(0,0, width,height);
        gameObjectEnd = new GameObjectEnd(0,0, width,height);
        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        debugCameraController = new DebugCameraController();
        debugCameraController.setStartPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        memoryInfo = new MemoryInfo(500);

        shapeRenderer = new ShapeRenderer();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

        // create a Rectangle to logically represents the rocket
        car = new DynamicGameObjectCar();

        policeCars = new Array<DynamicGameObjectPoliceCar>();
        fuels = new Array<DynamicGameObjectFuel>();
        fireballs = new Array<DynamicGameObjectFireball>();
        shields = new Array<DynamicGameObjectShield>();
        spawnPoliceCar();
        spawnFuel();
        spawnShield();

        explosion = new ParticleEffect();
        explosion.load(Gdx.files.internal("explosion.pe"), Gdx.files.internal(""));
        smoke = new ParticleEffect();
        smokeCar = new ParticleEffect();
        smoke.load(Gdx.files.internal("exhaust.smoke"), Gdx.files.internal(""));
        smokeCar.load(Gdx.files.internal("exhaust.smoke"), Gdx.files.internal(""));
        smoke.getEmitters().first().flipY();
        smoke.start();
        smokeCar.start();
    }

    private void spawnPoliceCar() {
        policeCars.add(DynamicGameObjectPoliceCar.Create());
    }
    private void spawnFuel() {
        fuels.add(DynamicGameObjectFuel.Create());
    }
    private void spawnShield() {
        shields.add(DynamicGameObjectShield.Create());
    }
    private void shootFireball() {
        //fireballs.add(new DynamicGameObjectFireball(, car.bounds.y + Assets.carImage.getHeight()));
        fireballs.add(DynamicGameObjectFireball.Create(car.bounds));
        Assets.fireballSound.play();
    }
    private void dropFuel() {
        DynamicGameObjectFuel.dropFuel(gameObjectScore);
    }
    private void commandTouched() {
        if (!paused) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos); // transform the touch/mouse coordinates to our camera's coordinate system
            car.commandTouched(touchPos);
        }
    }
    private void commandExitGame() {
        Gdx.app.exit();
    }
    @Override
    public void render() { //runs every frame
        if (shielded) {
            if (!paused) {
                shieldCounter--;
            }
            if (shieldCounter==0) {
                shielded=false;
                shieldCounter=500;
            }
        }
        //clear screen
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // tell the camera to update its matrices.
        camera.update();
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.setProjectionMatrix(camera.combined);
        batch.begin(); {
            batch.draw(Assets.backgroundImage, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        }
        batch.end();
        gameObjectEnd.startCounter();
        // process user input
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) debug = !debug;

        if (debug) {
            debugCameraController.handleDebugInput(Gdx.graphics.getDeltaTime());
            memoryInfo.update();
        }

        if(Gdx.input.isTouched()) commandTouched(); //mouse or touch screen
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) car.commandMoveLeft();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) car.commandMoveRight();
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) commandExitGame();
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && DynamicGameObjectFireball.fireballReloaded() && !paused) {
            shootFireball();
            explosion.start();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) paused=!paused;
        if (Gdx.input.isKeyPressed(Input.Keys.R) && gameObjectScore.isEnd()) {
            for (DynamicGameObjectPoliceCar policeCar : policeCars) {
                DynamicGameObjectPoliceCar.policeCarPool.free(policeCar);
            }
            for (DynamicGameObjectFuel fuel : fuels) {
                DynamicGameObjectFuel.fuelPool.free(fuel);
            }
            for (DynamicGameObjectFireball fireball : fireballs) {
                DynamicGameObjectFireball.fireballPool.free(fireball);
            }
            for (DynamicGameObjectShield shield : shields) {
                DynamicGameObjectShield.shieldPool.free(shield);
            }
            dispose();
            create();
        }
        if (gameObjectScore.isEnd()) {
            batch.begin();
            {
                gameObjectEnd.render(batch, viewport);
                gameObjectEnd.stopCounter();
                gameObjectScore.render(batch, viewport);
                gameObjectScore.stopCounter();
                Assets.font.setColor(Color.ORANGE);
                Assets.font.draw(batch, "PRESS R TO START AGAIN", viewport.getWorldWidth()/2f-170f, viewport.getWorldHeight() / 2f+50f);
            }
            batch.end();
        } else if (paused) {
            batch.begin();
            {
                gameObjectEnd.stopCounter();
                gameObjectScore.stopCounter();
                gameObjectScore.stopShieldCountdown();
                car.pauseShield();
                car.render(batch);
                explosion.draw(batch, 0);
                smoke.draw(batch, 0);
                smokeCar.draw(batch,0);
                for (DynamicGameObjectPoliceCar police : policeCars) {
                    police.render(batch);
                }
                for (DynamicGameObjectFuel fuel : fuels) {
                    fuel.render(batch);
                }
                for (DynamicGameObjectFireball fireball : fireballs) {
                    fireball.render(batch);
                }
                for (DynamicGameObjectShield shield : shields) {
                    shield.render(batch);
                }
                gameObjectScore.render(batch, viewport);
                Assets.font.setColor(Color.GREEN);
                Assets.font.draw(batch, "PAUSED", viewport.getWorldWidth() / 2f-50f, viewport.getWorldHeight() / 2f);
            }
            batch.end();
        }
        else {
            car.update(Gdx.graphics.getDeltaTime());
            explosion.setPosition(car.bounds.x + Assets.carImage.getWidth()/2f, car.bounds.y+Assets.carImage.getHeight());
            explosion.update(Gdx.graphics.getDeltaTime());
            smoke.setPosition(car.bounds.x+Assets.carImage.getWidth()/2f, car.bounds.y);
            smoke.update(Gdx.graphics.getDeltaTime());
            for (DynamicGameObjectPoliceCar policeCar : policeCars) {
                policeCar.update(Gdx.graphics.getDeltaTime());
                smokeCar.setPosition(policeCar.bounds.x+Assets.policeCarImage.getWidth()/2f, policeCar.bounds.y+Assets.policeCarImage.getHeight());
                smokeCar.update(Gdx.graphics.getDeltaTime());
            }
            for (DynamicGameObjectFuel fuel : fuels) {
                fuel.update(Gdx.graphics.getDeltaTime());
            }
            for (DynamicGameObjectFireball fireball : fireballs) {
                fireball.update(Gdx.graphics.getDeltaTime());
            }
            for (DynamicGameObjectShield shield : shields) {
                shield.update(Gdx.graphics.getDeltaTime());
            }

            if (DynamicGameObjectPoliceCar.isTimeToCreateNew()) spawnPoliceCar();
            if (DynamicGameObjectFuel.isTimeToCreateNew()) spawnFuel();
            if (DynamicGameObjectFuel.isTimeToDropFuel()) dropFuel();
            if (DynamicGameObjectShield.CreateNew()) spawnShield();

            for (Iterator<DynamicGameObjectPoliceCar> it_police = policeCars.iterator(); it_police.hasNext(); ) {
                DynamicGameObjectPoliceCar police = it_police.next();
                if (police.outOfScreen()) {
                    DynamicGameObjectPoliceCar.policeCarPool.free(police);
                    it_police.remove();
                }
                if (police.bounds.overlaps(car.bounds)) {
                    if (!shielded) {
                        DynamicGameObjectPoliceCar.policeCarPool.free(police);
                        police.updateScore(gameObjectScore);
                        it_police.remove();
                    } else {
                        DynamicGameObjectPoliceCar.policeCarPool.free(police);
                        police.updateShieldedScore(gameObjectScore);
                        it_police.remove();
                    }
                }
            }
            for (Iterator<DynamicGameObjectFuel> it_fuel = fuels.iterator(); it_fuel.hasNext(); ) {
                DynamicGameObjectFuel fuel = it_fuel.next();
                if (fuel.outOfScreen()) {
                    DynamicGameObjectFuel.fuelPool.free(fuel);
                    it_fuel.remove();
                }
                if (fuel.bounds.overlaps(car.bounds)) {
                    DynamicGameObjectFuel.fuelPool.free(fuel);
                    it_fuel.remove();
                    fuel.updateScore(gameObjectScore);
                }
            }
            for (Iterator<DynamicGameObjectFireball> it_fireball = fireballs.iterator(); it_fireball.hasNext();) {
                DynamicGameObjectFireball fireball = it_fireball.next();
                for (Iterator<DynamicGameObjectPoliceCar> it_police = policeCars.iterator(); it_police.hasNext();) {
                    DynamicGameObjectPoliceCar police = it_police.next();
                    if (fireball.bounds.overlaps(police.bounds)) {
                        DynamicGameObjectFireball.fireballPool.free(fireball);
                        it_fireball.remove();
                        DynamicGameObjectPoliceCar.policeCarPool.free(police);
                        it_police.remove();
                    }
                }
                if (fireball.outOfScreen()) {
                    DynamicGameObjectFireball.fireballPool.free(fireball);
                    it_fireball.remove();
                }
            }
            for (Iterator<DynamicGameObjectShield> it_shield = shields.iterator(); it_shield.hasNext();) {
                DynamicGameObjectShield shield = it_shield.next();
                if (shield.outOfScreen()) {
                    DynamicGameObjectShield.shieldPool.free(shield);
                    it_shield.remove();
                }
                if (shield.bounds.overlaps(car.bounds)) {
                    shielded = true;
                    car.setShieldActive();
                    Assets.shieldSound.play();
                    DynamicGameObjectShield.shieldPool.free(shield);
                    it_shield.remove();
                    shield.updateScore(gameObjectScore);
                }
            }

            batch.begin();
            {
                car.render(batch);
                explosion.draw(batch, Gdx.graphics.getDeltaTime());
                smoke.draw(batch, Gdx.graphics.getDeltaTime());
                smokeCar.draw(batch, Gdx.graphics.getDeltaTime());
                for (DynamicGameObjectPoliceCar police : policeCars) {
                    police.render(batch);
                }
                for (DynamicGameObjectFuel fuel : fuels) {
                    fuel.render(batch);
                }
                for (DynamicGameObjectFireball fireball : fireballs) {
                    fireball.render(batch);
                }
                for (DynamicGameObjectShield shield : shields) {
                    shield.render(batch);
                }
                gameObjectScore.render(batch, viewport);
            }
            batch.end();
        }
        if (debug) {
            debugCameraController.applyTo(camera);
            batch.begin();
            {
                // the average number of frames per second
                GlyphLayout layout = new GlyphLayout(Assets.font, "FPS:" + Gdx.graphics.getFramesPerSecond());
                Assets.font.setColor(Color.YELLOW);
                Assets.font.draw(batch, layout, viewport.getWorldWidth()- layout.width, viewport.getWorldHeight() - 50);

                // number of rendering calls, ever; will not be reset unless set manually
                Assets.font.setColor(Color.YELLOW);
                Assets.font.draw(batch, "RC:" + batch.totalRenderCalls, viewport.getWorldWidth() / 2f, viewport.getWorldHeight() - 20);

                memoryInfo.render(batch, Assets.font);
            }
            batch.end();

            batch.totalRenderCalls = 0;
            ViewportUtils.drawGrid(viewport, shapeRenderer, 50);

            // print rectangles
            shapeRenderer.setProjectionMatrix(camera.combined);
            // https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            {
                shapeRenderer.setColor(1, 1, 0, 1);
                for (DynamicGameObjectPoliceCar policeCar : policeCars) {
                    shapeRenderer.rect(policeCar.bounds.x, policeCar.bounds.y, Assets.policeCarImage.getWidth(), Assets.policeCarImage.getHeight());
                }
                for (DynamicGameObjectFuel fuel : fuels) {
                    shapeRenderer.rect(fuel.bounds.x, fuel.bounds.y, Assets.fuelImage.getWidth(), Assets.fuelImage.getHeight());
                }
                for (DynamicGameObjectFireball fireball: fireballs) {
                    shapeRenderer.rect(fireball.bounds.x, fireball.bounds.y, Assets.fireballImage.getWidth(), Assets.fireballImage.getHeight());
                }
                for (DynamicGameObjectShield shield: shields) {
                    shapeRenderer.rect(shield.bounds.x, shield.bounds.y ,Assets.shieldImage.getWidth(), Assets.shieldImage.getHeight());
                }
                shapeRenderer.rect(car.bounds.x, car.bounds.y, Assets.carImage.getWidth(), Assets.carImage.getHeight());
            }
            shapeRenderer.end();
        }
    }
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    @Override
    public void dispose() {
        Assets.dispose();
        batch.dispose();
        explosion.dispose();
        smoke.dispose();
        smokeCar.dispose();
    }
}

