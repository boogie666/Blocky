/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import mygame.blocks.Block;
import mygame.blocks.BlockTextureLocator;
import mygame.blocks.SimpleBlockTexture;

/**
 *
 * @author bogdanpandia
 */
public class StoneBlock implements Block{
    private final BlockTextureLocator blockTextureLocator;

    public StoneBlock() {
        blockTextureLocator = new SimpleBlockTexture(0, 0);
    }
    
    public BlockTextureLocator getTexture() {
        return blockTextureLocator;
    }
    
}
