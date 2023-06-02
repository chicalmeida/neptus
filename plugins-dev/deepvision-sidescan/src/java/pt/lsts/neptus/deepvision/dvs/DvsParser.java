package pt.lsts.neptus.deepvision.dvs;

import pt.lsts.neptus.NeptusLog;
import scala.sys.process.ProcessBuilderImpl;
import sun.jvm.hotspot.debugger.win32.coff.DebugVC50SrcModFileDesc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    private DvsHeader header = new DvsHeader();
    private DvsSonarData ping = new DvsSonarData();

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
        return (long)this.header.getLineRate();//TODO:*ping?
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

        long count = 0;
        long pos = 0;

        long maxTimestampHigh = 0;
        long maxTimestampLow = 0;
        long minTimestampHigh = Long.MAX_VALUE;
        long minTimestampLow = Long.MAX_VALUE;

        int headerSize=14;
        int posSize = 24;

        int version_size = 4;
        curPosition += version_size;

        try{
            while(true) {
                long channelSize = channel.size();
                if (curPosition + headerSize >= channelSize) {
                    break;
                }

                ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, curPosition, headerSize);
                buf.order(ByteOrder.LITTLE_ENDIAN);
                header.parse(buf);
                curPosition += headerSize;


                ping.setHeader(header);

                if(curPosition + posSize >= channelSize){
                    break;
                }
                buf = channel.map(FileChannel.MapMode.READ_ONLY, curPosition, posSize);
                ping.parsePing(buf);
                curPosition += posSize;

                long frequency = 340;
                long t = curPosition;
                int subsystem = 0;




            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    public boolean loadIndex() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(indexPath));
            index = (DvsIndex) in.readObject();

            Long[] tslisthigh;
            Long[] tslistlow;

            tslisthigh = index.positionMapHigh.keySet().toArray(new Long[] {});
            tslistlow = index.positionMapLow.keySet().toArray(new Long[] {});

            Arrays.sort(tslisthigh);
            Arrays.sort(tslistlow);

            tslist.put(SUBSYS_LOW, tslistlow);
            tslist.put(SUBSYS_HIGH, tslisthigh);

            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}


