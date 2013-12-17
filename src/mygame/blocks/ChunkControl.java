/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.blocks;

import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.AbstractControl;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.List;
import mygame.util.Vector3Int;
import org.apache.commons.collections.primitives.ArrayFloatList;
import org.apache.commons.collections.primitives.ArrayIntList;
import org.apache.commons.collections.primitives.FloatList;
import org.apache.commons.collections.primitives.IntList;

/**
 *
 * @author bogdanpandia
 */
public class ChunkControl extends AbstractControl implements Savable{
    private final List<Vector3f> verts = new ArrayList<Vector3f>();
    private final IntList indices = new ArrayIntList();
    private final FloatList normals = new ArrayFloatList();
    private final List<Vector2f> textureCoords = new ArrayList<Vector2f>();
    
    private final Block[][][] blocks;
    private final float blockSize;
    
    private final Vector3Int size;
    
    private final Material material;
    private boolean needsUpdate = false;
    private Geometry geom;
    private final Node node;
    private final BlockTerrainControl terrain;
    private final Vector3Int location;
    
    public ChunkControl(BlockTerrainControl terrain, Vector3Int location) {
        this.terrain = terrain;
        this.location = location;
        this.node = new Node("holderNode");
        this.size = terrain.getSettings().getChunkSize();
        this.blockSize = terrain.getSettings().getBlockSize();
        this.blocks = new Block[this.size.getX()][this.size.getY()][this.size.getZ()];
        this.material = terrain.getSettings().getMaterial();
    }

    @Override
    public void setSpatial(Spatial spatial) {
        Node old = (Node) spatial;
        super.setSpatial(spatial); 
        if(old != null){
            old.detachChild(node);
        }
        ((Node) spatial).attachChild(node);
    }
    
    
    
    public void putBlock(Vector3Int at, Block blockType){
        this.updateBlockAt(at,blockType);
    }
    
    public void removeBlock(Vector3Int from){
        this.updateBlockAt(from, null);
    }
    
    public Block getBlock(Vector3Int from){
        if(isValidPosition(from)){
            return blocks[from.getX()][from.getY()][from.getZ()];
        }
        return null;
    }
    
