package pt.lsts.neptus.deepvision.dvs;

import scala.sys.process.ProcessBuilderImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class DvsParser {
    private File f;
    private FileInputStream fis;
    private FileChannel channel;
    public DvsParser(File f){
        this.f=f;
    }

    public long getFirstTimeStamp() {
        return 0;
    }

    public long getLastTimeStamp() {
        return 0;
    }

    public DvsIndex getIndex() {
        return new DvsIndex();
    }

    public ArrayList<DvsSonarData> getPingAt(long timestamp1, int subsystem) {
        return new ArrayList<>();
    }

    public ArrayList<DvsSonarData> nextPing(int subsystem) {
        return new ArrayList<>();
    }

    public void cleanup() {
        try {
            if (fis != null) {
                fis.close();
            }
            if (channel != null) {
                channel.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
