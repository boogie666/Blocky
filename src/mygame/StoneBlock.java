/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import mygame.blocks.Block;
import mygame.blocks.BlockTexture;
import mygame.blocks.BlockTextureLocator;
import mygame.blocks.Face;
import mygame.blocks.SimpleBlockTexture;

/**
 *
 * @author bogdanpandia
 */
public class StoneBlock implements Block{
    private final BlockTextureLocator blockTextureLocator;

    public StoneBlock() {
        blockTextureLocator = new BlockTextureLocator() {

            public BlockTexture getFaceTexture(Face face) {
                switch(face){
                    case LEFT:
                        return new SimpleBlockTexture(0, 0);
                    case RIGHT:
                        return new SimpleBlockTexture(15,0);
                    default:
                        return new SimpleBlockTexture(5, 5);
                }
            }
        };
    }
    
    public BlockTextureLocator getTexture() {
        return blockTextureLocator;
    }
    
}
