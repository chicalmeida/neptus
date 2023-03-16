package pt.lsts.neptus.deepvision.dvs;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

public class DvsSonarData {

    private double lat;
    private double lon;
    private float speed;
    private float heading;
    private long timestamp;
    private int numberOfSamples;

    private DvsHeader header;

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public DvsHeader getHeader() {
        return this.header;
    }

    public void setHeader(DvsHeader header) {
        this.header = header;
    }

    public int getNumberOfSamples() {
        return this.numberOfSamples;
    }

    public void setNumberOfSamples(int numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    public double[] getData() {
        return null;
    }

    public double getLat() {
        return this.lat;
    }
    public void setLat(double lat){
        this.lat=lat;
    }

    public double getLon() {
        return this.lon;
    }
    public void setLon(double lon){
        this.lon=lon;
    }

    public float getHeading() {
        return this.heading;
    }

    public void setHeading(float heading) {
        this.heading = heading;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    void parser(ByteBuffer buffer){
        header.parse(buffer);
        int n;
        if((header.isLeft()==1) && (header.isRight()==1)){
            n=header.getnSamples()*2;
        }
        else{
            n=header.getnSamples();
        }
        setLat(buffer.get(18));
        setLon(buffer.get(26));
        setSpeed(buffer.get(30));
        setHeading(buffer.get(34));
    }

}
