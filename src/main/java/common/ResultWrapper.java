package common;

import java.io.Serializable;

public class ResultWrapper implements Serializable {
    private Integer chunkId;
    private Result result;
    
    public ResultWrapper() {
    }
    
    public ResultWrapper(Integer chunkId, Result result) {
        this.chunkId = chunkId;
        this.result = result;
    }
    public Integer getChunkId() {
        return chunkId;
    }
    public void setChunkId(Integer chunkId) {
        this.chunkId = chunkId;
    }
    public Result getResult() {
        return result;
    }
    public void setResult(Result result) {
        this.result = result;
    }
}
