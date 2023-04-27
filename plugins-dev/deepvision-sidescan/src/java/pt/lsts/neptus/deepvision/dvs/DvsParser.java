package pt.lsts.neptus.deepvision.dvs;

import pt.lsts.neptus.NeptusLog;
import scala.sys.process.ProcessBuilderImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class DvsParser {
    private File f;
    private FileInputStream fis;
    private FileChannel channel;

    private String indexPath;
    private LinkedHashMap<Integer, Long[]> tslist = new LinkedHashMap<Integer, Long[]>();
    final static int SUBSYS_LOW = 20;
    final static int SUBSYS_HIGH = 21;

    private DvsIndex index;

    private long curPosition = 0;
    public DvsParser(File f){
        try {
            this.f = f;
            fis = new FileInputStream(f);
            channel = fis.getChannel();

            indexPath = f.getParent() + "/mra/dvs.index";

            if (!new File(indexPath).exists()) {
                NeptusLog.pub().info("Generating JSF index for " + f.getAbsolutePath());
                generateIndex();
            }
            else {
                NeptusLog.pub().info("Loading JSF index for " + f.getAbsolutePath());
                if(!loadIndex()) {
                    NeptusLog.pub().error("Corrupted JSF index file. Trying to create a new index.");
                    generateIndex();
                }
            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public long getFirstTimeStamp() {
        return 0;
    }

    public long getLastTimeStamp() {
        return 0;
    }

    public DvsIndex getIndex() {
        return this.index;
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

    public void generateIndex(){
        DvsHeader header = new DvsHeader();
        DvsSonarData ping = new DvsSonarData();

        long count = 0;
        long pos = 0;

        long maxTimestampHigh = 0;
        long maxTimestampLow = 0;
        long minTimestampHigh = Long.MAX_VALUE;
        long minTimestampLow = Long.MAX_VALUE;

        int headerSize=18;

        try{
            while(true){
                long channelSize = channel.size();
                if (curPosition + headerSize >= channelSize) {
                    break;
                }

                ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, curPosition, headerSize);
                buf.order(ByteOrder.LITTLE_ENDIAN);
                header.parse(buf);
                curPosition += headerSize;

            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}


