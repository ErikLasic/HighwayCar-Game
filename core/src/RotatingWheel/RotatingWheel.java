package RotatingWheel;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RotatingWheel extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Texture texture;
    private Sprite sprite;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch = new SpriteBatch();
        texture =  new Texture(Gdx.files.internal("wheel.png"));

        sprite = new Sprite(texture);
        sprite.setSize(100,100);
        sprite.setOrigin(50,50); //Koordinati vrtenja glede na koordinate prostora na zaslonu
    }

    int rotacija = 0;
    int pozicija = 0;
    boolean zamenjaj = false;

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(zamenjaj) {
            sprite.setPosition(pozicija--,0); //Premikanje po x-osi
            sprite.setRotation(rotacija++); //Rotiranje slike
            if(pozicija == 0){
                zamenjaj =false;
            }
        } else {
            if (pozicija < Gdx.graphics.getWidth()) {
                sprite.setPosition(pozicija++, 0); //Premikanje po x-osi
                sprite.setRotation(rotacija--); //Rotiranje slike
                if(pozicija ==Gdx.graphics.getWidth()-texture.getWidth()/2f+100f){ //Od začetka do konca ekrana
                    zamenjaj =true;
                }
            }
        }
        batch.begin();
        sprite.draw(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
    }
}