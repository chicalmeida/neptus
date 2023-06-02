package pt.lsts.neptus.deepvision.dvs;

import java.nio.ByteBuffer;

public class DvsHeader {
    private float sampleRes;
    private float lineRate;
    private int nSamples;
    private int left; //00 if inactive, 01 if port active
    private int right; ////00 if inactive, 01 if port active

    private int channel;


    public DvsHeader(){
    }

    public void setSampleRes(float sampleRes){
        this.sampleRes = sampleRes;
    }

    public float getSampleRes() {
        return sampleRes;
    }

    public void setLineRate(float lineRate) {
        this.lineRate = lineRate;
    }

    public float getLineRate() {
        return lineRate;
    }

    public void setnSamples(int nSamples) {
        this.nSamples = nSamples;
    }

    public int getnSamples() {
        return nSamples;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int isLeft() {
        return left;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int isRight() {
        return right;
    }


    void parse(ByteBuffer buffer){
        setSampleRes(buffer.getFloat(0));
        setLineRate(buffer.getFloat(4));
        setnSamples(buffer.getInt(8));
        setLeft(buffer.get(12));
        setRight(buffer.get(13));
    }

    public int getChannel() {
        return channel;
    }
    public void setChannel(int channel){
        this.channel=channel;
    }
}
