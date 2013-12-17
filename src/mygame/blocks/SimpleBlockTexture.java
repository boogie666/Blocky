/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.blocks;

/**
 *
 * @author bogdanpandia
 */
public class SimpleBlockTexture implements BlockTexture,BlockTextureLocator{
    private final int row;
    private final int column;

    public SimpleBlockTexture(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public BlockTexture getFaceTexture(Face face) {
        return this;
    }
    
}
