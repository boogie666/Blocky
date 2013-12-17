package mygame.blocks;

import com.jme3.math.Vector3f;
import mygame.util.Vector3Int;

/**
 *
 * @author bogdan
 */
public enum Face {

    TOP(new Vector3Int(0, 1, 0), new Vector3f[]{
        new Vector3f(0,1,0),
        new Vector3f(0,1,1),
        new Vector3f(1,1,0),
        new Vector3f(1,1,1)
    },new int[]{
        2,0,1, 1,3,2
    },new int[][]{
        new int[]{0,0},
        new int[]{0,1},
        new int[]{1,0},
        new int[]{1,1},
    }), 
    BOTTOM(new Vector3Int(0, -1, 0), new Vector3f[]{
        new Vector3f(0,0,0),
        new Vector3f(0,0,1),
        new Vector3f(1,0,0),
        new Vector3f(1,0,1)
    },new int[]{
        2,3,1,1,0,2
    },new int[][]{
        new int[]{0,0},
        new int[]{0,1},
        new int[]{1,0},
        new int[]{1,1},
    }), 
    LEFT(new Vector3Int(-1, 0, 0), new Vector3f[]{
        new Vector3f(0,0,0),
        new Vector3f(0,1,0),
        new Vector3f(0,0,1),
        new Vector3f(0,1,1)
    },new int[]{
        2,3,1,1,0,2
    },new int[][]{
        new int[]{0,0},
        new int[]{0,1},
        new int[]{1,0},
        new int[]{1,1},
        
    }), 
    RIGHT(new Vector3Int(1, 0, 0), new Vector3f[]{
        new Vector3f(1,0,0),
        new Vector3f(1,1,0),
        new Vector3f(1,0,1),
        new Vector3f(1,1,1)
    },new int[]{
        2,0,1, 1,3,2
    },new int[][]{
        new int[]{0,0},
        new int[]{0,1},
        new int[]{1,0},
        new int[]{1,1},
    }), 
    FRONT(new Vector3Int(0, 0, 1), new Vector3f[]{
        new Vector3f(0,0,1),
        new Vector3f(1,0,1),
        new Vector3f(0,1,1),
        new Vector3f(1,1,1)
    },new int[]{
        2,0,1, 1,3,2
    },new int[][]{
        new int[]{1,1},
        new int[]{0,1},
        new int[]{1,0},
        new int[]{0,0},
        
    }),
    BACK(new Vector3Int(0, 0, -1), new Vector3f[]{
        new Vector3f(0,0,0),
        new Vector3f(1,0,0),
        new Vector3f(0,1,0),
        new Vector3f(1,1,0)
    },new int[]{
        2,3,1,1,0,2
    },new int[][]{
        new int[]{1,1},
        new int[]{0,1},
        new int[]{1,0},
        new int[]{0,0},
    });
    
    private final Vector3Int offset;
    private final Vector3f[] baseVerts;
    private final int[] baseIndices;
    private final int[][] texturePosition;
    
    Face(Vector3Int offset,Vector3f[] baseVerts,int[] baseIndices, int[][] texturePosition) {
        this.offset = offset;
        this.baseVerts = baseVerts;
        this.baseIndices = baseIndices;
        this.texturePosition = texturePosition;
    }

    Vector3Int getOffset() {
        return this.offset;
    }
    
    Vector3f[] getBaseVerts(){
        return this.baseVerts;
    }

    int[] getBaseIndices() {
        return baseIndices;
    }
    
    int[][] getTexturePositions(){
        return this.texturePosition;
    }
    
    
    
}
