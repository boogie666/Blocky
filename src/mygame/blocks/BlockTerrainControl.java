/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.blocks;

import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.HashMap;
import java.util.Map;
import mygame.BlockSettings;
import mygame.util.Vector3Int;

/**
 *
 * @author bogdan
 */
public class BlockTerrainControl extends AbstractControl implements Savable{
    private final ChunkControl[][][] chunks;
    private final Map<Class<? extends Block>, Block> blockTypes;
    private final Vector3Int worldSize;
    private final Vector3Int chunkSize;
    private final BlockSettings settings;
    
    private boolean needsUpdate = false;
    
    public BlockTerrainControl(BlockSettings settings) {
        this.worldSize = settings.getWorldSize();
        this.chunkSize = settings.getChunkSize();
        this.settings = settings;
        
        this.chunks = new ChunkControl[worldSize.getX()][worldSize.getY()][worldSize.getZ()];
        blockTypes = new HashMap<Class<? extends Block>, Block>();
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(needsUpdate){
            for(ChunkControl[][] i : chunks){
                for(ChunkControl[] j : i){
                    for(ChunkControl k : j){
                        if(k != null)
                            k.update(tpf);
                    }
                }
            }
            needsUpdate = false;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    public void registerBlock(Block block){
        this.blockTypes.put(block.getClass(), block);
    }
    
    public void setBlock(Vector3Int location, Class<? extends Block> blockType){
        this.updateBlock(location, this.blockTypes.get(blockType));
    }
    
    public void removeBlock(Vector3Int location){
        this.updateBlock(location, null);
    }
    
    public Block getBlock(Vector3Int location){
        ChunkPosition chunkPosition = getValidChunk(location);
        return chunkPosition.chunk.getBlock(chunkPosition.positionInChunk);
    }

    public Vector3Int getWorldSize() {
        return worldSize;
    }
    
    public BlockSettings getSettings() {
        return settings;
    }

    public ChunkControl[][][] getChunks() {
        return chunks;
    }
    
    void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }
    
    private void updateBlock(Vector3Int location, Block block){
        ChunkPosition chunkPosition = getValidChunk(location);
        ChunkControl chunk = chunkPosition.chunk;
        chunk.putBlock(chunkPosition.positionInChunk, block);
    }
    
    private ChunkPosition getValidChunk(Vector3Int location){
        int x = location.getX();
        int y = location.getY();
        int z = location.getZ();
        int cX = 0;
        int cY = 0;
        int cZ = 0;
        while(x >= chunkSize.getX()){
            x = x - chunkSize.getX();
            cX++;
        }
        while(y >= chunkSize.getY()){
            y = y - chunkSize.getY();
            cY++;
        }
        while(z >= chunkSize.getZ()){
            z = z - chunkSize.getZ();
            cZ++;
        }
        ChunkControl chunk = this.chunks[cX][cY][cZ];
        if(chunk == null){
            chunk = new ChunkControl(this, new Vector3Int(cX,cY,cZ));
            chunk.setSpatial(this.spatial);
            this.chunks[cX][cY][cZ] = chunk;
        }
        
        return new ChunkPosition(chunk, new Vector3Int(x, y, z));
    }

    private static final class ChunkPosition{
        final ChunkControl chunk;
        final Vector3Int positionInChunk;

        public ChunkPosition(ChunkControl chunk, Vector3Int positionInChunk) {
            this.chunk = chunk;
            this.positionInChunk = positionInChunk;
        }
                
    }
    
    public Vector3Int getPointedBlockLocation(Vector3f collisionLocation, boolean getNeighborLocation){
        Vector3Int blockLocation = new Vector3Int(
                (int) (collisionLocation.getX() / this.getSettings().getBlockSize()),
                (int) (collisionLocation.getY() / this.getSettings().getBlockSize()),
                (int) (collisionLocation.getZ() / this.getSettings().getBlockSize()));
        if((this.getBlock(blockLocation) != null) == getNeighborLocation){
            if((collisionLocation.getX() % this.getSettings().getBlockSize()) == 0) {
                blockLocation.subtractLocal(1, 0, 0);
            }
            else if((collisionLocation.getY() % this.getSettings().getBlockSize()) == 0){
                blockLocation.subtractLocal(0, 1, 0);
            }
            else if((collisionLocation.getZ() % this.getSettings().getBlockSize()) == 0){
                blockLocation.subtractLocal(0, 0, 1);
            }
        }
        return blockLocation;
    }
    
    
    
}
