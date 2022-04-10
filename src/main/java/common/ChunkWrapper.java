package common;

import java.io.Serializable;
import java.util.ArrayList;

import common.gpx.model.Waypoint;

public class ChunkWrapper implements Serializable {
    private Integer chunkId;
    private ArrayList<Waypoint> chunk;

    public ChunkWrapper() {
    }
    
    public ChunkWrapper(Integer chunkId, ArrayList<Waypoint> chunk) {
        this.chunkId = chunkId;
        this.chunk = chunk;
    }
    public Integer getChunkId() {
        return chunkId;
    }
    public void setChunkId(Integer chunkId) {
        this.chunkId = chunkId;
    }
    public ArrayList<Waypoint> getChunk() {
        return chunk;
    }
    public void setChunk(ArrayList<Waypoint> chunk) {
        this.chunk = chunk;
    }
}
