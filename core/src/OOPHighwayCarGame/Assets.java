package OOPHighwayCarGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Assets {
    public static Texture fuelImage;
    public static Texture carImage;
    public static Texture policeCarImage;
    public static Texture backgroundImage;
    public static Texture fireballImage;
    public static Texture shieldImage;
    public static Texture shieldedCarImage;
    public static Sound fireballSound;
    public static Sound carCrashSound;
    public static Sound fuelSound;
    public static Sound shieldSound;
    public static Sound carCrushSound;
    public static BitmapFont font;

    public static void load() {
        carImage = new Texture(Gdx.files.internal("car.png"));
        fuelImage = new Texture(Gdx.files.internal("fuel.png"));
        policeCarImage = new Texture(Gdx.files.internal("policecar.png"));
        fireballSound = Gdx.audio.newSound(Gdx.files.internal("fireballSound.wav"));
        fuelSound = Gdx.audio.newSound(Gdx.files.internal("fuelSound.wav"));
        carCrashSound = Gdx.audio.newSound(Gdx.files.internal("carCrash.wav"));
        backgroundImage = new Texture(Gdx.files.internal("highway.png"));
        fireballImage = new Texture(Gdx.files.internal("fireball.png"));
        shieldImage = new Texture(Gdx.files.internal("shield.png"));
        shieldSound = Gdx.audio.newSound(Gdx.files.internal("shieldSound.wav"));
        shieldedCarImage = new Texture(Gdx.files.internal("shieldedCar.png"));
        carCrushSound = Gdx.audio.newSound(Gdx.files.internal("carCrush.wav"));

        font = new BitmapFont();
        font.getData().setScale(2);
    }

    public static void dispose() {
        fuelImage.dispose();
        policeCarImage.dispose();
        carImage.dispose();
        fireballSound.dispose();
        fuelSound.dispose();
        carCrashSound.dispose();
        font.dispose();
        backgroundImage.dispose();
        shieldImage.dispose();
        shieldSound.dispose();
        shieldedCarImage.dispose();
        carCrushSound.dispose();
    }
    }
