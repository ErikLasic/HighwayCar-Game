package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class HighwayCarGame extends ApplicationAdapter {
    private Texture fuelImage;
    private Texture carImage;
    private Texture policeCarImage;
    private Texture backgroundImage;
    private Texture fireballImage;
    private Sound fireballSound;
    private Sound carCrashSound;
    private Sound fuelSound;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Rectangle car;
    private Array<Rectangle> fuels;    // special LibGDX Array
    private Array<Rectangle> policeCars;
    private Array<Rectangle> fireballs;
    private long lastFuelTime;
    private long lastPoliceCarTime;
    private long lastFireballTime;
    private int fuelPercentage;
    private int carHealth;    // starts with 3
    private long lastFuelDropTime;
    private long counter;

    public BitmapFont font;

    // all values are set experimental
    private static final int SPEED = 600;    // pixels per second
    private static final int SPEED_FUEL = 200; // pixels per second
    private static int SPEED_POLICECAR = 300;    // pixels per second
    private static final int SPEED_FIREBALL = 300;
    private static final long CREATE_FUEL_TIME = 1000000000;    // ns
    private static final long CREATE_POLICECAR_TIME = 500000000;    // ns
    private static final long FIREBALL_RELOAD = 1000000000;
    private static final long FUEL_DROP_TIME = 1000000000;

    @Override
    public void create() {
        font = new BitmapFont();
        font.getData().setScale(2);
        fuelPercentage = 100;
        carHealth = 3;
        counter = (long) Gdx.graphics.getDeltaTime();

        // default way to load a texture
        carImage = new Texture(Gdx.files.internal("car.png"));
        fuelImage = new Texture(Gdx.files.internal("fuel.png"));
        policeCarImage = new Texture(Gdx.files.internal("policecar.png"));
        fireballSound = Gdx.audio.newSound(Gdx.files.internal("fireballSound.wav"));
        fuelSound = Gdx.audio.newSound(Gdx.files.internal("fuelSound.wav"));
        carCrashSound = Gdx.audio.newSound(Gdx.files.internal("carCrash.wav"));
        backgroundImage = new Texture(Gdx.files.internal("highway.png"));
        fireballImage = new Texture(Gdx.files.internal("fireball.png"));

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        // create a Rectangle to logically represents the rocket
        car = new Rectangle();
        car.x = Gdx.graphics.getWidth() / 2f - carImage.getWidth() / 2f;    // center the rocket horizontally
        car.y = 20;    // bottom left corner of the rocket is 20 pixels above the bottom screen edge
        car.width = carImage.getWidth();
        car.height = carImage.getHeight();

        fuels = new Array<Rectangle>();
        policeCars = new Array<Rectangle>();
        fireballs = new Array<Rectangle>();

        lastFuelDropTime = TimeUtils.nanoTime();

        spawnFuel();
        spawnPoliceCar();
    }

    /**
     * Runs every frame.
     */
    @Override
    public void render() {
        // clear screen
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        counter++;

        // process user input
        if (Gdx.input.isTouched()) commandTouched();    // mouse or touch screen
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) commandMoveLeft();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) commandMoveRight();
        if (Gdx.input.isKeyPressed(Input.Keys.A)) commandMoveLeftCorner();
        if (Gdx.input.isKeyPressed(Input.Keys.S)) commandMoveRightCorner();
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) commandExitGame();
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && TimeUtils.nanoTime() - lastFireballTime > FIREBALL_RELOAD) commandShoot();

        if (TimeUtils.nanoTime() - lastFuelTime > CREATE_FUEL_TIME) spawnFuel();
        if (TimeUtils.nanoTime() - lastPoliceCarTime > CREATE_POLICECAR_TIME) spawnPoliceCar();

        batch.begin(); { //Display background 640x480
            batch.draw(backgroundImage, 0, 0);
        }
        batch.end();

        if (carHealth > 0 && fuelPercentage > 0) {

            if (TimeUtils.nanoTime() - lastFuelDropTime > FUEL_DROP_TIME) {
                fuelPercentage=fuelPercentage-10;
                lastFuelDropTime = TimeUtils.nanoTime();
            }

            for (Iterator<Rectangle> it = policeCars.iterator(); it.hasNext(); ) {
                Rectangle policeCar = it.next();
                policeCar.y -= SPEED_POLICECAR * Gdx.graphics.getDeltaTime();
                if (policeCar.y + policeCarImage.getHeight() < 0) it.remove();
                if (policeCar.overlaps(car)) {
                    carCrashSound.play();
                    carHealth--;
                    it.remove();
                }
            }

            for (Iterator<Rectangle> it = fuels.iterator(); it.hasNext(); ) {
                Rectangle fuel = it.next();
                fuel.y -= SPEED_FUEL * Gdx.graphics.getDeltaTime();
                if (fuel.y + fuelImage.getHeight() < 0) it.remove();    // from screen
                if (fuel.overlaps(car)) {
                    fuelSound.play();
                    fuelPercentage = 100;
                    lastFuelDropTime = TimeUtils.nanoTime();
                    it.remove();    // smart Array enables remove from Array
                }
            }

            for(Iterator<Rectangle> it = fireballs.iterator(); it.hasNext(); ) {
                Rectangle fireball= it.next();
                fireball.y += SPEED_FIREBALL * Gdx.graphics.getDeltaTime();
                for(Iterator<Rectangle> it_policeCar = policeCars.iterator(); it_policeCar.hasNext();) {
                    Rectangle policeCar = it_policeCar.next();
                    if (fireball.overlaps(policeCar)) {
                        it_policeCar.remove();
                        it.remove();
                    }
                }
                if (fireball.y > Gdx.graphics.getHeight()) it.remove();
            }

        } else {
            batch.begin();
            {
                counter--;
                font.setColor(Color.RED);
                font.draw(batch, "Game ended!", Gdx.graphics.getHeight() / 2f, Gdx.graphics.getHeight() / 2f);
                font.setColor(Color.BLACK);
                font.draw(batch, "SCORE:" + counter, Gdx.graphics.getHeight() / 2f, Gdx.graphics.getHeight() / 3f);
            }
            batch.end();
        }

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        {    // brackets added just for indent
            batch.draw(carImage, car.x, car.y);
            for (Rectangle policeCar : policeCars) {
                batch.draw(policeCarImage, policeCar.x, policeCar.y);
            }
            for (Rectangle fuel : fuels) {
                batch.draw(fuelImage, fuel.x, fuel.y);
            }
            for (Rectangle fireball : fireballs) {
                batch.draw(fireballImage, fireball.x, fireball.y);
            }
            font.setColor(Color.YELLOW);
            font.draw(batch, "" + fuelPercentage, Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 20);
            font.setColor(Color.ORANGE);
            font.draw(batch, "" + carHealth, 20, Gdx.graphics.getHeight() - 20);
            font.setColor(Color.GREEN);
            font.draw(batch, "" + counter, Gdx.graphics.getWidth()/2f - 25 , Gdx.graphics.getHeight() - 20);
        }
        batch.end();
    }

    /**
     * Release all the native resources.
     */
    @Override
    public void dispose() {
        fuelImage.dispose();
        policeCarImage.dispose();
        carImage.dispose();
        fireballSound.dispose();
        fuelSound.dispose();
        carCrashSound.dispose();
        batch.dispose();
        font.dispose();
        backgroundImage.dispose();
    }

    private void spawnFuel() {
        Rectangle fuel = new Rectangle();
        fuel.x = MathUtils.random(0, Gdx.graphics.getWidth() - fuelImage.getWidth());
        fuel.y = Gdx.graphics.getHeight();
        fuel.width = fuelImage.getWidth();
        fuel.height = fuelImage.getHeight();
        fuels.add(fuel);
        lastFuelTime = TimeUtils.nanoTime();
    }

    private void spawnPoliceCar() {
        Rectangle policeCar = new Rectangle();
        policeCar.x = MathUtils.random(0, Gdx.graphics.getWidth() - fuelImage.getWidth());
        policeCar.y = Gdx.graphics.getHeight();
        policeCar.width = policeCarImage.getWidth();
        policeCar.height = policeCarImage.getHeight();
        policeCars.add(policeCar);
        lastPoliceCarTime = TimeUtils.nanoTime();
    }

    private void commandShoot() {
        Rectangle fireball = new Rectangle();
        fireball.x = car.x + (float) carImage.getWidth() / 2f - (float)fireballImage.getWidth() / 2f;
        fireball.y = car.y + carImage.getHeight();
        fireball.width = fireballImage.getWidth();
        fireball.height = fireballImage.getHeight();
        fireballs.add(fireball);
        lastFireballTime = TimeUtils.nanoTime();
        fireballSound.play();
    }

    private void commandMoveLeft() {
        car.x -= SPEED * Gdx.graphics.getDeltaTime();
        if (car.x < 0) car.x = 0;
    }

    private void commandMoveRight() {
        car.x += SPEED * Gdx.graphics.getDeltaTime();
        if (car.x > Gdx.graphics.getWidth() - carImage.getWidth())
            car.x = Gdx.graphics.getWidth() - carImage.getWidth();
    }

    private void commandMoveLeftCorner() {
        car.x = 0;
    }

    private void commandMoveRightCorner() {
        car.x = Gdx.graphics.getWidth() - carImage.getWidth();
    }

    private void commandTouched() {
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos); // transform the touch/mouse coordinates to our camera's coordinate system
        car.x = touchPos.x - carImage.getWidth() / 2f;
    }

    private void commandExitGame() {
        Gdx.app.exit();
    }
}
