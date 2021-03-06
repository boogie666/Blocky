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
import com.jme3.terrain.heightmap.HeightMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final Set<ChunkControl> updateables = new HashSet<ChunkControl>();
    private final List<BlockTerrainListener> listeners = new ArrayList<BlockTerrainListener>();
    
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
            for(ChunkControl c : this.updateables){
                c.update(tpf);
                for(BlockTerrainListener l : listeners){
                    l.onChunkUpdated(c);
                }
            }
            
            this.updateables.clear();
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
        this.updateables.add(chunk);
        this.needsUpdate = true;
    }
    
    private ChunkPosition getValidChunk(Vector3Int location){
        int x = location.getX();
        int y = location.getY();
        int z = location.getZ();
        int cX = 0;
        int cY = 0;
        int cZ = 0;
        while(x >= chunkSize.getX()){
            x -= chunkSize.getX();
            cX++;
        }
        while(y >= chunkSize.getY()){
            y -= chunkSize.getY();
            cY++;
        }
        while(z >= chunkSize.getZ()){
            z -= chunkSize.getZ();
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
    
    public void loadFromHeightMap(Vector3Int location,HeightMap heightmap, Class<? extends Block> blockClass){
        setBlocksFromHeightmap(location, getHeightmapBlockData(heightmap.getScaledHeightMap(), heightmap.getSize()), blockClass);
    }

    private static int[][] getHeightmapBlockData(float[] heightmapData, int length){
        int[][] data = new int[heightmapData.length / length][length];
        int x = 0;
        int z = 0;
        for(int i=0;i<heightmapData.length;i++){
            data[x][z] = (int) Math.round(heightmapData[i]);
            x++;
            if((x != 0) && ((x % length) == 0)){
                x = 0;
                z++;
            }
        }
        return data;
    }

    public void setBlocksFromHeightmap(Vector3Int location, int[][] heightmap, Class<? extends Block> blockClass){
        Vector3Int tmpLocation = new Vector3Int();
        Vector3Int tmpSize = new Vector3Int();
        for(int x=0;x<heightmap.length;x++){
            for(int z=0;z<heightmap[0].length;z++){
                tmpLocation.set(location.getX() + x, location.getY(), location.getZ() + z);
                tmpSize.set(1, heightmap[x][z], 1);
                setBlockArea(tmpLocation, tmpSize, blockClass);
            }
        }
    }
    
    public void setBlockArea(Vector3Int location, Vector3Int size, Class<? extends Block> blockClass){
        Vector3Int tmpLocation = new Vector3Int();
        for(int x=0;x<size.getX();x++){
            for(int y=0;y<size.getY();y++){
                for(int z=0;z<size.getZ();z++){
                    tmpLocation.set(location.getX() + x, location.getY() + y, location.getZ() + z);
                    setBlock(tmpLocation, blockClass);
                }
            }
        }
    }
    
    public void addListener(BlockTerrainListener listener){
        this.listeners.add(listener);
    }
    
    public void removeListener(BlockTerrainListener listener){
        this.listeners.remove(listener);
    }
    
}