    private void updateBlockAt(final Vector3Int at, Block type){
        if(this.isValidPosition(at)){
            if (geom == null) {
                geom = new Geometry("terrainGeometry");
                geom.setMaterial(this.material);
            }
            blocks[at.getX()][at.getY()][at.getZ()] = type;
            needsUpdate = true;
            this.terrain.setNeedsUpdate(true);
        }
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if(needsUpdate){
            needsUpdate = false;
            geom.setMesh(regenerateMesh());
            geom.updateModelBound();
            if(!node.hasChild(geom)){
                node.attachChild(geom);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    private Mesh regenerateMesh(){
        Mesh m = new Mesh();
        for(int i=0;i<size.getX(); i++){
            for(int j = 0; j<size.getY(); j++){
                for(int k =0; k< size.getZ(); k++){
                    Block bt  = blocks[i][j][k];
                    if(bt != null){
                        Vector3f loc = new Vector3f(i*this.blockSize, j*this.blockSize, k*this.blockSize);
                        Vector3Int virtualLocation = new Vector3Int(i, j, k);
                        if(shouldAddFace(virtualLocation, Face.FRONT)){
                            updateIndices(verts.size(),Face.FRONT);
                            updateVerts(loc,Face.FRONT);
                            updateTextures(bt.getTexture(),Face.FRONT);
                        }
                        if(shouldAddFace(virtualLocation, Face.BACK)){
                            updateIndices(verts.size(),Face.BACK);
                            updateVerts(loc,Face.BACK);
                            updateTextures(bt.getTexture(),Face.BACK);
                        }
                        if(shouldAddFace(virtualLocation, Face.LEFT)){
                            updateIndices(verts.size(),Face.LEFT);
                            updateVerts(loc,Face.LEFT);
                            updateTextures(bt.getTexture(),Face.LEFT);
                        }
                        if(shouldAddFace(virtualLocation, Face.RIGHT)){
                            updateIndices(verts.size(),Face.RIGHT);
                            updateVerts(loc,Face.RIGHT);
                            updateTextures(bt.getTexture(),Face.RIGHT);
                        }
                        if(shouldAddFace(virtualLocation, Face.TOP)){
                            updateIndices(verts.size(),Face.TOP);
                            updateVerts(loc,Face.TOP);
                            updateTextures(bt.getTexture(),Face.TOP);
                        }
                        if(shouldAddFace(virtualLocation, Face.BOTTOM)){
                            updateIndices(verts.size(),Face.BOTTOM);
                            updateVerts(loc,Face.BOTTOM);
                            updateTextures(bt.getTexture(),Face.BOTTOM);
                        }
                        
                    }
                }
            }
        }
        Vector3f[] vertsArray = new Vector3f[this.verts.size()];
        int[] indicesArray = this.indices.toArray();
        Vector2f[] textureCoordsArray = new Vector2f[this.textureCoords.size()];
        float[] normalsArray = this.normals.toArray();
        
        m.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(verts.toArray(vertsArray)));
        m.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indicesArray));
        m.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(textureCoords.toArray(textureCoordsArray)));
        m.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(normalsArray));
        
        verts.clear();
        indices.clear();
        textureCoords.clear();
        normals.clear();
        
        m.updateBound();
        return m;
    }
    
    private boolean shouldAddFace(Vector3Int position, Face face){
        Vector3Int offset = position.add(face.getOffset());
        int x = offset.getX();
        int y = offset.getY();
        int z = offset.getZ();
        if(x < 0){
            return checkNextChunk(offset,face);
        }else if(x > size.getX()-1){
            return checkNextChunk(offset,face);
        }
        if(y < 0){
            return checkNextChunk(offset,face);
        }else if(y > size.getY()-1){
            return checkNextChunk(offset,face);
        }
        if(z < 0){
            return checkNextChunk(offset,face);
        }else if(z > size.getZ()-1){
            return checkNextChunk(offset,face);
        }
        
        return blocks[x][y][z] == null;
    }

    private void updateVerts(Vector3f location, Face face) {
        float sizeX = this.blockSize * this.size.getX() * this.location.getX();
        float sizeY = this.blockSize * this.size.getY() * this.location.getY();
        float sizeZ = this.blockSize * this.size.getZ() * this.location.getZ();
        for(Vector3f vert : face.getBaseVerts()){
            this.verts.add(vert.mult(this.blockSize).addLocal(location).addLocal(sizeX, sizeY, sizeZ));
        }
        Vector3Int normal = face.getOffset();
        for(int i=0; i<4; i++){
            normals.add(normal.getX());
            normals.add(normal.getY());
            normals.add(normal.getZ());
        }
    }

    private void updateIndices(int count, Face face) {
        for(int i : face.getBaseIndices()){
            indices.add(count+i);
        }
    }

    private void updateTextures(BlockTextureLocator textureLocator, Face face) {
        BlockTexture texture = textureLocator.getFaceTexture(face);
        int x = texture.getColumn();
        int y = texture.getRow();
        for(int[] texOffset : face.getTexturePositions()){
            textureCoords.add(getTextureCoords(x, y, texOffset[0],texOffset[1]));
        }
    }
    
    private static Vector2f getTextureCoords(int row, int column, int addX, int addY){
        float textureCount = 16;
        float textureUnit = 1/textureCount;
        float x = (((column + addX) * textureUnit));
        float y = ((((-1 * row) + (addY - 1)) * textureUnit) + 1);
        return new Vector2f(x, y);
    }

    private boolean checkNextChunk(Vector3Int position, Face face) {
//        Vector3Int worldSize = terrain.getWorldSize();
//        int bx = this.location.getX();
//        if(face == Face.LEFT){
//            bx--;
//            if(bx <0){
//                return true;
//            }
//        }
//        if(face == Face.RIGHT){
//            bx++;
//            if(bx >= worldSize.getX()){
//                return true;
//            }
//        }
//        int by = this.location.getY();
//        if(face == Face.TOP){
//            by++;
//            if(by >= worldSize.getY()){
//                return true;
//            }
//        }
//        if(face == Face.BOTTOM){
//            by--;
//            if(by < 0){
//                return true;
//            }
//        }
//        int bz = this.location.getZ();
//        if(face == Face.FRONT){
//            bz++;
//            if(bz >= worldSize.getZ()){
//                return true;
//            }
//        }
//        if(face == Face.BACK){
//            bz--;
//            if(bz < 0){
//                return true;
//            }
//        }
//        ChunkControl[][][] chunks= terrain.getChunks();
        return true;
    }
    
    
    private boolean isValidPosition(Vector3Int position){
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        return x >= 0 && x < size.getX() && y >= 0 && y<size.getY() && z>=0 && z<size.getZ();
        
    }
    
    
    
}
