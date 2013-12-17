package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import mygame.blocks.BlockTerrainControl;
import mygame.util.Vector3Int;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    SpotLight sl = new SpotLight();

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf); //To change body of generated methods, choose Tools | Templates.
        sl.setPosition(cam.getLocation());
        sl.setDirection(cam.getDirection());
    }
    
    
    
    @Override
    public void simpleInitApp() {
        final BlockSettings blockSettings = new BlockSettings(this);
        blockSettings.setChunkSize(new Vector3Int(16,16,16));
        blockSettings.setMaterial(assetManager.loadMaterial("Materials/BlockyTexture.j3m"));
        blockSettings.setWorldSize(new Vector3Int(50, 10, 50));
        final BlockTerrainControl blocks = new BlockTerrainControl(blockSettings);
        //geom2.move(8, 0, 0);
        Node terrain = new Node();
        terrain.addControl(blocks);
        blocks.registerBlock(new StoneBlock());
        
        sl.setSpotInnerAngle(15 * FastMath.DEG_TO_RAD);
        sl.setSpotOuterAngle(25 * FastMath.DEG_TO_RAD);
        sl.setSpotRange(30);
        rootNode.addLight(sl);
        
        int size = 50;
        for(int i=0;i<size*4;i++){
            for(int j=0;j<20;j++){
                for(int k =0; k<size*4;k++){
//                    if(i==j || j==k || i==k){
//                        continue;
//                    }
//                    if(i+j==size || j+k==size || i+k==size ){
//                        continue;
//                    }
                    blocks.setBlock(new Vector3Int(i, j, k), StoneBlock.class);    
                }
                
            }
        }
        BitmapText hudText = new BitmapText(guiFont, false);          
        hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        hudText.setColor(ColorRGBA.White);                             // font color
        hudText.setText("+");             // the text
        hudText.setLocalTranslation(this.settings.getWidth()/2, this.settings.getHeight()/2 + hudText.getLineHeight(), 0); // position
        guiNode.attachChild(hudText);

//        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        
        rootNode.attachChild(terrain);
        flyCam.setMoveSpeed(50);
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        inputManager.addListener(new ActionListener() {
            int count = 50;
            int up = 50;
            int multiplier = 1;
            public void onAction(String name, boolean isPressed, float tpf) {
                if(!isPressed){
                    return;
                }
                CollisionResults results = new CollisionResults();
                // Convert screen click to 3d position
                Vector3f click3d = cam.getWorldCoordinates(new Vector2f(cam.getWidth() / 2, cam.getHeight() / 2), 0f).clone();
                Vector3f dir = cam.getWorldCoordinates(new Vector2f(cam.getWidth() / 2, cam.getHeight() / 2), 1f).subtractLocal(click3d).normalizeLocal();
                // Aim the ray from the clicked spot forwards.
                Ray ray = new Ray(click3d, dir);
                // Collect intersections between ray and all nodes in results list.
                rootNode.collideWith(ray, results);
                CollisionResult result = results.getClosestCollision();
                if(result != null){
                    Vector3f position = result.getContactPoint();
                    Vector3Int blockPosition = blocks.getPointedBlockLocation(position, true);

                    System.out.println(blockPosition);
                    blocks.setBlock(blockPosition, StoneBlock.class);
//                    c.removeBlock(blockPosition);
                }
        
                
            }
        }, "toggle");
        inputManager.addMapping("toggle", new KeyTrigger(KeyInput.KEY_SPACE));
        
    }
 
}

